/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *        or more contributor license agreements.  See the NOTICE file
 *        distributed with this work for additional information
 *        regarding copyright ownership.  The ASF licenses this file
 *        to you under the Apache License, Version 2.0 (the
 *        "License"); you may not use this file except in compliance
 *        with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing,
 *        software distributed under the License is distributed on an
 *        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *        KIND, either express or implied.  See the License for the
 *        specific language governing permissions and limitations
 *        under the License.
 */

#include <stdio.h>
#include <libspotify/api.h>
#include <sys/time.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>

#include "Logging.h"
#include "JNIHelpers.h"
#include "JahSpotify.h"
#include "jahspotify_impl_JahSpotifyImpl.h"
#include "AppKey.h"
#include "audio.h"
#include "Callbacks.h"
#include "ThreadHelpers.h"

#define MAX_LENGTH_FOLDER_NAME 256

/// The global session handle
sp_session *g_sess = NULL;
/// Handle to the curren track
sp_track *g_currenttrack = NULL;

jobject g_libraryListener = NULL;
jobject g_connectionListener = NULL;
jobject g_playbackListener = NULL;
jobject g_searchCompleteListener = NULL;
jobject g_mediaLoadedListener = NULL;
extern jclass g_linkClass;

/// The output queue for audo data
static audio_fifo_t g_audiofifo;
/// Synchronization mutex for the main thread
static pthread_mutex_t g_notify_mutex;
/// Synchronization condition variable for the main thread
static pthread_cond_t g_notify_cond;
/// Synchronization variable telling the main thread to process events
static int g_notify_do;
/// Non-zero when a track has ended and a new one has not yet started a new one
static int g_playback_done;

static bool g_audio_initialized = JNI_FALSE;

void populateJAlbumInstanceFromAlbumBrowse(JNIEnv *env, sp_album *album, sp_albumbrowse *albumBrowse, jobject albumInstance);
void populateJArtistInstanceFromArtistBrowse(JNIEnv *env, sp_artistbrowse *artistBrowse, jobject artist);
jobject createJArtistInstance(JNIEnv *env, sp_artist *artist);
jobject createJAlbumInstance(JNIEnv *env, sp_album *album);
jobject createJTrackInstance(JNIEnv *env, sp_track *track);
jobject createJPlaylistInstance(JNIEnv *env, sp_playlist *playlist);

/* --------------------------  PLAYLIST CALLBACKS  ------------------------- */
/**
 * Callback from libspotify, saying that a track has been added to a playlist.
 *
 * @param  pl          The playlist handle
 * @param  tracks      An array of track handles
 * @param  num_tracks  The number of tracks in the \c tracks array
 * @param  position    Where the tracks were inserted
 * @param  userdata    The opaque pointer
 */
static void SP_CALLCONV tracks_added ( sp_playlist *pl, sp_track * const *tracks,
                           int num_tracks, int position, void *userdata )
{
    log_debug("jahspotify","tracks_added","Tracks added: playlist: %s numtracks: %d position: %d",sp_playlist_name(pl),num_tracks,position);
}

/**
 * Callback from libspotify, saying that a track has been removed from a playlist.
 *
 * @param  pl          The playlist handle
 * @param  tracks      An array of track indices
 * @param  num_tracks  The number of tracks in the \c tracks array
 * @param  userdata    The opaque pointer
 */
static void SP_CALLCONV tracks_removed ( sp_playlist *pl, const int *tracks,
                             int num_tracks, void *userdata )
{
    log_debug("jahspotify","tracks_removed","Tracks removed: playlist: %s numtracks: %d",sp_playlist_name(pl), num_tracks);
}

/**
 * Callback from libspotify, telling when tracks have been moved around in a playlist.
 *
 * @param  pl            The playlist handle
 * @param  tracks        An array of track indices
 * @param  num_tracks    The number of tracks in the \c tracks array
 * @param  new_position  To where the tracks were moved
 * @param  userdata      The opaque pointer
 */
static void SP_CALLCONV tracks_moved ( sp_playlist *pl, const int *tracks,
                           int num_tracks, int new_position, void *userdata )
{
    log_debug("jahspotify","tracks_moved","Tracks moved: playlist: %s numtracks: %d",sp_playlist_name(pl), num_tracks);
}

/**
 * Callback from libspotify. Something renamed the playlist.
 *
 * @param  pl            The playlist handle
 * @param  userdata      The opaque pointer
 */
static void SP_CALLCONV playlist_renamed ( sp_playlist *pl, void *userdata )
{
    log_debug("jahspotify","tracks_renamed","Tracks renamed: playlist: %s",sp_playlist_name(pl));
}

static void SP_CALLCONV playlist_state_changed ( sp_playlist *pl, void *userdata )
{
    sp_link *link = sp_link_create_from_playlist(pl);
    char *linkName = malloc(sizeof(char)*100);

    log_debug("jahspotify","playlist_state_changed","State changed on playlist: %s",sp_playlist_name(pl));
    
    if (link)
    {
      sp_link_as_string(link,linkName, 100);
      log_debug("jahspotify","playlist_state_changed", "Playlist state changed: %s link: %s (loaded: %s)",sp_playlist_name ( pl ), linkName, (sp_playlist_is_loaded(pl) ? "yes" : "no"));
      
      if (sp_playlist_is_loaded(pl))
      {
        signalPlaylistLoaded(pl,0);
      }
      
      sp_link_release(link);
    }
    if (linkName) free(linkName);
}

static void SP_CALLCONV playlist_update_in_progress ( sp_playlist *pl, bool done, void *userdata )
{
    const char *name = sp_playlist_name ( pl );
    char *playListlinkStr;
    char *trackLinkStr;
    sp_link *link;
    int trackCounter;
    
    log_debug("jahspotify","playlist_update_in_progress","Update in progress: %s (done: %s)", name, (done ? "yes" : "no"));
    
    if (done)
    {
        link = sp_link_create_from_playlist(pl);
        if (link)
        {
            playListlinkStr =  calloc ( 1, sizeof ( char ) * ( 100 ) );
            sp_link_as_string(link,playListlinkStr,100);
            sp_link_release(link);
            signalPlaylistSeen(name,playListlinkStr);
        }

    }

}

static void SP_CALLCONV playlist_metadata_updated ( sp_playlist *pl, void *userdata )
{
    log_debug("jahspotify","playlist_metadata_updated","Metadata updated: %s",sp_playlist_name(pl));
    // signalMetadataUpdated(pl);
}

/**
 * The callbacks we are interested in for individual playlists.
 */
static sp_playlist_callbacks pl_callbacks =
{
    .tracks_added = &tracks_added,
    .tracks_removed = &tracks_removed,
    .tracks_moved = &tracks_moved,
    .playlist_renamed = &playlist_renamed,
    .playlist_state_changed = &playlist_state_changed,
    .playlist_update_in_progress = &playlist_update_in_progress,
    .playlist_metadata_updated = &playlist_metadata_updated,

};


/* --------------------  PLAYLIST CONTAINER CALLBACKS  --------------------- */
/**
 * Callback from libspotify, telling us a playlist was added to the playlist container.
 *
 * We add our playlist callbacks to the newly added playlist.
 *
 * @param  pc            The playlist container handle
 * @param  pl            The playlist handle
 * @param  position      Index of the added playlist
 * @param  userdata      The opaque pointer
 */
static void SP_CALLCONV playlist_added ( sp_playlistcontainer *pc, sp_playlist *pl,
                             int position, void *userdata )
{
    log_debug("jahspotify","playlist_added","Playlist added: %s (loaded: %s)",sp_playlist_name(pl),sp_playlist_is_loaded(pl) ? "Yes" : "No");
    // sp_playlist_add_callbacks ( pl, &pl_callbacks, NULL );
}

/**
 * Callback from libspotify, telling us a playlist was removed from the playlist container.
 *
 * This is the place to remove our playlist callbacks.
 *
 * @param  pc            The playlist container handle
 * @param  pl            The playlist handle
 * @param  position      Index of the removed playlist
 * @param  userdata      The opaque pointer
 */
static void SP_CALLCONV playlist_removed ( sp_playlistcontainer *pc, sp_playlist *pl,
                               int position, void *userdata )
{
    const char *name = sp_playlist_name(pl);
    log_debug("jahspotify","playlist_removed","Playlist removed: %s",name);
    sp_playlist_remove_callbacks ( pl, &pl_callbacks, NULL );
}


/**
 * Callback from libspotify, telling us the rootlist is fully synchronized
 *
 * @param  pc            The playlist container handle
 * @param  userdata      The opaque pointer
 */
static void SP_CALLCONV container_loaded ( sp_playlistcontainer *pc, void *userdata )
{
    char *folderName = calloc ( 1, sizeof ( char ) * ( MAX_LENGTH_FOLDER_NAME ) );
    int i;


    if ( folderName == NULL )
    {
        log_error("jahspotify","container_loaded","Could not allocate folder name variable" );
        return;
    }

    // log_error("jahspotify: Rootlist synchronized (%d playlists)\n",sp_playlistcontainer_num_playlists ( pc ) );
    signalSynchStarting(sp_playlistcontainer_num_playlists (pc));

    for ( i = 0; i < sp_playlistcontainer_num_playlists ( pc ); ++i )
    {
        sp_playlist *pl = sp_playlistcontainer_playlist ( pc, i );
        sp_playlist_add_callbacks ( pl, &pl_callbacks, NULL );

        sp_link *link = sp_link_create_from_playlist(pl);

        char *linkStr = calloc(1, sizeof(char) * 100);
        if (link)
        {
            sp_link_add_ref(link);
            sp_link_as_string(link,linkStr,100);
        }
        else
        {
            strcpy(linkStr,"N/A\0");
            // strcpy(linkStr,sp_playlist_name(pl));
        }
        switch ( sp_playlistcontainer_playlist_type ( pc,i ) )
        {
          case SP_PLAYLIST_TYPE_PLAYLIST:
            signalPlaylistSeen(sp_playlist_name ( pl ),linkStr);
            break;
          case SP_PLAYLIST_TYPE_START_FOLDER:
            sp_playlistcontainer_playlist_folder_name ( pc,i,folderName, MAX_LENGTH_FOLDER_NAME);
            signalStartFolderSeen(folderName, sp_playlistcontainer_playlist_folder_id(pc,i));
            break;
          case SP_PLAYLIST_TYPE_END_FOLDER:
            sp_playlistcontainer_playlist_folder_name ( pc,i,folderName,MAX_LENGTH_FOLDER_NAME);
            signalEndFolderSeen();
            break;
          case SP_PLAYLIST_TYPE_PLACEHOLDER:
              log_debug("jahspotify","container_loaded","Placeholder");
              break;
          default:
              log_warn("jahspotify","container_loaded","Unhandled playlist type: %d", sp_playlistcontainer_playlist_type ( pc,i ));
        }

        if (link)
            sp_link_release(link);
        free(linkStr);
    }
    signalSynchCompleted();
    free ( folderName );
}


/**
 * The playlist container callbacks
 */
static sp_playlistcontainer_callbacks pc_callbacks =
{
    .playlist_added = &playlist_added,
    .playlist_removed = &playlist_removed,
    .container_loaded = &container_loaded,
};



/* ---------------------------  SESSION CALLBACKS  ------------------------- */
/**
 * This callback is called when an attempt to login has succeeded or failed.
 *
 * @sa sp_session_callbacks#logged_in
 */
static void SP_CALLCONV logged_in ( sp_session *sess, sp_error error )
{
    sp_playlistcontainer *pc = sp_session_playlistcontainer ( sess );
    int i;

    sp_playlistcontainer_add_callbacks ( sp_session_playlistcontainer ( g_sess ),&pc_callbacks,NULL );

    
    if ( SP_ERROR_OK != error )
    {
        log_error("jahspotify","logged_in","Login failed: %s", sp_error_message ( error ) );
        exit ( 2 );
    }

    log_debug("jahspotify","logged_in","Login Success: %d", sp_playlistcontainer_num_playlists(pc));

    for (i = 0; i < sp_playlistcontainer_num_playlists(pc); ++i) {
        sp_playlist *pl = sp_playlistcontainer_playlist(pc, i);

        sp_playlist_add_callbacks(pl, &pl_callbacks, NULL);
    }

    signalLoggedIn();

    log_debug("jahspotify","logged_in","All done");
}

static void SP_CALLCONV logged_out ( sp_session *sess )
{
    log_debug("jahspotify","logged_out","Logged out");
    signalLoggedOut();
}


/**
 * This callback is called from an internal libspotify thread to ask us to
 * reiterate the main loop.
 *
 * We notify the main thread using a condition variable and a protected variable.
 *
 * @sa sp_session_callbacks#notify_main_thread
 */
static void SP_CALLCONV notify_main_thread ( sp_session *sess )
{
    pthread_mutex_lock ( &g_notify_mutex );
    g_notify_do = 1;
    pthread_cond_signal ( &g_notify_cond );
    pthread_mutex_unlock ( &g_notify_mutex );
}

/**
 * This callback is used from libspotify whenever there is PCM data available.
 *
 * @sa sp_session_callbacks#music_delivery
 */
static int SP_CALLCONV music_delivery ( sp_session *sess, const sp_audioformat *format,
                            const void *frames, int num_frames )
{
    audio_fifo_t *af = &g_audiofifo;
    audio_fifo_data_t *afd;
    size_t s;

    if (num_frames == 0)
        return 0; // Audio discontinuity, do nothing


    pthread_mutex_lock(&af->mutex);

    /* Buffer one second of audio */
    if (af->qlen > format->sample_rate)
    {
        pthread_mutex_unlock(&af->mutex);
        return 0;
    }

    s = num_frames * sizeof(int16_t) * format->channels;

    afd = calloc(1, sizeof(audio_fifo_data_t) + s);
    memcpy(afd->samples, frames, s);

    afd->nsamples = num_frames;

    afd->rate = format->sample_rate;
    afd->channels = format->channels;

    TAILQ_INSERT_TAIL(&af->q, afd, link);
    af->qlen += num_frames;

    pthread_cond_signal(&af->cond);

    pthread_mutex_unlock(&af->mutex);

    return num_frames;
}


/**
 * This callback is used from libspotify when the current track has ended
 *
 * @sa sp_session_callbacks#end_of_track
 */
static void SP_CALLCONV end_of_track ( sp_session *sess )
{
    pthread_mutex_lock ( &g_notify_mutex );
    g_playback_done = 1;
    pthread_cond_signal ( &g_notify_cond );
    pthread_mutex_unlock ( &g_notify_mutex );
}


/**
 * Callback called when libspotify has new metadata available
 *
 * Not used in this example (but available to be able to reuse the session.c file
 * for other examples.)
 *
 * @sa sp_session_callbacks#metadata_updated
 */
static void SP_CALLCONV metadata_updated ( sp_session *sess )
{
    log_debug("jahspotify","metadata_updated","Metadata updated" );
}

/**
 * Notification that some other connection has started playing on this account.
 * Playback has been stopped.
 *
 * @sa sp_session_callbacks#play_token_lost
 */
static void SP_CALLCONV play_token_lost ( sp_session *sess )
{
    log_error("jahspotify","play_token_lost","Play token lost" );
    if ( g_currenttrack != NULL )
    {
        sp_session_player_unload ( g_sess );
        g_currenttrack = NULL;
    }
}

static void SP_CALLCONV userinfo_updated (sp_session *sess)
{
    log_debug("jahspotify","userinfo_updated","User information updated");
}

static void SP_CALLCONV log_message(sp_session *session, const char *data)
{
    log_debug("jahspotify","log_message","Spotify log message: %s",data);
}

static void SP_CALLCONV connection_error(sp_session *session, sp_error error)
{
    log_error("jahspotify","connection_error","Error: %s",sp_error_message(error));
    // FIXME: should propagate this to java land
    if (error == SP_ERROR_OK)
    {
      signalConnected();
    }
    else
    {
        log_error("jahspotify","connection_error","Unhandled error: %s",sp_error_message(error));
    }
}

static void SP_CALLCONV streaming_error(sp_session *session, sp_error error)
{
    log_error("jahspotify","streaming_error","Streaming error: %s",sp_error_message(error));
    // FIXME: should propagate this to java land
}

static void SP_CALLCONV start_playback(sp_session *session)
{
    log_debug("jahspotify","start_playback","Next playback about to start, initiating pre-load sequence");
    startPlaybackSignalled();
}

static void SP_CALLCONV stop_playback(sp_session *session)
{
  log_debug("jahspotify","stop_playback","Playback should stop");
}

static void SP_CALLCONV message_to_user(sp_session *session, const char *data)
{
  log_debug("jahspotify","message_to_user","Message to user: ", data);
}


/**
 * The session callbacks
 */
static sp_session_callbacks session_callbacks =
{
    .message_to_user = &message_to_user,
    .logged_in = &logged_in,
    .logged_out = &logged_out,
    .notify_main_thread = &notify_main_thread,
    .music_delivery = &music_delivery,
    .metadata_updated = &metadata_updated,
    .play_token_lost = &play_token_lost,
    .log_message = log_message,
    .end_of_track = &end_of_track,
    .userinfo_updated = &userinfo_updated,
    .connection_error = &connection_error,
    .streaming_error = &streaming_error,
    .start_playback = &start_playback
};


static sp_session_config spconfig =
{
    .api_version = SPOTIFY_API_VERSION,
    .cache_location = "/var/lib/jahspotify/cache",
    .settings_location = "/var/lib/jahspotify/",
    .application_key = g_appkey,
    .application_key_size = 0, // Set in main()
    .user_agent = "jahspotify/0.0.1",
    .callbacks = &session_callbacks,
    NULL,
};

static void SP_CALLCONV searchCompleteCallback(sp_search *result, void *userdata)
{
    int32_t *token = (int32_t*)userdata;
    
    if (sp_search_error(result) == SP_ERROR_OK)
    {
        signalSearchComplete(result, *token);
    }
    else
    {
        log_error("jahspotify","searchCompleteCallback","Search completed with error: %s\n",sp_error_message(sp_search_error(result)));
    }
}

JNIEXPORT void JNICALL Java_jahspotify_impl_JahSpotifyImpl_nativeInitiateSearch(JNIEnv *env, jobject obj,
                    jint javaToken,
                    jobject javaNativeSearchParameters)
{
  char *nativeQuery;
  int32_t *token = calloc(1, sizeof(int32_t));
  int32_t numAlbums;
  int32_t albumOffset;
  int32_t numArtists; 
  int32_t artistOffset;
  int32_t numTracks;
  int32_t trackOffset;
  jint value;
  
  *token = javaToken;
  
  getObjectIntField(env,javaNativeSearchParameters,"numAlbums",&value);
  numAlbums = value;
  getObjectIntField(env,javaNativeSearchParameters,"albumOffset",&value);
  albumOffset = value;
  getObjectIntField(env,javaNativeSearchParameters,"numArtists",&value);
  numArtists = value;
  getObjectIntField(env,javaNativeSearchParameters,"artistOffset",&value);
  artistOffset = value;
  getObjectIntField(env,javaNativeSearchParameters,"numTracks",&value);
  numTracks = value;
  getObjectIntField(env,javaNativeSearchParameters,"trackOffset",&value);
  trackOffset = value;

  if (createNativeString(env, getObjectStringField(env, javaNativeSearchParameters,"_query"),&nativeQuery) != 1)
  {
      // FIXME: Handle error
  }
  
  sp_search *search = sp_search_create(g_sess,nativeQuery,trackOffset,numTracks,albumOffset,numAlbums,artistOffset,numArtists,0,0,0,searchCompleteCallback,token);

}

JNIEXPORT jboolean JNICALL Java_jahspotify_impl_JahSpotifyImpl_registerNativeMediaLoadedListener (JNIEnv *env, jobject obj, jobject mediaLoadedListener)
{
   g_mediaLoadedListener =  (*env)->NewGlobalRef(env, mediaLoadedListener);
   log_debug("jahspotify","registerNativeMediaLoadedListener","Registered media loaded listener: 0x%x\n", (int)g_mediaLoadedListener);
}


JNIEXPORT jboolean JNICALL Java_jahspotify_impl_JahSpotifyImpl_registerNativeSearchCompleteListener (JNIEnv *env, jobject obj, jobject searchCompleteListener)
{
   g_searchCompleteListener =  (*env)->NewGlobalRef(env, searchCompleteListener);
   log_debug("jahspotify","registerNativeSearchCompleteListener","Registered search complete listener: 0x%x\n", (int)g_searchCompleteListener);
}

JNIEXPORT jboolean JNICALL Java_jahspotify_impl_JahSpotifyImpl_registerNativePlaybackListener (JNIEnv *env, jobject obj, jobject playbackListener)
{
    g_playbackListener = (*env)->NewGlobalRef(env, playbackListener);
    log_debug("jahspotify","registerNativePlaybackListener","Registered playback listener: 0x%x\n", (int)g_playbackListener);
}

JNIEXPORT jboolean JNICALL Java_jahspotify_impl_JahSpotifyImpl_registerNativeLibraryListener (JNIEnv *env, jobject obj, jobject libraryListener)
{
    g_libraryListener = (*env)->NewGlobalRef(env, libraryListener);
    log_debug("jahspotify","registerNativeLibraryListener","Registered playlist listener: 0x%x\n", (int)g_libraryListener);
}

JNIEXPORT jboolean JNICALL Java_jahspotify_impl_JahSpotifyImpl_registerNativeConnectionListener (JNIEnv *env, jobject obj, jobject connectionListener)
{
    g_connectionListener = (*env)->NewGlobalRef(env, connectionListener);
    log_debug("jahspotify","registerNativeConnectionListener","Registered connection listener: 0x%x\n", (int)g_connectionListener);

	// TODO: Done here because otherwise it will be done too late.
	pthread_mutex_init ( &g_notify_mutex, NULL );
    pthread_cond_init ( &g_notify_cond, NULL );
}

JNIEXPORT jboolean JNICALL Java_jahspotify_impl_JahSpotifyImpl_unregisterListeners (JNIEnv *env, jobject obj)
{
    if (g_libraryListener)
    {
        (*env)->DeleteGlobalRef(env, g_libraryListener);
        g_libraryListener = NULL;
    }

    if (g_mediaLoadedListener)
    {
        (*env)->DeleteGlobalRef(env, g_mediaLoadedListener);
        g_mediaLoadedListener = NULL;
    }

    if (g_searchCompleteListener)
    {
        (*env)->DeleteGlobalRef(env, g_searchCompleteListener);
        g_searchCompleteListener = NULL;
    }

    if (g_connectionListener)
    {
        (*env)->DeleteGlobalRef(env, g_connectionListener);
        g_connectionListener = NULL;
    }
}

JNIEXPORT jobject JNICALL Java_jahspotify_impl_JahSpotifyImpl_retrieveUser (JNIEnv *env, jobject obj)
{
    sp_user *user = sp_session_user(g_sess);
    const char *value = NULL;
    int country = 0;

    log_error("jahspotify","Java_jahspotify_impl_JahSpotifyImpl_retrieveUser","Retrieving user" );
    
    int count = 0;
    while (!sp_user_is_loaded(user) && count < 10)
    {
        log_error("jahspotify","Java_jahspotify_impl_JahSpotifyImpl_retrieveUser","Waiting for user to load\n" );
        usleep(250);
        count ++;
    }
    
    if (count == 10)
    {
        log_warn("jahspotify","Java_jahspotify_impl_JahSpotifyImpl_retrieveUser","Timeout while waiting for user to load" );
        return NULL;
    }

    jobject userInstance = createInstance(env,"jahspotify/media/User");
    if (!userInstance)
    {
        log_error("jahspotify","Java_jahspotify_impl_JahSpotifyImpl_retrieveUser","Could not create instance of jahspotify.media.User");
        return NULL;
    }

    if (sp_user_is_loaded(user))
    {
        log_error("jahspotify","Java_jahspotify_impl_JahSpotifyImpl_retrieveUser","User is loaded" );
        value = sp_user_display_name(user);
        if (value)
        {
            setObjectStringField(env,userInstance,"fullName",value);
        }
        value = sp_user_canonical_name(user);
        if (value)
        {
            setObjectStringField(env,userInstance,"userName",value);
        }
        value = sp_user_display_name(user);
        if (value)
        {
            setObjectStringField(env,userInstance,"displayName",value);
        }

        // Country encoded in an integer 'SE' = 'S' << 8 | 'E'
        country = sp_session_user_country(g_sess);
        char countryStr[] = "  ";
        countryStr[0] = (byte)(country >> 8);
        countryStr[1] = (byte)country;
        setObjectStringField(env,userInstance,"country",countryStr);

        return userInstance;
    }

    return NULL;

}

JNIEXPORT jobjectArray JNICALL Java_jahspotify_impl_JahSpotifyImpl_getTracksForPlaylist ( JNIEnv *env, jobject obj, jstring uri)
{
    jstring str = NULL;
    int i;
    jclass strCls = (*env)->FindClass(env,"Ljava/lang/String;");
    jobjectArray strarray = (*env)->NewObjectArray(env,6,strCls,NULL);
    for (i=0;i<6;i++)
    {
        str = (*env)->NewStringUTF(env,"VIPIN");
        (*env)->SetObjectArrayElement(env,strarray,i,str);
        (*env)->DeleteLocalRef(env,str);
    }
    return strarray;

}

char* createLinkStr(sp_link *link)
{
    char *linkStr = calloc ( 1 , sizeof ( char ) * ( 100 ) );
    int len = sp_link_as_string(link,linkStr,100);
    return linkStr;
}


jobject createJLinkInstance(JNIEnv *env, sp_link *link)
{
    jobject linkInstance = NULL;
    jmethodID jMethod = NULL;

    char *linkStr = malloc ( sizeof ( char ) * ( 100 ) );

    int len = sp_link_as_string(link,linkStr,100);
    
    jstring jString = (*env)->NewStringUTF(env, linkStr);

    jMethod = (*env)->GetStaticMethodID(env, g_linkClass, "create", "(Ljava/lang/String;)Ljahspotify/media/Link;");

    linkInstance = (*env)->CallStaticObjectMethod(env,g_linkClass,jMethod,jString);

    if (!linkInstance)
    {
        log_error("jahspotify","createJLinkInstance","Could not create instance of jahspotify.media.Link");
        goto exit;
    }

exit:
    if (linkStr)
    {
        free(linkStr);
    }
    return linkInstance;

}


jobject createJTrackInstance(JNIEnv *env, sp_track *track)
{
    jclass jClass;
    jobject trackInstance;

    jClass = (*env)->FindClass(env, "jahspotify/media/Track");
    if (jClass == NULL)
    {
        log_error("jahspotify","createJTrackInstance","Could not load jahnotify.media.Track");
        return NULL;
    }
    
    trackInstance = createInstanceFromJClass(env,jClass);
    if (!trackInstance)
    {
        log_error("jahspotify","createJTrackInstance","Could not create instance of jahspotify.media.Track");
        return NULL;
    }

    sp_link *trackLink = sp_link_create_from_track(track,0);

    if (trackLink)
    {
        sp_link_add_ref(trackLink);
        jobject trackJLink = createJLinkInstance(env,trackLink);
        setObjectObjectField(env,trackInstance,"id","Ljahspotify/media/Link;",trackJLink);

        setObjectStringField(env,trackInstance,"title",sp_track_name(track));
        setObjectIntField(env,trackInstance,"length",sp_track_duration(track));
        // setObjectIntField(env,trackInstance,"popularity",sp_track_popularity(track));
        setObjectIntField(env,trackInstance,"trackNumber",sp_track_index(track));

        sp_album *album = sp_track_album(track);
        if (album)
        {
            sp_album_add_ref(album);
            sp_link *albumLink = sp_link_create_from_album(album);
            if (albumLink)
            {
                sp_link_add_ref(albumLink);

                jobject albumJLink = createJLinkInstance(env,albumLink);

                jmethodID jMethod = (*env)->GetMethodID(env,jClass,"setAlbum","(Ljahspotify/media/Link;)V");

                if (jMethod == NULL)
                {
                    log_error("jahspotify","createJTrackInstance","Could not load method setAlbum(link) on class Track");
                    return NULL;
                }

                // set it on the track
                (*env)->CallVoidMethod(env,trackInstance,jMethod,albumJLink);

                sp_link_release(albumLink);

            }
            sp_album_release(album);
        }

        int numArtists = sp_track_num_artists(track);
        if (numArtists > 0)
        {
            jmethodID jMethod = (*env)->GetMethodID(env,jClass,"addArtist","(Ljahspotify/media/Link;)V");

            if (jMethod == NULL)
            {
                log_error("jahspotify","createJTrackInstance","Could not load method addArtist(link) on class Track");
                return NULL;
            }

            int i = 0;
            for (i = 0; i < numArtists; i++)
            {
                sp_artist *artist = sp_track_artist(track,i);
                if (artist)
                {
                    sp_artist_add_ref(artist);

                    sp_link *artistLink = sp_link_create_from_artist(artist);
                    if (artistLink)
                    {
                        sp_link_add_ref(artistLink);

                        jobject artistJLink = createJLinkInstance(env,artistLink);

                        // set it on the track
                        (*env)->CallVoidMethod(env,trackInstance,jMethod,artistJLink);

                        sp_link_release(artistLink);

                    }
                    sp_artist_release(artist);
                }
            }
        }

        sp_link_release(trackLink);
    }
    return trackInstance;
}

char* toHexString(byte* bytes)
{
    char   ls_hex[3] = "";
    int    i = 0;
    int    j = 0;
    char hash[40];
    byte *theBytes = bytes;

    memset(ls_hex, '\0', 3);

    j = 0;
    for (i=0; i < 20; i++)
    {
        sprintf(ls_hex, "%.2X", *theBytes);
        theBytes++;
        hash[j++] = ls_hex[0];
        hash[j++] = ls_hex[1];
    }

    char *finalHash = calloc(1, sizeof(char)*40);
    strcpy(finalHash,hash);

    return finalHash;
}

void SP_CALLCONV artistBrowseCompleteCallback(sp_artistbrowse *result, void *userdata)
{
  int32_t *token = (int32_t*)userdata;
  if (token == NULL)
  {
    signalArtistBrowseLoaded(result, 0);
  }
  else
  {
    signalArtistBrowseLoaded(result, *token);
  }
}

void SP_CALLCONV imageLoadedCallback(sp_image *image, void *userdata)
{
  int32_t *token = (int32_t*)userdata;
  if (token == NULL)
  {
    signalImageLoaded(image, 0);
  }
  else
  {
    signalImageLoaded(image, *token);
  }
  
}
/*
void trackLoadedCallback(sp_track *track, void *userdata)
{
  int32_t *token = (int32_t*)userdata;
  signalTrackLoaded(track, *token);
}*/

void SP_CALLCONV albumBrowseCompleteCallback(sp_albumbrowse *result, void *userdata)
{
  int32_t *token = (int32_t*)userdata;
  if (token == NULL)
  {
    signalAlbumBrowseLoaded(result, 0);
  }
  else
  {
    signalAlbumBrowseLoaded(result, *token);
  }
}

void populateJAlbumInstanceFromAlbumBrowse(JNIEnv *env, sp_album *album, sp_albumbrowse *albumBrowse, jobject albumInstance)
{
  jclass albumJClass;
  
  sp_album_add_ref(album);
  sp_albumbrowse_add_ref(albumBrowse);
  
  albumJClass = (*env)->FindClass(env, "jahspotify/media/Album");
  if (albumJClass == NULL)
  {
    log_error("jahspotify","populateJAlbumInstanceFromAlbumBrowse","Could not load jahnotify.media.Album");
    return;
  }
  
  
  // By now it looks like the album will also be loaded 
  sp_link *albumLink = sp_link_create_from_album(album);
  
  if (albumLink)
  {
    sp_link_add_ref(albumLink);
    
    jobject albumJLink = createJLinkInstance(env, albumLink);
    setObjectObjectField(env,albumInstance,"id","Ljahspotify/media/Link;",albumJLink);
    
    setObjectStringField(env,albumInstance,"name",sp_album_name(album));
    setObjectIntField(env,albumInstance,"year",sp_album_year(album));
    
    sp_albumtype albumType = sp_album_type(album);
    
    jclass albumTypeJClass = (*env)->FindClass(env, "jahspotify/media/AlbumType");
    jmethodID jMethod = (*env)->GetStaticMethodID(env,albumTypeJClass,"fromOrdinal","(I)Ljahspotify/media/AlbumType;");
    jobject albumTypeEnum = (jobjectArray)(*env)->CallStaticObjectMethod(env, albumTypeJClass, jMethod,(int)albumType);
    setObjectObjectField(env,albumInstance,"type","Ljahspotify/media/AlbumType;",albumTypeEnum);
    
    sp_link *albumCoverLink = sp_link_create_from_album_cover(album, SP_IMAGE_SIZE_SMALL);
    if (albumCoverLink)
    {
      sp_link_add_ref(albumCoverLink);
      
      jobject albumCoverJLink = createJLinkInstance(env, albumCoverLink);
      setObjectObjectField(env,albumInstance,"cover","Ljahspotify/media/Link;",albumCoverJLink);
      
      sp_image *albumCoverImage = sp_image_create_from_link(g_sess,albumCoverLink);
      if (albumCoverImage)
      {
        sp_image_add_ref(albumCoverImage);
        sp_image_add_load_callback(albumCoverImage,imageLoadedCallback,NULL);
      }
      
      sp_link_release(albumCoverLink);
      
    }
    
    sp_artist *artist = sp_album_artist(album);
    if (artist)
    {
      sp_artist_add_ref(artist);
      
      sp_link *artistLink = sp_link_create_from_artist(artist);
      
      if (artistLink)
      {
        sp_link_add_ref(artistLink);
        
        jobject artistJLink = createJLinkInstance(env,artistLink);
        
        setObjectObjectField(env,albumInstance,"artist","Ljahspotify/media/Link;",artistJLink);
        
        sp_link_release(artistLink);
      }
      
      sp_artist_release(artist);
    }
    
    sp_link_release(albumLink);
  }
  
  int numTracks = sp_albumbrowse_num_tracks(albumBrowse);
  if (numTracks > 0)
  {
    // Add each track to the album - also pass in the disk as need be
    jmethodID addTrackJMethodID = (*env)->GetMethodID(env,albumJClass,"addTrack","(ILjahspotify/media/Link;)V");
    int i = 0;
    for (i = 0; i < numTracks; i++)
    {
      sp_track *track = sp_albumbrowse_track(albumBrowse,i);
      
      if (track)
      {
        sp_track_add_ref(track);
        
        sp_link *trackLink = sp_link_create_from_track(track,0);
        if (trackLink)
        {
          sp_link_add_ref(trackLink);
          jobject trackJLink = createJLinkInstance(env,trackLink);
          (*env)->CallVoidMethod(env, albumInstance, addTrackJMethodID,sp_track_disc(track),trackJLink);
          sp_link_release(trackLink);
        }
      }
    }
  }
  
  int numCopyrights = sp_albumbrowse_num_copyrights(albumBrowse);
  if (numCopyrights > 0)
  {
    // Add copyrights to album
    jmethodID addCopyrightMethodID = (*env)->GetMethodID(env,albumJClass,"addCopyright","(Ljava/lang/String;)V");
    int i = 0;
    for (i = 0; i < numCopyrights; i++)
    {
      const char *copyright = sp_albumbrowse_copyright(albumBrowse,i);
      if (copyright)
      {
        jstring str = (*env)->NewStringUTF(env, copyright);
        (*env)->CallVoidMethod(env, albumInstance, addCopyrightMethodID,str);
      }
    }
  }
  
  const char *review = sp_albumbrowse_review(albumBrowse);
  if (review)
  {
    setObjectStringField(env,albumInstance,"review",review);
  }
  
  sp_album_release(album);
  sp_albumbrowse_release(albumBrowse);
  
}

jobject createJAlbumInstance(JNIEnv *env, sp_album *album)
{
    jobject albumInstance;
    jclass albumJClass;

    sp_album_add_ref(album);
    
    albumJClass = (*env)->FindClass(env, "jahspotify/media/Album");
    if (albumJClass == NULL)
    {
        log_error("jahspotify","createJAlbumInstance","Could not load jahnotify.media.Album");
        sp_album_release(album);
        return NULL;
    }
    
    albumInstance = createInstanceFromJClass(env,albumJClass);
    if (!albumInstance)
    {
        log_error("jahspotify","createJAlbumInstance","Could not create instance of jahspotify.media.Album");
        sp_album_release(album);
        return NULL;
    }

    sp_albumbrowse *albumBrowse = sp_albumbrowse_create(g_sess,album,albumBrowseCompleteCallback,NULL);

    if (albumBrowse)
    {
      sp_albumbrowse_add_ref(albumBrowse);
      
      int count = 0;
      
      if (!sp_albumbrowse_is_loaded(albumBrowse))
      {
          log_trace("jahspotify","createJAlbumInstance","Album not loaded, will have to wait for callback");
          sp_albumbrowse_release(albumBrowse);
          sp_album_release(album);
          return NULL;
      }

      populateJAlbumInstanceFromAlbumBrowse(env, album,albumBrowse, albumInstance);
      
      sp_albumbrowse_release(albumBrowse);
      
    }
    
    sp_album_release(album);
    
    return albumInstance;

}

void populateJArtistInstanceFromArtistBrowse(JNIEnv *env, sp_artistbrowse *artistBrowse, jobject artistInstance)
{
  log_debug("jahspotify","populateJArtistInstanceFromArtistBrowse","Populating artist browse instance");
  
  sp_artistbrowse_add_ref(artistBrowse);
  
  int numSimilarArtists = sp_artistbrowse_num_similar_artists(artistBrowse);
  
  jclass jClass = (*env)->FindClass(env, "jahspotify/media/Artist");
  
  if (numSimilarArtists > 0)
  {
    jmethodID jMethod = (*env)->GetMethodID(env,jClass,"addSimilarArtist","(Ljahspotify/media/Link;)V");
    
    if (jMethod == NULL)
    {
      log_error("jahspotify","populateJArtistInstanceFromArtistBrowse","Could not load method addSimilarArtist(link) on class Artist");
      sp_artistbrowse_release(artistBrowse);
      return;
    }
    
    // Load the artist links
    int count = 0;
    for (count = 0; count < numSimilarArtists; count++)
    {
      sp_artist *similarArtist = sp_artistbrowse_similar_artist(artistBrowse,0);
      if (similarArtist)
      {
        sp_artist_add_ref(similarArtist);
        
        sp_link *similarArtistLink = sp_link_create_from_artist(similarArtist);
        
        if (similarArtistLink)
        {
          sp_link_add_ref(similarArtistLink);
          
          jobject similarArtistJLink = createJLinkInstance(env,similarArtistLink);
          
          // set it on the track
          (*env)->CallVoidMethod(env,artistInstance,jMethod,similarArtistJLink);
          
          sp_link_release(similarArtistLink);
        }
        
        sp_artist_release(similarArtist);
      }
    }
  }
  
  int numPortraits = sp_artistbrowse_num_portraits(artistBrowse);
  
  if (numPortraits > 0)
  {
    jmethodID jMethod = (*env)->GetMethodID(env,jClass,"addPortrait","(Ljahspotify/media/Link;)V");
    
    if (jMethod == NULL)
    {
      log_error("jahspotify","populateJArtistInstanceFromArtistBrowse","Could not load method addAlbum(link) on class Artist");
      sp_artistbrowse_release(artistBrowse);
      return;
    }
    
    int count = 0;
    
    for (count = 0; count < numPortraits; count++)
    {
      // Load portrait url
      const byte *portraitURI = sp_artistbrowse_portrait(artistBrowse,count);
      
      if (portraitURI)
      {
        char *portraitURIStr = toHexString((byte*)portraitURI);
        const char spotifyURI[] = "spotify:image:";
        int len = strlen(spotifyURI) + strlen(portraitURIStr);
        char *dest = calloc(1, len+1);
        dest[0] = 0;
        strcat(dest,spotifyURI);
        strcat(dest,portraitURIStr);
        
        sp_link *portraitLink = sp_link_create_from_string(dest);
        if (portraitLink)
        {
          // sp_image *portrait = sp_image_create_from_link(g_sess,portraitLink);
          // if (portrait)
          // {
            // sp_image_add_ref(portrait);
            // sp_image_add_load_callback(portrait,imageLoadedCallback,NULL);
            //}
            
            sp_link_add_ref(portraitLink);
            
            jobject portraitJLlink = createJLinkInstance(env,portraitLink);
            
            // setObjectObjectField(env,artistInstance,"portrait","Ljahspotify/media/Link;",portraitJLlink);
            (*env)->CallVoidMethod(env,artistInstance,jMethod,portraitJLlink);
            
            sp_link_release(portraitLink);
        }
        
        
        free(dest);
        free(portraitURIStr);
        
      }
    }
  }
  
  int numAlbums = sp_artistbrowse_num_albums(artistBrowse);
  if (numAlbums > 0)
  {
    jmethodID jMethod = (*env)->GetMethodID(env,jClass,"addAlbum","(Ljahspotify/media/Link;)V");
    
    if (jMethod == NULL)
    {
      log_error("jahspotify","populateJArtistInstanceFromArtistBrowse","Could not load method addAlbum(link) on class Artist");
      sp_artistbrowse_release(artistBrowse);
      return;
    }
    
    int count = 0;
    for (count = 0; count < numAlbums; count++)
    {
      sp_album *album = sp_artistbrowse_album(artistBrowse,count);
      if (album)
      {
        sp_album_add_ref(album);
        sp_link *albumLink = sp_link_create_from_album(album);
        if (albumLink)
        {
          sp_link_add_ref(albumLink);
          jobject albumJLink = createJLinkInstance(env,albumLink);
          // set it on the track
          (*env)->CallVoidMethod(env,artistInstance,jMethod,albumJLink);
          sp_link_release(albumLink);
        }
        sp_album_release(album);
      }
    }
  }
  
  const char *bios = sp_artistbrowse_biography(artistBrowse);
  
  if (bios)
  {
    setObjectStringField(env,artistInstance,"bios",bios);
  }
  
  sp_artistbrowse_release(artistBrowse);
  
}

jobject createJArtistInstance(JNIEnv *env, sp_artist *artist)
{
    jobject artistInstance = NULL;
    sp_link *artistLink = NULL;
    
    sp_artist_add_ref(artist);
    
    jclass jClass = (*env)->FindClass(env, "jahspotify/media/Artist");
    
    if (jClass == NULL)
    {
      log_error("jahspotify","createJArtistInstance","Could not load jahnotify.media.Artist");
      sp_artist_release(artist);
      return NULL;
    }
    
    artistInstance = createInstanceFromJClass(env, jClass);
    
    artistLink = sp_link_create_from_artist(artist);

    if (artistLink)
    {
        sp_link_add_ref(artistLink);

        jobject artistJLink = createJLinkInstance(env, artistLink);

        setObjectObjectField(env,artistInstance,"id","Ljahspotify/media/Link;",artistJLink);

        sp_link_release(artistLink);

        setObjectStringField(env,artistInstance,"name",sp_artist_name(artist));
  
        
        sp_artistbrowse *artistBrowse = sp_artistbrowse_create(g_sess,artist,SP_ARTISTBROWSE_NO_TRACKS,artistBrowseCompleteCallback,NULL);
        
        if (artistBrowse)
        {
            sp_artistbrowse_add_ref(artistBrowse);

            int count = 0;
            if (!sp_artistbrowse_is_loaded(artistBrowse))
            {
              log_trace("jahspotify","createJArtistInstance","Artist not loaded, will have to wait for callback");
              sp_artistbrowse_release(artistBrowse);
              sp_artist_release(artist);
              return NULL;
            }
            
            log_debug("jahspotify","createJArtistInstance","Artist browse loaded, proceeding to create from the artist");
            
            populateJArtistInstanceFromArtistBrowse(env,artistBrowse,artistInstance);

            sp_artistbrowse_release(artistBrowse);
        }
    }
    
    sp_artist_release(artist);
    
    return artistInstance;

}


jobject createJPlaylist(JNIEnv *env, sp_playlist *playlist)
{
    jobject playlistInstance;
    jmethodID jMethod;
    jclass jClass;

    jClass = (*env)->FindClass(env, "jahspotify/media/Playlist");
    if (jClass == NULL)
    {
        log_error("jahspotify","createJPlaylist","Could not load jahnotify.media.Playlist");
        return NULL;
    }

    playlistInstance = createInstanceFromJClass(env, jClass);
    if (!playlistInstance)
    {
        log_error("jahspotify","createJPlaylist","Could not create instance of jahspotify.media.Playlist");
        return NULL;
    }

    sp_link *playlistLink = sp_link_create_from_playlist(playlist);
    if (playlistLink)
    {
        jobject playlistJLink = createJLinkInstance(env,playlistLink);
        setObjectObjectField(env,playlistInstance,"id","Ljahspotify/media/Link;",playlistJLink);
        sp_link_release(playlistLink);
    }

    setObjectStringField(env,playlistInstance,"name",sp_playlist_name(playlist));

    sp_user *owner = sp_playlist_owner(playlist);
    if (owner)
    {
        setObjectStringField(env,playlistInstance,"author",sp_user_display_name(owner));
        sp_user_release(owner);
    }

    // Lookup the method now - saves us looking it up for each iteration of the loop
    jMethod = (*env)->GetMethodID(env,jClass,"addTrack","(Ljahspotify/media/Link;)V");

    if (jMethod == NULL)
    {
        log_error("jahspotify","createJPlaylist","Could not load method addTrack(track) on class Playlist");
        return NULL;
    }

    int numTracks = sp_playlist_num_tracks(playlist);
    setObjectIntField(env, playlistInstance, "numTracks", numTracks);
    
    int trackCounter = 0;
    for (trackCounter = 0 ; trackCounter < numTracks; trackCounter++)
    {
        sp_track *track = sp_playlist_track(playlist,trackCounter);
        if (track)
        {
            sp_track_add_ref(track);
            sp_link *trackLink = sp_link_create_from_track(track, 0);
            if (trackLink)
            {
                sp_link_add_ref(trackLink);
                jobject trackJLink = createJLinkInstance(env,trackLink);
                // Add it to the playlist
                (*env)->CallVoidMethod(env,playlistInstance,jMethod,trackJLink);
                sp_link_release(trackLink);
            }
            sp_track_release(track);

        }
    }

    return playlistInstance;

}

JNIEXPORT jobject JNICALL Java_jahspotify_impl_JahSpotifyImpl_retrieveArtist ( JNIEnv *env, jobject obj, jstring uri)
{
    jobject artistInstance;
    uint8_t *nativeUri = NULL;

    nativeUri = ( uint8_t * ) ( *env )->GetStringUTFChars ( env, uri, NULL );

    sp_link *link = sp_link_create_from_string(nativeUri);
    if (link)
    {
      sp_artist *artist= sp_link_as_artist(link);

      if (artist)
      {
  sp_artist_add_ref(artist);
  artistInstance = createJArtistInstance(env, artist);
  sp_artist_release(artist);
      }
      sp_link_release(link);
    }

    if (nativeUri)
      (*env)->ReleaseStringUTFChars(env, uri,nativeUri);

    return artistInstance;
}


JNIEXPORT jobject JNICALL Java_jahspotify_impl_JahSpotifyImpl_retrieveAlbum ( JNIEnv *env, jobject obj, jstring uri)
{
    jobject albumInstance;
    uint8_t *nativeUri = NULL;

    nativeUri = ( uint8_t * ) ( *env )->GetStringUTFChars ( env, uri, NULL );

    sp_link *link = sp_link_create_from_string(nativeUri);
    if (link)
    {
        sp_album *album= sp_link_as_album(link);

        if (album)
        {
            sp_album_add_ref(album);
            
            albumInstance = createJAlbumInstance(env, album);
      
            sp_album_release(album);
        }
        sp_link_release(link);
    }

    if (nativeUri)
        (*env)->ReleaseStringUTFChars(env, uri,nativeUri);

    return albumInstance;
}

JNIEXPORT jboolean JNICALL Java_jahspotify_impl_JahSpotifyImpl_nativeShutdown ( JNIEnv *env, jobject obj)
{
  sp_session_logout(g_sess);
}


JNIEXPORT jobject JNICALL Java_jahspotify_impl_JahSpotifyImpl_retrieveTrack ( JNIEnv *env, jobject obj, jstring uri)
{
    jobject trackInstance;
    uint8_t *nativeUri = NULL;

    nativeUri = ( uint8_t * ) ( *env )->GetStringUTFChars ( env, uri, NULL );

    sp_link *link = sp_link_create_from_string(nativeUri);
    if (!link)
    {
        // hmm
        log_error("jahspotify","Java_jahspotify_impl_JahSpotifyImpl_retrieveTrack","Could not create link!" );
        return JNI_FALSE;
    }

    sp_track *track = sp_link_as_track(link);
        
    int count = 0;
    while (!sp_track_is_loaded(track) && count < 10)
    {
        log_error("jahspotify","Java_jahspotify_impl_JahSpotifyImpl_retrieveTrack","Waiting for track to be loaded ..." );
        usleep(250);
        count++;
    }
    
    if (count == 10)
    {
      return NULL;
    }

    trackInstance = createJTrackInstance(env, track);

    if (track)
        sp_track_release(track);
    if (link)
        sp_link_release(link);
    if (nativeUri)
        free(nativeUri);

    return trackInstance;
}

JNIEXPORT jobject JNICALL Java_jahspotify_impl_JahSpotifyImpl_retrievePlaylist ( JNIEnv *env, jobject obj, jstring uri)
{
    jobject playlistInstance;
    uint8_t *nativeUri = NULL;

    nativeUri = ( uint8_t * ) ( *env )->GetStringUTFChars ( env, uri, NULL );

    log_debug("jahspotify","retrievePlaylist","Retrieving playlist: %s",nativeUri );
    
    sp_link *link = sp_link_create_from_string(nativeUri);
    if (!link)
    {
        // hmm
        log_error("jahspotify","retrievePlaylist","Could not create link!" );
        return JNI_FALSE;
    }

    sp_playlist *playlist = sp_playlist_create(g_sess,link);

    int count = 0;
    while (!sp_playlist_is_loaded(playlist) && count < 10)
    {
        log_debug("jahspotify","retrievePlaylist","Waiting for playlist to be loaded ..." );
        usleep(250);
    }
    if (count == 10)
    {
      log_warn("jahspotify","retrievePlaylist","Timeout while waiting for playlist to load ..." );
      
      return NULL;
    }

    playlistInstance = createJPlaylist(env, playlist);

    if (playlist)
        sp_playlist_release(playlist);
    if (link)
        sp_link_release(link);
    if (nativeUri)
        free(nativeUri);

    return playlistInstance;

}

JNIEXPORT jobjectArray JNICALL Java_jahspotify_impl_JahSpotifyImpl_nativeReadTracks (JNIEnv *env, jobject obj, jobjectArray uris)
{
    // For each track, read out the info and populate all of the info in the Track instance
}

JNIEXPORT jint JNICALL Java_jahspotify_impl_JahSpotifyImpl_nativePause (JNIEnv *env, jobject obj)
{
    log_debug("jahspotify","nativeResume","Pausing playback");
    if (g_currenttrack)
    {
        sp_session_player_play(g_sess,0);
    }
}

JNIEXPORT jint JNICALL Java_jahspotify_impl_JahSpotifyImpl_nativeResume (JNIEnv *env, jobject obj)
{
    log_debug("jahspotify","nativeResume","Resuming playback");
    if (g_currenttrack)
    {
        sp_session_player_play(g_sess,1);
    }
}

JNIEXPORT jint JNICALL Java_jahspotify_impl_JahSpotifyImpl_readImage (JNIEnv *env, jobject obj, jstring uri, jobject outputStream)
{
    uint8_t *nativeURI = ( uint8_t * ) ( *env )->GetStringUTFChars ( env, uri, NULL );
    sp_link *imageLink = sp_link_create_from_string(nativeURI);
    size_t size;
    jclass jClass;
    int numBytesWritten = 0;
    
    log_debug("jahspotify","readImage","Loading image: %s", nativeURI);
    
    if (imageLink)
    {
        sp_link_add_ref(imageLink);
        sp_image *image = sp_image_create_from_link(g_sess,imageLink);
        if (image)
        {
            // Reference is released by the image loaded callback
            sp_image_add_ref(image);
            sp_image_add_load_callback(image, imageLoadedCallback, NULL);
      
            if (sp_image_is_loaded(image))
            {
                byte *data = (byte*)sp_image_data(image,&size);

                log_debug("jahspotify","readImage","Have image data: len: %d", size);

                jClass = (*env)->FindClass(env,"Ljava/io/OutputStream;");
                if (jClass == NULL)
                {
                    log_error("jahspotify","readImage","Could not load class java.io.OutputStream");
                    goto fail;
                }

                // Lookup the method now - saves us looking it up for each iteration of the loop
                jmethodID jMethod = (*env)->GetMethodID(env,jClass,"write","(I)V");

                if (jMethod == NULL)
                {
                    log_error("jahspotify","Java_jahspotify_impl_JahSpotifyImpl_readImage","Could not load method write(int) on class java.io.OutputStream");
                    goto fail;
                }

                int i = 0;
                for (i = 0; i < size; i++)
                {
                    (*env)->CallVoidMethod(env,outputStream,jMethod,*data);
                    data++;
                    numBytesWritten++;
                }
            }
        }
        sp_link_release(imageLink);
    }
    
    goto exit;

fail:
  numBytesWritten = -1;
  
exit: 
  
  return numBytesWritten;

}

JNIEXPORT void JNICALL Java_jahspotify_impl_JahSpotifyImpl_nativeTrackSeek(JNIEnv *env, jobject obj, jint offset)
{
    log_debug("jahspotify","nativeTrackSeek","Seeking in track offset: %d", offset);
    sp_session_player_seek(g_sess,offset);
}

JNIEXPORT void JNICALL Java_jahspotify_impl_JahSpotifyImpl_nativeStopTrack (JNIEnv *env, jobject obj)
{
    log_debug("jahspotify","nativeStopTrack","Stopping playback");
    sp_session_player_unload(g_sess);
}


JNIEXPORT jint JNICALL Java_jahspotify_impl_JahSpotifyImpl_nativePlayTrack (JNIEnv *env, jobject obj, jstring uri)
{
    uint8_t *nativeURI = NULL;

    nativeURI = ( uint8_t * ) ( *env )->GetStringUTFChars ( env, uri, NULL );

    log_debug("jahspotify","nativePlayTrack","Initiating play: %s", nativeURI);
    
    if (!g_audio_initialized)
    {
        audio_init(&g_audiofifo);
        g_audio_initialized = JNI_TRUE;
    }

    // For each track, read out the info and populate all of the info in the Track instance
    sp_link *link = sp_link_create_from_string(nativeURI);
    if (link)
    {
        sp_track *t = sp_link_as_track(link);

        if (!t)
        {
            log_error("jahspotify","nativePlayTrack","No track from link");
            return;
        }

        int count = 0;
        while (!sp_track_is_loaded(t) && count < 10)
        {
            log_debug("jahspotify","nativePlayTrack","Waiting for track ...");
            usleep(250);
            count ++;
        }

        if (count == 10)
        {
          // Hmm timeout loading the track ... what now?
        }
        
        if (sp_track_error(t) != SP_ERROR_OK)
        {
            log_debug("jahspotify","nativePlayTrack","Error with track: %s",sp_error_message(sp_track_error(t)));
            return;
        }

        log_debug("jahspotify","nativePlayTrack","track name: %s duration: %d",sp_track_name(t),sp_track_duration(t));

        if (g_currenttrack == t)
        {
            log_warn("jahspotify","nativePlayTrack","Same track, will not play");
            return;
        }

        // If there is one playing, unload that now
        if (g_currenttrack && t != g_currenttrack)
        {
            // Unload the current track now
            sp_session_player_unload(g_sess);

            sp_link *currentTrackLink = sp_link_create_from_track(g_currenttrack,0);
            char *currentTrackLinkStr = NULL;
            if (currentTrackLink)
            {
                currentTrackLinkStr = calloc ( 1, sizeof ( char ) * ( 100 ) );
                sp_link_as_string(currentTrackLink,currentTrackLinkStr,100);
                sp_link_release(currentTrackLink);
            }

            signalTrackEnded(currentTrackLinkStr, JNI_TRUE);

            if (currentTrackLinkStr)
            {
                free(currentTrackLinkStr);
            }

            sp_track_release(g_currenttrack);

            g_currenttrack = NULL;

        }

        sp_track_add_ref(t);

        sp_error result = sp_session_player_load(g_sess, t);

        if (sp_track_error(t) != SP_ERROR_OK)
        {
            log_error("jahspotify","nativePlayTrack","Issue loading track: %s", sp_error_message((sp_track_error(t))));
            goto fail;
        }

        log_debug("jahspotify","nativePlayTrack","Track loaded: %s", (result == SP_ERROR_OK ? "yes" : "no"));

        // Update the global reference
        g_currenttrack = t;

        // Start playing the next track
        sp_session_player_play(g_sess, 1);

        log_debug("jahspotify","nativePlayTrack","Playing track");

        sp_link_release(link);

        signalTrackStarted(nativeURI);

        goto exit;
    }
    else
    {
        log_error("jahspotify","nativePlayTrack","Unable to load link at this point");
        goto fail;
    }
    
    goto exit;

fail:
  log_error("jahspotify","nativePlayTrack","Error starting play");

exit:
  if (nativeURI) (*env)->ReleaseStringUTFChars(env, uri, (char *)nativeURI);

    
}

JNIEXPORT jfloat JNICALL Java_jahspotify_impl_JahSpotifyImpl_getAudioGain( JNIEnv *env, jobject obj )
{
    float gain = get_audio_gain();
    return gain;
}

JNIEXPORT void JNICALL Java_jahspotify_impl_JahSpotifyImpl_setAudioGain( JNIEnv *env, jobject obj, jfloat gain)
{
    set_audio_gain(gain);
}


/**
 * A track has ended. Remove it from the playlist.
 *
 * Called from the main loop when the music_delivery() callback has set g_playback_done.
 */
static void track_ended(void)
{
    log_debug("jahspotify","track_ended","Called");

    int tracks = 0;

    if (g_currenttrack)
    {
        sp_link *link = sp_link_create_from_track(g_currenttrack,0);
        char *trackLinkStr = NULL;
        if (link)
        {
            trackLinkStr = calloc ( 1, sizeof ( char ) * ( 100 ) );
            sp_link_as_string(link,trackLinkStr,100);
            sp_link_release(link);
        }

        sp_session_player_unload(g_sess);

        sp_track_release(g_currenttrack);

        g_currenttrack = NULL;

        signalTrackEnded(trackLinkStr,JNI_FALSE);

        if (trackLinkStr)
        {
            free(trackLinkStr);
        }
    }


}

JNIEXPORT jint JNICALL Java_jahspotify_impl_JahSpotifyImpl_initialize ( JNIEnv *env, jobject obj, jstring username, jstring password )
{
    sp_session *sp;
    sp_error err;
    uint8_t *nativePassword = NULL;
    uint8_t *nativeUsername = NULL;
    int next_timeout = 0;

    if ( !username || !password )
    {
        log_error("jahspotify","Java_jahspotify_impl_JahSpotifyImpl_initialize","Username or password not specified" );
        return 1;
    }

    /* Create session */
    spconfig.application_key_size = g_appkey_size;
    err = sp_session_create ( &spconfig, &sp );

    if ( SP_ERROR_OK != err )
    {
        log_error("jahspotify", "Java_jahspotify_impl_JahSpotifyImpl_initialize","Unable to create session: %s\n",sp_error_message ( err ) );
        return 1;
    }

    log_debug("jahspotify", "Java_jahspotify_impl_JahSpotifyImpl_initialize","Session created 0x%x", sp);

    nativeUsername = ( uint8_t * ) ( *env )->GetStringUTFChars ( env, username, NULL );
    nativePassword = ( uint8_t * ) ( *env )->GetStringUTFChars ( env, password, NULL );

    log_debug("jahspotify", "Java_jahspotify_impl_JahSpotifyImpl_initialize","Locking");
    
    g_sess = sp;

//    pthread_mutex_init ( &g_notify_mutex, NULL );
//    pthread_cond_init ( &g_notify_cond, NULL );

    log_debug("jahspotify","Java_jahspotify_impl_JahSpotifyImpl_initialize","Initiating login: %s", nativeUsername );

    sp_session_login ( sp, nativeUsername, nativePassword,0,NULL);

    pthread_mutex_lock ( &g_notify_mutex );
  
    for ( ;; )
    {
        if ( next_timeout == 0 )
        {
            while ( !g_notify_do && !g_playback_done )
                pthread_cond_wait ( &g_notify_cond, &g_notify_mutex );
        }
        else
        {
            struct timespec ts;

#if _POSIX_TIMERS > 0
            clock_gettime ( CLOCK_REALTIME, &ts );
#else
            struct timeval tv;
            gettimeofday ( &tv, NULL );
			//TIMEVAL_TO_TIMESPEC ( &tv, &ts );
			(&ts)->tv_sec = (&tv)->tv_sec;
			(&ts)->tv_nsec = (&tv)->tv_usec * 1000;
            ///TIMEVAL_TO_TIMESPEC ( &tv, &ts );
#endif
            ts.tv_sec += next_timeout / 1000;
            ts.tv_nsec += ( next_timeout % 1000 ) * 1000000;

            pthread_cond_timedwait ( &g_notify_cond, &g_notify_mutex, &ts );
        }

        g_notify_do = 0;
        pthread_mutex_unlock ( &g_notify_mutex );

        if ( g_playback_done )
        {
            track_ended();
            g_playback_done = 0;
        }

        sp_connectionstate conn_state = sp_session_connectionstate(sp);
        switch (conn_state)
        {
        case SP_CONNECTION_STATE_UNDEFINED:
        case SP_CONNECTION_STATE_LOGGED_OUT:
        case SP_CONNECTION_STATE_LOGGED_IN:
            break;
        case SP_CONNECTION_STATE_DISCONNECTED:
            log_warn ("jahspotify","Java_jahspotify_impl_JahSpotifyImpl_initialize", "Disconnected!");
      signalDisconnected();
            break;
        }


        do
        {
            sp_session_process_events ( sp, &next_timeout );
        }
        while ( next_timeout == 0 );

        pthread_mutex_lock ( &g_notify_mutex );
    }

    // FIXME: Release the username/password allocated?

    return 0;

}



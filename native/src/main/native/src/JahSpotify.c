#include <stdio.h>
#include <libspotify/api.h>
#include <sys/time.h>
#include <string.h>
#include <unistd.h>
#include <stdlib.h>


#include "JNIHelpers.h"
#include "JahSpotify.h"
#include "jahspotify_impl_JahSpotifyImpl.h"
#include "AppKey.h"
#include "audio.h"
#include "Callbacks.h"
#include "ThreadHelpers.h"

#define MAX_LENGTH_FOLDER_NAME 256

/// The global session handle
sp_session *g_sess;
/// Handle to the curren track
sp_track *g_currenttrack;

jobject g_playlistListener = NULL;
jobject g_connectionListener = NULL;
jobject g_playbackListener = NULL;


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

jobject createJArtistInstance(JNIEnv *env, sp_artist *artist);
jobject createJLinkInstance(JNIEnv *env, sp_link *link);
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
static void tracks_added ( sp_playlist *pl, sp_track * const *tracks,
                           int num_tracks, int position, void *userdata )
{
}

/**
 * Callback from libspotify, saying that a track has been removed from a playlist.
 *
 * @param  pl          The playlist handle
 * @param  tracks      An array of track indices
 * @param  num_tracks  The number of tracks in the \c tracks array
 * @param  userdata    The opaque pointer
 */
static void tracks_removed ( sp_playlist *pl, const int *tracks,
                             int num_tracks, void *userdata )
{
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
static void tracks_moved ( sp_playlist *pl, const int *tracks,
                           int num_tracks, int new_position, void *userdata )
{
}

/**
 * Callback from libspotify. Something renamed the playlist.
 *
 * @param  pl            The playlist handle
 * @param  userdata      The opaque pointer
 */
static void playlist_renamed ( sp_playlist *pl, void *userdata )
{
}

static void playlist_state_changed ( sp_playlist *pl, void *userdata )
{
    /*sp_link *link = sp_link_create_from_playlist(pl);
    char *linkName = malloc(sizeof(char)*100);

    if (link)
    {
      sp_link_as_string(link,linkName, 100);
      fprintf ( stderr,"jahspotify: playlist state changed: %s link: %s (loaded: %s)\n",sp_playlist_name ( pl ), linkName, (sp_playlist_is_loaded(pl) ? "yes" : "no"));
      sp_link_release(link);
    }
    if (linkName) free(linkName);*/
}

static void playlist_update_in_progress ( sp_playlist *pl, bool done, void *userdata )
{
    const char *name = sp_playlist_name ( pl );
    char *playListlinkStr;
    char *trackLinkStr;
    sp_link *link;
    int trackCounter;

    // fprintf ( stderr,"jahspotify: playlist update in progress: %s (done: %s)\n",name, (done ? "yes" : "no"));
    if (done)
    {
        link = sp_link_create_from_playlist(pl);
        if (link)
        {
            playListlinkStr =  malloc ( sizeof ( char ) * ( 100 ) );
            sp_link_as_string(link,playListlinkStr,100);
            sp_link_release(link);
            signalPlaylistSeen(name,playListlinkStr);
        }



        //sp_link_release(link);
        //  int numTracks = sp_playlist_num_tracks(pl);
        //  fprintf ( stderr,"jahspotify: playlist: %s num tracks: %d id: %s\n",name,numTracks,playListlinkStr);

        //  for (trackCounter = 0 ; trackCounter < numTracks; trackCounter++)
        //  {
        //      sp_track *track = sp_playlist_track(pl,trackCounter);
        //      if (sp_track_is_loaded(track))
        //      {
        //	  link = sp_link_create_from_track(track,0);
        //	  trackLinkStr = malloc ( sizeof ( char ) * ( 100 ) );
        //	  sp_link_as_string(link,trackLinkStr,100);
        //	  fprintf ( stderr,"jahspotify: track name: %s track id: %s\n",sp_track_name(track),trackLinkStr);
        //	  sp_link_release(link);
        //	  if (trackLinkStr) (trackLinkStr);
        //      }
        //      sp_track_release(track);
        //  }
        //
        //  if (playListlinkStr) free(playListlinkStr);
        // }
    }




}

static void playlist_metadata_updated ( sp_playlist *pl, void *userdata )
{
    signalMetadataUpdated();
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
static void playlist_added ( sp_playlistcontainer *pc, sp_playlist *pl,
                             int position, void *userdata )
{
    // fprintf(stderr,"jahspotify: playlist added: %s (loaded: %s)\n ",sp_playlist_name(pl),sp_playlist_is_loaded(pl) ? "Yes" : "No");
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
static void playlist_removed ( sp_playlistcontainer *pc, sp_playlist *pl,
                               int position, void *userdata )
{
    const char *name = sp_playlist_name(pl);
    // fprintf(stderr,"jahspotify: playlist removed: %s\n ",name);
    sp_playlist_remove_callbacks ( pl, &pl_callbacks, NULL );
}


/**
 * Callback from libspotify, telling us the rootlist is fully synchronized
 *
 * @param  pc            The playlist container handle
 * @param  userdata      The opaque pointer
 */
static void container_loaded ( sp_playlistcontainer *pc, void *userdata )
{
    char *folderName = malloc ( sizeof ( char ) * ( MAX_LENGTH_FOLDER_NAME ) );
    int i;


    if ( folderName == NULL )
    {
        fprintf ( stderr, "jahspotify: Could not allocate folder name variable)\n" );
        return;
    }

    // fprintf ( stderr, "jahspotify: Rootlist synchronized (%d playlists)\n",sp_playlistcontainer_num_playlists ( pc ) );
    signalSynchStarting(sp_playlistcontainer_num_playlists (pc));

    for ( i = 0; i < sp_playlistcontainer_num_playlists ( pc ); ++i )
    {
        sp_playlist *pl = sp_playlistcontainer_playlist ( pc, i );
        sp_playlist_add_callbacks ( pl, &pl_callbacks, NULL );

        sp_link *link = sp_link_create_from_playlist(pl);

        char *linkStr = malloc(sizeof(char) * 100);
        if (link)
        {
            sp_link_add_ref(link);
            sp_link_as_string(link,linkStr,100);
        }
        else
        {
            strcpy(linkStr,"N/A\0");
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
            fprintf ( stderr,"jahspotify: placeholder\n");
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
static void logged_in ( sp_session *sess, sp_error error )
{
    sp_playlistcontainer *pc = sp_session_playlistcontainer ( sess );
    int i;

    if ( SP_ERROR_OK != error )
    {
        fprintf ( stderr, "jahspotify: Login failed: %s\n", sp_error_message ( error ) );
        exit ( 2 );
    }

    fprintf ( stderr, "jahspotify: Login Success: %d\n", sp_playlistcontainer_num_playlists(pc));

    for (i = 0; i < sp_playlistcontainer_num_playlists(pc); ++i) {
        sp_playlist *pl = sp_playlistcontainer_playlist(pc, i);

        sp_playlist_add_callbacks(pl, &pl_callbacks, NULL);
    }

    signalLoggedIn();

}

static void logged_out ( sp_session *sess )
{
}


/**
 * This callback is called from an internal libspotify thread to ask us to
 * reiterate the main loop.
 *
 * We notify the main thread using a condition variable and a protected variable.
 *
 * @sa sp_session_callbacks#notify_main_thread
 */
static void notify_main_thread ( sp_session *sess )
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
static int music_delivery ( sp_session *sess, const sp_audioformat *format,
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

    afd = malloc(sizeof(audio_fifo_data_t) + s);
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
static void end_of_track ( sp_session *sess )
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
static void metadata_updated ( sp_session *sess )
{
    fprintf ( stderr, "jahspotify: metadata updated\n" );
}

/**
 * Notification that some other connection has started playing on this account.
 * Playback has been stopped.
 *
 * @sa sp_session_callbacks#play_token_lost
 */
static void play_token_lost ( sp_session *sess )
{
    fprintf ( stderr, "jahspotify: play token lost\n" );
    if ( g_currenttrack != NULL )
    {
        sp_session_player_unload ( g_sess );
        g_currenttrack = NULL;
    }
}

static void userinfo_updated (sp_session *sess)
{
    printf("jahspotify::userinfo_updated: user information updated\n");
}

static void log_message(sp_session *session, const char *data)
{
    fprintf(stderr,"jahspotify::log_message: %s\n",data);
}

static void connection_error(sp_session *session, sp_error error)
{
    fprintf(stderr,"jahspotify::connection_error: error=%s\n",sp_error_message(error));
    // FIXME: should propagate this to java land
}

static void streaming_error(sp_session *session, sp_error error)
{
    fprintf(stderr,"jahspotify::streaming_error: error=%s\n",sp_error_message(error));
    // FIXME: should propagate this to java land
}

static void start_playback(sp_session *session)
{
    fprintf(stderr,"jahspotify::start_playback: next playback about to start, initiating pre-load sequence\n");
    startPlaybackSignalled();
}

static void stop_playback(sp_session *session)
{
    fprintf(stderr,"jahspotify::stop_playback: playback should stop\n");
}


/**
 * The session callbacks
 */
static sp_session_callbacks session_callbacks =
{
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
    .start_playback = &start_playback,
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


void searchCompleteCallback(sp_search *result, void *userdata)
{
  if (sp_search_error(result) == SP_ERROR_OK)
  {
    fprintf(stderr,"jahspotify::searchCompleteCallback: Search complete: tracks: %d albums: %d artists:%d\n", sp_search_num_tracks(result), sp_search_num_albums(result), sp_search_num_artists(result));
  }
  else
  {
    fprintf(stderr,"jahspotify::searchCompleteCallback: Search completed with error: %s\n",sp_error_message(sp_search_error(result)));
  }
}

JNIEXPORT void JNICALL Java_jahspotify_impl_JahSpotifyImpl_initiateSearch(JNIEnv *env, jobject obj, jstring query)
{
    sp_search_create(g_sess,"tag:new",0,255,0,255,0,255,searchCompleteCallback,NULL);
}

JNIEXPORT jboolean JNICALL Java_jahspotify_impl_JahSpotifyImpl_registerPlaybackListener (JNIEnv *env, jobject obj, jobject playbackListener)
{
    g_playbackListener = (*env)->NewGlobalRef(env, playbackListener);
    fprintf ( stderr, "Registered playback listener: 0x%x\n", (int)g_playbackListener);
}

JNIEXPORT jboolean JNICALL Java_jahspotify_impl_JahSpotifyImpl_registerPlaylistListener (JNIEnv *env, jobject obj, jobject playlistListener)
{
    g_playlistListener = (*env)->NewGlobalRef(env, playlistListener);
    fprintf ( stderr, "Registered playlist listener: 0x%x\n", (int)g_playlistListener);
}

JNIEXPORT jboolean JNICALL Java_jahspotify_impl_JahSpotifyImpl_registerConnectionListener (JNIEnv *env, jobject obj, jobject connectionListener)
{
    g_connectionListener = (*env)->NewGlobalRef(env, connectionListener);
    fprintf ( stderr, "Registered connection listener: 0x%x\n", (int)g_connectionListener);
}

JNIEXPORT jboolean JNICALL Java_jahspotify_impl_JahSpotifyImpl_unregisterPlaylistListener (JNIEnv *env, jobject obj)
{
    if (g_playlistListener)
    {
        (*env)->DeleteGlobalRef(env, g_playlistListener);
        g_playlistListener = NULL;
    }
}


JNIEXPORT jboolean JNICALL Java_jahspotify_impl_JahSpotifyImpl_unregisterConnectionListener (JNIEnv *env, jobject obj)
{
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

    while (!sp_user_is_loaded(user))
    {
        fprintf ( stderr, "jahspotify::Java_jahspotify_impl_JahSpotifyImpl_retrieveUser: waiting for user to load\n" );
        sleep(1);
    }

    jclass jClass = (*env)->FindClass(env, "jahspotify/media/User");
    if (jClass == NULL)
    {
        fprintf(stderr,"jahspotify::Java_jahspotify_impl_JahSpotifyImpl_retrieveUser: could not load jahnotify.media.User\n");
        return NULL;
    }

    jobject userInstance = (*env)->AllocObject(env,jClass);
    if (!userInstance)
    {
        fprintf(stderr,"jahspotify::Java_jahspotify_impl_JahSpotifyImpl_retrieveUser: could not create instance of jahspotify.media.User\n");
        return NULL;
    }

    while (!sp_user_is_loaded(user))
    {
        sleep(1);
    }

    if (sp_user_is_loaded(user))
    {
        fprintf ( stderr, "jahspotify::Java_jahspotify_impl_JahSpotifyImpl_retrieveUser: user is loaded\n" );
        value = sp_user_full_name(user);
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
        value = sp_user_picture(user);
        if (value)
        {
            setObjectStringField(env,userInstance,"imageURL",value);
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

jobject createJLinkInstance(JNIEnv *env, sp_link *link)
{
    jobject linkInstance = NULL;
    jmethodID jMethod = NULL;

    jclass jClass = (*env)->FindClass(env, "jahspotify/media/Link");

    if (jClass == NULL)
    {
        fprintf(stderr,"jahspotify::createJLinkInstance: could not load jahspotify.media.Link\n");
        goto exit;
    }

    char *linkStr = malloc ( sizeof ( char ) * ( 100 ) );

    int len = sp_link_as_string(link,linkStr,100);

    jstring jString = (*env)->NewStringUTF(env, linkStr);

    jMethod = (*env)->GetStaticMethodID(env, jClass, "create", "(Ljava/lang/String;)Ljahspotify/media/Link;");

    linkInstance = (*env)->CallStaticObjectMethod(env,jClass,jMethod,jString);

    if (!linkInstance)
    {
        fprintf(stderr,"jahspotify::createJLinkInstance: could not create instance of jahspotify.media.Link\n");
        goto exit;
    }

exit:
    if (linkStr)
    {
        free(linkStr);
    }
    return linkInstance;

}


char* createLinkStr(sp_link *link)
{
    char *linkStr = malloc ( sizeof ( char ) * ( 100 ) );
    int len = sp_link_as_string(link,linkStr,100);
    return linkStr;
}


jobject createJTrackInstance(JNIEnv *env, sp_track *track)
{
    jclass jClass;
    jobject trackInstance;

    jClass = (*env)->FindClass(env, "jahspotify/media/Track");
    if (jClass == NULL)
    {
        fprintf(stderr,"jahspotify::createJTrackInstance: could not load jahnotify.media.Track\n");
        return NULL;
    }

    trackInstance = (*env)->AllocObject(env,jClass);
    if (!trackInstance)
    {
        fprintf(stderr,"jahspotify::createJTrackInstance: could not create instance of jahspotify.media.Track\n");
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
        setObjectIntField(env,trackInstance,"popularity",sp_track_popularity(track));
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
                    fprintf(stderr,"jahspotify::createJTrackInstance: could not load method setAlbum(link) on class Track\n");
                    return NULL;
                }

                // set it on the track
                (*env)->CallVoidMethod(env,trackInstance,jMethod,albumJLink);

                sp_link_release(albumLink);

            }

            int numArtists = sp_track_num_artists(track);
            if (numArtists > 0)
            {
                jmethodID jMethod = (*env)->GetMethodID(env,jClass,"addArtist","(Ljahspotify/media/Link;)V");

                if (jMethod == NULL)
                {
                    fprintf(stderr,"jahspotify::createJTrackInstance: could not load method addArtist(link) on class Track\n");
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

            sp_album_release(album);
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

    char *finalHash = malloc(sizeof(char)*40);
    strcpy(finalHash,hash);

    return finalHash;
}

void artistBrowseCompleteCallback(sp_artistbrowse *result, void *userdata)
{
  signalArtistBrowseLoaded(result);
}

void imageLoadedCallback(sp_image *image, void *userdata)
{
  signalImageLoaded(image);
}

void trackLoadedCallback(sp_track *track, void *userdata)
{
  signalTrackLoaded(track);
}

void albumBrowseCompleteCallback(sp_albumbrowse *result, void *userdata)
{
  signalAlbumBrowseLoaded(result);
}

jobject createJAlbumInstance(JNIEnv *env, sp_album *album)
{
    jclass albumJClass;
    jobject albumInstance;

    albumJClass = (*env)->FindClass(env, "jahspotify/media/Album");
    if (albumJClass == NULL)
    {
        fprintf(stderr,"jahspotify::createJAlbumInstance: could not load jahnotify.media.Album\n");
        return NULL;
    }

    albumInstance = (*env)->AllocObject(env,albumJClass);
    if (!albumInstance)
    {
        fprintf(stderr,"jahspotify::createJAlbumInstance: could not create instance of jahspotify.media.Album\n");
        return NULL;
    }

    sp_albumbrowse *albumBrowse = sp_albumbrowse_create(g_sess,album,albumBrowseCompleteCallback,NULL);

    if (albumBrowse)
    {
      sp_albumbrowse_add_ref(albumBrowse);
      
      int count = 0;
      
      while (!sp_albumbrowse_is_loaded(albumBrowse) && count < 5)
      {
	fprintf(stderr,"jahspotify::createJAlbumInstance: waiting for album browse load to complete\n");
	sleep(1);
	count++;
      }
      
      if (count == 5)
      {
	sp_albumbrowse_release(albumBrowse);
	return NULL;
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

	  sp_link *albumCoverLink = sp_link_create_from_album_cover(album);
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
      sp_albumbrowse_release(albumBrowse);
    }
    return albumInstance;

}

jobject createJArtistInstance(JNIEnv *env, sp_artist *artist)
{
    jclass jClass;
    jobject artistInstance = createInstance(env,"jahspotify/media/Artist");
    sp_link *artistLink = sp_link_create_from_artist(artist);

    jClass = (*env)->FindClass(env, "jahspotify/media/Artist");
    if (jClass == NULL)
    {
        fprintf(stderr,"jahspotify::createJArtistInstance: could not load jahnotify.media.Artist\n");
        return NULL;
    }

    if (artistLink)
    {
        sp_link_add_ref(artistLink);

        jobject artistJLink = createJLinkInstance(env, artistLink);

        setObjectObjectField(env,artistInstance,"id","Ljahspotify/media/Link;",artistJLink);

        sp_link_release(artistLink);

        setObjectStringField(env,artistInstance,"name",sp_artist_name(artist));
	
        sp_artistbrowse *artistBrowse = sp_artistbrowse_create(g_sess,artist,artistBrowseCompleteCallback,NULL);

        if (artistBrowse)
        {
            sp_artistbrowse_add_ref(artistBrowse);

            while (!sp_artistbrowse_is_loaded(artistBrowse))
            {
                usleep(100);
            }

            int numSimilarArtists = sp_artistbrowse_num_similar_artists(artistBrowse);

            if (numSimilarArtists > 0)
            {
                jmethodID jMethod = (*env)->GetMethodID(env,jClass,"addSimilarArtist","(Ljahspotify/media/Link;)V");

                if (jMethod == NULL)
                {
                    fprintf(stderr,"jahspotify::createJTrackInstance: could not load method setAlbum(link) on class Track\n");
                    return NULL;
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
                // Load portrait url
                const byte *portraitURI = sp_artistbrowse_portrait(artistBrowse,0);

                if (portraitURI)
                {
                    char *portraitURIStr = toHexString((byte*)portraitURI);
                    const char spotifyURI[] = "spotify:image:";
                    int len = strlen(spotifyURI) + strlen(portraitURIStr);
                    char *dest = malloc(len+1);
                    dest[0] = 0;
                    strcat(dest,spotifyURI);
                    strcat(dest,portraitURIStr);

                    sp_link *portraitLink = sp_link_create_from_string(dest);

		    sp_image *portrait = sp_image_create_from_link(g_sess,portraitLink);
		    if (portrait)
		    {
		      sp_image_add_ref(portrait);
		      sp_image_add_load_callback(portrait,imageLoadedCallback,NULL);
		    }
		    
                    free(dest);
                    free(portraitURIStr);

                    if (portraitLink)
                    {
                        sp_link_add_ref(portraitLink);

                        jobject portraitJLlink = createJLinkInstance(env,portraitLink);
                        setObjectObjectField(env,artistInstance,"portrait","Ljahspotify/media/Link;",portraitJLlink);

                        sp_link_release(portraitLink);
                    }
                }
            }

            // sp_artistbrowse_num_albums()
            // sp_artistbrowse_album()

            const char *bios = sp_artistbrowse_biography(artistBrowse);
	    if (bios)
	    {
	      setObjectStringField(env,artistInstance,"bios",bios);
	    }

            // sp_artistbrowse_num_tracks()
            // sp_artistbrowse_track()

            sp_artistbrowse_release(artistBrowse);
        }
    }
    return artistInstance;

}


jobject createJPlaylist(JNIEnv *env, sp_playlist *playlist)
{
    jclass jClass;
    jobject playlistInstance;
    jmethodID jMethod;

    jClass = (*env)->FindClass(env, "jahspotify/media/Playlist");
    if (jClass == NULL)
    {
        fprintf(stderr,"jahspotify::createJPlaylist: could not load jahnotify.media.Playlist\n");
        return NULL;
    }

    playlistInstance = (*env)->AllocObject(env,jClass);
    if (!playlistInstance)
    {
        fprintf(stderr,"jahspotify::createJPlaylist: could not create instance of jahspotify.media.Playlistt\n");
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
        fprintf(stderr,"jahspotify::createJPlaylist: could not load method addTrack(track) on class Playlist\n");
        return NULL;
    }

    int numTracks = sp_playlist_num_tracks(playlist);
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

JNIEXPORT jboolean JNICALL Java_jahspotify_impl_JahSpotifyImpl_shutdown ( JNIEnv *env, jobject obj)
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
        fprintf ( stderr, "jahspotify::Java_jahspotify_impl_JahSpotifyImpl_retrieveTrack: Could not create link!\n" );
        return JNI_FALSE;
    }

    sp_track *track = sp_link_as_track(link);

    while (!sp_track_is_loaded(track))
    {
        fprintf ( stderr, "jahspotify::Java_jahspotify_impl_JahSpotifyImpl_retrieveTrack: Waiting for track to be loaded ...\n" );
        sleep(1);
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

    sp_link *link = sp_link_create_from_string(nativeUri);
    if (!link)
    {
        // hmm
        fprintf ( stderr, "jahspotify::Java_jahspotify_impl_JahSpotifyImpl_retrievePlaylist: Could not create link!\n" );
        return JNI_FALSE;
    }

    sp_playlist *playlist = sp_playlist_create(g_sess,link);

    while (!sp_playlist_is_loaded(playlist))
    {
        fprintf ( stderr, "jahspotify::Java_jahspotify_impl_JahSpotifyImpl_retrievePlaylist: Waiting for playlist to be loaded ...\n" );
        sleep(1);
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

JNIEXPORT int JNICALL Java_jahspotify_impl_JahSpotifyImpl_pause (JNIEnv *env, jobject obj)
{
    if (g_currenttrack)
    {
        sp_session_player_play(g_sess,0);
    }
}

JNIEXPORT int JNICALL Java_jahspotify_impl_JahSpotifyImpl_resume (JNIEnv *env, jobject obj)
{
    if (g_currenttrack)
    {
        sp_session_player_play(g_sess,1);
    }
}

JNIEXPORT int JNICALL Java_jahspotify_impl_JahSpotifyImpl_readImage (JNIEnv *env, jobject obj, jstring uri, jobject outputStream)
{
    uint8_t *nativeURI = ( uint8_t * ) ( *env )->GetStringUTFChars ( env, uri, NULL );
    sp_link *imageLink = sp_link_create_from_string(nativeURI);
    size_t size;
    jclass jClass;
    int numBytesWritten = -1;
    
    if (imageLink)
    {
        sp_link_add_ref(imageLink);
        sp_image *image = sp_image_create_from_link(g_sess,imageLink);
        if (image)
        {
            sp_image_add_ref(image);
	    sp_image_add_load_callback(image, imageLoadedCallback, NULL);
	    
	    int count = 0;
	    
            while (!sp_image_is_loaded(image) && count < 5)
            {
                sleep(1);
		count++;
            }
            
            if (count == 5)
	    {
                fprintf(stderr,"jahspotify::Java_jahspotify_impl_JahSpotifyImpl_readImage: Timeout waiting for image to load ...\n");
		sp_image_release(image);
		sp_link_release(imageLink);
		return -1;
	    }

            byte *data = (byte*)sp_image_data(image,&size);

            jClass = (*env)->FindClass(env,"Ljava/io/OutputStream;");
            if (jClass == NULL)
            {
                fprintf(stderr,"jahspotify::Java_jahspotify_impl_JahSpotifyImpl_readImage: could not load class java.io.OutputStream\n");
                return -1;
            }
            // Lookup the method now - saves us looking it up for each iteration of the loop
            jmethodID jMethod = (*env)->GetMethodID(env,jClass,"write","(I)V");

            if (jMethod == NULL)
            {
                fprintf(stderr,"jahspotify::Java_jahspotify_impl_JahSpotifyImpl_readImage: could not load method write(int) on class java.io.OutputStream\n");
                return -1;
            }

            int i = 0;
            for (i = 0; i < size; i++)
            {
                (*env)->CallVoidMethod(env,outputStream,jMethod,*data);
                data++;
		numBytesWritten++;
            }
            sp_image_release(image);
        }
        sp_link_release(imageLink);
    }
    // Plus one because we start at -1
    return numBytesWritten+1;

}

JNIEXPORT int JNICALL Java_jahspotify_impl_JahSpotifyImpl_playTrack (JNIEnv *env, jobject obj, jstring uri)
{
    uint8_t *nativeURI = NULL;

    fprintf(stderr,"jahspotify::Java_jahspotify_impl_JahSpotifyImpl_play: Initiating play\n");

    nativeURI = ( uint8_t * ) ( *env )->GetStringUTFChars ( env, uri, NULL );

    fprintf(stderr,"jahspotify::Java_jahspotify_impl_JahSpotifyImpl_play: uri: %s\n", nativeURI);

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
            fprintf(stderr,"No track from link\n");
            return;
        }

        while (!sp_track_is_available(g_sess,t))
        {
            fprintf(stderr,"jahspotify::Java_jahspotify_impl_JahSpotifyImpl_play: Waiting for track ...\n");
            sleep(1);
        }

        if (sp_track_error(t) != SP_ERROR_OK)
        {
            fprintf(stderr,"jahspotify::Java_jahspotify_impl_JahSpotifyImpl_play: Error with track: %s\n",sp_error_message(sp_track_error(t)));

            return;
        }

        fprintf(stderr,"jahspotify::Java_jahspotify_impl_JahSpotifyImpl_play: name: %s duration: %d\n",sp_track_name(t),sp_track_duration(t));

        if (g_currenttrack == t)
        {
            fprintf(stderr,"jahspotify::Java_jahspotify_impl_JahSpotifyImpl_play: Same track!\n");
            return;
        }

        // If there is one playing, unload that now
        if (g_currenttrack && t != g_currenttrack)
        {
            // audio_fifo_flush(&g_audiofifo);

            // Unload the current track now
            sp_session_player_unload(g_sess);

            sp_link *currentTrackLink = sp_link_create_from_track(g_currenttrack,0);
            char *currentTrackLinkStr = NULL;
            if (currentTrackLink)
            {
                currentTrackLinkStr = malloc ( sizeof ( char ) * ( 100 ) );
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
        else
        {
            // audio_fifo_flush(&g_audiofifo);
            // audio_close();
            // audio_init(&g_audiofifo);
        }

        sp_track_add_ref(t);

        sp_error result = sp_session_player_load(g_sess, t);

        if (sp_track_error(t) != SP_ERROR_OK)
        {
            fprintf(stderr,"jahspotify::Java_jahspotify_impl_JahSpotifyImpl_play: Issue loading track: %s\n", sp_error_message((sp_track_error(t))));
        }

        fprintf(stderr,"jahspotify::Java_jahspotify_impl_JahSpotifyImpl_play: Track loaded: %s\n", (result == SP_ERROR_OK ? "yes" : "no"));

        // Update the global reference
        g_currenttrack = t;

        // Start playing the next track
        sp_session_player_play(g_sess, 1);

        fprintf(stderr,"jahspotify::Java_jahspotify_impl_JahSpotifyImpl_play: Playing track\n");

        sp_link_release(link);

        signalTrackStarted(nativeURI);

        return 0;
    }
    else
    {
        fprintf(stderr,"jahspotify::Java_jahspotify_impl_JahSpotifyImpl_play: Unable to load link at this point\n");
    }

    return 1;

}




/**
 * A track has ended. Remove it from the playlist.
 *
 * Called from the main loop when the music_delivery() callback has set g_playback_done.
 */
static void track_ended(void)
{
    fprintf(stderr,"jahspotify::track_ended: called\n");

    int tracks = 0;

    if (g_currenttrack)
    {
        sp_link *link = sp_link_create_from_track(g_currenttrack,0);
        char *trackLinkStr = NULL;
        if (link)
        {
            trackLinkStr = malloc ( sizeof ( char ) * ( 100 ) );
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

JNIEXPORT int JNICALL Java_jahspotify_impl_JahSpotifyImpl_initialize ( JNIEnv *env, jobject obj, jstring username, jstring password )
{
    sp_session *sp;
    sp_error err;
    uint8_t *nativePassword = NULL;
    uint8_t *nativeUsername = NULL;
    int next_timeout = 0;

    if ( !username || !password )
    {
        fprintf ( stderr, "Username or password not specified\n" );
        return 1;
    }

    /* Create session */
    spconfig.application_key_size = g_appkey_size;
    err = sp_session_create ( &spconfig, &sp );

    if ( SP_ERROR_OK != err )
    {
        fprintf ( stderr, "Unable to create session: %s\n",sp_error_message ( err ) );
        return 1;
    }

    fprintf ( stderr, "Session created %x\n",(int)sp);

    nativeUsername = ( uint8_t * ) ( *env )->GetStringUTFChars ( env, username, NULL );
    nativePassword = ( uint8_t * ) ( *env )->GetStringUTFChars ( env, password, NULL );

    g_sess = sp;

    pthread_mutex_init ( &g_notify_mutex, NULL );
    pthread_cond_init ( &g_notify_cond, NULL );

    sp_playlistcontainer_add_callbacks ( sp_session_playlistcontainer ( g_sess ),&pc_callbacks,NULL );

    fprintf ( stderr, "Initiating login: %s\n",nativeUsername );

    sp_session_login ( sp, nativeUsername, nativePassword );
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
            TIMEVAL_TO_TIMESPEC ( &tv, &ts );
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
            fprintf ( stderr, "Disconnected!\n");
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



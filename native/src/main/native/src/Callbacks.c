#include <unistd.h>
#include <jni.h>
#include <stdint.h>
#include <libspotify/api.h>
#include <string.h>
#include <stdlib.h>

#include "Callbacks.h"
#include "JahSpotify.h"
#include "JNIHelpers.h"
#include "ThreadHelpers.h"

extern jobject createJLinkInstance(JNIEnv *env, sp_link *link);

extern sp_session *g_sess;
extern sp_track *g_currenttrack;

extern jobject g_libraryListener;
extern jobject g_connectionListener;
extern jobject g_playbackListener;
extern jobject g_searchCompleteListener;
extern jobject g_mediaLoadedListener;

extern jclass g_playbackListenerClass;
extern jclass g_libraryListenerClass;
extern jclass g_connectionListenerClass;
extern jclass g_searchCompleteListenerClass;
extern jclass g_nativeSearchResultClass;
extern jclass g_mediaLoadedListenerClass;

extern JavaVM* g_vm;

void* threaded_startPlaybackSignalled(void* threadargs);
void* threaded_signalPlaylistSeen(void *threadarg);
void* threaded_signalTrackEnded(void *threadarg);
void* threaded_signalTrackStarted(void *threadarg);
void* threaded_signalStartFolderSeen(void *threadarg);
void* threaded_signalSynchCompleted(void *threadarg);
void* threaded_signalMetadataUpdated(void *threadarg);
void* threaded_signalSynchStarting(void *threadarg);
void* threaded_signalEndFolderSeen(void *threadarg);
void* threaded_signalLoggedIn(void *threadarg);
void* threaded_signalConnected(void *threadarg);

void* threaded_signalSearchComplete(void *threadarg);
void* threaded_signalTrackLoaded(void *threadarg);
void* threaded_signalImageLoaded(void *threadarg);
void* threaded_signalArtistBrowseLoaded(void *threadarg);
void* threaded_signalAlbumBrowseLoaded(void *threadarg);
void* threaded_signalPlaylistLoaded(void *threadarg);

struct threaded_signalTrackEnded_Parameters
{
    char *uri;
    bool forcedTrackEnd;
};

struct threaded_signalStartFolderSeen_Parameters
{
    char *folderName;
    uint64_t folderId;
};

struct threaded_signalPlaylistSeen_Parameters
{
    char *linkName;
    char *playlistName;
};

struct threaded_signalSearchComplete_Parameters
{
    sp_search *search;
    int32_t token;
};

struct threaded_signalArtistBrowse_Parameters
{
    sp_artistbrowse *artistBrowse;
    int32_t token;
};

struct threaded_signalImageLoaded_Parameters
{
    sp_image *image;
    int32_t token;
};

struct threaded_signalAlbumBrowseLoaded_Parameters
{
    sp_albumbrowse *albumBrowse;
    int32_t token;
};

struct threaded_signalTrackLoaded_Parameters
{
    sp_track *track;
    int32_t token;
};

struct threaded_signalPlaylistLoaded_Parameters
{
    sp_playlist *playlist;
    int32_t token;
};


void* threaded_signalTrackLoaded(void *threadarg)
{
  JNIEnv* env = NULL;
  jmethodID method;

  fprintf(stderr,"jahspotify::threaded_signalTrackLoaded: Track loaded\n");

  struct threaded_signalTrackLoaded_Parameters *threaded_signalTrackLoaded_Params = (struct threaded_signalTrackLoaded_Parameters*)threadarg;
  
  sp_track *track = threaded_signalTrackLoaded_Params->track;
  fprintf(stderr,"jahspotify::threaded_signalTrackLoaded: Track loaded: token: %d\n",threaded_signalTrackLoaded_Params->token);
  
  
  if (!retrieveEnv((JNIEnv*)&env))
  {
    goto fail;
  }
  
  method = (*env)->GetMethodID(env, g_mediaLoadedListenerClass, "track", "(ILjahspotify/media/Link;)V");

  if (method == NULL)
  {
      fprintf(stderr,"jahspotify::threaded_signalTrackLoaded: could not load callback method searchCompleted() on class SearchListener\n");
      goto fail;
  }
  
  sp_link *link = sp_link_create_from_track(track,0);
  
  sp_link_add_ref(link);

  jobject jLink = createJLinkInstance(env,link);
  
  sp_link_release(link);
  
  (*env)->CallVoidMethod(env,g_mediaLoadedListener,method,threaded_signalTrackLoaded_Params->token,jLink);
  if (checkException(env) != 0)
  {
    fprintf(stderr,"jahspotify::threaded_signalTrackLoaded: exception while calling listener\n");
  }

  fprintf(stderr,"jahspotify::threaded_signalTrackLoaded: callback invokved\n");

fail:

exit:
  
  sp_track_release(track);
}

void* threaded_signalImageLoaded(void *threadarg)
{
  JNIEnv* env = NULL;
  jmethodID method;
  struct threaded_signalImageLoaded_Parameters *threaded_signalImageLoaded_Params = (struct threaded_signalImageLoaded_Parameters*)threadarg;
  sp_image *image = threaded_signalImageLoaded_Params->image;

  fprintf(stderr,"jahspotify::threaded_signalImageLoaded: Image loaded: token: %d\n",threaded_signalImageLoaded_Params->token);

  if (!retrieveEnv((JNIEnv*)&env))
  {
    goto fail;
  }
  
  method = (*env)->GetMethodID(env, g_mediaLoadedListenerClass, "image", "(ILjahspotify/media/Link;)V");

  if (method == NULL)
  {
      fprintf(stderr,"jahspotify::threaded_signalImageLoaded: could not load callback method image() on class NativeMediaLoadedListener\n");
      goto fail;
  }
  
  sp_link *link = sp_link_create_from_image(image);
  
  sp_link_add_ref(link);

  jobject jLink = createJLinkInstance(env,link);
  
  sp_link_release(link);
  
  (*env)->CallVoidMethod(env,g_mediaLoadedListener,method,threaded_signalImageLoaded_Params->token,jLink);
  if (checkException(env) != 0)
  {
    fprintf(stderr,"jahspotify::threaded_signalImageLoaded: exception while calling listener\n");
  }

fail:

exit:
  
  sp_image_release(image);
}

void* threaded_signalArtistBrowseLoaded(void *threadarg)
{
}

void* threaded_signalAlbumBrowseLoaded(void *threadarg)
{
}

void* threaded_signalPlaylistLoaded(void *threadarg)
{
  struct threaded_signalPlaylistLoaded_Parameters *threaded_signalPlaylistLoaded_Params = (struct threaded_signalPlaylistLoaded_Parameters*)threadarg;
  JNIEnv* env = NULL;
  jmethodID method;

  sp_playlist *playlist = threaded_signalPlaylistLoaded_Params->playlist;

  // fprintf(stderr,"jahspotify::threaded_signalPlaylistLoaded: Playlist loaded: token: %d\n",threaded_signalPlaylistLoaded_Params->token);

  if (!retrieveEnv((JNIEnv*)&env))
  {
    goto fail;
  }
  
  method = (*env)->GetMethodID(env, g_mediaLoadedListenerClass, "playlist", "(ILjahspotify/media/Link;)V");

  if (method == NULL)
  {
      fprintf(stderr,"jahspotify::threaded_signalPlaylistLoaded: could not load callback method playlist(link) on class NativeMediaLoadedListener\n");
      goto fail;
  }
  
  sp_link *link = sp_link_create_from_playlist(playlist);
  
  sp_link_add_ref(link);

  jobject jLink = createJLinkInstance(env,link);
  
  sp_link_release(link);
  
  (*env)->CallVoidMethod(env,g_mediaLoadedListener,method,threaded_signalPlaylistLoaded_Params->token,jLink);
  if (checkException(env) != 0)
  {
    fprintf(stderr,"jahspotify::threaded_signalPlaylistLoaded: exception while calling listener\n");
  }

fail:

exit:
  
  sp_playlist_release(playlist);

  
}


jint addObjectToCollection(JNIEnv *env, jobject collection, jobject object)
{
    jclass clazz;
    jmethodID methodID;

    clazz = (*env)->GetObjectClass(env, collection);
    if (clazz == NULL)
        return 1;

    methodID = (*env)->GetMethodID(env, clazz,"add","(Ljava/lang/Object;)Z");
    if (methodID == NULL)
        return 1;
    
    // Invoke the method
    jboolean result = (*env)->CallBooleanMethod(env,collection,methodID,object);
    if (checkException(env) != 0)
    {
      fprintf(stderr,"jahspotify::addObjectToCollection: exception while adding \n");
    }


}

void* threaded_signalSearchComplete(void *threadarg)
{
    JNIEnv* env = NULL;
    jmethodID method;
    sp_search *search = NULL; 
    jobject jLink;
    jobject nativeSearchResult;
    jobject trackLinkCollection;
    jobject albumLinkCollection;
    jobject artistLinkCollection;
    struct threaded_signalSearchComplete_Parameters *threaded_signalSearchComplete_Params = (struct threaded_signalSearchComplete_Parameters*)threadarg;
    int numResultsFound = 0;
    int index = 0;
    
    // fprintf(stderr,"jahspotify::threaded_signalSearchComplete: Search complete\n");

    if (!retrieveEnv((JNIEnv*)&env))
    {
        goto fail;
    }
    
    search = threaded_signalSearchComplete_Params->search;

    // Create the Native Search Result instance
    nativeSearchResult = createInstanceFromJClass(env,g_nativeSearchResultClass);
    
    trackLinkCollection = createInstance(env,"java/util/ArrayList");
    setObjectObjectField(env,nativeSearchResult,"tracksFound","Ljava/util/List;",trackLinkCollection);
    
    numResultsFound = sp_search_num_tracks(search);
    for (index = 0; index < numResultsFound; index++)
    {
      sp_track *track = sp_search_track(search, index);
      if (track)
      {
	sp_track_add_ref(track);
	
	if (sp_track_is_loaded(track))
	{
	  sp_link *link = sp_link_create_from_track(track,0);
	  if (link)
	  {
	    sp_link_add_ref(link);
	    jLink = createJLinkInstance(env, link);
	    addObjectToCollection(env, trackLinkCollection,jLink);
	    sp_link_release(link);
	  }
	}
	else
	{
	  fprintf(stderr,"jahspotify::threaded_signalSearchComplete: Track not loaded\n");
	}
	
	sp_track_release(track);
	
      }
    }
    
    albumLinkCollection = createInstance(env,"java/util/ArrayList");
    setObjectObjectField(env,nativeSearchResult,"albumsFound","Ljava/util/List;",albumLinkCollection);

    numResultsFound = sp_search_num_albums(search);
    for (index = 0; index < numResultsFound; index++)
    {
      sp_album *album = sp_search_album(search, index);
      if (album)
      {
	sp_album_add_ref(album);
	
	if (sp_album_is_loaded(album))
	{
	  sp_link *link = sp_link_create_from_album(album);
	  if (link)
	  {
	    sp_link_add_ref(link);
	    jLink = createJLinkInstance(env, link);
	    addObjectToCollection(env, albumLinkCollection,jLink);
	    sp_link_release(link);
	  }
	}
	else
	{
	  fprintf(stderr,"jahspotify::threaded_signalSearchComplete: Album not loaded\n");
	}
	
	sp_album_release(album);
	
      }
    }
        
    
    artistLinkCollection = createInstance(env,"java/util/ArrayList");
    setObjectObjectField(env,nativeSearchResult,"artistsFound","Ljava/util/List;",artistLinkCollection);
    
    numResultsFound = sp_search_num_artists(search);
    for (index = 0; index < numResultsFound; index++)
    {
      sp_artist *artist = sp_search_artist(search, index);
      if (artist)
      {
	sp_artist_add_ref(artist);
	
	if (sp_artist_is_loaded(artist))
	{
	  sp_link *link = sp_link_create_from_artist(artist);
	  if (link)
	  {
	    sp_link_add_ref(link);
	    jLink = createJLinkInstance(env, link);
	    addObjectToCollection(env, artistLinkCollection,jLink);
	    sp_link_release(link);
	  }
	}
	else
	{
	  fprintf(stderr,"jahspotify::threaded_signalSearchComplete: Artist not loaded\n");
	}
	
	sp_artist_release(artist);
	
      }
    }
  
    setObjectIntField(env,nativeSearchResult,"totalNumTracks",sp_search_total_tracks(search));
    setObjectIntField(env,nativeSearchResult,"trackOffset",sp_search_num_tracks(search));
   
    setObjectIntField(env,nativeSearchResult,"totalNumAlbums",sp_search_total_albums(search));
    setObjectIntField(env,nativeSearchResult,"albumOffset",sp_search_num_albums(search));
    
    setObjectIntField(env,nativeSearchResult,"totalNumArtists",sp_search_total_artists(search));
    setObjectIntField(env,nativeSearchResult,"artistOffset",sp_search_num_artists(search));

    setObjectStringField(env,nativeSearchResult,"query",sp_search_query(search));
    setObjectStringField(env,nativeSearchResult,"didYouMean",sp_search_did_you_mean(search));
    
    method = (*env)->GetMethodID(env, g_searchCompleteListenerClass, "searchCompleted", "(ILjahspotify/SearchResult;)V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalSearchComplete: could not load callback method searchCompleted() on class SearchListener\n");
        goto fail;
    }
    
    (*env)->CallVoidMethod(env,g_searchCompleteListener,method,threaded_signalSearchComplete_Params->token,nativeSearchResult);
    if (checkException(env) != 0)
    {
      fprintf(stderr,"jahspotify::threaded_signalSearchComplete: exception while calling search complete listener\n");
    }

fail:
    
exit:
    sp_search_release(search);
    detachThread();
    
}

void* threaded_startPlaybackSignalled(void* threadargs)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    jstring nextUriStr;
    char *nextUri;
    
    if (!retrieveEnv((JNIEnv*)&env))
    {
        goto fail;
    }
    
    method = (*env)->GetMethodID(env, g_playbackListenerClass, "nextTrackToPreload", "()Ljava/lang/String;");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_startPlaybackSignalled: could not load callback method string nextTrackToPreload() on class PlaybackListener\n");
        goto fail;
    }

    nextUriStr = (*env)->CallObjectMethod(env, g_playbackListener, method);
    checkException(env);

    if (nextUriStr)
    {
      nextUri = ( uint8_t * ) ( *env )->GetStringUTFChars ( env, nextUriStr, NULL );

      sp_link *link = sp_link_create_from_string(nextUri);
      if (link)
      {
	sp_track *track = sp_link_as_track(link);
	sp_link_release(link);
	sp_error error = sp_session_player_prefetch(g_sess,track);
	sp_track_release(track);
	if (error != SP_ERROR_OK)
	{
	  fprintf(stderr,"jahspotify::threaded_startPlaybackSignalled: error prefetch: %s\n",sp_error_message(error));
	  goto fail;
	}
      }
    }

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_startPlaybackSignalled: error during callback\n");

exit:

    if (nextUri) 
    {
      (*env)->ReleaseStringUTFChars(env, nextUriStr,nextUri);
    }

    result = detachThread();
}  
 
void* threaded_signalPlaylistSeen(void *threadarg)
{
    struct threaded_signalPlaylistSeen_Parameters *params = (struct threaded_signalPlaylistSeen_Parameters*)threadarg;

    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID aMethod;
    jstring playListStr;
    jstring linkNameStr;

    if (!retrieveEnv((JNIEnv*)&env))
    {
        goto fail;
    }

    aMethod = (*env)->GetMethodID(env, g_libraryListenerClass, "playlist", "(Ljava/lang/String;Ljava/lang/String;)V");

    if (aMethod == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalPlaylistSeen: could not load callback method playlistSeen(string) on class NativeLibraryListener\n");
        goto fail;
    }

    if (params->linkName)
    {
        linkNameStr = (*env)->NewStringUTF(env, params->linkName);
        if (linkNameStr == NULL)
        {
            fprintf(stderr,"jahspotify::threaded_signalPlaylistSeen: error creating java string\n");
            goto fail;
        }
    }

    if (params->playlistName)
    {
        playListStr = (*env)->NewStringUTF(env, params->playlistName);
        if (playListStr == NULL)
        {
            fprintf(stderr,"jahspotify::threaded_signalPlaylistSeen: error creating java string\n");
            goto fail;
        }
    }

    (*env)->CallVoidMethod(env, g_libraryListener, aMethod, playListStr,linkNameStr);
    if (checkException(env) != 0)
    {
      fprintf(stderr,"jahspotify::threaded_signalPlaylistSeen: exception while calling callback\n");
    }

    

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalPlaylistSeen: error during callback\n");


exit:
    if (params) free(params);
    if (linkNameStr) (*env)->DeleteLocalRef(env, linkNameStr);
    if (playListStr) (*env)->DeleteLocalRef(env, playListStr);

    result = detachThread();

}

void* threaded_signalTrackEnded(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    struct threaded_signalTrackEnded_Parameters *threaded_signalTrackEnded_Params = (struct threaded_signalTrackEnded_Parameters*)threadarg;
    jstring uriStr;

    if (!retrieveEnv((JNIEnv*)&env))
    {
        goto fail;
    }
    
    if (threaded_signalTrackEnded_Params->uri)
    {
        uriStr = (*env)->NewStringUTF(env, threaded_signalTrackEnded_Params->uri);
        if (uriStr == NULL)
        {
            fprintf(stderr,"jahspotify::threaded_signalTrackEnded: error creating java string\n");
            goto fail;
        }
    }

    method = (*env)->GetMethodID(env, g_playbackListenerClass, "trackEnded", "(Ljava/lang/String;Z)V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalTrackEnded: could not load callback method trackEnded(string) on class jahnotify.PlaybackListener\n");
        goto fail;
    }

    fprintf(stderr,"jahspotify::threaded_signalTrackEnded: calling callback\n");
    (*env)->CallVoidMethod(env, g_playbackListener, method,uriStr,threaded_signalTrackEnded_Params->forcedTrackEnd);
    if (checkException(env) != 0)
    {
      fprintf(stderr,"jahspotify::threaded_signalTrackEnded: exception while calling callback\n");
    }

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalLoggedIn: error during callback\n");

exit:
    if (uriStr) (*env)->DeleteLocalRef(env, uriStr);
    if (threaded_signalTrackEnded_Params) free(threaded_signalTrackEnded_Params);
    
    result = detachThread();
    
}

void* threaded_signalTrackStarted(void *threadarg)
{

    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    char *uri = (char*)threadarg;

    jstring uriStr;
    
    if (!retrieveEnv((JNIEnv*)&env))
    {
        goto fail;
    }
    
    if (uri)
    {
        uriStr = (*env)->NewStringUTF(env,uri);
        if (uriStr == NULL)
        {
            fprintf(stderr,"jahspotify::threaded_signalTrackStarted: error creating java string\n");
            goto fail;
        }
    }

    method = (*env)->GetMethodID(env, g_playbackListenerClass, "trackStarted", "(Ljava/lang/String;)V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalTrackStarted: could not load callback method trackStarted(string) on class jahnotify.PlaybackListener\n");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_playbackListener, method,uriStr);
    checkException(env);

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalTrackStarted: error during callback\n");

exit:
    if (uriStr) (*env)->DeleteLocalRef(env, uriStr);
    if (uri) free(uri);

    result = detachThread();
}


void* threaded_signalStartFolderSeen(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;

    jstring folderNameStr;
    struct threaded_signalStartFolderSeen_Parameters *threaded_signalFolderSeen_Params = (struct threaded_signalStartFolderSeen_Parameters*)threadarg;

    if (!retrieveEnv((JNIEnv*)&env))
    {
        goto fail;
    }

    if (threaded_signalFolderSeen_Params->folderName)
    {
        folderNameStr = (*env)->NewStringUTF(env, threaded_signalFolderSeen_Params->folderName);
        if (folderNameStr == NULL)
        {
            fprintf(stderr,"jahspotify::threaded_signalStartFolderSeen: error creating java string\n");
            goto fail;
        }
    }

    method = (*env)->GetMethodID(env, g_libraryListenerClass, "startFolder", "(Ljava/lang/String;J)V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalStartFolderSeen: could not load callback method startFolder(string) on class jahnotify.PlaylistListener\n");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_libraryListener, method,folderNameStr,threaded_signalFolderSeen_Params->folderId);
    if (checkException(env) != 0)
    {
      fprintf(stderr,"jahspotify::threaded_signalStartFolderSeen: exception while calling callback\n");
    }

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalLoggedIn: error during callback\n");

exit:
    if (folderNameStr) (*env)->DeleteLocalRef(env, folderNameStr);
    if (threaded_signalFolderSeen_Params) free(threaded_signalFolderSeen_Params);

    result = detachThread();
    
}

void* threaded_signalMetadataUpdated(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;

    if (!retrieveEnv((JNIEnv*)&env))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_libraryListenerClass, "metadataUpdated", "(Ljava/lang/String;)V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalMetadataUpdated: could not load callback method metadataUpdated() on class NativeLibraryListener\n");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_libraryListener, method);
    if (checkException(env) != 0)
    {
      fprintf(stderr,"jahspotify::threaded_signalMetadataUpdated: exception while calling callback\n");
    }


    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalMetadataUpdated: error during callback\n");

exit:

    result = detachThread();
    
}


void* threaded_signalSynchCompleted(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;

    if (!retrieveEnv((JNIEnv*)&env))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_libraryListenerClass, "synchCompleted", "()V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalSynchCompleted: could not load callback method synchCompleted() on class jahnotify.PlaylistListener\n");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_libraryListener, method);
    checkException(env);

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalSynchCompleted: error during callback\n");

exit:

    result = detachThread();
    
}


void* threaded_signalSynchStarting(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    int numPlaylists = *(int*)threadarg;

    if (!retrieveEnv((JNIEnv*)&env))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_libraryListenerClass, "synchStarted", "(I)V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalSynchStarting: could not load callback method synchStarted(int) on class jahnotify.PlaylistListener\n");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_libraryListener, method, numPlaylists);
    checkException(env);

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalSynchStarting: error during callback\n");

exit:

    result = detachThread();
    
}


void* threaded_signalEndFolderSeen(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;

    if (!retrieveEnv((JNIEnv*)&env))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_libraryListenerClass, "endFolder", "()V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalEndFolderSeen: could not load callback method endFolder() on class jahnotify.PlaylistListener\n");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_libraryListener, method);
    checkException(env);

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalEndFolderSeen: error during callback\n");

exit:

    result = detachThread();

}

void* threaded_signalConnected(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;

    if (!retrieveEnv((JNIEnv*)&env))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_connectionListenerClass, "connected", "()V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalConnected: could not load callback method connected() on class ConnectionListener\n");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_connectionListener, method);
    checkException(env);

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalConnected: error during callback\n");
exit:
    detachThread();
}

void* threaded_signalLoggedIn(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;

    if (!retrieveEnv((JNIEnv*)&env))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_connectionListenerClass, "loggedIn", "()V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalLoggedIn: could not load callback method loggedIn() on class ConnectionListener\n");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_connectionListener, method);
    if (checkException(env) != 0)
    {
      fprintf(stderr,"jahspotify::threaded_signalLoggedIn: exception while calling listener\n");
    }

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalLoggedIn: error during callback\n");

exit:
    detachThread();

}

void startPlaybackSignalled()
{
  // Not threaded since we would have to 'post' this into the event loop for processing
  threaded_signalLoggedIn(NULL);
/*  if (placeInThread(threaded_startPlaybackSignalled,0) != 0)
  {
    fprintf ( stderr, "jahspotify::startPlaybackSignalled: error placing onto thread\n");
  }*/
}

int signalConnected()
{
  if (!g_connectionListener)
  {
    fprintf ( stderr, "jahspotify::signalLoggedIn: no connection listener registered\n");
    return 1;
  }

  //    threaded_signalLoggedIn(NULL);
  if (placeInThread(threaded_signalConnected,NULL) != 0)
  {
    fprintf ( stderr, "jahspotify::signalLoggedIn: error placing onto thread\n");
  }

  return 0;
}

int signalDisconnected()
{
}

int signalLoggedOut()
{
}

int signalLoggedIn()
{
    if (!g_connectionListener)
    {
        fprintf ( stderr, "jahspotify::signalLoggedIn: no connection listener registered\n");
        return 1;
    }

    //    threaded_signalLoggedIn(NULL);
    if (placeInThread(threaded_signalLoggedIn,NULL) != 0)
    {
      fprintf ( stderr, "jahspotify::signalLoggedIn: error placing onto thread\n");
    }

    return 0;
}

int signalStartFolderSeen(char *folderName, uint64_t folderId)
{
    char *folderNameCopy;
    struct threaded_signalStartFolderSeen_Parameters *threaded_signalFolderSeen_Params = malloc(sizeof(struct threaded_signalStartFolderSeen_Parameters));
    
    if (!g_libraryListener)
    {
        fprintf ( stderr, "jahspotify::signalStartFolderSeen: no playlist listener registered\n");
        return 1;
    }

    folderNameCopy = malloc(sizeof(char) * (strlen(folderName)+1));
    strcpy(folderNameCopy,folderName);

    threaded_signalFolderSeen_Params->folderName = folderNameCopy;
    threaded_signalFolderSeen_Params->folderId = folderId;
    
    threaded_signalStartFolderSeen(threaded_signalFolderSeen_Params);
    // if (placeInThread(threaded_signalStartFolderSeen,folderNameCopy) != 0)
    // {
    //   fprintf ( stderr, "jahspotify::signalStartFolderSeen: error placing onto thread\n");
    // }

    return 0;
}

int signalSynchStarting(int numPlaylists)
{
  int numPlayLists = numPlaylists;
    if (!g_libraryListener)
    {
        fprintf ( stderr, "jahspotify::signalSynchStarting: no playlist listener registered\n");
        return 1;
    }
    
    threaded_signalSynchStarting(&numPlaylists);
    
    // if (placeInThread(threaded_signalSynchStarting,&numPlaylists) != 0)
    // {
    //  fprintf ( stderr, "jahspotify::signalSynchStarting: error placing onto thread\n");
    // }

    return 0;
}

int signalSynchCompleted()
{
    if (!g_libraryListener)
    {
        fprintf ( stderr, "jahspotify::signalSynchCompleted: no playlist listener registered\n");
        return 1;
    }
    // threaded_signalSynchCompleted(NULL);
    if (placeInThread(threaded_signalSynchCompleted,NULL) != 0)
    {
      fprintf ( stderr, "jahspotify::signalSynchStarting: error placing onto thread\n");
    }

    return 0;
}

int signalMetadataUpdated(sp_playlist *playlist)
{
    if (!g_libraryListener)
    {
        fprintf ( stderr, "jahspotify::signalMetadataUpdated: no playlist listener registered\n");
        return 1;
    }
    threaded_signalMetadataUpdated(NULL);
//     if (placeInThread(threaded_signalMetadataUpdated,NULL) != 0)
//     {
//       fprintf ( stderr, "jahspotify::signalMetadataUpdated: error placing onto thread\n");
//     }

    return 0;
}


int signalEndFolderSeen()
{
    if (!g_libraryListener)
    {
        fprintf ( stderr, "jahspotify::signalEndFolderSeen: no playlist listener registered\n");
        return 1;
    }
   
   threaded_signalEndFolderSeen(NULL);
    // if (placeInThread(threaded_signalEndFolderSeen,1) != 0)
    //{
    // fprintf ( stderr, "jahspotify::signalEndFolderSeen: error placing onto thread\n");
    // }
    
    return 0;
}

int signalTrackEnded(char *uri, bool forcedTrackEnd)
{
  struct threaded_signalTrackEnded_Parameters *threaded_signalTrackEnded_Params;

  if (!g_playbackListener)
  {
    fprintf ( stderr, "jahspotify::signalTrackEnded: no playback listener\n"); 
    return 1;
  }
  
  threaded_signalTrackEnded_Params = calloc(1, sizeof(struct threaded_signalTrackEnded_Parameters));
  
  threaded_signalTrackEnded_Params->uri = strdup(uri);
  threaded_signalTrackEnded_Params->forcedTrackEnd = forcedTrackEnd;
  
  if (placeInThread(threaded_signalTrackEnded,threaded_signalTrackEnded_Params) != 0)
  {
    fprintf ( stderr, "jahspotify::signalTrackEnded: error placing onto thread\n");
  }

}

int signalTrackStarted(char *uri)
{
  char *uriCopy;

  fprintf ( stderr, "jahspotify::signalTrackStarted: uri: %s\n", uri); 
  if (!g_playbackListener)
  {
    fprintf ( stderr, "jahspotify::signalTrackStarted: no playback listener\n"); 
    return 1;
  }
  
  uriCopy = strdup(uri);

  fprintf ( stderr, "jahspotify::signalTrackStarted: placing in thread\n", uriCopy); 
  if (placeInThread(threaded_signalTrackStarted,uriCopy) != 0)
  {
    fprintf ( stderr, "jahspotify::signalTrackStarted: error placing onto thread\n");
  }
  
}

int signalPlaylistSeen(const char *playlistName, char *linkName)
{
    char *linkNameCopy;
    char *playlistNameCopy;
    struct threaded_signalPlaylistSeen_Parameters *threaded_signalPlaylistSeen_Params;

    if (!g_libraryListener)
    {
        fprintf ( stderr, "jahspotify::signalPlaylistSeen: no playlist listener registered\n");
        return 1;
    }

    // fprintf(stderr,"jahspotify::signalPlaylistSeen: playlistName: %s linkName: %s \n",playlistName,linkName);

    linkNameCopy = malloc(sizeof(char) * (strlen(linkName)+1));
    strcpy(linkNameCopy,linkName);

    playlistNameCopy = malloc(sizeof(char) * (strlen(playlistName)+1));
    strcpy(playlistNameCopy,playlistName);

    threaded_signalPlaylistSeen_Params = malloc(sizeof(struct threaded_signalPlaylistSeen_Parameters));

    threaded_signalPlaylistSeen_Params->linkName = linkNameCopy;
    threaded_signalPlaylistSeen_Params->playlistName = playlistNameCopy;

    threaded_signalPlaylistSeen(threaded_signalPlaylistSeen_Params);

    return 0;
}

int signalArtistBrowseLoaded(sp_artistbrowse *artistBrowse, int32_t token)
{
}

int signalImageLoaded(sp_image *image, int32_t token)
{
  
  if (!g_mediaLoadedListener)
  {
      fprintf ( stderr, "jahspotify::signalImageLoaded: no playlist media loaded listener registered\n");
      return 1;
  }

  struct threaded_signalImageLoaded_Parameters *threaded_signalImageLoaded_Params = calloc(1,sizeof(struct threaded_signalImageLoaded_Parameters));
  
  threaded_signalImageLoaded_Params->image = image;
  threaded_signalImageLoaded_Params->token = token;

  if (placeInThread(threaded_signalImageLoaded,threaded_signalImageLoaded_Params) != 0)
  {
    fprintf ( stderr, "jahspotify::signalImageLoaded: error placing onto thread\n");
  }    
}

int signalPlaylistLoaded(sp_playlist *playlist, int32_t token)
{
    if (!g_mediaLoadedListener)
    {
        fprintf ( stderr, "jahspotify::signalPlaylistLoaded: no playlist media loaded listener registered\n");
        return 1;
    }

  sp_playlist_add_ref(playlist);

  struct threaded_signalPlaylistLoaded_Parameters *threaded_signalPlaylistLoaded_Params = calloc(1,sizeof(struct threaded_signalPlaylistLoaded_Parameters));
  
  threaded_signalPlaylistLoaded_Params->playlist = playlist;
  threaded_signalPlaylistLoaded_Params->token = token;

/*  if (placeInThread(threaded_signalPlaylistLoaded,threaded_signalPlaylistLoaded_Params) != 0)
  {
    fprintf ( stderr, "jahspotify::signalPlaylistLoaded: error placing onto thread\n");
  }*/
  threaded_signalPlaylistLoaded(threaded_signalPlaylistLoaded_Params);

}

int signalAlbumBrowseLoaded(sp_albumbrowse *albumBrowse, int32_t token)
{
   if (!g_mediaLoadedListener)
    {
        fprintf ( stderr, "jahspotify::signalAlbumBrowseLoaded: no playlist media loaded listener registered\n");
        return 1;
    }
  
  sp_albumbrowse_add_ref(albumBrowse);

  struct threaded_signalAlbumBrowseLoaded_Parameters *threaded_signalAlbumBrowseLoaded_Params = calloc(1,sizeof(struct threaded_signalAlbumBrowseLoaded_Parameters));
  
  threaded_signalAlbumBrowseLoaded_Params->albumBrowse = albumBrowse;
  threaded_signalAlbumBrowseLoaded_Params->token = token;

  if (placeInThread(threaded_signalAlbumBrowseLoaded,threaded_signalAlbumBrowseLoaded_Params) != 0)
  {
    fprintf ( stderr, "jahspotify::signalImageLoaded: error placing onto thread\n");
  }
}

int signalTrackLoaded(sp_track *track, int32_t token)
{
    if (!g_mediaLoadedListener)
    {
        fprintf ( stderr, "jahspotify::signalTrackLoaded: no playlist media loaded listener registered\n");
        return 1;
    }
  sp_track_add_ref(track);
  struct threaded_signalTrackLoaded_Parameters *threaded_signalTrackLoaded_Params = calloc(1,sizeof(struct threaded_signalTrackLoaded_Parameters));
  
  threaded_signalTrackLoaded_Params->track = track;
  threaded_signalTrackLoaded_Params->token = token;

  if (placeInThread(threaded_signalTrackLoaded,threaded_signalTrackLoaded_Params) != 0)
  {
    fprintf ( stderr, "jahspotify::signalTrackLoaded: error placing onto thread\n");
  }
}

int signalSearchComplete(sp_search *search, int32_t token)
{
   if (!g_searchCompleteListener)
    {
        fprintf ( stderr, "jahspotify::signalSearchComplete: no playlist media loaded listener registered\n");
        return 1;
    }
  sp_search_add_ref(search);
  struct threaded_signalSearchComplete_Parameters *threaded_signalSearchComplete_Params = calloc(1,sizeof(struct threaded_signalSearchComplete_Parameters));
  
  threaded_signalSearchComplete_Params->search = search;
  threaded_signalSearchComplete_Params->token = token;

  if (placeInThread(threaded_signalSearchComplete,threaded_signalSearchComplete_Params) != 0)
  {
    fprintf ( stderr, "jahspotify::signalSearchComplete: error placing onto thread\n");
  }

}

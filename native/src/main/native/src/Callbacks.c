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

extern sp_session *g_sess;
extern sp_track *g_currenttrack;

extern jobject g_playlistListener;
extern jobject g_connectionListener;
extern jobject g_playbackListener;

extern jclass g_playbackListenerClass;
extern jclass g_playlistListenerClass;
extern jclass g_connectionListenerClass;

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

void* threaded_signalSearchComplete(void *threadarg);
void* threaded_signalTrackLoaded(void *threadarg);
void* threaded_signalImageLoaded(void *threadarg);
void* threaded_signalArtistBrowseLoaded(void *threadarg);
void* threaded_signalAlbumBrowseLoaded(void *threadarg);

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


void* threaded_signalTrackLoaded(void *threadarg)
{
  struct threaded_signalTrackLoaded_Parameters *threaded_signalTrackLoaded_Params = (struct threaded_signalTrackLoaded_Parameters*)threadarg;
  sp_track *track = threaded_signalTrackLoaded_Params->track;
  fprintf(stderr,"jahspotify::threaded_signalTrackLoaded: Track loaded: token: %d\n",threaded_signalTrackLoaded_Params->token);
  sp_track_release(track);
}

void* threaded_signalImageLoaded(void *threadarg)
{
  struct threaded_signalImageLoaded_Parameters *threaded_signalImageLoaded_Params = (struct threaded_signalImageLoaded_Parameters*)threadarg;
  sp_image *image = threaded_signalImageLoaded_Params->image;
  fprintf(stderr,"jahspotify::threaded_signalImageLoaded: Image loaded: token: %d\n",threaded_signalImageLoaded_Params->token);
  sp_image_release(image);
}

void* threaded_signalArtistBrowseLoaded(void *threadarg)
{
}

void* threaded_signalAlbumBrowseLoaded(void *threadarg)
{
}

void* threaded_signalSearchComplete(void *threadarg)
{
  
  struct threaded_signalSearchComplete_Parameters *threaded_signalSearchComplete_Params = (struct threaded_signalSearchComplete_Parameters*)threadarg;
  sp_search *search = threaded_signalSearchComplete_Params->search;
  
  fprintf(stderr,"jahspotify::threaded_signalSearchComplete: Search complete: token: %d\n",threaded_signalSearchComplete_Params->token);
  fprintf(stderr,"jahspotify::threaded_signalSearchComplete: Search complete: tracks: %d albums: %d artists:%d\n", sp_search_num_tracks(search), sp_search_num_albums(search), sp_search_num_artists(search));
  
  sp_search_release(search);

}

void* threaded_startPlaybackSignalled(void* threadargs)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    jstring nextUriStr;
    char *nextUri;
    int alreadyAttachedToThread;
    
    if (!retrieveEnv((JNIEnv*)&env,&alreadyAttachedToThread))
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


    if (alreadyAttachedToThread != 0)
    {
        // Detach from the thread at this point - since we re-attached, this is required
        result = (*g_vm)->DetachCurrentThread(g_vm);
    }

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
    int alreadyAttachedToThread = 0;

    if (!retrieveEnv((JNIEnv*)&env,&alreadyAttachedToThread))
    {
        goto fail;
    }

    aMethod = (*env)->GetMethodID(env, g_playlistListenerClass, "playlist", "(Ljava/lang/String;Ljava/lang/String;)V");

    if (aMethod == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalPlaylistSeen: could not load callback method playlistSeen(string) on class PlaylistListener\n");
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

    (*env)->CallVoidMethod(env, g_playlistListener, aMethod, playListStr,linkNameStr);

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalPlaylistSeen: error during callback\n");


exit:
    if (params) free(params);
    if (linkNameStr) (*env)->DeleteLocalRef(env, linkNameStr);
    if (playListStr) (*env)->DeleteLocalRef(env, playListStr);

    if (alreadyAttachedToThread != 0)
    {
        // Detach from the thread at this point - since we re-attached, this is required
        result = (*g_vm)->DetachCurrentThread(g_vm);
    }

}

void* threaded_signalTrackEnded(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    int alreadyAttachedToThread = 0;
    struct threaded_signalTrackEnded_Parameters *threaded_signalTrackEnded_Params = (struct threaded_signalTrackEnded_Parameters*)threadarg;
    jstring uriStr;

    if (!retrieveEnv((JNIEnv*)&env,&alreadyAttachedToThread))
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
    fprintf(stderr,"jahspotify::threaded_signalTrackEnded: callback called\n");

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalLoggedIn: error during callback\n");

exit:
    if (uriStr) (*env)->DeleteLocalRef(env, uriStr);
    if (threaded_signalTrackEnded_Params) free(threaded_signalTrackEnded_Params);
    if (alreadyAttachedToThread != 0)
    {
        // Detach from the thread at this point - since we re-attached, this is required
        result = (*g_vm)->DetachCurrentThread(g_vm);
    }

}

void* threaded_signalTrackStarted(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    int alreadyAttachedToThread = 0;
    char *uri = (char*)threadarg;

    jstring uriStr;

    if (!retrieveEnv((JNIEnv*)&env,&alreadyAttachedToThread))
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

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalTrackStarted: error during callback\n");

exit:
    if (uriStr) (*env)->DeleteLocalRef(env, uriStr);
    if (uri) free(uri);
    if (alreadyAttachedToThread != 0)
    {
        // Detach from the thread at this point - since we re-attached, this is required
        result = (*g_vm)->DetachCurrentThread(g_vm);
    }

}


void* threaded_signalStartFolderSeen(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    int alreadyAttachedToThread = 0;

    jstring folderNameStr;
    struct threaded_signalStartFolderSeen_Parameters *threaded_signalFolderSeen_Params = (struct threaded_signalStartFolderSeen_Parameters*)threadarg;

    if (!retrieveEnv((JNIEnv*)&env,&alreadyAttachedToThread))
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

    method = (*env)->GetMethodID(env, g_playlistListenerClass, "startFolder", "(Ljava/lang/String;J)V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalStartFolderSeen: could not load callback method startFolder(string) on class jahnotify.PlaylistListener\n");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_playlistListener, method,folderNameStr,threaded_signalFolderSeen_Params->folderId);

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalLoggedIn: error during callback\n");

exit:
    if (folderNameStr) (*env)->DeleteLocalRef(env, folderNameStr);
    if (threaded_signalFolderSeen_Params) free(threaded_signalFolderSeen_Params);
    if (alreadyAttachedToThread != 0)
    {
        // Detach from the thread at this point - since we re-attached, this is required
        result = (*g_vm)->DetachCurrentThread(g_vm);
    }

}

void* threaded_signalMetadataUpdated(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    int alreadyAttachedToThread = 0;

    if (!retrieveEnv((JNIEnv*)&env,&alreadyAttachedToThread))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_playlistListenerClass, "metadataUpdated", "()V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalMetadataUpdated: could not load callback method metadataUpdated() on class jahnotify.PlaylistListener\n");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_playlistListener, method);

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalMetadataUpdated: error during callback\n");

exit:
    if (alreadyAttachedToThread != 0)
    {
        // Detach from the thread at this point - since we re-attached, this is required
        result = (*g_vm)->DetachCurrentThread(g_vm);
    }
}


void* threaded_signalSynchCompleted(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    int alreadyAttachedToThread = 0;

    if (!retrieveEnv((JNIEnv*)&env,&alreadyAttachedToThread))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_playlistListenerClass, "synchCompleted", "()V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalSynchCompleted: could not load callback method synchCompleted() on class jahnotify.PlaylistListener\n");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_playlistListener, method);

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalSynchCompleted: error during callback\n");

exit:
    if (alreadyAttachedToThread != 0)
    {
        // Detach from the thread at this point - since we re-attached, this is required
        result = (*g_vm)->DetachCurrentThread(g_vm);
    }
}


void* threaded_signalSynchStarting(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    int alreadyAttachedToThread = 0;
    int numPlaylists = *(int*)threadarg;

    if (!retrieveEnv((JNIEnv*)&env,&alreadyAttachedToThread))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_playlistListenerClass, "synchStarted", "(I)V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalSynchStarting: could not load callback method synchStarted(int) on class jahnotify.PlaylistListener\n");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_playlistListener, method, numPlaylists);

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalSynchStarting: error during callback\n");

exit:
    if (!alreadyAttachedToThread)
    {
        // Detach from the thread at this point - since we re-attached, this is required
        result = (*g_vm)->DetachCurrentThread(g_vm);
    }
}


void* threaded_signalEndFolderSeen(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    int alreadyAttachedToThread = 0;

    if (!retrieveEnv((JNIEnv*)&env,&alreadyAttachedToThread))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_playlistListenerClass, "endFolder", "()V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalEndFolderSeen: could not load callback method endFolder() on class jahnotify.PlaylistListener\n");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_playlistListener, method);

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalEndFolderSeen: error during callback\n");

exit:
    if (!alreadyAttachedToThread)
    {
        // Detach from the thread at this point - since we re-attached, this is required
        result = (*g_vm)->DetachCurrentThread(g_vm);
    }
}


void* threaded_signalLoggedIn(void *threadarg)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    int alreadyAttachedToThread = 0;

    if (!retrieveEnv((JNIEnv*)&env,&alreadyAttachedToThread))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_connectionListenerClass, "loggedIn", "()V");

    if (method == NULL)
    {
        fprintf(stderr,"jahspotify::threaded_signalLoggedIn: could not load callback method loggedIn() on class ConnectionListener\n");
        goto fail;
    }

    fprintf(stderr,"jahspotify::threaded_signalLoggedIn: calling listener\n");

    (*env)->CallVoidMethod(env, g_connectionListener, method);

    goto exit;

fail:
    fprintf(stderr,"jahspotify::threaded_signalLoggedIn: error during callback\n");

exit:
    if (!alreadyAttachedToThread)
    {
        // Detach from the thread at this point - since we re-attached, this is required
        result = (*g_vm)->DetachCurrentThread(g_vm);
    }

}

void startPlaybackSignalled()
{
  placeInThread(threaded_startPlaybackSignalled,0);
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
    
    if (!g_playlistListener)
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
    if (!g_playlistListener)
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
    if (!g_playlistListener)
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

int signalMetadataUpdated()
{
    if (!g_playlistListener)
    {
        fprintf ( stderr, "jahspotify::signalMetadataUpdated: no playlist listener registered\n");
        return 1;
    }
    if (placeInThread(threaded_signalMetadataUpdated,NULL) != 0)
    {
      fprintf ( stderr, "jahspotify::signalMetadataUpdated: error placing onto thread\n");
    }

    return 0;
}


int signalEndFolderSeen()
{
    if (!g_playlistListener)
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

void signalTrackEnded(char *uri, bool forcedTrackEnd)
{
  char *uriCopy;
  struct threaded_signalTrackEnded_Parameters *threaded_signalTrackEnded_Params;

  if (!g_playbackListener)
  {
    fprintf ( stderr, "jahspotify::signalTrackEnded: no playback listener\n"); 
    return;
  }
  
  uriCopy = malloc(sizeof(char) * (strlen(uri)+1));
  strcpy(uriCopy,uri);
  
  threaded_signalTrackEnded_Params = malloc(sizeof(struct threaded_signalTrackEnded_Parameters));
  
  threaded_signalTrackEnded_Params->uri = uriCopy;
  threaded_signalTrackEnded_Params->forcedTrackEnd = forcedTrackEnd;
  
  threaded_signalTrackEnded(threaded_signalTrackEnded_Params);

}

void signalTrackStarted(char *uri)
{
  char *uriCopy;

  fprintf ( stderr, "jahspotify::signalTrackStarted: uri: %s\n", uri); 
  if (!g_playbackListener)
  {
    fprintf ( stderr, "jahspotify::signalTrackStarted: no playback listener\n"); 
    return;
  }
  
  uriCopy = malloc(sizeof(char) * (strlen(uri)+1));
  strcpy(uriCopy,uri);

  if (placeInThread(threaded_signalTrackStarted,uri) != 0)
  {
    fprintf ( stderr, "jahspotify::signalTrackStarted: error placing onto thread\n");
  }
  
}

int signalPlaylistSeen(const char *playlistName, char *linkName)
{
    char *linkNameCopy;
    char *playlistNameCopy;
    struct threaded_signalPlaylistSeen_Parameters *threaded_signalPlaylistSeen_Params;

    if (!g_playlistListener)
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

void signalArtistBrowseLoaded(sp_artistbrowse *artistBrowse, int32_t token)
{
}

void signalImageLoaded(sp_image *image, int32_t token)
{
  sp_image_add_ref(image);

  struct threaded_signalImageLoaded_Parameters *threaded_signalImageLoaded_Params = calloc(1,sizeof(struct threaded_signalImageLoaded_Parameters));
  
  threaded_signalImageLoaded_Params->image = image;
  threaded_signalImageLoaded_Params->token = token;

  if (placeInThread(threaded_signalImageLoaded,threaded_signalImageLoaded_Params) != 0)
  {
    fprintf ( stderr, "jahspotify::signalImageLoaded: error placing onto thread\n");
  }}

void signalAlbumBrowseLoaded(sp_albumbrowse *albumBrowse, int32_t token)
{
  
}

void signalTrackLoaded(sp_track *track, int32_t token)
{
  sp_track_add_ref(track);
  struct threaded_signalTrackLoaded_Parameters *threaded_signalTrackLoaded_Params = calloc(1,sizeof(struct threaded_signalTrackLoaded_Parameters));
  
  threaded_signalTrackLoaded_Params->track = track;
  threaded_signalTrackLoaded_Params->token = token;

  if (placeInThread(threaded_signalTrackLoaded,threaded_signalTrackLoaded_Params) != 0)
  {
    fprintf ( stderr, "jahspotify::signalTrackLoaded: error placing onto thread\n");
  }
}

void signalSearchComplete(sp_search *search, int32_t token)
{
  sp_search_add_ref(search);
  struct threaded_signalSearchComplete_Parameters *threaded_signalSearchComplete_Params = calloc(1,sizeof(struct threaded_signalSearchComplete_Parameters));
  
  threaded_signalSearchComplete_Params->search = search;
  threaded_signalSearchComplete_Params->token = token;

  if (placeInThread(threaded_signalSearchComplete,threaded_signalSearchComplete_Params) != 0)
  {
    fprintf ( stderr, "jahspotify::signalSearchComplete: error placing onto thread\n");
  }

}

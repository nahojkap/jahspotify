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
#include "Logging.h"

extern void populateJAlbumInstanceFromAlbumBrowse(JNIEnv *env, sp_album *album, sp_albumbrowse *albumBrowse,
        jobject albumInstance);
extern void populateJArtistInstanceFromArtistBrowse(JNIEnv *env, sp_artistbrowse *artistBrowse, jobject artist);
extern jobject createJLinkInstance(JNIEnv *env, sp_link *link);
extern jobject createJAlbumInstance(JNIEnv *env, sp_album *album);
extern jobject createJTrackInstance(JNIEnv *env, sp_track *track);
extern jobject createJPlaylistInstance(JNIEnv *env, sp_playlist *playlist);

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

jint addObjectToCollection(JNIEnv *env, jobject collection, jobject object)
{
    jclass clazz;
    jmethodID methodID;

    clazz = (*env)->GetObjectClass(env, collection);
    if (clazz == NULL )
        return 1;

    methodID = (*env)->GetMethodID(env, clazz, "add", "(Ljava/lang/Object;)Z");
    if (methodID == NULL )
        return 1;

    // Invoke the method
    jboolean result = (*env)->CallBooleanMethod(env, collection, methodID, object);
    if (checkException(env) != 0)
    {
        log_error("callbacks", "addObjectToCollection", "Exception while adding object to collection");
    }

}

void startPlaybackSignalled()
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    jstring nextUriStr;
    char *nextUri;

     log_debug("callbacks","startPlaybackSignalled","About to start pre-loading track");


     if (!retrieveEnv((JNIEnv*)&env))
     {
         goto fail;
     }

     method = (*env)->GetMethodID(env, g_playbackListenerClass, "nextTrackToPreload", "()Ljava/lang/String;");

     if (method == NULL)
     {
         log_error("callbacks","startPlaybackSignalled","Could not load callback method string nextTrackToPreload() on class PlaybackListener");
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

             int count = 0;
             while (!sp_track_is_loaded( track ) && count < 4)
             {
                 usleep(250);
                 count ++;
             }

             if (count == 4)
             {
                 log_warn("jahspotify","startPlaybackSignalled","Waited for track to load but took too long" );
                 goto exit;
             }

             sp_error error = sp_session_player_prefetch(g_sess,track);
             // sp_track_release(track);
             if (error != SP_ERROR_OK)
             {
                 log_error("callbacks","startPlaybackSignalled","Error prefetch: %s",sp_error_message(error));
                 goto fail;
             }
         }
     }

     goto exit;

     fail:
     log_error("callbacks","startPlaybackSignalled","Error during callback");

     exit:

     if (nextUri)
     {
         (*env)->ReleaseStringUTFChars(env, nextUriStr,nextUri);
     }

}

int signalConnected()
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;

    if (!g_connectionListener)
    {
        log_error("jahspotify", "signalConnected", "No connection listener registered");
        return 1;
    }

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_connectionListenerClass, "connected", "()V");

    if (method == NULL )
    {
        log_error("callbacks", "signalConnected",
                "Could not load callback method connected() on class ConnectionListener");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_connectionListener, method);
    checkException(env);

    goto exit;

    fail: log_error("callbacks", "signalConnected", "Error during callback");

    exit: detachThread();

}

int signalDisconnected()
{
}

int signalLoggedOut()
{
}

int signalLoggedIn()
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;

    if (!g_connectionListener)
    {
        log_error("jahspotify", "signalLoggedIn", "No connection listener registered");
        return 1;
    }

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_connectionListenerClass, "loggedIn", "()V");

    if (method == NULL )
    {
        log_error("callbacks", "signalLoggedIn",
                "Could not load callback method loggedIn() on class ConnectionListener");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_connectionListener, method);
    if (checkException(env) != 0)
    {
        log_error("callbacks", "signalLoggedIn", "Exception while calling listener");
        goto fail;
    }

    goto exit;

    fail: log_error("callbacks", "signalLoggedIn", "Error during callback");

    exit: detachThread();

}

int signalStartFolderSeen(char *folderName, uint64_t folderId, int index)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    jstring folderNameStr;

    if (!g_libraryListener)
    {
        log_error("jahspotify", "signalStartFolderSeen", "No playlist listener registered");
        return 1;
    }

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    if (folderName)
    {
        folderNameStr = (*env)->NewStringUTF(env, folderName);
        if (folderNameStr == NULL )
        {
            log_error("callbacks", "signalStartFolderSeen", "Error creating java string");
            goto fail;
        }
    }

    method = (*env)->GetMethodID(env, g_libraryListenerClass, "startFolder", "(Ljava/lang/String;JI)V");

    if (method == NULL )
    {
        log_error("callbacks", "signalStartFolderSeen",
                "Could not load callback method startFolder(string) on class jahnotify.PlaylistListener");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_libraryListener, method, folderNameStr, folderId, index);
    if (checkException(env) != 0)
    {
        log_error("callbacks", "signalStartFolderSeen", "Exception while calling callback");
        goto fail;
    }

    goto exit;

    fail: log_error("callbacks", "signalStartFolderSeen", "Error during callback");

    exit: if (folderNameStr)
        (*env)->DeleteLocalRef(env, folderNameStr);

    result = detachThread();

}

int signalSynchStarting(int numPlaylists)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;

    if (!g_libraryListener)
    {
        log_error("jahspotify", "signalSynchStarting", "No playlist listener registered");
        return 1;
    }

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_libraryListenerClass, "synchStarted", "(I)V");

    if (method == NULL )
    {
        log_error("callbacks", "signalSynchStarting",
                "Could not load callback method synchStarted(int) on class jahnotify.PlaylistListener");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_libraryListener, method, numPlaylists);
    checkException(env);

    goto exit;

    fail: log_error("callbacks", "signalSynchStarting", "Error during callback");

    exit:

    result = detachThread();
}

int signalSynchCompleted()
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;

    if (!g_libraryListener)
    {
        log_error("jahspotify", "signalSynchCompleted", "No playlist listener registered");
        return 1;
    }

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_libraryListenerClass, "synchCompleted", "()V");

    if (method == NULL )
    {
        log_error("callbacks", "signalSynchCompleted",
                "Could not load callback method synchCompleted() on class jahnotify.PlaylistListener");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_libraryListener, method);
    checkException(env);

    goto exit;

    fail:

    log_error("callbacks", "signalSynchCompleted", "Error during callback");

    exit:

    result = detachThread();
}

int signalMetadataUpdated(sp_playlist *playlist)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    sp_link *playlistLink = NULL;

    char *linkStr = NULL;
    jstring playlistLinkStr;

    if (!g_libraryListener)
    {
        log_error("callbacks", "signalMetadataUpdated", "No playlist listener registered");
        return 1;
    }

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    log_debug("callbacks", "signalMetadataUpdated", "Metadata updated for %s", sp_playlist_name(playlist));

    playlistLink = sp_link_create_from_playlist(playlist);
    if (playlistLink)
    {
        linkStr = calloc(1, sizeof(char) * 100);
        sp_link_as_string(playlistLink, linkStr, 100);
    }

    if (linkStr)
    {
        log_debug("callbacks", "signalMetadataUpdated", "Metadata updated for %s", linkStr);
        playlistLinkStr = (*env)->NewStringUTF(env, linkStr);
        free(linkStr);
        if (playlistLinkStr == NULL )
        {
            log_error("callbacks", "signalMetadataUpdated", "Error creating java string");
            goto fail;
        }

        method = (*env)->GetMethodID(env, g_libraryListenerClass, "metadataUpdated", "(Ljava/lang/String;)V");

        if (method == NULL )
        {
            log_error("callbacks", "signalMetadataUpdated",
                    "Could not load callback method metadataUpdated() on class NativeLibraryListener");
            goto fail;
        }

        (*env)->CallVoidMethod(env, g_libraryListener, method, playlistLinkStr);
        if (checkException(env) != 0)
        {
            log_error("callbacks", "signalMetadataUpdated", "Exception while calling callback");
        }
        goto exit;
    }

    fail:
    
    log_error("callbacks", "signalMetadataUpdated", "Error during callback");

    exit:

    result = detachThread();

}

int signalEndFolderSeen(int index)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;

    if (!g_libraryListener)
    {
        log_error("jahspotify", "signalEndFolderSeen", "No playlist listener registered");
        return 1;
    }

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_libraryListenerClass, "endFolder", "(I)V");

    if (method == NULL )
    {
        log_error("callbacks", "signalEndFolderSeen",
                "Could not load callback method endFolder() on class jahnotify.PlaylistListener");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_libraryListener, method,index);
    checkException(env);

    goto exit;

    fail: log_error("callbacks", "signalEndFolderSeen", "Error during callback");

    exit:

    result = detachThread();
}

int signalTrackEnded(char *uri, bool forcedTrackEnd)
{
    if (!g_playbackListener)
    {
        log_error("jahspotify", "signalTrackEnded", "No playback listener");
        return 1;
    }

    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    jstring uriStr;

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    if (uri)
    {
        uriStr = (*env)->NewStringUTF(env, uri);
        if (uriStr == NULL )
        {
            log_error("callbacks", "signalTrackEnded", "Error creating java string");
            goto fail;
        }
    }

    method = (*env)->GetMethodID(env, g_playbackListenerClass, "trackEnded", "(Ljava/lang/String;Z)V");

    if (method == NULL )
    {
        log_error("callbacks", "signalTrackEnded",
                "Could not load callback method trackEnded(string) on class jahnotify.PlaybackListener");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_playbackListener, method, uriStr, forcedTrackEnd);
    if (checkException(env) != 0)
    {
        log_error("callbacks", "signalTrackEnded", "Exception while calling callback");
        goto fail;
    }

    goto exit;

    fail: log_error("callbacks", "signalTrackEnded", "Error during callback\n");

    exit: if (uriStr)
        (*env)->DeleteLocalRef(env, uriStr);

    result = detachThread();

}

int signalTrackStarted(char *uri)
{
    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID method;
    jstring uriStr;

    log_debug("callbacks", "signalTrackStarted", "URI: %s", uri);
    if (!g_playbackListener)
    {
        log_error("callbacks", "signalTrackStarted", "No playback listener");
        return 1;
    }

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    if (uri)
    {
        uriStr = (*env)->NewStringUTF(env, uri);
        if (uriStr == NULL )
        {
            log_error("callbacks", "signalTrackStarted", "Error creating java string");
            goto fail;
        }
    }

    method = (*env)->GetMethodID(env, g_playbackListenerClass, "trackStarted", "(Ljava/lang/String;)V");

    if (method == NULL )
    {
        log_error("callbacks", "signalTrackStarted",
                "Could not load callback method trackStarted(string) on class jahnotify.PlaybackListener");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_playbackListener, method, uriStr);
    checkException(env);

    goto exit;

    fail: log_error("callbacks", "signalTrackStarted", "Error during callback");

    exit: if (uriStr)
        (*env)->DeleteLocalRef(env, uriStr);

    result = detachThread();
}

int signalPlaylistSeen(const char *playlistName, char *linkName, int index)
{
    log_debug("callbacks", "signalPlaylistSeen", "Playlist seen: name: %s link: %s", playlistName, linkName);

    if (!g_libraryListener)
    {
        log_error("jahspotify", "signalPlaylistSeen", "No playlist listener registered");
        return 1;
    }

    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID aMethod;

    jstring playListStr;
    jstring linkNameStr;

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    aMethod = (*env)->GetMethodID(env, g_libraryListenerClass, "playlist", "(Ljava/lang/String;Ljava/lang/String;I)V");

    if (aMethod == NULL )
    {
        log_error("callbacks", "signalPlaylistSeen",
                "Could not load callback method playlist(string,string,int) on class NativeLibraryListener");
        goto fail;
    }

    if (linkName)
    {
        linkNameStr = (*env)->NewStringUTF(env, linkName);
        if (linkNameStr == NULL )
        {
            log_error("callbacks", "signalPlaylistSeen", "Error creating java string");
            goto fail;
        }
    }

    if (playlistName)
    {
        playListStr = (*env)->NewStringUTF(env, playlistName);
        if (playListStr == NULL )
        {
            log_error("callbacks", "signalPlaylistSeen", "Error creating java string");
            goto fail;
        }
    }

    (*env)->CallVoidMethod(env, g_libraryListener, aMethod, playListStr, linkNameStr,index);
    if (checkException(env) != 0)
    {
        log_error("callbacks", "signalPlaylistSeen", "Exception while calling callback");
        goto fail;
    }

    goto exit;

    fail: log_error("callbacks", "signalPlaylistSeen", "Error during callback");

    exit: if (linkNameStr)
        (*env)->DeleteLocalRef(env, linkNameStr);
    if (playListStr)
        (*env)->DeleteLocalRef(env, playListStr);

    result = detachThread();

}


int signalPlaylistUpdate(const char *playlistName, char *linkName, bool complete)
{
    log_debug("callbacks", "signalPlaylistUpdate", "Playlist seen: name: %s link: %s", playlistName, linkName);

    if (!g_libraryListener)
    {
        log_error("jahspotify", "signalPlaylistUpdate", "No playlist listener registered");
        return 1;
    }

    JNIEnv* env = NULL;
    int result;
    jclass aClass;
    jmethodID aMethod;

    jstring playListStr = NULL;
    jstring linkNameStr = NULL;

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    aMethod = (*env)->GetMethodID(env, g_libraryListenerClass, "playlist", "(Ljava/lang/String;Ljava/lang/String;I)V");

    if (aMethod == NULL )
    {
        log_error("callbacks", "signalPlaylistUpdate",
                "Could not load callback method playlist(string, string, int) on class NativeLibraryListener");
        goto fail;
    }

    if (linkName)
    {
        linkNameStr = (*env)->NewStringUTF(env, linkName);
        if (linkNameStr == NULL )
        {
            log_error("callbacks", "signalPlaylistSeen", "Error creating java string");
            goto fail;
        }
    }

    if (playlistName)
    {
        playListStr = (*env)->NewStringUTF(env, playlistName);
        if (playListStr == NULL )
        {
            log_error("callbacks", "signalPlaylistUpdate", "Error creating java string");
            goto fail;
        }
    }

    (*env)->CallVoidMethod(env, g_libraryListener, aMethod, playListStr, linkNameStr, 0);
    if (checkException(env) != 0)
    {
        log_error("callbacks", "signalPlaylistUpdate", "Exception while calling callback");
        goto fail;
    }

    goto exit;

    fail: log_error("callbacks", "signalPlaylistUpdate", "Error during callback");

    exit:

    if (linkNameStr)
        (*env)->DeleteLocalRef(env, linkNameStr);
    if (playListStr)
        (*env)->DeleteLocalRef(env, playListStr);

    result = detachThread();

}

int signalArtistBrowseLoaded(sp_artistbrowse *artistBrowse, int32_t token)
{
    JNIEnv* env = NULL;
    jclass aClass;
    jmethodID aMethod;

    sp_link *artistLink = NULL;
    jobject artistInstance = NULL;
    jclass jClass;

    log_debug("jahspotify", "signalArtistBrowseLoaded", "Artist browse loaded");

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    jClass = (*env)->FindClass(env, "jahspotify/media/Artist");
    if (jClass == NULL )
    {
        log_error("jahspotify", "signalArtistBrowseLoaded", "Could not load jahnotify.media.Artist");
        goto fail;
    }

    artistInstance = createInstanceFromJClass(env, jClass);

    if (!g_mediaLoadedListener)
    {
        log_error("jahspotify", "signalArtistBrowseLoaded", "No playlist media loaded listener registered");
        goto fail;
    }

    aMethod = (*env)->GetMethodID(env, g_mediaLoadedListenerClass, "artist", "(ILjahspotify/media/Artist;)V");

    if (aMethod == NULL )
    {
        log_error("callbacks", "signalArtistBrowseLoaded",
                "Could not load callback method artist(int,artist) on class NativeMediaLoadedListener");
        goto fail;
    }

    sp_artist *artist = sp_artistbrowse_artist(artistBrowse);
    if (!artist)
    {
        log_error("callbacks", "signalArtistBrowseLoaded", "Could not load artist from ArtistBrowse");
        goto fail;
    }

    sp_artist_add_ref(artist);

    artistLink = sp_link_create_from_artist(artist);

    sp_link_add_ref(artistLink);

    jobject artistJLink = createJLinkInstance(env, artistLink);

    setObjectObjectField(env, artistInstance, "id", "Ljahspotify/media/Link;", artistJLink);

    sp_link_release(artistLink);

    setObjectStringField(env, artistInstance, "name", sp_artist_name(artist));

    sp_artist_release(artist);

    // Convert the instance to an artist
    // Pass it up in the callback
    populateJArtistInstanceFromArtistBrowse(env, artistBrowse, artistInstance);

    (*env)->CallVoidMethod(env, g_mediaLoadedListener, aMethod, token, artistInstance);
    if (checkException(env) != 0)
    {
        log_error("callbacks", "signalArtistBrowseLoaded", "Exception while calling callback");
        goto fail;
    }

    goto exit;

    fail: log_error("jahspotify", "signalArtistBrowseLoaded", "Error occurred while processing callback");

    exit:
    if (artistBrowse)
    {
        sp_artistbrowse_release(artistBrowse);
    }
    detachThread();

}

int signalImageLoaded(sp_image *image, int32_t token)
{

    if (!g_mediaLoadedListener)
    {
        log_error("jahspotify", "signalImageLoaded", "No playlist media loaded listener registered");
        return 1;
    }

    JNIEnv* env = NULL;
    jmethodID method;

    log_debug("callbacks", "signalImageLoaded", "Image loaded: token: %d\n", token);

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_mediaLoadedListenerClass, "image",
            "(ILjahspotify/media/Link;Ljahspotify/media/ImageSize;[B)V");

    if (method == NULL )
    {
        log_error("callbacks", "signalImageLoaded",
                "Could not load callback method image(Link) on class NativeMediaLoadedListener");
        goto fail;
    }

    sp_link *link = sp_link_create_from_image(image);

    sp_link_add_ref(link);

    jobject jLink = createJLinkInstance(env, link);

    sp_link_release(link);

    (*env)->CallVoidMethod(env, g_mediaLoadedListener, method, token, jLink, NULL, NULL );
    if (checkException(env) != 0)
    {
        log_error("callbacks", "signalImageLoaded", "Exception while calling listener");
        goto fail;
    }

    log_debug("callbacks", "signalImageLoaded", "Callback invokved");

    goto exit;

    fail:

    exit:

    sp_image_release(image);
    detachThread();

}

int signalPlaylistLoaded(sp_playlist *playlist, int32_t token)
{
    if (!g_mediaLoadedListener)
    {
        log_error("jahspotify", "signalPlaylistLoaded", "No playlist media loaded listener registered");
        return 1;
    }

    sp_playlist_add_ref(playlist);

    JNIEnv* env = NULL;
    jmethodID method;

    log_debug("jahspotify", "signalPlaylistLoaded", "Playlist loaded: token: %d", token);

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    method = (*env)->GetMethodID(env, g_mediaLoadedListenerClass, "playlist", "(ILjahspotify/media/Link;)V");

    if (method == NULL )
    {
        log_error("callbacks", "signalPlaylistLoaded",
                "Could not load callback method playlist(Link) on class NativeMediaLoadedListener");
        goto fail;
    }

    sp_link *link = sp_link_create_from_playlist(playlist);

    sp_link_add_ref(link);

    jobject jLink = createJLinkInstance(env, link);

    sp_link_release(link);

    (*env)->CallVoidMethod(env, g_mediaLoadedListener, method, token, jLink);
    if (checkException(env) != 0)
    {
        log_error("callbacks", "signalPlaylistLoaded", "exception while calling listener");
        goto fail;
    }

    log_debug("callbacks", "signalPlaylistLoaded", "Callback invokved");

    goto exit;

    fail:

    exit:

    sp_playlist_release(playlist);
    detachThread();

}

int signalAlbumBrowseLoaded(sp_albumbrowse *albumBrowse, int32_t token)
{
    JNIEnv* env = NULL;
    jclass aClass;
    jmethodID aMethod;

    sp_album *album = NULL;
    sp_link *albumLink = NULL;
    jobject albumInstance = NULL;
    jclass jClass;

    if (!g_mediaLoadedListener)
    {
        log_error("jahspotify", "signalAlbumBrowseLoaded", "No album media loaded listener registered");
        goto fail;
    }

    log_debug("jahspotify", "signalAlbumBrowseLoaded", "Albumbrowse loaded");

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    jClass = (*env)->FindClass(env, "jahspotify/media/Album");
    if (jClass == NULL )
    {
        log_error("jahspotify", "signalAlbumBrowseLoaded", "Could not load jahnotify.media.Album");
        goto fail;
    }

    albumInstance = createInstanceFromJClass(env, jClass);

    aMethod = (*env)->GetMethodID(env, g_mediaLoadedListenerClass, "album", "(ILjahspotify/media/Album;)V");

    if (aMethod == NULL )
    {
        log_error("callbacks", "signalAlbumBrowseLoaded",
                "Could not load callback method album(int,album) on class NativeMediaLoadedListener");
        goto fail;
    }

    album = sp_albumbrowse_album(albumBrowse);

    if (!album)
    {
        log_error("callbacks", "signalAlbumBrowseLoaded", "Could not load album from AlbumBrowse");
        goto fail;
    }
    sp_album_add_ref(album);

    albumLink = sp_link_create_from_album(album);

    sp_link_add_ref(albumLink);

    jobject albumJLink = createJLinkInstance(env, albumLink);

    setObjectObjectField(env, albumInstance, "id", "Ljahspotify/media/Link;", albumJLink);

    setObjectStringField(env, albumInstance, "name", sp_album_name(album));

    // Convert the instance to an artist
    // Pass it up in the callback
    populateJAlbumInstanceFromAlbumBrowse(env, album, albumBrowse, albumInstance);

    (*env)->CallVoidMethod(env, g_mediaLoadedListener, aMethod, token, albumInstance);
    if (checkException(env) != 0)
    {
        log_error("callbacks", "signalAlbumBrowseLoaded", "Exception while calling callback");
        goto fail;
    }

    goto exit;

    fail:

    exit:

    if (albumLink)
    {
        sp_link_release(albumLink);
    }

    if (album)
    {
        sp_album_release(album);
    }
    if (albumBrowse)
    {
        sp_albumbrowse_release(albumBrowse);
    }
    detachThread();

}

// int signalTrackLoaded(sp_track *track, int32_t token)
// {
//   if (!g_mediaLoadedListener)
//   {
//       log_error("jahspotify","signalTrackLoaded","No playlist media loaded listener registered");
//       return 1;
//   }
//   
//   JNIEnv* env = NULL;
//   jmethodID method;
//   
//   log_debug("callbacks","signalTrackLoaded","Track loaded: token: %d", token);
//   
//   if (!retrieveEnv((JNIEnv*)&env))
//   {
//       goto fail;
//   }
//   
//   method = (*env)->GetMethodID(env, g_mediaLoadedListenerClass, "track", "(ILjahspotify/media/Link;)V");
//   
//   if (method == NULL)
//   {
//       log_error("callbacks","signalTrackLoaded","Could not load callback method track(Link) on class NativeMediaLoadedListener");
//       goto fail;
//   }
//   
//   sp_link *link = sp_link_create_from_track(track,0);
//   
//   sp_link_add_ref(link);
//   
//   jobject jLink = createJLinkInstance(env,link);
//   
//   sp_link_release(link);
//   
//   (*env)->CallVoidMethod(env,g_mediaLoadedListener,method,token,jLink);
//   if (checkException(env) != 0)
//   {
//       log_error("callbacks","signalTrackLoaded","Exception while calling listener");
//       goto fail;
//   }
//   
//   log_debug("callbacks","signalTrackLoaded","Callback invokved");
//   goto exit;
//   
//   fail:
//   
//   exit:
//   
//   sp_track_release(track);
// }

int signalSearchComplete(sp_search *search, int32_t token)
{
    if (!g_searchCompleteListener)
    {
        log_error("jahspotify", "signalSearchComplete", "No playlist media loaded listener registered");
        return 1;
    }

    sp_search_add_ref(search);
    JNIEnv* env = NULL;
    jmethodID method;
    jobject jLink;
    jobject nativeSearchResult;
    jobject trackLinkCollection;
    jobject albumLinkCollection;
    jobject artistLinkCollection;
    int numResultsFound = 0;
    int index = 0;

    log_debug("jahspotify", "signalSearchComplete", "Search complete: token: %d", token);

    if (!retrieveEnv((JNIEnv*) &env))
    {
        goto fail;
    }

    // Create the Native Search Result instance
    nativeSearchResult = createInstanceFromJClass(env, g_nativeSearchResultClass);

    trackLinkCollection = createInstance(env, "java/util/ArrayList");
    setObjectObjectField(env, nativeSearchResult, "tracksFound", "Ljava/util/List;", trackLinkCollection);

    numResultsFound = sp_search_num_tracks(search);
    for (index = 0; index < numResultsFound; index++)
    {
        sp_track *track = sp_search_track(search, index);
        if (track)
        {
            sp_track_add_ref(track);

            if (sp_track_is_loaded(track))
            {
                sp_link *link = sp_link_create_from_track(track, 0);
                if (link)
                {
                    sp_link_add_ref(link);
                    jLink = createJLinkInstance(env, link);
                    addObjectToCollection(env, trackLinkCollection, jLink);
                    sp_link_release(link);
                }
            } else
            {
                log_error("jahspotify", "signalSearchComplete", "Track not loaded");
            }

            sp_track_release(track);

        }
    }

    albumLinkCollection = createInstance(env, "java/util/ArrayList");
    setObjectObjectField(env, nativeSearchResult, "albumsFound", "Ljava/util/List;", albumLinkCollection);

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
                    addObjectToCollection(env, albumLinkCollection, jLink);
                    sp_link_release(link);
                }
            } else
            {
                log_error("jahspotify", "signalSearchComplete", "Album not loaded");
            }

            sp_album_release(album);

        }
    }

    artistLinkCollection = createInstance(env, "java/util/ArrayList");
    setObjectObjectField(env, nativeSearchResult, "artistsFound", "Ljava/util/List;", artistLinkCollection);

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
                    addObjectToCollection(env, artistLinkCollection, jLink);
                    sp_link_release(link);
                }
            } else
            {
                log_error("jahspotify", "signalSearchComplete", "Artist not loaded");
            }

            sp_artist_release(artist);

        }
    }

    setObjectIntField(env, nativeSearchResult, "totalNumTracks", sp_search_total_tracks(search));
    setObjectIntField(env, nativeSearchResult, "trackOffset", sp_search_num_tracks(search));

    setObjectIntField(env, nativeSearchResult, "totalNumAlbums", sp_search_total_albums(search));
    setObjectIntField(env, nativeSearchResult, "albumOffset", sp_search_num_albums(search));

    setObjectIntField(env, nativeSearchResult, "totalNumArtists", sp_search_total_artists(search));
    setObjectIntField(env, nativeSearchResult, "artistOffset", sp_search_num_artists(search));

    setObjectStringField(env, nativeSearchResult, "query", sp_search_query(search));
    setObjectStringField(env, nativeSearchResult, "didYouMean", sp_search_did_you_mean(search));

    method = (*env)->GetMethodID(env, g_searchCompleteListenerClass, "searchCompleted",
            "(ILjahspotify/SearchResult;)V");

    if (method == NULL )
    {
        log_error("jahspotify", "signalSearchComplete",
                "Could not load callback method searchCompleted() on class SearchListener");
        goto fail;
    }

    (*env)->CallVoidMethod(env, g_searchCompleteListener, method, token, nativeSearchResult);
    if (checkException(env) != 0)
    {
        log_error("jahspotify", "signalSearchComplete", "Exception while calling search complete listener");
        goto fail;
    }

    goto exit;

    fail:

    exit: sp_search_release(search);
    detachThread();

}

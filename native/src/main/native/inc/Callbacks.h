#ifndef JAHSPOTIFY_CALLBACKS

#define JAHSPOTIFY_CALLBACKS

void* threaded_startPlaybackSignalled(void* threadargs);
void* threaded_signalPlaylistSeen(void *threadarg);
void* threaded_signalTrackEnded(void *threadarg);
void* threaded_signalTrackStarted(void *threadarg);
void* threaded_signalStartFolderSeen(void *threadarg);
void* threaded_signalSynchCompleted(void *threadarg);
void* threaded_signalSynchStarting(void *threadarg);
void* threaded_signalEndFolderSeen(void *threadarg);
void* threaded_signalLoggedIn(void *threadarg);

void startPlaybackSignalled();
int signalLoggedIn();
int signalStartFolderSeen(char *folderName, uint64_t folderId);
int signalSynchStarting(int numPlaylists);
int signalSynchCompleted();
int signalEndFolderSeen();
void signalTrackEnded(char *uri, bool forcedTrackEnd);
void signalTrackStarted(char *uri);
int signalPlaylistSeen(const char *playlistName, char *linkName);

#endif
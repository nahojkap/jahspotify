#ifndef JAHSPOTIFY_CALLBACKS

#define JAHSPOTIFY_CALLBACKS

#include <libspotify/api.h>

void startPlaybackSignalled();
int signalLoggedIn();
int signalStartFolderSeen(char *folderName, uint64_t folderId);
int signalSynchStarting(int numPlaylists);
int signalSynchCompleted();
int signalMetadataUpdated();
int signalEndFolderSeen();

void signalTrackEnded(char *uri, bool forcedTrackEnd);
void signalTrackStarted(char *uri);
int signalPlaylistSeen(const char *playlistName, char *linkName);

void signalSearchComplete(sp_search *search, int32_t token);
void signalImageLoaded(sp_image *image, void *userData);
void signalTrackLoaded(sp_track *track, void *userData);
void signalAlbumBrowseLoaded(sp_albumbrowse *albumBrowse, void *userData);
void signalArtistBrowseLoaded(sp_artistbrowse *artistBrowse, void *userData);

#endif
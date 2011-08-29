#ifndef JAHSPOTIFY_CALLBACKS

#define JAHSPOTIFY_CALLBACKS

#include <libspotify/api.h>

void startPlaybackSignalled();
int signalLoggedIn();
int signalConnected();
int signalDisconnected();
int signalLoggedOut();

int signalStartFolderSeen(char *folderName, uint64_t folderId);
int signalSynchStarting(int numPlaylists);
int signalSynchCompleted();
int signalMetadataUpdated(sp_playlist *playlist);
int signalEndFolderSeen();

int signalTrackEnded(char *uri, bool forcedTrackEnd);
int signalTrackStarted(char *uri);
int signalPlaylistSeen(const char *playlistName, char *linkName);

int signalSearchComplete(sp_search *search, int32_t token);
int signalImageLoaded(sp_image *image, int32_t token);
int signalTrackLoaded(sp_track *track, int32_t token);
int signalPlaylistLoaded(sp_playlist *track, int32_t token);
int signalAlbumBrowseLoaded(sp_albumbrowse *albumBrowse, int32_t token);
int signalArtistBrowseLoaded(sp_artistbrowse *artistBrowse, int32_t token);

#endif
#ifndef JAHSPOTIFY_CALLBACKS

#define JAHSPOTIFY_CALLBACKS

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

void signalImageLoaded(sp_image *image);
void signalTrackLoaded(sp_track *track);
void signalAlbumBrowseLoaded(sp_album_browse *albumBrowse);
void signalArtistBrowseLoaded(sp_artist_browse *artistBrowse);

#endif
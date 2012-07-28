===============================================================================
Jah'Spotify Project
===============================================================================

## Introduction

Jah'Spotify is a Java wrapper built on top of the Spotify native APIs (libspotify)

Currently supports:

* retrieving library (all user playlists/folders)
* retrieve a playlist
* retrieve a folder (including/excluding sub-folders and playlists)
* retrieve an album
* retrieve a track
* retrieve an image
* add tracks to a queue (single queue currently supported)
* play tracks
* pause/skip functions
* dynamic playlist using echonest apis
* basic historical track list view
* very basic android app for remote control

In addition, all the above functions are accessible over a JSON based RESTful API (provided by the services and web
modules).

## To build

Jah'Spotify supports the Linux and Windows versions of libspotify (see below for more details on building on Windows).

To build the sources first check them out from git

    git clone git://github.com/johanlindquist/jahspotify.git
    cd jahspotify

Next, you need to download and install libspotify & request an API key from Spotify.  This can be done
on the http://developer.spotify.com website.

Generate the key and download the C code version of it.  Place this in a file called AppKey.h in the

    native/src/main/native/inc

directory.  You may want to place some #ifndef APPKEY statements in this to prevent linking problems.

Finally, execute the Maven build

    mvn clean install

### Building on Windows

#### Before compiling

1. Download MinGW and put the bin folder in your PATH.
2. Download the OpenAL SDK from and copy the include folder to the MinGW include folder and rename it to AL
3. The native pom.xml has a reference to a local LibSpotify folder. Change this to your own Spotify folder. You'll get an error that the api.h file can't be found if you don't.
4. Add a reference to your OpenAL installation folder

You can find the OpenAL SDK at:

    http://connect.creativelabs.com/developer/Wiki/OpenAL%20SDK%20for%20Windows.aspx

## Running

Download and install MongoDB

### Running on Windows

For windows, you will need to download a few more dependencies:

- pthread (http://sources.redhat.com/pthreads-win32/). pthreadGC2.dll needs to be in your path.
- I didn't have to add any paths to OpenAL but I installed the SDK. Let me know if you get a message complaining that not all dependencies are available.

## Modules

* android

  provides a simple app for browsing playlists and queueing tracks

* api

  provides the basic operations for interacting with Jah'Spotify (and in turn libspotify)

* native

  contains all native & JNI code interacting with libspotify

* native-jar

  contains wrapper code to load libjahspotify from a jar

* services

  provides all Jah'Spotify Spring services

* web

  provides the RESTful API (json based)

* web-client

  provides java client for interacting with the RESTful API

* web-common

  provides the java beans which are serialized over the RESTful API

* storage

  provides basic storage implementations for caching media objects (tracks/images/etc)

* executable-war

  provides a single, executable Jah'Spotify war file

## UI

TBD

## REST API

To run up the Jah'Spotify RESTful API

    mvn jetty:run -Djahspotify.spotify.username=<your username> -Djahspotify.spotify.password=<your password>

NOTE: The username and password can also be specified in your Maven settings.xml

[http://localhost:8080/jahspotify/system/status](http://localhost:8080/jahspotify/system/status)

All media can be retrieved using the Media Controller URL

    http://localhost:8080/jahspotify/media/<URI>

Where the URI is any of the URIs specified below, in the more specialized controllers:

### URLs

__Library retrieval__

[http://localhost:8080/jahspotify/library/](http://localhost:8080/jahspotify/library/)

__Folder retrieval__

[http://localhost:8080/jahspotify/folder/jahspotify:folder:9594c66fa67e43ca](http://localhost:8080/jahspotify/folder/jahspotify:folder:9594c66fa67e43ca)

__Playlist retrieval__

[http://localhost:8080/jahspotify/playlist/spotify:user:dummy-user:playlist:0s8KIfDTmZz5zupnkqF6FO](http://localhost:8080/jahspotify/playlist/spotify:user:dummy-user:playlist:0s8KIfDTmZz5zupnkqF6FO)

__Album retrieval__

[http://localhost:8080/jahspotify/album/spotify:album:3PogVmhNucYNfyywZvTd7F](http://localhost:8080/jahspotify/album/spotify:album:3PogVmhNucYNfyywZvTd7F)

__Artist retrieval__

[http://localhost:8080/jahspotify/artist/spotify:artist:7dGJo4pcD2V6oG8kP0tJRR](http://localhost:8080/jahspotify/artist/spotify:artist:7dGJo4pcD2V6oG8kP0tJRR)

__Track retrieval__

[http://localhost:8080/jahspotify/track/spotify:track:7mliwEVqxIuwLmHdTXlBrx](http://localhost:8080/jahspotify/track/spotify:track:7mliwEVqxIuwLmHdTXlBrx)

__Image retrieval__

[http://localhost:8080/jahspotify/image/spotify:image:e99e74261d120029fecfde36ab1c07a0eb99e54d](http://localhost:8080/jahspotify/image/spotify:image:e99e74261d120029fecfde36ab1c07a0eb99e54d)

__Adding a track to the play queue__

[http://localhost:8080/jahspotify/queue/jahspotify:queue:default/add/spotify:track:2eEUnqeLUjxkefHrIgqgAd](http://localhost:8080/jahspotify/queue/jahspotify:queue:default/add/spotify:track:2eEUnqeLUjxkefHrIgqgAd)

__Retrieving play queue__

[http://localhost:8080/jahspotify/queue/jahspotify:queue:default](http://localhost:8080/jahspotify/queue/jahspotify:queue:default)

__Searching__

[http://localhost:8080/jahspotify/search/?query=alika&numTracks=1](http://localhost:8080/jahspotify/search/?query=alika&numTracks=1)

#### While playing:

__Skip to next track in queue__

[http://localhost:8080/jahspotify/player/skip](http://localhost:8080/jahspotify/player/skip)

__Pause playback__

[http://localhost:8080/jahspotify/player/pause](http://localhost:8080/jahspotify/player/pause)

__Resume play__

[http://localhost:8080/jahspotify/player/resume](http://localhost:8080/jahspotify/player/resume)

__Stop play__

[http://localhost:8080/jahspotify/player/stop](http://localhost:8080/jahspotify/player/stop)

__Play__

[http://localhost:8080/jahspotify/player/play](http://localhost:8080/jahspotify/player/play)

__Seek__

[http://localhost:8080/jahspotify/player/seek?offset=43](http://localhost:8080/jahspotify/player/seek?offset=43)

__Volume Up__

[http://localhost:8080/jahspotify/player/volume-up](http://localhost:8080/jahspotify/player/volume-up)

__Volume Down__

[http://localhost:8080/jahspotify/player/volume-down](http://localhost:8080/jahspotify/player/volume-down)

There are other URLs - please examine the web module for them.

## Discussions and other:

For now, there is nothing concrete setup but try the issues page on github, contacting us on github.  Also found at times
on freenode - #jahspotify.

## Licensing

All Jah'Spotify code is released under the Apache 2.0 license

## Contributors

Niels vd Weem

Thanks!

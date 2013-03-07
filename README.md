===============================================================================
Jah'Spotify Project
===============================================================================

## Introduction

Jah'Spotify is a Java wrapper built on top of the Spotify native APIs (libspotify) exposed both as an HTML5 UI and a JSON REST based API

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
* JSON reset based API
* web-ui based on JQuery Mobile

In addition, all the above functions are accessible over a JSON based RESTful API (provided by the services and web
modules).

Note: Currently, JahSpotify relies on existing playlists created from Spotify - there is currently no option to add
playlists/folders using the API or UI - it is being worked on ;)

## To build

Jah'Spotify supports the Linux and Windows versions of libspotify (see below for more details on building on Windows).

### Prerequisites

The following prerequisites are required in order to build Jah'Spotify

* libspotify
* Spotify API key
* OpenAL

### Checking out the sources

To build the sources first check them out from git

    git clone git://github.com/johanlindquist/jahspotify.git
    cd jahspotify

### Preparing Spotify API key and libraries

Next, you need to download and install libspotify & request an API key from Spotify.  This can be done
on the [libspotify](https://developer.spotify.com/technologies/libspotify/) website or via your OS package provider (Homebrew for OS X for example)

Generate the key and download the C code version of it.  Place this in a file called AppKey.h in the

    native/src/main/native/inc

directory.  You may want to place some #ifndef APPKEY statements in this to prevent linking problems.

### Performing the build

Ensure that your JAVA_HOME environment variable points to the JDK (v1.6.x) you want to use.

    export JAVA_HOME=<path/to/your/jdk>

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

Next, define the following environment properties:

    JAHSPOTIFY_SPOTIFY_DIR=c:\path\to\spotify\installation\LibSpotify\
    JAHSPOTIFY_OPENAL_DIR=c:\path\to\openal\installation\OpenAL 1.1 SDK\

#### Performing the build

To build, simply execute maven build

    mvn clean install

### Building on OX X

Depending on your

## Running

Jah'Spotity will run both from within Maven (using Jetty) and in a web container such as Tomcat.

### Prerequisites

Jah'Spotify uses MongoDB for storing the historical statistics and tracks played.  This can be downloaded from [MongoDB](http://www.mongodb.org/downloads).  As long as MongoDB is running when Jah'Spotify is started (and the connection does not require credentials), it should work.

### Configuration

TDB

### Running on Windows

For windows, you will need to download a few more dependencies:

- pthreads (http://sources.redhat.com/pthreads-win32/). pthreadGC2.dll needs to be in your path.
- Install the OpenAL SDK for Windows

### Using Maven/Jetty

To run up the Jah'Spotify with Maven/Jetty

    mvn jetty:run -Djahspotify.spotify.username=<your username> -Djahspotify.spotify.password=<your password>

NOTE: The username and password can also be specified in your Maven settings.xml

### Using Tomcat

TBD

#### Configuration

TBD

## UI

To access the HTML5 UI of JahSpotify, simply point your browser to:

[http://localhost:8080/jahspotify/index.html](http://localhost:8080/jahspotify/index.html)

## REST API

Once running, Jah'Spotify exposes it's  RESTful API

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

## Developing

### Modules

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


## Discussions and other:

For now, there is nothing concrete setup but try the issues page on github, contacting me on github.  Also found at times
on freenode - #jahspotify.  Or try Twitter [@nahojkap](https://twitter.com/nahojkap)

## Licensing

All Jah'Spotify code is released under the Apache 2.0 license

## Contributors

Niels vd Weem

Thanks!

## Sponsors

### YourKit

Yourkit is kindly supporting open source projects with its full-featured Java Profiler.

YourKit, LLC is the creator of innovative and intelligent tools for profiling Java and .NET applications.

Take a look at YourKit's leading software products: <a href="http://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a> and <a href="http://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>.

![YourKit](yourkit.png)


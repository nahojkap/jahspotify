#Jah'Spotify Project

##Introduction

An attempt to get the Jah'Spotify project compatible with Windows. For the original project visit https://github.com/johanlindquist/jahspotify.

## Before compiling

- I did not test this on Linux. If you use Linux then use the Johan Lindquist branch. It should still work though.
- Download MinGW and put the bin folder in your PATH.
- Download the OpenAL SDK from and copy the include folder to the MinGW include folder and rename it to AL
- The native pom.xml has a reference to my local LibSpotify folder. Change this to your own Spotify folder. You'll get an error that the api.h file can't be found if you don't.
- Also add the reference to your OpenAL installation folder
- For the rest, use Johan's steps for building.

You can find the OpenAL SDK at:

    http://connect.creativelabs.com/developer/Wiki/OpenAL%20SDK%20for%20Windows.aspx

## Running
The tool isn't quite working yet. Following the compile steps you will be able to run the Java part, but it won't o anything useful yet.
You will also need to download a few more dependencies:
- phtread (http://sources.redhat.com/pthreads-win32/). pthreadGC2.dll needs to be in your path.
- mongodb, and have the daemon running.
- I didn't have to add any paths to OpenAL but I installed the SDK. Let me know if you get a message complaining that not all dependencies are available.

Johan states that the run command is as follows:

    mvn jetty:run -Dspotify.username=<your username> -Dspotify.password=<your password>

This is incorrect, it should be:

    mvn jetty:run -Djahspotify.spotify.username=<your username> -Djahspotify.spotify.password=<your password>


##Licensing

All Jah'Spotify code is released under the Apache 2.0 license
#Jah'Spotify Project

##Introduction

An attempt to get the Jah'Spotify project compatible with Windows. For the original project visit https://github.com/johanlindquist/jahspotify.

## Before compiling

- I did not test this on Linux. If you use Linux then use the Johan Lindquist branch. It should still work though.
- The native pom.xml has a reference to my local LibSpotify folder. Change this to your own Spotify folder. You'll get an error that the api.h file can't be found if you don't.
- Download MinGW and put the bin folder in your PATH.
- For the rest, use Johan's steps for building. 

##Licensing

All Jah'Spotify code is released under the Apache 2.0 license
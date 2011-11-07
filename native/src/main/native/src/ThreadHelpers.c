#include <stdio.h>
#include <pthread.h>

#include "ThreadHelpers.h"

int placeInThread(void *threadFunction, void *threadParams)
{
    pthread_t thread_id;
    pthread_attr_t attr;
    int result;

    pthread_attr_init(&attr);
    
    result = pthread_attr_setstacksize(&attr, 512*1024);
    if (result != 0)
    {
        fprintf(stderr,"jahspotify::placeInThread: error setting stack size\n");
    }

    result = pthread_create(&thread_id, &attr, (void*) threadFunction, (void *)threadParams);
    if (result != 0)
    {
        fprintf(stderr,"jahspotify::placeInThread: error starting thread: %d\n", result);
	return 1;
    }
    else
    {
        fprintf(stderr,"jahspotify::placeInThread: thread started: id: %llu\n", (long long unsigned int)thread_id);
      return 0;
    }
    
}


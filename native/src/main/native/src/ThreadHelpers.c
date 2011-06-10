#include <stdio.h>
#include <pthread.h>

#include "ThreadHelpers.h"

int placeInThread(void *threadFunction, void *threadParams)
{
    pthread_t thread_id;
    size_t size;
    pthread_attr_t attr;

    pthread_attr_init(&attr);
    
    pthread_attr_getstacksize(&attr, &size);

    if (!pthread_attr_setstacksize(&attr, 512*1024))
    {
        // fprintf(stderr,"jahspotify::placeInThread: error setting stack size\n");
    }

    pthread_create(&thread_id, &attr, (void*) threadFunction, (void *)threadParams);
    
    return 0;
    
}


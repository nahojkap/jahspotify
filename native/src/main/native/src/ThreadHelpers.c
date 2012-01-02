#include <stdio.h>
#include <pthread.h>

#include "ThreadHelpers.h"
#include "Logging.h"

int placeInThread(void *threadFunction, void *threadParams)
{
    pthread_t thread_id;
    pthread_attr_t attr;
    int result;

    pthread_attr_init(&attr);
    
    result = pthread_attr_setstacksize(&attr, 512*1024);
    if (result != 0)
    {
        log_error("threadhelpers","placeInThread","Error setting stack size");
    }

    result = pthread_create(&thread_id, &attr, (void*) threadFunction, (void *)threadParams);
    if (result != 0)
    {
        log_error("threadhelpers","placeInThread","Error starting thread: %d", result);
        return 1;
    }
    else
    {
        log_trace("threadhelpers","placeInThread","Thread started: id: %llu", (long long unsigned int)thread_id);
        return 0;
    }
    
}


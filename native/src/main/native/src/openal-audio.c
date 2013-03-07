/*
 * Copyright (c) 2010 Spotify Ltd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *
 * OpenAL audio output driver.
 *
 * This file is part of the libspotify examples suite.
 */

#include <AL/al.h>
#include <AL/alc.h>
#include <errno.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/time.h>

#include "audio.h"
#include "Logging.h"

#define NUM_BUFFERS 3

static ALuint source = 0;


static void error_exit(const char *msg)
{
    puts(msg);
    exit(1);
}



void audio_close()
{
    
    ALCdevice *device = NULL;
    ALCcontext *context = NULL;
    // Exit
    context=alcGetCurrentContext();
    if (context)
    {
        device=alcGetContextsDevice(context);
        alcMakeContextCurrent(NULL);
        alcDestroyContext(context);
        if (device)
            alcCloseDevice(device);
    }

}

float get_audio_gain()
{
    ALfloat currentGain = -1;
    alGetSourcef(source, AL_GAIN, &currentGain);
    return currentGain;
}

void set_audio_gain(float gain)
{
    alSourcef(source, AL_GAIN, gain);
}


static int queue_buffer(ALuint source, audio_fifo_t *af, ALuint buffer)
{
    audio_fifo_data_t *afd = audio_get(af);
    alBufferData(buffer,
                 afd->channels == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16,
                 afd->samples,
                 afd->nsamples * afd->channels * sizeof(short),
                 afd->rate);
    alSourceQueueBuffers(source, 1, &buffer);
    free(afd);
    return 1;
}



static void* audio_start(void *aux)
{
    audio_fifo_t *af = aux;
    audio_fifo_data_t *afd;
    ALCdevice *device = NULL;
    ALCcontext *context = NULL;
    ALuint buffers[NUM_BUFFERS];
    ALint processed;
    ALenum error;
    ALint rate;
    ALint channels;
    
    device = alcOpenDevice(NULL); /* Use the default device */
    if (!device) error_exit("failed to open device");
    context = alcCreateContext(device, NULL);
    alcMakeContextCurrent(context);
    alListenerf(AL_GAIN, 1.0f);
    alDistanceModel(AL_NONE);
    alGenBuffers((ALsizei)NUM_BUFFERS, buffers);
    alGenSources(1, &source);

    /* First prebuffer some audio */
    queue_buffer(source, af, buffers[0]);
    queue_buffer(source, af, buffers[1]);
    queue_buffer(source, af, buffers[2]);
    for (;;) {

        alSourcePlay(source);
        for (;;) {
            /* Wait for some audio to play */
            alGetSourcei(source, AL_BUFFERS_PROCESSED, &processed);
            if (processed <= 0)
            {
                usleep(200);
                continue;
            }

            /* Remove old audio from the queue.. */
            ALuint buffer;
            
            alSourceUnqueueBuffers(source, 1, &buffer);

            /* and queue some more audio */
            afd = audio_get(af);

            alGetBufferi(buffer, AL_FREQUENCY, &rate);
            alGetBufferi(buffer, AL_CHANNELS, &channels);
            if (afd->rate != rate || afd->channels != channels) 
            {
                log_debug("openal","audio_start","rate or channel count changed, resetting");
                break;
            }

            alBufferData(buffer,
                afd->channels == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16,
                afd->samples,
                afd->nsamples * afd->channels * sizeof(int16_t),
                afd->rate);

            free(afd);

            ALenum error = alGetError();
            if (error != AL_NO_ERROR)
            {
                log_error("openal","audio_start","Error buffering: %s", alGetString(error));
                return;
            }

            alSourceQueueBuffers(source, 1, &buffer);

            error = alGetError();
            if (alGetError() != AL_NO_ERROR)
            {
                log_error("openal","audio_start","Error queing buffering: %s", alGetString(error));
                return;
            }


            alGetSourcei(source, AL_SOURCE_STATE, &processed);
            if (processed != AL_PLAYING)
            {
                // Resume playing
                alSourcePlay(source);
            }

            if ((error = alcGetError(device)) != AL_NO_ERROR) {
                log_error("openal","audio_start","Error queing buffering: %s", alGetString(error));
                exit(1);
            }
            
        }

        /* Format or rate changed, so we need to reset all buffers */
        alSourcei(source, AL_BUFFER, 0);
        alSourceStop(source);

        /* Make sure we don't lose the audio packet that caused the change */
        alBufferData(buffers[0],
                     afd->channels == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16,
                     afd->samples,
                     afd->nsamples * afd->channels * sizeof(short),
                     afd->rate);

        free(afd);

        alSourceQueueBuffers(source, 1, &buffers[0]);
        queue_buffer(source, af, buffers[1]);
        queue_buffer(source, af, buffers[2]);
      
    }
}


void audio_init(audio_fifo_t *af)
{
    pthread_t tid;

    TAILQ_INIT(&af->q);
    af->qlen = 0;

    pthread_mutex_init(&af->mutex, NULL);
    pthread_cond_init(&af->cond, NULL);

    pthread_create(&tid, NULL, audio_start, af);
}

void audio_fifo_flush(audio_fifo_t *af)
{
    audio_fifo_data_t *afd;

    pthread_mutex_lock(&af->mutex);

    while ((afd = TAILQ_FIRST(&af->q))) {
        TAILQ_REMOVE(&af->q, afd, link);
        free(afd);
    }

    af->qlen = 0;
    pthread_mutex_unlock(&af->mutex);
}

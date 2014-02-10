/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *        or more contributor license agreements.  See the NOTICE file
 *        distributed with this work for additional information
 *        regarding copyright ownership.  The ASF licenses this file
 *        to you under the Apache License, Version 2.0 (the
 *        "License"); you may not use this file except in compliance
 *        with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing,
 *        software distributed under the License is distributed on an
 *        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *        KIND, either express or implied.  See the License for the
 *        specific language governing permissions and limitations
 *        under the License.
 */

var mediaQueuedResultTimer = $.timer(function ()
{
    // Close the popup now!
    $("#mediaQueuedResult").popup("close");
    mediaQueuedResultTimer.reset();

});

function loadMedia(id)
{
    $.mobile.loading("show", {
            text: "Queuing ...",
            textVisible: true,
            theme: 'a',
            textonly: false,
            html: ""
        });

    mediaURL = "/jahspotify/queue/jahspotify:queue:default/add/" + id;
    $.getJSON(mediaURL).done(function (data)
    {
        if (data.responseStatus == 'OK')
        {
            // Set the text accordingly $('#mediaQueuedResultMessage' );
            $("#mediaQueuedResultMessage").html("Media successfully enqueued");
            mediaQueuedResultTimer.set({ time: 2000, autostart: true });
        }
        else
        {
            // Error - set message!
            $("#mediaQueuedResultMessage").html("Error while enqueueing media!");
        }
        // $.mobile.hidePageLoadingMsg();

        $.mobile.loading('hide');

        $("#mediaQueuedResult").popup("open");
    });
}


function currentMediaPlayerStatus(callback)
{
    $.getJSON("/jahspotify/player/status",function (data)
    {
        callback(data);
    });
}

function currentQueue(callback)
{
    $.getJSON("/jahspotify/queue/jahspotify:queue:default/",function (data)
    {
        callback(data);
    });
}


function media(id, callback)
{
    $.getJSON("/jahspotify/media/" + id,function (data)
    {
        callback(data);
    });
}


function currentQueueStatus(callback)
{
    $.getJSON("/jahspotify/queue/jahspotify:queue:default/status",function (data)
    {
        callback(data);
    });
}

function starTrack( link, trackID, starredState, starSuccessFunction, starFailedFunction )
{
    invokeRestfulGet("/jahspotify/track/" + trackID + "/star?value="+starredState, "Updating", starSuccessFunction, starFailedFunction);
}


function skip( skipSuccessFunction, skipFailedFunction )
{
    invokeRestfulGet("/jahspotify/player/skip", "Skipping", skipSuccessFunction, skipFailedFunction);
}

function pause( pauseSuccessFunction, pauseFailedFunction )
{
    invokeRestfulGet("/jahspotify/player/pause", "Pausing", pauseSuccessFunction, pauseFailedFunction);
}

function invokeRestfulGet( url, text, successFunction, failedFunction )
{
    $.mobile.loading("show", {
        text: text,
        textVisible: true,
        theme: 'a',
        textonly: false,
        html: ""
    });

    $.getJSON( url ).done(function ( data )
                                                {
                                                    $.mobile.loading('hide');

                                                    // Evaluate the result of the skip
                                                    if ( data.responseStatus == 'OK' )
                                                    {
                                                        if (successFunction != null)
                                                        {
                                                            // If successful
                                                            successFunction();
                                                        }
                                                    }
                                                    else
                                                    {
                                                        if (failedFunction != null)
                                                        {
                                                            // if not successful
                                                            failedFunction();
                                                        }
                                                        else
                                                        {
                                                            alert("Invocation failed miserably and no fail function defined!")
                                                        }
                                                    }
                                                } ).fail( function ()
                                                          {
                                                              $.mobile.loading('hide');

                                                              // if not successful
                                                              if (failedFunction != null)
                                                              {
                                                                failedFunction();
                                                              }
                                                              else
                                                              {
                                                                  alert("Invocation failed miserably and no fail function defined!")
                                                              }
                                                          } )
}


function resume( resumeSuccessFunction, resumeFailedFunction)
{
    invokeRestfulGet("/jahspotify/player/resume", resumeSuccessFunction, resumeFailedFunction);
}

function gotoLink( s )
{
    $.mobile.changePage( s );
}

function showQueueSettings()
{
    $.mobile.changePage( '/jahspotify/jsp/queue-settings-dialog.jsp', {transition:'pop', role:'dialog'} );
}


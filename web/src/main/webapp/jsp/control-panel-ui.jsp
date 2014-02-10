<%--
~ Licensed to the Apache Software Foundation (ASF) under one
~        or more contributor license agreements.  See the NOTICE file
~        distributed with this work for additional information
~        regarding copyright ownership.  The ASF licenses this file
~        to you under the Apache License, Version 2.0 (the
~        "License"); you may not use this file except in compliance
~        with the License.  You may obtain a copy of the License at
~
~          http://www.apache.org/licenses/LICENSE-2.0
~
~        Unless required by applicable law or agreed to in writing,
~        software distributed under the License is distributed on an
~        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
~        KIND, either express or implied.  See the License for the
~        specific language governing permissions and limitations
~        under the License.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script>

    function refreshControlPanel()
    {
        var h = $(window).height();

        $('#loadingQueueInformation').show();

        $('#playerPauseButton').hide();
        $('#playerResumeButton').hide();
        $('#playerSkipButton').hide();
        $('#queueRandomTrack').hide();

        currentQueue(function (queue)
        {
            $('#loadingQueueInformation').hide();

            if (queue.queueStatus.queueState == 'PLAYING' || queue.queueStatus.queueState == 'PAUSED')
            {

                if (queue.queueStatus.queueState == 'PAUSED')
                {
                    $('#playerResumeButton').show();
                    $('#playerSkipButton').show();
                }
                else
                {
                    $('#playerPauseButton').show();
                }
                $('#playerSkipButton').show();

                $('#currentTrack').show();
                media(queue.currentlyPlaying.trackID, function (track)
                {
                    $('#currentTrackName').text(track.title);
                    media(track.album.id, function (album)
                    {

                        $('#currentTrackImage').attr("src", "/jahspotify/media/" + album.cover.id);
                        $('#currentTrackAlbum').text(album.name);

                        $("#ctrlpanel").trigger("updatelayout");

                    });


                });


            }
            else
            {
                // Show the link to load random track
                $('#queueRandomTrack').show();

                if (queue.queueStatus.currentQueueSize == 0)
                {
                    $('#noTracks').show();
                }
                $("#ctrlpanel").trigger("updatelayout");

            }

        });

    }

    function setEventsOnPanel()
    {
        $("#ctrlpanel").panel({
            beforeopen: function (event, ui)
            {
                refreshControlPanel();
            }
        });
    }


    function skipSuccess()
    {
        $("#ctrlpanel").panel("close");
        $("#actionResultMessage").html("Track skipped");
        popupShowTimer.set({ time: 100, autostart: true });
        popupCloseTimer.set({ time: 3000, autostart: true });
    }

    function skipFailed()
    {
        $("#ctrlpanel").panel("close");
        $("#actionResultMessage").html("Track skip failed!");
        popupShowTimer.set({ time: 100, autostart: true });
        popupCloseTimer.stop();
    }

    function resumeSuccess()
    {
        $("#ctrlpanel").panel("close");
        $("#actionResultMessage").html("Track resumed");
        popupShowTimer.set({ time: 100, autostart: true });
        popupCloseTimer.set({ time: 3000, autostart: true });
    }

    function resumeFailed()
    {
        $("#ctrlpanel").panel("close");
        $("#actionResultMessage").html("Resume failed!");
        popupShowTimer.set({ time: 100, autostart: true });
        popupCloseTimer.stop();
    }

    function pauseSuccess()
    {
        $("#ctrlpanel").panel("close");
        $("#actionResultMessage").html("Track paused");
        popupShowTimer.set({ time: 100, autostart: true });
        popupCloseTimer.set({ time: 3000, autostart: true });

    }

    function pauseFailed()
    {
        $("#ctrlpanel").panel("close");
        $("#actionResultMessage").html("Pause failed!");
        popupShowTimer.set({ time: 100, autostart: true });
        popupCloseTimer.stop();
    }

    var popupCloseTimer = $.timer(function ()
    {
        $("#actionResultPopup").popup("close");
        popupCloseTimer.stop();
    });

    var popupShowTimer = $.timer(function ()
    {
        $("#actionResultPopup").popup("open");
    });

    var panelTimer = $.timer(function ()
    {
        // Load the current queue status

        // Evaluate the current state
        // playing: show the pause button
        // paused: show the play button
        // if not playing and no track - show the 'random track' link?

        // Load the current and the next track

        // If current track is available, populate and show the LI element accordingly
        // as well as hide the 'no track' available

        // Evaluate whether a next track is available:
        // - available: populate and show the next track LI element as well as enable/show
        // the next track button
        // - not available: show the 'random track' link?

    });

    /* $( document ).on( "pageinit", function ()
     {
     $( "#popupPanel" ).on( {
     popupbeforeposition:function ()
     {

     alert("opening popup");


     // Set up to reload every 2 seconds when the panel is open
     panelTimer.set( { time:2000, autostart:true } );
     }
     } );

     $( "#popupPanel" ).on( {
     popupafterclose:function ()
     {
     // Cancel the reloading when the panel is closed
     panelTimer.stop();
     alert("closing popup");

     }
     } );
     } );
     */
    // TODO: Add function that will 're-load' the statuses' below


</script>


<div data-role="popup" id="mediaQueuedResult" data-theme="b" data-overlay-theme="b" class="ui-content">

    <a href="#" data-rel="back" data-role="button" data-icon="delete" data-iconpos="notext"
       class="ui-btn-right">Close</a>

    <p id="mediaQueuedResultMessage">No message</p>
</div>

<div data-role="panel" id="ctrlpanel" data-position="right" data-display="overlay" data-theme="b">

    <ul data-role="listview" data-inset="false">
        <li>
            <div>

                <ul data-role="listview" data-inset="true">
                    <li id="loadingQueueInformation">
                        <img id="loadingCurrentTrackSpinner" src="/jahspotify/css/images/ajax-loader.gif"
                             width="100" height="100"/>

                        <p>Loading Queue Information ...</p>
                    </li>
                </ul>

                <ul data-role="listview" data-inset="true">
                    <li id="noTracks" style="display: none;">
                        <p>No tracks queued</p>
                    </li>
                </ul>

                <ul data-role="listview" data-inset="true">
                    <li id="currentTrack" style="display: none;">
                        <img id="currentTrackImage" src="/jahspotify/css/images/ajax-loader.gif"
                             width="100" height="100"/>

                        <p id="currentTrackName"></p>

                        <p id="currentTrackAlbum"></p>
                    </li>
                </ul>

            </div>

        </li>

        <li>

            <%--
                        <div data-type="horizontal" data-mini="true" data-role="controlgroup"
                             style="text-align: center">
            --%>
            <a id="playerPauseButton" data-icon="gear" href="#" data-role="button"
               onclick="pause(pauseSuccess, pauseFailed);" data-mini="true">pause</a>
            <a id="playerResumeButton" data-icon="grid" href="#" data-role="button"
               onclick="resume(resumeSuccess, resumeFailed);"
               data-mini="true">resume</a>
            <a id="playerSkipButton" data-icon="help" href="#" data-role="button"
               onclick="skip(skipSuccess,skipFailed);"
               data-mini="true">skip</a>
            <%--
                            <a id="queueRandomTrack" data-icon="plus" href="#" data-role="button"
                               onclick="queueRandom(queueRandomSuccess,queueRandomFailed);"
                               data-mini="true">queue random track</a>
            --%>
            <%--
                        </div>
            --%>
        </li>

        <li>

            <%--
                        <div data-role="controlgroup" data-type="horizontal" class="ui-body ui-body-b">
            --%>

            <ul data-role="listview" data-inset="false">

                <li>
                    <a id="currentQueueButton" data-icon="grid" data-mini="true" data-role="button"
                       onclick="gotoLink('/jahspotify/ui/queue/current')">current
                        queue
                    </a>
                    <a id="searchButton" data-icon="search" data-mini="true" data-role="button"
                       onclick="gotoLink('/jahspotify/ui/search')">search
                    </a>
                    <a id="homeButton" data-icon="home" data-mini="true" data-role="button"
                       onclick="gotoLink('/jahspotify/index.jsp')">home
                    </a>
                </li>
            </ul>
            <%--
                            </div>
            --%>
        </li>
    </ul>

</div>


<script>
    setEventsOnPanel();
    $('#mediaQueuedResult').popup();
</script>


<script>
    $(document).on("pagecreate", function ()
    {
        $("body > [data-role='panel']").panel();
        $("body > [data-role='button']").button();
        $("body > [data-role='panel'] [data-role='listview']").listview();
        $("body > [data-role='panel'] [data-role='controlgroup']").controlgroup();
    });
    $(document).on("pageshow", function ()
    {
        $("body > [data-role='header']").toolbar();
        $("body > [data-role='button']").button();
        $("body > [data-role='header'] [data-role='navbar']").navbar();
        $("body > [data-role='panel'] [data-role='controlgroup']").controlgroup();
    });
</script>

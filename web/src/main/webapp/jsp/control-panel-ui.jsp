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

  function showControlPanel()
  {
    var h = $( window ).height();
    $( "#popupPanel" ).css( "height", h );

    $( '#loadingQueueInformation' ).show();

    $( '#playerPauseButton' ).hide();
    $( '#playerResumeButton' ).hide();
    $( '#playerSkipButton' ).hide();
    $( '#queueRandomTrack' ).hide();

    currentQueueStatus( function ( queueStatus )
                        {
                          $( '#loadingQueueInformation' ).hide();

                          if ( queueStatus.queueState == 'PLAYING' )
                          {
                            $( '#playerPauseButton' ).show();
                            $( '#playerSkipButton' ).show();
                          }
                          else if ( queueStatus.queueState == 'PAUSED' )
                          {
                            $( '#playerResumeButton' ).show();
                            $( '#playerSkipButton' ).show();
                          }
                          else
                          {
                            // Show the link to load random track
                            $( '#queueRandomTrack' ).show();
                            if ( queueStatus.currentQueueSize == 0 )
                            {
                              $( '#noTracks' ).show();
                            }

                          }

                        } );

    hehe = $( "#popupPanel" );
    $( "#popupPanel" ).popup( "open" );

  }

  function skipSuccess()
  {
    $( "#actionResultMessage" ).html( "Track skipped" );
    $( "#actionResultPopup" ).popup( "open" );
    popupCloseTimer.set( { time:2000, autostart:true } );
  }

  function skipFailed()
  {
    $( "#actionResultMessage" ).html( "Track skip failed!" );
    $( "#actionResultPopup" ).popup( "open" );
    popupCloseTimer.stop();
  }

  function resumeSuccess()
  {
    $( "#actionResultMessage" ).html( "Track resumed" );
    $( "#actionResultPopup" ).popup( "open" );
    popupCloseTimer.set( { time:2000, autostart:true } );

  }

  function resumeFailed()
  {
    $( "#actionResultMessage" ).html( "Resume failed!" );
    $( "#actionResultPopup" ).popup( "open" );
    popupCloseTimer.stop();
  }

  function pauseSuccess()
  {
    $( "#actionResultMessage" ).html( "Track paused" );
    $( "#actionResultPopup" ).popup( "open" );
    popupCloseTimer.set( { time:2000, autostart:true } );

  }

  function pauseFailed()
  {
    $( "#actionResultMessage" ).html( "Pause failed!" );
    $( "#actionResultPopup" ).popup( "open" );
    popupCloseTimer.stop();
  }

  var popupCloseTimer = $.timer( function ()
                                 {
                                   alert( "Closing popup" );
                                   $( "#actionResultPopup" ).popup( "close" );
                                   popupCloseTimer.stop();
                                 } );

  var panelTimer = $.timer( function ()
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

                            } );

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

<%--
<div data-role="popup" id="actionResultPopup" data-corners="false" data-theme="none"
     data-shadow="false" data-tolerance="0,0">

  <a id="actionResultPopupCloseButton" onclick="panelTimer.stop(); return true;" href="#" data-rel="back"
     data-role="button"
     data-theme="f"
     data-icon="delete"
     data-iconpos="notext"
     class="ui-btn-right">Close</a>

  <p id="actionResultMessage">Track skipped</p>

</div>--%>

<div data-role="popup" id="popupPanel" data-corners="false" data-theme="none" data-shadow="false" data-tolerance="0,0">

  <div>

    <div>

      <ul data-role="listview" data-theme="a" data-inset="false">
        <li>
          <div>

            <ul data-role="listview" data-theme="a" data-inset="true">
              <li id="loadingQueueInformation">
                <p>Loading Queue Information ...</p>
              </li>
            </ul>

            <ul data-role="listview" data-theme="a" data-inset="true">
              <li id="noTracks" style="display: none;">
                <p>No tracks queued</p>
              </li>
            </ul>

            <ul data-role="listview" data-theme="a" data-inset="true">
              <li id="currentTrack" style="display: none;">
                <img src="/jahspotify/media/spotify:image:950bb7d4f6208eb4d10533da25d2f8d86bda2ba4" width="100" h
                     eight="100"/>

                <p>What I Got</p>

                <p>Sublime</p>
              </li>
            </ul>

            <ul data-role="listview" data-theme="a" data-inset="true">
              <li id="nextTrack" style="display: none;">
                <p>What I Got</p>

                <p>Sublime</p>
              </li>
            </ul>


            <ul data-role="listview" data-theme="a" data-inset="true">
              <li id="playerControl" style="padding: 0;">

                <div data-type="horizontal" data-mini="true" data-role="controlgroup" style="text-align: center">
                  <a id="playerPauseButton" data-icon="gear" href="#" data-role="button" data-theme="a"
                     onclick="pause(pauseSuccess, pauseFailed);"
                     data-mini="true"
                      >pause</a>
                  <a id="playerResumeButton" data-icon="grid" href="#" data-role="button" data-theme="a"
                     onclick="resume(resumeSuccess, resumeFailed);"
                     data-mini="true">resum
                    e</a>
                  <a id="playerSkipButton" data-icon="help" href="#" data-role="button" data-theme="a"
                     onclick="skip(skipSuccess,skipFailed);"
                     data-mini="true">skip</a>
                  <a id="queueRandomTrack" data-icon="plus" href="#" data-role="button" data-theme="a"
                     onclick="queueRandom(queueRandomSuccess,queueRandomFailed);"
                     data-mini="true">queue random track</a>
                </div>
              </li>
            </ul>

          </div>
        </li>

      </ul>

    </div>


    <%--
          </li>
        </ul>
    --%>

    <ul data-role="listview" data-theme="a" data-inset="false">

      <li>

        <button id="currentQueueButton" data-theme="a" data-icon="grid" data-mini="true"
                onclick="gotoLink('/jahspotify/ui/queue/current')">current
          queue
        </button>
        <button id="searchButton" data-theme="a" data-icon="grid" data-mini="true"
                onclick="gotoLink('/jahspotify/ui/search')">search
        </button>
        <button id="homeButton" data-theme="a" data-icon="grid" data-mini="true"
                onclick="gotoLink('/jahspotify/index.jsp')">home
        </button>
      </li>
    </ul>

  </div>


</div>

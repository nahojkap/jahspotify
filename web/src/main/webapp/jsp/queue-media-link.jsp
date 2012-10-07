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


<c:if test="${!popupLoaded}" scope="request" var="popupLoaded">

  <script>

    var mediaQueuedResultTimer = $.timer( function ()
                         {
                           // Close the popup now!
                           $( "#mediaQueuedResult" ).popup( "close" );
                           // mediaQueuedResultTimer.reset();

                         } );

    function loadMedia( id )
    {
      // Queue the given media

      $.mobile.showPageLoadingMsg();

      mediaURL = "/jahspotify/queue/jahspotify:queue:default/add/" + id;
      var jqxhr = $.getJSON( mediaURL ).done( function ( data )
                                              {
                                                if ( data.responseStatus == 'OK' )
                                                {
                                                  // Set the text accordingly $('#mediaQueuedResultMessage' );
                                                  $("#mediaQueuedResultMessage").html("Media successfully enqueued");
                                                  // mediaQueuedResultTimer.set( { time:2000, autostart:true } );
                                                }
                                                else
                                                {
                                                  // Error - set message!
                                                  $("#mediaQueuedResultMessage").html("Error while enqueueing media!");
                                                }
                                                $.mobile.hidePageLoadingMsg();
                                                $( "#mediaQueuedResult" ).popup( "open" );
                                              } );
    }
  </script>

  <div data-role="popup" id="mediaQueuedResult" data-overlay-theme="e" class="ui-content">

    <a href="#" data-rel="back" data-role="button" data-theme="f" data-icon="delete" data-iconpos="notext"
       class="ui-btn-right">Close</a>

    <p id="mediaQueuedResultMessage">No message</p>
  </div>

  <c:set scope="request" var="popupLoaded" value="true"/>

</c:if>

<%--

--%>

<%--
<c:url var="queueTrackURL" value="/ui/queue/add/${mediaId}}"/>
                        <a href="<c:out value="${queueTrackURL}"/>" data-rel="dialog" data-transition="fade">Enqueue</a>
--%>

<a id="actionButton-<c:out value='${mediaId}'/>" onclick="loadMedia('<c:out value="${mediaId}"/>')" href="#"
   data-theme="a" data-icon="plus" >Enqueue</a>


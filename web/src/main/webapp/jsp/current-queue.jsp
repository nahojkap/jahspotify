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

<%@ include file="/jsp/header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="jah" uri="http://jahtify.com/jsp/jstl/tags" %>

<div data-role="page" id="currentQueue" data-theme="b">

  <!-- /header -->
  <div class="mainHeaderPanel" data-position="inline" data-role="header" role="banner" data-position="fixed">
    <h1>Play Queue</h1>
    <a href="<c:out value='${homeURL}'/>" data-icon="home" data-iconpos="notext" data-direction="reverse">Home</a>
    <c:url var="queueSettingsURL" value="/jsp/queue-settings-dialog.jsp"/>
    <a href="<c:out value='${queueSettingsURL}'/>" data-icon="gear" data-rel="dialog" class="ui-btn-right"
       data-iconpos="notext">Options</a>

  </div>


  <div data-role="content">

    <c:choose>
      <c:when test="${!empty(currentTrack)}">

        <div align="center" >

          <ul data-role="listview" data-inset="true" data-split-icon="minus">
            <li data-role="list-divider" data-theme="a">Currently Playing</li>

              <jah:fulltrack link="${currentTrack.trackID}" var="fullTrack"/>

            <c:url var="trackURL" value="/ui/media/track/${fullTrack.id.id}"/>
            <li id="<c:out value='%{currentTrack.id.id}'/>">
              <a href="<c:out value="${trackURL}"/>">
                  <jah:duration var="duration" duration="${fullTrack.length}"/>
                  <jah:image link="${fullTrack.albumCoverLink.id}"/>

                <div>
                  <h4><c:out value="${fullTrack.title}"/> <span
                      style="vertical-align: middle; font-weight: lighter; font-size: 60%">(<c:out value="${duration}"/>)</span></h4>

                  <p style="font-weight: bold; font-size: 65%">
                    <c:out value="${fullTrack.albumName}"/></p>

                  <p style="font-weight: bold; font-size: 50%">
                    <c:forEach items="${fullTrack.artistNames}" var="artistName">
                      <c:out value="${artistName}"/>
                    </c:forEach>
                  </p>
                </div>
              </a>
            </li>

          </ul>
        </div>

        <c:choose>
          <c:when test="${!empty(queuedTracks)}">
            <ul data-role="listview" data-inset="true" data-split-icon="minus">

              <li data-role="list-divider" data-theme="a">Enqueued Track(s)</li>

              <c:forEach items="${queuedTracks}" var="queuedTrack">

                  <jah:fulltrack link="${queuedTrack.trackID}" var="track"/>

                <c:url var="trackURL" value="/ui/media/track/${track.id.id}"/>

                <li id="<c:out value='%{track.id.id}'/>">
                  <a href="<c:out value="${trackURL}"/>">
                    <jah:duration var="duration" duration="${track.length}"/>

                      <jah:image link="${track.albumCoverLink.id}"/>

                    <div>
                      <h4><c:out value="${track.title}"/> <span
                          style="vertical-align: middle; font-weight: lighter; font-size: 60%">(<c:out value="${duration}"/>)</span></h4>

                      <p style="font-weight: bold; font-size: 65%">
                        <c:out value="${track.albumName}"/></p>

                      <p style="font-weight: bold; font-size: 50%">
                          <c:forEach items="${track.artistNames}" var="artistName">
                            <c:url var="artistURL" value="/ui/media/${track.id.id}"/>
                            <c:out value="${artistName}"/>
                          </c:forEach>
                      </p>
                    </div>
                  </a>

                  <c:url var="removeTrackURL" value="/ui/queue/remove/${queuedTrack.id}"/>
                  <a href="<c:out value="${removeTrackURL}"/>" data-rel="dialog" data-transition="slideup">Remove Track</a>

                </li>

              </c:forEach>

            </ul>
          </c:when>
          <c:otherwise>
            <p>Queue is empty</p>
          </c:otherwise>
        </c:choose>
      </c:when>
      <c:otherwise>
        <div><p>Queue is empty</p></div>
        <div><p>Start Random One</p></div>
      </c:otherwise>
    </c:choose>
  </div>

  <%@ include file="/jsp/footer-bar.jsp" %>

</div>

<%@ include file="/jsp/footer.jsp" %>

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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<div id="track" data-role="page" data-theme="b" class="homeBody">


    <div data-role="popup" id="trackPanel" class="ui-content">

        <div data-role="controlgroup" class="ui-body ui-body-b">

            <!-- View the album link -->
            <c:url var="albumURL" value="/ui/media/album/${track.album.id}"/>
            <a href="<c:out value='${albumURL}'/>" data-role="button" data-icon="spotify-album" data-icon-theme="e">Album</a>

            <c:choose>
                <c:when test="${fn:length(fullTrack.artistNames) gt 1}">
                    <c:url var="artistBrowseURL" value="/ui/media/artists/${fullTrack.id.id}"/>
                    <a data-prefetch="true" data-rel="dialog" href="<c:out value='${artistBrowseURL}'/>"
                       data-icon="arrow-r" data-role="button">Artists</a><
                </c:when>
                <c:otherwise>
                    <c:set var="artistLink" value="${fullTrack.artistLinks[0].id}"/>
                    <c:url var="artistBrowseURL" value="/ui/media/artist/${artistLink}"/>
                    <a href="<c:out value='${artistBrowseURL}'/>" data-role="button" data-icon="spotify-artist">Artist</a>
                </c:otherwise>
            </c:choose>

            <%--
                        <c:url var="starTrackURL" value="/ui/media/track/${fullTrack.id.id}/star"/>
                        <a href="#" data-role="button" data-icon="plus" data-icon-theme="e">Add to Playlist</a>

                        <c:url var="starTrackURL" value="/ui/media/track/${fullTrack.id.id}/star"/>
                        <a href="#" data-role="button" data-icon="minus" data-icon-theme="e">Remove from Playlist</a>

                        <c:url var="starTrackURL" value="/ui/media/track/${fullTrack.id.id}/star"/>
                        <a href="#" data-role="button" data-icon="star" data-icon-theme="e">Star</a>
            --%>

        </div>
    </div>

    <%@ include file="/jsp/header-bar.jsp" %>

    <div data-role="content">
        <div class="content-primary" align="center">
            <div class="ui-body ui-body-b">

                <div align="center" style="line-height: 0.5em">
                    <jah:duration var="duration" duration="${fullTrack.length}"/>
                    <c:url var="albumCoverURL" value="/media/${fullTrack.albumCoverLink.id}"/>
                    <div>
                        <img src="<c:out value='${albumCoverURL}'/>"/>
                    </div>

                    <p style="font-weight: 900;"><c:out value="${fullTrack.title}"/> <span
                                                    style="vertical-align: middle; font-weight: lighter; font-size: 60%">(<c:out value="${duration}"/>)</span></p>

                    <p style="font-weight: bold; font-size: 80%"><c:out value="${fullTrack.albumName}"/></p>

                    <p style="font-weight: bold; font-size: 70%"><c:forEach items="${fullTrack.artistNames}"
                                                                            varStatus="rowCounter" var="artistName">
                        <c:set var="artistLink" value="${fullTrack.artistLinks[rowCounter.count]}"/>
                        <c:out value="${artistName}"/>
                        <c:if test="${not rowCounter.last}">, </c:if>
                    </c:forEach>
                    </p>
                </div>

                <div data-role="controlgroup" data-type="horizontal" class="ui-body ui-body-b">

                    <c:choose>
                        <c:when test="${track.starred}">
                            <c:set var="onclick" value="starTrack(this,'${fullTrack.id.id}',false,skipSuccess,skipFailed);"/>
                            <a href="#" onclick="<c:out value='${onclick}'/>" data-role="button" data-icon="spotify-unstar">Unstar</a>
                        </c:when>
                        <c:otherwise>
                            <c:set var="onclick" value="starTrack(this,'${fullTrack.id.id}',true,null,null);"/>
                            <a href="#" onclick="<c:out value='${onclick}'/>" data-role="button" data-icon="spotify-star">Star</a>

                        </c:otherwise>
                    </c:choose>

                    <c:url var="queueTrackURL" value="/ui/queue/add/${fullTrack.id.id}"/>
                    <a data-rel="dialog" data-role="button" data-transition="fade" href="<c:out value='${queueTrackURL}'/>" data-icon="spotify-enqueue">Enqueue</a>

                    <a data-rel="popup" href="#trackPanel" data-icon="spotify-more" data-role="button" id="morePanelButton">More</a>

                </div>

            </div>
        </div>

    </div>

    <%@ include file="/jsp/footer-bar.jsp" %>


</div>

<%@ include file="/jsp/footer.jsp" %>

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

<div id="track" data-role="page" data-theme="g" class="homeBody">

  <%@ include file="/jsp/header-bar.jsp" %>

  <div data-role="content">
    <div class="content-primary" align="center">
      <div class="ui-body ui-body-a">

        <div data-role="controlgroup" data-type="horizontal" align="right">
          <a href="index.html" data-role="button" data-icon="star" data-iconpos="notext">Star this track</a>

          <c:url var="queueTrackURL" value="/ui/queue/add/${track.id.id}"/>
          <a data-role="button" data-rel="dialog" data-transition="fade" href="<c:out value='${queueTrackURL}'/>"
             data-icon="grid" data-iconpos="notext">Enqueue</a></li>
          <a href="index.html" data-role="button" data-icon="plus" data-iconpos="notext">Add to Playlist</a>

        </div>

        <div align="center" style="line-height: 0.5em">
          <jah:duration var="duration" duration="${track.length}"/>
          <c:url var="albumCoverURL" value="/media/${track.albumCoverLink.id}"/>
          <div>
            <img src="<c:out value='${albumCoverURL}'/>"/>
          </div>

          <p style="font-weight: 900;"><c:out value="${track.title}"/> (<c:out value="${duration}"/>)</p>

          <p style="font-weight: bold; font-size: 80%"><c:out value="${track.albumName}"/></p>

          <p style="font-weight: bold; font-size: 70%"><c:forEach items="${track.artistNames}"
                                                                  varStatus="rowCounter" var="artistName">
            <c:set var="artistLink" value="${track.artistLinks[rowCounter.count]}"/>
            <c:out value="${artistName}"/>
            <c:if test="${not rowCounter.last}">, </c:if>
          </c:forEach>
          </p>
        </div>

        <div data-role="navbar" data-theme="g">
          <ul>

            <c:url var="queueTrackURL" value="/ui/media/track/${track.id.id}/star?"/>
            <li><a href="#" data-icon="star" data-icon-theme="c">Star</a></li>

            <c:url var="queueTrackURL" value="/ui/queue/add/${track.id.id}"/>
            <li><a data-rel="dialog" data-transition="fade" href="<c:out value='${queueTrackURL}'/>" data-icon="grid">Enqueue</a>
            </li>
            <c:choose>
              <c:when test="${fn:length(track.artistNames) gt 1}">
                <c:url var="artistBrowseURL" value="/ui/media/artists/${track.id.id}"/>
                <li><a data-prefetch="true" data-rel="dialog" href="<c:out value='${artistBrowseURL}'/>"
                       data-icon="people">Artists</a></li>
              </c:when>
              <c:otherwise>
                <c:set var="artistLink" value="${track.artistLinks[0].id}"/>
                <c:url var="artistBrowseURL" value="/ui/media/artist/${artistLink}"/>
                <li><a href="<c:out value='${artistBrowseURL}'/>" data-icon="people">Artist</a></li>
              </c:otherwise>
            </c:choose>

            <li><a href="#" data-icon="gear">More</a></li>
          </ul>
        </div>

      </div>
    </div>

  </div>

  <%@ include file="/jsp/footer-bar.jsp" %>


</div>

<%@ include file="/jsp/footer.jsp" %>

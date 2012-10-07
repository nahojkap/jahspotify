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

<div data-role="page" id="tracks" data-title="Tracks">

  <jsp:include page="/jsp/header-bar.jsp"/>

  <div data-role="content">

    <div class="content-primary">
      <ul data-role="listview" data-theme="a" data-inset="true" data-filter="true" data-split-icon="plus"
          data-split-theme="a" data-count-theme="b">
        <c:forEach items="${queryResult.tracksFound}" var="trackLink">

          <jah:media link="${trackLink.id}" var="track"/>
          <jah:media link="${track.album.id}" var="album"/>

          <c:url var="trackURL" value="/ui/media/track/${track.id.id}"/>
          <li id="<c:out value='%{track.id.id}'/>">
            <a href="<c:out value="${trackURL}"/>">
              <jah:duration var="duration" duration="${track.length}"/>
              <c:url var="albumCoverURL" value="/media/${album.cover.id}"/>
              <img src="<c:out value="${albumCoverURL}"/>"/>

              <div>

                <h4><c:out value="${track.title}"/> <span
                    style="vertical-align: middle; font-weight: lighter; font-size: 60%">(<c:out
                    value="${duration}"/>)</span></h4>

                <p style="font-weight: bold; font-size: 65%">
                  <c:out value="${album.name}"/></p>

              </div>
            </a>
            <c:url var="queueTrackURL" value="/ui/queue/add/${track.id.id}"/>
            <a href="<c:out value="${queueTrackURL}"/>" data-rel="dialog" data-transition="fade">Enqueue</a>
          </li>
        </c:forEach>
      </ul>
    </div>

  </div>

  <div data-role="footer">
    <div data-role="navbar">
      <ul>
        <li><a href="#tracks">Tracks</a></li>
        <li><a href="#albums">Albums</a></li>
        <li><a href="#artists">Artists</a></li>
      </ul>
    </div>

  </div>


</div>


<div data-role="page" id="albums">

  <jsp:include page="/jsp/header-bar.jsp"/>

  <div data-role="content">

    <div class="content-primary">
      <ul data-role="listview" data-theme="a" data-inset="true" data-filter="true" data-split-icon="plus"
          data-split-theme="a" data-count-theme="b">
        <c:forEach items="${queryResult.albumsFound}" var="albumLink">

          <jah:media link="${albumLink.id}" var="album"/>

          <c:url var="albumURL" value="/ui/media/album/${albumLink.id}"/>
          <li id="<c:out value='%{albumLink.id}'/>">
            <a href="<c:out value="${albumURL}"/>">
              <jah:duration var="duration" duration="${track.length}"/>
              <c:url var="albumCoverURL" value="/media/${album.cover.id}"/>
              <img src="<c:out value="${albumCoverURL}"/>"/>

              <div>

                <h4><c:out value="${album.name}"/></h4>

              </div>
            </a>
            <c:url var="queueAlbumURL" value="/ui/queue/add/${track.id.id}"/>
            <a href="<c:out value="${queueAlbumURL}"/>" data-rel="dialog" data-transition="fade">Enqueue</a>
          </li>
        </c:forEach>
      </ul>
    </div>

  </div>

  <div data-role="footer">
    <div data-role="navbar">
      <ul>
        <li><a href="#tracks">Tracks</a></li>
        <li><a href="#albums">Albums</a></li>
        <li><a href="#artists">Artists</a></li>
      </ul>
    </div>

  </div>

</div>


<div data-role="page" id="artists">

  <jsp:include page="/jsp/header-bar.jsp"/>

  <div data-role="content">

    <div class="content-primary">
      <ul data-role="listview" data-theme="a" data-inset="true" data-filter="true" data-split-icon="plus"
          data-split-theme="a" data-count-theme="b">
        <c:forEach items="${queryResult.artistsFound}" var="artistLink">

          <jah:media link="${artistLink.id}" var="artist"/>

          <li id="<c:out value='%{artistLink.id}'/>">
            <c:url var="artistURL" value="/ui/media/artist/${artistLink.id}"/>
            <a href="<c:out value="${artistURL}"/>">

              <c:if test="${not empty artist.portraits}">
                <c:url var="artistPortraitURL" value="/media/${artist.portraits[0].id}"/>
                <div>
                  <img src="<c:out value="${artistPortraitURL}"/>"/>
                </div>
              </c:if>
              <c:out value="${artist.name}"/>
            </a>

              <%-- <c:url var="queueAlbumURL" value="/ui/queue/add/${albumLink.id}"/>
           <a href="<c:out value="${queueAlbumURL}"/>" data-rel="dialog"
              data-transition="fade">Enqueue</a>--%>
          </li>
        </c:forEach>
      </ul>
    </div>

  </div>

  <div data-role="footer">
    <div data-role="navbar">
      <ul>
        <li><a href="#tracks">Tracks</a></li>
        <li><a href="#albums">Albums</a></li>
        <li><a href="#artists">Artists</a></li>
      </ul>
    </div>

  </div>

</div>


<%@ include file="/jsp/footer.jsp" %>

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

<div data-role="page" id="folder">

  <jsp:include page="/jsp/header-bar.jsp"/>

  <div data-role="content">
    <div class="content-primary">

    <ul data-role="listview" data-theme="a" data-inset="true" data-filter="true" data-split-icon="plus"
        data-split-theme="a" data-count-theme="b">
      <c:forEach items="${entry.subEntries}" var="subEntry">
        <c:url var="subEntryURL" value="/ui/media/library/${subEntry.id}"/>
        <li>
          <a href="<c:out value="${subEntryURL}"/>">
              <%--<c:choose>
                  <c:when test="${subEntry.type == 'PLAYLIST'}">
                      <jah:media link="${subEntry.id}" var="playlist"/>
                      <c:if test="${not empty playlist.picture}">
                          <c:url var="playlistImage" value="/media/${playlist.picture.id}"/>
                          <img src="<c:out value="${playlistImage}"/>"/>
                      </c:if>
                  </c:when>
                  <c:otherwise>
                      <img src=""/>
                  </c:otherwise>
              </c:choose>--%>
            <h3><c:out value="${subEntry.name}"/></h3>
            <span class="ui-li-count"><c:out value="${subEntry.numEntries}"/></span>
            <c:url var="queueTrackURL" value="/ui/queue/add/${subEntry.id}"/>
            <%--<a href="<c:out value="${queueTrackURL}"/>" data-rel="dialog" data-transition="slideup">Enqueue
            <c:choose><c:when test="${subEntry.type == 'FOLDER'}">Folder</c:when><c:otherwise>Playlist</c:otherwise></c:choose></a>--%>
                  <c:set var="mediaId" value="${subEntry.id}" scope="request"/>
                           <jsp:include page="queue-media-link.jsp"/>

          </a>
        </li>
      </c:forEach>
    </ul>
    </div>

  </div>


  <script>

/*

    $( "li" ).bind( "taphold", function ( event )
    {
      // $(this).remove();
      // alert("tap hold")
    } );

    $( "li" ).bind( "swiperight", function ( event )
    {
      // $(this).remove();
      // alert("swipe right")
    } );
*/

  </script>

  <%@ include file="/jsp/footer-bar.jsp" %>

</div>

<%@ include file="/jsp/footer.jsp" %>

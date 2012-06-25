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

<div id="artist" data-role="page" data-theme="g" class="homeBody">


    <!-- /header -->
    <div class="mainHeaderPanel" data-role="header" role="banner" data-position="fixed">
        <h3><c:out value="${pageTitle}"/></h3>
        <a href="/jahspotify/index.html" data-icon="home" data-iconpos="notext" data-direction="reverse">Home</a>

        <c:url var="playControllerURL" value="/jsp/play-controller-dialog.jsp"/>
        <a href="<c:out value='${playControllerURL}'/>" data-icon="gear" data-rel="dialog"
           class="ui-btn-right">Player</a>
    </div>


    <div data-role="content">
        <div class="content-primary" align="center">
            <div class="ui-body ui-body-a">

                <div align="center">

                    <c:if test="${not empty artist.portraits}">
                        <c:url var="artistPortraitURL" value="/media/${artist.portraits[0].id}"/>
                        <div>
                            <img src="<c:out value="${artistPortraitURL}"/>"/>
                        </div>
                    </c:if>

                    <p style="font-weight: 900;"><c:out value="${artist.name}"/></p>

                    <c:if test="${not empty artist.bioParagraphs}">

                        <div data-role="collapsible" data-mini="true">
                            <h3>Biography</h3>

                            <div class="ui-body ui-body-a" align="left">
                                <c:forEach var="bioParagraph" items="${artist.bioParagraphs}">
                                    <p style="line-height: 1.0em"><c:out value="${bioParagraph}" escapeXml="false"/></p>
                                </c:forEach>
                            </div>
                        </div>

                    </c:if>

                    <c:if test="${not empty artist.topHitTracks}">

                        <ul data-role="listview" data-theme="a" data-inset="true"
                            data-split-icon="plus"
                            data-split-theme="a" data-count-theme="b">
                            <li data-role="list-divider" data-theme="a">Top Hits</li>

                            <c:forEach items="${artist.topHitTracks}" var="link">

                                <jah:media link="${link.id}" var="track"/>
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

                                                <%--<p style="font-weight: bold; font-size: 50%"><c:forEach
                                                        items="${track.artistNames}" var="artistName">
                                                    <c:url var="artistURL" value="/ui/media/${track.id.id}"/>
                                                    <c:out value="${artistName}"/>
                                                </c:forEach>
                                                </p>--%>
                                        </div>
                                    </a>
                                    <c:url var="queueTrackURL" value="/ui/queue/add/${track.id.id}"/>
                                    <a href="<c:out value="${queueTrackURL}"/>" data-rel="dialog"
                                       data-transition="fade">Enqueue</a>
                                </li>
                            </c:forEach>
                        </ul>


                    </c:if>

                </div>

                <div data-role="navbar" data-theme="g">
                    <ul>
                        <c:url var="artistAlbumsURL" value="/ui/media/artist-albums/${artist.id.id}"/>
                        <li><a href="<c:out value='${artistAlbumsURL}'/>" data-icon="plus">Albums</a></li>
                        <c:url var="similarArtistsURL" value="/ui/media/artist-similar/${artistLink}"/>
                        <li><a href="<c:out value='${similarArtistsURL}'/>" data-icon="grid">Similar Artists</a></li>
                        <li><a href="#" data-icon="gear">More</a></li>
                    </ul>
                </div>

            </div>
        </div>

    </div>

    <%@ include file="/jsp/footer.jsp" %>


</div>
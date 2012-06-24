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

<div id="playList" data-role="page" data-theme="g" class="homeBody">


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

                <div align="center" style="line-height: 0.5em">

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

                    <c:if test="${not empty artist.albums}">

                        <ul data-role="listview" data-theme="a" data-inset="true" data-filter="true"
                            data-split-icon="plus" data-split-theme="a" data-count-theme="b">
                            <c:forEach items="${artist.albums}" var="albumLink">
                                <jah:media link="${albumLink.id}" var="album"/>
                                <li id="<c:out value='%{albumLink.id}'/>">
                                    <c:url var="albumURL" value="/ui/media/album/${albumLink.id}"/>
                                    <a href="<c:out value="${albumURL}"/>">
                                        <c:url var="albumCoverURL" value="/media/${album.cover.id}"/>
                                        <img src="<c:out value="${albumCoverURL}"/>"/>
                                        <c:out value="${album.name}"/>
                                    </a></li>


                            </c:forEach>
                        </ul>
                    </c:if>


                </div>

                <div data-role="navbar" data-theme="g">
                    <ul>
                        <c:url var="artistAlbumsURL" value="/ui/media/artist-albums/${artistLink}"/>
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
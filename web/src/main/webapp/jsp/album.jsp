s<%--
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

<div id="album" data-role="page" data-theme="b" class="homeBody">

    <jsp:include page="/jsp/header-bar.jsp"/>

    <div data-role="content">
        <div class="content-primary" align="center">
            <div class="ui-body ui-body-b">
                <div align="center">
                    <c:if test="${not empty album.cover}">
                        <div class="loading" id="loader">
                            <jah:image link="${album.cover.id}" baseurl="/media/" context="/jahspotify"/>
                        </div>
                    </c:if>

                    <p style="font-weight: 900;"><c:out value="${album.name}"/></p>
                    <jah:media link="${album.artist.id}" var="artist"/>
                    <p style="font-weight: 900;"><c:out value="${artist.name}"/></p>

                    <p style="font-weight: 900;"><c:out value="${album.year}"/></p>

                    <p style="font-weight: 900;"><c:out value="${album.copyrights}"/></p>

                    <c:if test="${not empty album.discs}">

                        <c:forEach items="${album.discs}" var="disc" varStatus="counter">

                            <div data-role="collapsible" data-mini="true" data-collapsed="${counter.count != 1}">
                                <h3>Disc <c:out value="${counter.count}"/></h3>

                                <c:if test="${not empty disc.tracks}">

                                <ul data-role="listview" data-theme="b" data-inset="true" data-split-icon="gear"
                                    data-split-theme="b" data-count-theme="b">

<%--
                                    <ul data-role="listview" data-theme="b" data-inset="true"
                                        data-split-icon="plus"
                                        data-split-theme="a" data-count-theme="b">
--%>

                                        <c:forEach items="${disc.tracks}" var="link">

                                            <jah:fulltrack link="${link.id}" var="track"/>

                                            <li id="<c:out value='%{track.id.id}'/>">
                                                <c:url var="trackURL" value="/ui/media/track/${track.id.id}"/>
                                                <a href="<c:out value="${trackURL}"/>">
                                                    <jah:duration var="duration" duration="${track.length}"/>

                                                    <jah:image link="${track.albumCoverLink.id}" baseurl="/media/"context="/jahspotify"/>

                                                    <div>
                                                        <h4><c:out value="${track.title}"/> <span style="vertical-align: middle; font-weight: lighter; font-size: 60%">(<c:out value="${duration}"/>)</span></h4>

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

                                                <c:set var="mediaId" value="${track.id.id}" scope="request"/>
                                                <jsp:include page="queue-media-link.jsp"/>

                                            </li>


                                        </c:forEach>

                                    </ul>
                                </c:if>
                            </div>
                        </c:forEach>

                    </c:if>

                </div>


            </div>


        </div>
    </div>

    <%@ include file="/jsp/footer-bar.jsp" %>

</div>

<%@ include file="/jsp/footer.jsp" %>

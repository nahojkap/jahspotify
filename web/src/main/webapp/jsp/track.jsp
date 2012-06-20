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

                <div data-role="controlgroup" data-type="horizontal" align="right">
                    <a href="index.html" data-role="button" data-icon="star" data-iconpos="notext">Star this track</a>
                    <a href="index.html" data-role="button" data-icon="plus" data-iconpos="notext">Queue Track</a>
                </div>

                <div align="center" style="line-height: 0.5em">
                    <jah:duration var="duration" value="${track.length}"/>
                    <c:url var="albumCoverURL" value="/media/${track.albumCoverLink.id}"/>
                    <div>
                        <img src="<c:out value='${albumCoverURL}'/>"/>
                    </div>

                    <p style="font-weight: 900;"><c:out value="${track.title}"/> (<c:out value="${duration}"/>)</p>
                    <p style="font-weight: bold; font-size: 80%"><c:out value="${track.albumName}"/></p>
                    <p style="font-weight: bold; font-size: 70%"><c:forEach items="${track.artistNames}" varStatus="rowCounter" var="artistName">
                        <c:set var="artistLink" value="${track.artistLinks[rowCounter.count]}"/>
                        <c:out value="${artistName}"/>
                        <c:if test="${not rowCounter.last}">, </c:if>
                    </c:forEach>
                    </p>
                </div>

                <div data-role="navbar" data-theme="g">
                    <ul>
                        <li><a href="#" data-icon="plus">Add To</a></li>
                        <li><a href="#" data-icon="grid">Queue</a></li>
                        <li><a href="#" data-icon="people">Artist</a></li>
                        <li><a href="#" data-icon="gear">More</a></li>
                    </ul>
                </div>
                <!-- /navbar -->
                <%--

                                <label for="browse-artists" class="select">Browse Artist(s):</label>
                                <select name="browse-artists" id="browse-artists" data-mini="true">
                                    <c:forEach items="${track.artistNames}" varStatus="rowCounter" var="artistName">
                                        <c:set var="artistLink" value="${track.artistLinks[rowCounter.count]}"/>
                                        <option value="<c:out value='${artistLink}'/>"><c:out value="${artistName}"/></option>
                                    </c:forEach>
                                </select>
                --%>

            </div>
        </div>

    </div>

    <%@ include file="/jsp/footer.jsp" %>


</div>
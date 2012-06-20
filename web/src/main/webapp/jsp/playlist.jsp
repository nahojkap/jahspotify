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
        <a href="/jahspotify/index.html" data-icon="home" data-iconpos="notext" data-direction="reverse">Home</a>
        <h1><c:out value="${pageTitle}"/></h1>
        <c:url var="playControllerURL" value="/jsp/play-controller-dialog.jsp"/>
        <a href="<c:out value='${playControllerURL}'/>" data-icon="gear" data-rel="dialog" class="ui-btn-right" data-iconpos="notext">Player</a>
    </div>


    <div data-role="content">
        <div class="content-primary">
            <ul data-role="listview" data-theme="a" data-inset="true" data-filter="true" data-split-icon="plus"
                data-split-theme="a" data-count-theme="b">
                <c:forEach items="${tracks}" var="track">
                    <c:url var="trackURL" value="/ui/media/${track.id.id}"/>
                    <li>
                        <a href="<c:out value="${trackURL}"/>" >
                            <jah:duration var="duration" value="${track.length}"/>
                            <c:url var="albumCoverURL" value="/media/${track.albumCoverLink.id}"/>
                            <img src="<c:out value="${albumCoverURL}"/>"/>
                            <h3><c:out value="${track.title}"/> [<c:out value="${duration}"/>]</h3>
                            <p>
                                <strong>
                                    <c:out value="${track.albumName}"/> &#183;
                                    <c:forEach items="${track.artistNames}" var="artistName">
                                        <c:url var="artistURL" value="/ui/media/${track.id.id}"/>
                                        <c:out value="${artistName}"/>
                                    </c:forEach>
                                </strong>
                            </p>
                        </a>
                        <c:url var="queueTrackURL" value="/ui/queue/add/${track.id.id}"/>
                        <a href="<c:out value="${queueTrackURL}"/>" data-rel="dialog" data-transition="slideup">Queue
                            Track</a>
                    </li>
                </c:forEach>
            </ul>
        </div>

    </div>

    <%@ include file="/jsp/footer.jsp" %>


</div>
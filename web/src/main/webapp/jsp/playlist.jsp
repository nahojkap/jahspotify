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

<!-- /header -->
<div class="mainHeaderPanel" data-position="inline" data-role="header" role="banner">
    <h1><c:out value="${pageTitle}"/></h1>
    <a href="/jahspotify/index.html" data-icon="home" data-iconpos="notext" data-direction="reverse">Home</a>
</div>


<div data-role="content">
    <div class="content-primary">
        <ul data-role="listview" data-theme="a" data-inset="true" data-filter="true" data-split-icon="plus"
            data-split-theme="a" data-count-theme="b">
            <c:forEach items="${tracks}" var="track">
                <c:url var="trackURL" value="/ui/media/${track.id.id}"/>
                <li>
                    <a href="<c:out value="${trackURL}"/>">
                        <c:url var="albumCoverURL" value="/media/${track.albumCoverLink.id}"/>
                        <img src="<c:out value="${albumCoverURL}"/>"/>
                        <h3><c:out value="${track.title}"/></h3>
                        <p><strong><c:out value="${track.artistNames}"/></strong></p>
                        <p><strong>(<c:out value="${track.length}"/>)</strong></p>
                    </a>
                    <c:url var="queueTrackURL" value="/queue/jahspotify:queue:default/add/${track.id.id}"/>
                    <a href="<c:out value="${queueTrackURL}"/>" data-rel="dialog" data-transition="slideup">Queue Track</a>
                </li>
            </c:forEach>
        </ul>
    </div>

</div>
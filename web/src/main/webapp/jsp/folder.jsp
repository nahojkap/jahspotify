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
<div class="mainHeaderPanel" data-role="header" role="banner" data-position="fixed">
    <h1><c:out value="${pageTitle}"/></h1>
    <a href="/jahspotify/index.html" data-icon="home" data-iconpos="notext" data-direction="reverse">Home</a>

    <c:url var="playControllerURL" value="/jsp/play-controller-dialog.jsp"/>
    <a href="<c:out value='${playControllerURL}'/>" data-icon="gear" data-rel="dialog" class="ui-btn-right" data-iconpos="notext"  data-role="button">Player</a>

</div>


<div data-role="content">

  <ul data-role="listview" data-theme="a" data-inset="true" data-filter="true" data-split-icon="plus"
        data-split-theme="a" data-count-theme="c">
        <c:forEach items="${entry.subEntries}" var="subEntry">
            <c:url var="subEntryURL" value="/ui/media/library/${subEntry.id}"/>
            <li >


                <a href="<c:out value="${subEntryURL}"/>" >
                    <h3><c:out value="${subEntry.name}"/></h3>
                    <span class="ui-li-count"><c:out value="${subEntry.numEntries}"/></span>
                    <c:url var="queueTrackURL" value="/ui/queue/add/${subEntry.id}"/>
                    <a href="<c:out value="${queueTrackURL}"/>" data-rel="dialog" data-transition="slideup">Queue
                        <c:choose><c:when
                                test="${subEntry.type == 'FOLDER'}">Folder</c:when><c:otherwise>Playlist</c:otherwise></c:choose></a>
                </a>
            </li>
        </c:forEach>
    </ul>

</div>

<%@ include file="/jsp/footer.jsp" %>


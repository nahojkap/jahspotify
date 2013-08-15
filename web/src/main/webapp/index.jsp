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

<div id="homePage" data-role="page" data-theme="b" class="homeBody">

    <c:set var="pageTitle" value="Jah'Spotify" scope="request"/>
    <jsp:include page="/jsp/header-bar.jsp"/>

    <div class="mainContentPanel" data-role="content">

        <ul data-role="listview" data-theme="b" data-inset="true" data-split-icon="arrow-le" data-split-theme="b" data-count-theme="b">

            <li data-role="list-divider" data-theme="a"></li>

            <li>
                <c:url var="inboxURL" value="/ui/media/library/jahspotify:inbox"/>
                <a class="wrapper" href="<c:out value='${inboxURL}'/>" id="INBOX">Inbox</a>
            </li>

            <li>
                <c:url var="starredURL" value="/ui/media/library/jahspotify:starred"/>
                <a class="wrapper" href="<c:out value='${starredURL}'/>" id="STARRED">Starred</a>
            </li>

            <li>
                <c:url var="playlistsURL" value="/ui/media/library/jahspotify:folder:ROOT"/>
                <a class="wrapper" href="<c:out value='${playlistsURL}'/>" id="PLAYLISTS">Playlists</a>
            </li>

            <li>
                <c:url var="searchURL" value="/ui/search"/>
                <a class="wrapper" href="<c:out value='${searchURL}'/>" id="SEARCH">Search</a>
            </li>

            <li data-role="list-divider" data-theme="a"></li>

            <li>
                <c:url var="queueURL" value="/ui/queue/current"/>
                <a class="wrapper" href="<c:out value='${queueURL}'/>" id="QUEUE">Queue</a>
            </li>

            <li>
                <c:url var="historyURL" value="/ui/history/recent?count=50"/>
                <a class="wrapper" href="<c:out value='${historyURL}'/>" id="HISTORY">History</a>
            </li>

            <li data-role="list-divider" data-theme="a"></li>

            <li>
                <c:url var="serverSettingsURL" value="/ui/history/recent"/>
                <a class="wrapper" href="serverSettings" id="SERVERSETTINGS">Server Settings</a>
            </li>

            <li data-role="list-divider" data-theme="a"></li>

        </ul>
    </div>

    <%@ include file="/jsp/footer-bar.jsp" %>

</div>

<%@ include file="/jsp/footer.jsp" %>

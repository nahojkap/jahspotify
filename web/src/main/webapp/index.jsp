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

<div id="homePage" data-role="page" data-theme="g" class="homeBody">

  <c:set var="pageTitle" value="Jah'Spotify" scope="request"/>
  <jsp:include page="/jsp/header-bar.jsp"/>

  <div class="mainContentPanel" data-role="content">

    <ul data-role="listview" data-theme="a" data-inset="true" data-split-icon="arrow-le"
        data-split-theme="a" data-count-theme="b">
      <li data-role="list-divider" data-theme="a"></li>

      <li><a class="wrapper" href="ui/media/library/jahspotify:inbox" id="Inbox">Inbox</a></li>
      <li><a class="wrapper" href="ui/media/library/jahspotify:starred" id="Starred">Starred</a></li>
      <li><a class="wrapper" href="ui/media/library/jahspotify:folder:ROOT" id="Playlists" data-ajax="false">Playlists</a></li>

      <li><a class="wrapper" href="ui/search" id="Search">Search</a></li>

      <li data-role="list-divider" data-theme="a"></li>
      <li>
        <a class="wrapper" href="ui/queue/current" id="Queue">Queue</a>
      </li>
      <li>
        <a class="wrapper" href="ui/history/recent" id="History">History</a>
      </li>
      <li data-role="list-divider" data-theme="a"></li>
      <li>
        <a class="wrapper" href="serverSettings" id="ServerSettings">Server Settings</a>
      </li>
      <li data-role="list-divider" data-theme="a"></li>

    </ul>
  </div>


  <div data-role="footer" style="text-align: left;" class="ui-bar" data-theme="a" data-position="fixed">
    <div style="line-height: 0.1em;">
      <p style="text-align: left; font-size: 60%">&copy; 2012 Jah'Spotify</p>

      <p style="text-align: left; font-size: 40%">Powered by Spotify&trade; Core & The EchoNest API</p>
    </div>
  </div>

</div>

<%@ include file="/jsp/footer.jsp" %>

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

<div id="playList" data-role="page" data-theme="g">

    <!-- /header -->
    <div class="mainHeaderPanel" data-position="inline" data-role="header" role="banner">
        <h1><c:out value="${pageTitle}"/></h1>
        <a href="<c:out value='${homeURL}'/>" data-icon="home" data-iconpos="notext" data-direction="reverse">Home</a>
        <c:url var="queueSettingsURL" value="/jsp/queue-settings-dialog.jsp"/>
        <a href="<c:out value='${queueSettingsURL}'/>" data-icon="gear" data-rel="dialog"
           class="ui-btn-right">Options</a>
    </div>


    <div data-role="footer">
        <div data-role="navbar">
            <ul>
                <li><a href="a.html" class="ui-btn-active">Tracks</a></li>
                <li><a href="b.html">Albums</a></li>
                <li><a href="c.html">Artists</a></li>
            </ul>
        </div>

        </div>

    <%@ include file="/jsp/footer.jsp" %>

</div>
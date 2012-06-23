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

<div id="playList" data-role="page" data-theme="g" class="homeBody">

    <!-- /header -->
    <div class="mainHeaderPanel" data-theme="o" data-position="inline" data-role="header" role="banner">
        <h1><c:out value="${pageTitle}"/></h1>
    </div>


    <div data-role="content" data-inset="true" class="ui-body ui-body-a">

        <div class="content-primary" data-theme="a" data-inset="true" class="ui-body ui-body-a">

            <ul data-role="listview" data-theme="a" data-count-theme="b">
                <c:forEach items="${artists}" var="artist" varStatus="rowCounter">
                    <c:url var="artistURL" value="/ui/media/artist/${artist.id.id}"/>
                    <li id="<c:out value='%{artist.id.id}'/>">
                        <a href="<c:out value="${artistURL}"/>">
                            <c:if test="${not empty artist.portraits}">
                                <c:url var="artistPortraitURL" value="/media/${artist.portraits[0].id}"/>
                                <img src="<c:out value="${artistPortraitURL}"/>"/>
                            </c:if>
                            <h4><c:out value="${artist.name}"/></h4>
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>

    <%@ include file="/jsp/dialog-footer.jsp" %>

</div>
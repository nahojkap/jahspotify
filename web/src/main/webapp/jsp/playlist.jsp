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

  <jsp:include page="/jsp/header-bar.jsp" />

    <div data-role="content">
        <div class="content-primary">
            <ul data-role="listview" data-theme="a" data-inset="true" data-filter="true" data-split-icon="plus"
                data-split-theme="a" data-count-theme="b">
                <c:forEach items="${tracks}" var="track">
                    <c:url var="trackURL" value="/ui/media/track/${track.id.id}"/>
                    <li id="<c:out value='${track.id.id}'/>">
                        <a href="<c:out value="${trackURL}"/>" >
                            <jah:duration var="duration" duration="${track.length}"/>
                            <c:url var="albumCoverURL" value="/media/${track.albumCoverLink.id}"/>
                            <img src="<c:out value="${albumCoverURL}"/>"/>
                            <div>
                            <h4><c:out value="${track.title}"/> <span style="vertical-align: middle; font-weight: lighter; font-size: 60%">(<c:out value="${duration}"/>)</span></h4>
                            <p style="font-weight: bold; font-size: 65%">
                                <c:out value="${track.albumName}"/></p>
                                <p style="font-weight: bold; font-size: 50%"><c:forEach items="${track.artistNames}" var="artistName">
                                    <c:url var="artistURL" value="/ui/media/${track.id.id}"/>
                                    <c:out value="${artistName}"/>
                                </c:forEach>
                            </p>
                            </div>
                        </a>

                        <c:set var="mediaId" value="${track.id.id}" scope="request"/>
                        <jsp:include page="queue-media-link.jsp"/>

<%--
                        <c:url var="queueTrackURL" value="/ui/queue/add/${track.id.id}"/>
                        <a href="<c:out value="${queueTrackURL}"/>" data-rel="dialog" data-transition="fade">Enqueue</a>
--%>

                    </li>
                </c:forEach>
            </ul>
        </div>

    </div>

    <script>
        $("li").bind ("swiperight", function (event)

        {
          alert("Swiping : " + this.id);

        });
    </script>

    <%@ include file="/jsp/footer-bar.jsp" %>

</div>

<%@ include file="/jsp/footer.jsp" %>

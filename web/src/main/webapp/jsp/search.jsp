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


<div id="searchStart" data-role="page" data-theme="b" class="homeBody">

    <c:set var="pageTitle" value="Search" scope="request"/>
    <jsp:include page="/jsp/header-bar.jsp"/>

    <div class="mainContentPanel" data-role="content">
        <div class="content-primary">
            <div class="ui-body ui-body-b">
                <c:url var="searchExectuteURL" value="/ui/search/execute"/>
                <form action="<c:out value='${searchExectuteURL}'/>" method="get">
                    <div data-role="fieldcontain" >
                        <label for="query">Search Query:</label>
                        <c:if test="${not empty searchResult}">
                            <c:set var="previousQuery" value="${searchResult.query}"/>
                        </c:if>
                        <input type="text" name="query" id="query" value="<c:out value='${previousQuery}'/>"
                               placeholder="Search Query"/>
                    </div>

                    <div data-role="fieldcontain">
                        <fieldset data-role="controlgroup" data-mini="true">
                            <legend>Scope:</legend>

                            <input type="checkbox" name="artists" id="artists" class="custom"/>
                            <label for="artists">Artists</label>

                            <input type="checkbox" name="tracks" id="tracks" class="custom"/>
                            <label for="tracks">Tracks</label>

                            <input type="checkbox" name="albums" id="albums" class="custom"/>
                            <label for="albums">Albums</label>

                        </fieldset>
                    </div>

                    <div data-role="fieldcontain" data-mini="true">
                        <label for="numResults">Results per Page:</label>
                        <input data-mini="true" type="range" name="numResults" id="numResults" value="20" min="1"
                               max="255"
                               data-highlight="true"/>
                    </div>

                    <div>
                        <fieldset>
                            <div>
                                <button type="submit" data-mini="true">Search</button>
                            </div>
                        </fieldset>
                    </div>

                </form>
            </div>
        </div>
    </div>

    <%@ include file="/jsp/footer-bar.jsp" %>

</div>

<%@ include file="/jsp/footer.jsp" %>

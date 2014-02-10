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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div data-theme="b" data-role="header">
    <h1><c:out value="${pageTitle}"/></h1>
    <a href="<c:out value='${homeURL}'/>" data-icon="home" data-iconpos="notext" data-direction="reverse" rel="external">Home</a>
    <a href="#ctrlpanel" data-theme="b" data-icon="gear" data-role="button" class="ui-btn-right" id="controlPanelButton" data-iconpos="notext">Control Panel</a>
</div>


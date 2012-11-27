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
<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:url var="homeURL" value="/index.jsp" scope="request"/>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>

    <title>Jah'Spotify</title>

    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <c:url var="jqueryURL" value="/scripts/jquery-1.8.1.min.js"/>
    <script src="${jqueryURL}"></script>

    <c:url var="jqueryMobileURL" value="/scripts/jquery.mobile-1.2.0-rc.2.min.js"/>
    <script src="${jqueryMobileURL}"></script>

    <c:url var="jqueryMobileCSSURL" value="/css/jquery.mobile-1.2.0-rc.2.min.css"/>
    <link href="<c:out value='${jqueryMobileCSSURL}'/>" type="text/css" rel="stylesheet">

    <c:url var="cssURL" value="/css/jahspotify-main.css"/>
    <link href="<c:out value='${cssURL}'/>" type="text/css" rel="stylesheet">

    <c:url var="timerURL" value="/scripts/jquery.timer.js"/>
    <script src="<c:out value='${timerURL}'/>"></script>

    <c:url var="timeagoURL" value="/scripts/jquery.timeago.js"/>
    <script src="<c:out value='${timeagoURL}'/>"></script>

    <c:url var="jahspotifyURL" value="/scripts/jahspotify.js"/>
    <script src="<c:out value='${jahspotifyURL}'/>"></script>


    <script>
        $(document).bind("mobileinit", function ()
        {
            $.mobile.ajaxEnabled = false;
            $.mobile.pushStateEnabled = false;
        });

        $(document).bind('pageinit', function ()
        {
            jQuery("abbr.timeago").timeago();
        });
    </script>

</head>


<body>
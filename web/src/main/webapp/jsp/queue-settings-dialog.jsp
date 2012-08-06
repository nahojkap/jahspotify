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
<div class="mainHeaderPanel" data-theme="o" data-position="inline" data-role="header" role="banner">
    <h1><c:out value="${pageTitle}"/></h1>
</div>

<script>

    function clearQueue()
    {
        $.mobile.showPageLoadingMsg();
        $.ajax({
            url:"/jahspotify/queue/jahspotify:queue:default/clear"
        }).done(function ()
                {
                    $.mobile.hidePageLoadingMsg();
                    $('.ui-dialog').dialog('close');

                });


    }

</script>

<div data-role="content" data-inset="true" class="ui-body ui-body-a">
    <div data-theme="a" data-inset="true" class="ui-body ui-body-a">

        <form action="#" method="get">

            <div data-role="fieldcontain">
                <label for="shuffle">Shuffle</label>
                <select name="shuffle" id="shuffle" data-role="slider">
                    <option value="off">Off</option>
                    <option value="on">On</option>
                </select>
            </div>

            <div data-role="fieldcontain">
                <label for="repeat">Repeat:</label>
                <select name="repeat" id="repeat" data-role="slider">
                    <option value="off">Off</option>
                    <option value="on">On</option>
                </select>
            </div>

            <div data-role="fieldcontain">
                <label for="repeat">Auto-Fill Empty Queue:</label>
                <select name="autofill" id="autofill" data-role="slider">
                    <option value="off">Off</option>
                    <option value="on">On</option>
                </select>
            </div>

        </form>
    </div>

    <div class="content-primary" data-theme="a" data-inset="true" class="ui-body ui-body-a">


        <div data-role="fieldcontain">
            <label for="clearQueue"></label>
            <a href="#" id="clearQueue" data-role="button" onclick="clearQueue()">Clear Queue</a>
        </div>


        <%--
                   <div data-role="fieldcontain">
                       <label for="slider2">Flip switch:</label>
                       <select name="slider2" id="slider2" data-role="slider">
                           <option value="off">Off</option>
                           <option value="on">On</option>
                       </select>
                   </div>

                   <div data-role="fieldcontain">
                       <fieldset data-role="controlgroup">
                           <legend>Choose as many snacks as you'd like:</legend>
                           <input type="checkbox" name="checkbox-1a" id="checkbox-1a" class="custom"/>
                           <label for="checkbox-1a">Repeat Current Track</label>

                           <input type="checkbox" name="checkbox-2a" id="checkbox-2a" class="custom"/>
                           <label for="checkbox-2a">Repeat Current Queue</label>

                       </fieldset>
                   </div>


       <div data-role="fieldcontain">
           <fieldset data-role="controlgroup">
               <legend>Queue Mode:</legend>
               <input type="radio" name="radio-choice-1" id="radio-choice-1" value="choice-1" checked="checked"/>
               <label for="radio-choice-1">Repeat Current Track</label>

               <input type="radio" name="radio-choice-1" id="radio-choice-2" value="choice-2"/>
               <label for="radio-choice-2">Repeat Current Queue</label>

               <input type="radio" name="radio-choice-1" id="radio-choice-4" value="choice-4"/>
               <label for="radio-choice-4">Lizard</label>

           </fieldset>
       </div>
        --%>

    </div>
</div>

<%@ include file="/jsp/dialog-footer.jsp" %>

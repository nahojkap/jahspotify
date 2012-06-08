package jahspotify.web.api;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *        or more contributor license agreements.  See the NOTICE file
 *        distributed with this work for additional information
 *        regarding copyright ownership.  The ASF licenses this file
 *        to you under the Apache License, Version 2.0 (the
 *        "License"); you may not use this file except in compliance
 *        with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing,
 *        software distributed under the License is distributed on an
 *        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *        KIND, either express or implied.  See the License for the
 *        specific language governing permissions and limitations
 *        under the License.
 */

import javax.servlet.http.*;

import jahspotify.web.*;
import org.apache.commons.logging.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
public class ConfigurationController extends BaseController
{
    private Log _log = LogFactory.getLog(ConfigurationController.class);

    @RequestMapping(value = "/system/intialize", method = RequestMethod.POST)
    public void initialize(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(jahspotify.web.ResponseStatus.OK);
        simpleStatusResponse.setDetail("SYSTEM_INITIALIZED");
        writeResponse(httpServletResponse, simpleStatusResponse);
    }


}

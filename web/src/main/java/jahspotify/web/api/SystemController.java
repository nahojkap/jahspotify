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

import jahspotify.services.QueueManager;
import jahspotify.web.system.SystemStatus;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
@RequestMapping(value = "/system", method = RequestMethod.GET)
public class SystemController extends BaseController
{
    @Autowired
    private QueueManager _queueManager;

    private Log _log = LogFactory.getLog(SystemController.class);

    private long _upSince = System.currentTimeMillis();

    @RequestMapping(value = "/status", method = RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public SystemStatus status()
    {
        _log.debug("Request for the system status");

        final jahspotify.services.QueueStatus queueStatus = _queueManager.getQueueStatus(QueueManager.DEFAULT_QUEUE_LINK);

        SystemStatus systemStatus = new SystemStatus();
        systemStatus.setQueueStatus(QueueWebHelper.convertToWebQueueStatus(queueStatus));

        systemStatus.setQueueConfiguration(QueueWebHelper.convertToWebQueueConfiguration(_queueManager.getQueueConfiguration(QueueManager.DEFAULT_QUEUE_LINK)));
        systemStatus.setCurrentQueue(_queueManager.getCurrentQueue(1).getId().asString());

        systemStatus.setUpSince(_upSince);

        systemStatus.setTotalMemory(Runtime.getRuntime().totalMemory());
        systemStatus.setMaxMemory(Runtime.getRuntime().maxMemory());
        systemStatus.setFreeMemory(Runtime.getRuntime().freeMemory());

        systemStatus.setNumberProcessors(Runtime.getRuntime().availableProcessors());

        return systemStatus;

    }

    @RequestMapping(value = "/cache/info", method = RequestMethod.GET,produces = "application/json")
    public void listCaches(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
    }

    @RequestMapping(value = "/cache/purge", method = RequestMethod.GET,produces = "application/json")
    public void purgeCaches(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
    }

}

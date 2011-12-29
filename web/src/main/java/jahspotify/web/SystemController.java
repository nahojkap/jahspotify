package jahspotify.web;

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

import jahspotify.service.QueueManager;
import jahspotify.web.queue.*;
import jahspotify.web.system.SystemStatus;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
public class SystemController extends BaseController
{
    @Autowired
        QueueManager _queueManager;

    private Log _log = LogFactory.getLog(SystemController.class);

    private long _upSince = System.currentTimeMillis();

    @RequestMapping(value = "/system/status", method = RequestMethod.GET)
    public void status(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _log.debug("Request for the system status");

        final jahspotify.service.QueueStatus queueStatus = _queueManager.getQueueStatus();

        SystemStatus systemStatus = new SystemStatus();
        systemStatus.setQueueStatus(convertToWeb(queueStatus));

        systemStatus.setUpSince(_upSince);

        systemStatus.setTotalMemory(Runtime.getRuntime().totalMemory());
        systemStatus.setMaxMemory(Runtime.getRuntime().maxMemory());
        systemStatus.setFreeMemory(Runtime.getRuntime().freeMemory());

        systemStatus.setNumberProcessors(Runtime.getRuntime().availableProcessors());

        writeResponseGeneric(httpServletResponse, systemStatus);

    }

    private QueueStatus convertToWeb(final jahspotify.service.QueueStatus queueStatus)
    {
        QueueStatus webQueueStatus = new QueueStatus();

        webQueueStatus.setQueueState(QueueWebHelper.convertToQueueStatus(queueStatus.getMediaPlayerState()));
        webQueueStatus.setCurrentQueueSize(queueStatus.getCurrentQueueSize());
        webQueueStatus.setMaxQueueSize(queueStatus.getMaxQueueSize());
        webQueueStatus.setQueueState(QueueState.valueOf(queueStatus.getMediaPlayerState().name()));
        webQueueStatus.setTotalPlaytime(queueStatus.getTotalPlaytime());
        webQueueStatus.setTotalTracksCompleted(queueStatus.getTotalTracksCompleted());
        webQueueStatus.setTotalTracksPlayed(queueStatus.getTotalTracksPlayed());
        webQueueStatus.setTotalTracksSkipped(queueStatus.getTotalTracksSkipped());

        return webQueueStatus;
    }



}

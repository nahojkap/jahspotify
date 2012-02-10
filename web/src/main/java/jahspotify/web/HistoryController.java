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

import jahspotify.storage.statistics.HistoricalStorage;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
public class HistoryController extends BaseController
{
    @Autowired
    @Qualifier(value ="in-memory")
    private HistoricalStorage _historicalStorage;

    @Value(value="${jahspotify.history.default-count}")
    private int _defaultCount = 100;

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public void getHistory(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        int index = Integer.parseInt(httpServletRequest.getParameter("index") == null ? "0" : httpServletRequest.getParameter("index"));
        int count = Integer.parseInt(httpServletRequest.getParameter("index") == null ? Integer.toString(_defaultCount) : httpServletRequest.getParameter("count"));
        writeResponseGeneric(httpServletResponse,_historicalStorage.getHistory(index,count,null));
    }

    @RequestMapping(value = "/history/", method = RequestMethod.GET)
    public void getHistoryRoot(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
    }

}

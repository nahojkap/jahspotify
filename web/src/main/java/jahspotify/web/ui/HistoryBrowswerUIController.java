package jahspotify.web.ui;

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

import java.util.Collection;

import jahspotify.storage.statistics.*;
import jahspotify.web.api.BaseController;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Johan Lindquist
 */
@Controller
@RequestMapping(value = "/ui/history")
public class HistoryBrowswerUIController extends BaseController
{
    @Autowired
    @Qualifier(value = "mongodb")
    HistoricalStorage _historicalStorage;

    @RequestMapping(value = "/recent")
    public ModelAndView retrieveHistory()
    {
        final ModelAndView modelAndView = new ModelAndView("/jsp/history.jsp");

        final Collection<TrackHistory> trackHistories = _historicalStorage.getHistory(0,200);

        Collection<TrackHistory> today = getTodays(trackHistories);
        Collection<TrackHistory> yesteday = getYesterdays(trackHistories);

        modelAndView.addObject("today",today);
        modelAndView.addObject("yesterday",yesteday);
        modelAndView.addObject("trackHistories",trackHistories);

        return modelAndView;

    }

    private Collection<TrackHistory> getYesterdays(final Collection<TrackHistory> trackHistories)
    {
        return trackHistories;
    }

    private Collection<TrackHistory> getTodays(final Collection<TrackHistory> trackHistories)
    {
        return trackHistories;
    }


}

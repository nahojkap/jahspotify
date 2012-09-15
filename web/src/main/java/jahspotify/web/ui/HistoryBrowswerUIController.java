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

import java.util.*;

import jahspotify.media.Link;
import jahspotify.storage.statistics.*;
import jahspotify.web.api.BaseController;
import org.joda.time.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ModelAndView retrieveHistory(@RequestParam(value = "index", defaultValue = "0") int index, @RequestParam(value = "count",defaultValue = "${jahspotify.history.default-count}")int count)
    {
        final ModelAndView modelAndView = new ModelAndView("/jsp/history.jsp");
        final Collection<TrackHistory> trackHistories = _historicalStorage.getHistory(index, count);
        modelAndView.addObject("trackHistories", trackHistories);
        modelAndView.addObject( "index", index );
        modelAndView.addObject( "count", count );
        return modelAndView;
    }

    @RequestMapping(value = "/trackhistory/{queueTrackLink}")
    public ModelAndView trackHistoryDetail(@PathVariable(value = "queueTrackLink") String queueTrackLink)
    {
        final Link link = Link.create(queueTrackLink);

        final TrackHistory trackHistory = _historicalStorage.getHistory(link);
        final TrackStatistics trackStatistics = _historicalStorage.getTrackStatistics(trackHistory.getTrackLink());

        final ModelAndView modelAndView = new ModelAndView("/jsp/track-history-detail.jsp");
        modelAndView.addObject("trackStatistics", trackStatistics);
        return modelAndView;
    }

}

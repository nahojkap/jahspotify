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

import java.io.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

import com.google.gson.Gson;
import jahspotify.storage.statistics.*;
import jahspotify.storage.statistics.TrackHistory;
import org.springframework.beans.BeanUtils;
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
    @Qualifier(value = "mongodb")
    private HistoricalStorage _historicalStorage;

    @RequestMapping(value = "/history/count", method = RequestMethod.GET,produces = "application/json")
    public void getHistoryCount(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        final int count = _historicalStorage.getHistoryCount(null);
    }

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public void getHistory(final HttpServletResponse httpServletResponse, @RequestParam(value = "index", defaultValue = "0") int index, @RequestParam(value = "count",defaultValue = "${jahspotify.history.default-count}")int count)
    {
        final HistoryCursor history = _historicalStorage.getHistory(index, count, null);
        httpServletResponse.setContentType("application/json");
        serializeHistoryCursor(history, httpServletResponse);
    }

    public void serializeHistoryCursor(HistoryCursor historyCursor, HttpServletResponse httpServletResponse)
    {
        try
        {
            final ServletOutputStream httpOutputStream = httpServletResponse.getOutputStream();
            final BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(httpOutputStream));
            outputStream.write("{");
            outputStream.write("\"count\":");
            outputStream.write("" + historyCursor.getCount());

            if (historyCursor.getCount() > 0)
            {
                Gson gson = new Gson();

                outputStream.write(",");
                outputStream.write("\"tracks\":[");
                while (historyCursor.hasNext())
                {
                    outputStream.write(gson.toJson(toWebTrack(historyCursor.next())));
                    if (historyCursor.hasNext())
                    {
                        outputStream.write(",");
                    }
                    outputStream.flush();
                }
                outputStream.write("]");
            }
            outputStream.write("}");
            outputStream.flush();
            outputStream.close();
            httpOutputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private jahspotify.web.media.TrackHistory toWebTrack(final TrackHistory next)
    {
        jahspotify.web.media.TrackHistory trackHistory = new jahspotify.web.media.TrackHistory();
        trackHistory.setTrackLink(toWebLink(next.getTrackLink()));
        trackHistory.setQueue(toWebLink(next.getQueue()));
        BeanUtils.copyProperties(next,trackHistory,new String[] { "trackLink", "queue"});
        return trackHistory;
    }


    @RequestMapping(value = "/history", method = RequestMethod.POST, produces = "application/json", headers="Accept=application/json")
    public void postHistory(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
    }

}

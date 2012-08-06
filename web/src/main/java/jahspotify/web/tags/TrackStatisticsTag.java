package jahspotify.web.tags;

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

import javax.servlet.jsp.tagext.Tag;

import jahspotify.media.Link;
import jahspotify.storage.statistics.*;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * @author Johan Lindquist
 */
public class TrackStatisticsTag extends RequestContextAwareTag
{
    private String _var;
    private String _link;

    public void setVar(String var)
    {
        _var = var;
    }

    public void setLink(final String link)
    {
        _link = link;
    }

    @Override
    protected int doStartTagInternal() throws Exception
    {
        final HistoricalStorage historicalStorage = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext()).getBean(MongoDBHistoricalStorage.class);
        final TrackStatistics trackStatistics = historicalStorage.getTrackStatistics(Link.create(_link));
        pageContext.setAttribute(_var, trackStatistics);
        return Tag.SKIP_BODY;

    }
}

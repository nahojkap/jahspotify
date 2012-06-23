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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.*;

/**
 * @author Johan Lindquist
 */
public class DurationFormatTag extends TagSupport
{

    private String _var;
    private long _duration;

    public void setVar(String var)
    {
        _var = var;
    }
    
    public void setDuration(long duration)
    {
        _duration = duration;
    }

    @Override
    public int doEndTag() throws JspException
    {
        pageContext.setAttribute(_var,formatDuration(_duration));
        return Tag.SKIP_BODY;
    }

    private String formatDuration(final long duration)
    {
        long durationSeconds = duration / 1000;
        long hours = durationSeconds / 3600;
        long remaining = durationSeconds % 3600;
        long minutes = remaining / 60;
        minutes = minutes + (hours * 60);
        long seconds = remaining % 60;

        StringBuilder sb = new StringBuilder();
        /*if (hours < 10)
        {
           sb.append('0');
        }
        sb.append(hours);
        sb.append(':');*/

        if (minutes < 10)
        {
           sb.append('0');
        }
        sb.append(minutes);
        sb.append(':');

        if (seconds < 10)
        {
           sb.append('0');
        }
        sb.append(seconds);
        return sb.toString();
    }
}

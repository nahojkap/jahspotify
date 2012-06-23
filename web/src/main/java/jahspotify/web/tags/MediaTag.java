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

import jahspotify.JahSpotify;
import jahspotify.media.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * @author Johan Lindquist
 */
public class MediaTag extends RequestContextAwareTag
{
    @Autowired
    private JahSpotify _jahSpotify;

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
        pageContext.setAttribute(_var,_jahSpotify.readTrack(Link.create(_link)));
        return Tag.SKIP_BODY;
    }
}

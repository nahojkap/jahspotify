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

import jahspotify.JahSpotify;
import jahspotify.media.*;
import jahspotify.services.QueueManager;
import jahspotify.web.api.BaseController;
import jahspotify.web.media.FullTrack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Johan Lindquist
 */
@Controller
@RequestMapping(value = "/ui/media")
public class MediaBrowserUIController extends BaseController
{
    @Autowired
    JahSpotify _jahSpotify;

    @Autowired
    QueueManager _queueManager;

    @RequestMapping(value="/{link}")
    public ModelAndView retrievePlaylistView(@PathVariable(value = "link") String linkStr)
    {
        final Link link = Link.create(linkStr);

        final ModelAndView modelAndView = new ModelAndView();
        if (link.isFolderLink())
        {
            final LibraryEntry entry = _jahSpotify.readFolder(link,0);
            modelAndView.addObject("entry",entry);
            String name = entry.getName();
            if (link.equals(Link.create("jahspotify:folder:ROOT")))
            {
                name = "Top";
            }
            modelAndView.addObject("pageTitle", name);
            modelAndView.setViewName("/jsp/folder.jsp");
        }
        else if (link.isPlaylistLink())
        {
            final Playlist playlist = _jahSpotify.readPlaylist(link, 0, 0);
            final Set<FullTrack> tracks = new TreeSet<FullTrack>(new Comparator<FullTrack>()
            {
                @Override
                public int compare(final FullTrack o1, final FullTrack o2)
                {
                    return o1.getTitle().compareTo(o2.getTitle());
                }
            });
            for (final Link trackLink : playlist.getTracks())
            {
                FullTrack track = createFullTrack(_jahSpotify.readTrack(trackLink));
                tracks.add(track);
            }

            modelAndView.addObject("playlist",playlist);
            modelAndView.addObject("tracks",tracks);
            modelAndView.addObject("pageTitle", playlist.getName());
            modelAndView.setViewName("/jsp/playlist.jsp");
        }
        return modelAndView;
    }

}

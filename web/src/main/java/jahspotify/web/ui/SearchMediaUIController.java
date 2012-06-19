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

import jahspotify.*;
import jahspotify.query.TokenQuery;
import jahspotify.services.SearchEngine;
import jahspotify.web.media.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Johan Lindquist
 */
@Controller
@RequestMapping(value = "/ui/search")
public class SearchMediaUIController
{
    @Autowired
    SearchEngine _searchEngine;

    @RequestMapping(value = {"/", ""})
    public ModelAndView loadSearchPage()
    {
        final ModelAndView modelAndView = new ModelAndView("/jsp/search.jsp");
        return modelAndView;
    }

    @RequestMapping(value = {"/execute"})
    public ModelAndView executeSearchQuery(@RequestParam String query, @RequestParam(defaultValue = "0") int trackOffset, @RequestParam(defaultValue = "0") int numTracks, @RequestParam(defaultValue = "255") int albumOffset, @RequestParam(defaultValue = "255") int numAlbums, @RequestParam(defaultValue = "0") int artistOffset, @RequestParam(defaultValue = "255") int numArtists)
    {
        final ModelAndView modelAndView = new ModelAndView("/jsp/search-result.jsp");

        final Search search = new Search(TokenQuery.token(query));
        search.setNumTracks(numTracks);
        search.setNumAlbums(numAlbums);
        search.setNumArtists(numArtists);
        search.setTrackOffset(trackOffset);
        search.setAlbumOffset(albumOffset);
        search.setArtistOffset(artistOffset);

        final jahspotify.SearchResult searchResult = _searchEngine.search(search);
        modelAndView.addObject("queryResult", searchResult);

        return modelAndView;
    }



}

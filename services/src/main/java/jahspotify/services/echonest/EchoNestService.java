package jahspotify.services.echonest;

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
import java.net.*;
import java.util.*;

import javax.annotation.*;

import com.echonest.api.v4.*;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import jahspotify.services.nuances.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
public class EchoNestService
{
    @Value(value = "${jahspotify.echonest.api-key}")
    private String _apiKey = "N6E4NIOVYMTHNDM8J";

    private URL _echoNestListStylesURL;
    private URL _echoNestListMoodsURL;

    private EchoNestAPI _echoNestApi;

    @PostConstruct
    private void initialize() throws Exception
    {
        _echoNestApi = new EchoNestAPI(_apiKey);

        _echoNestListMoodsURL = new URL("http://developer.echonest.com/api/v4/artist/list_terms?api_key=" + _apiKey + "&format=json&type=mood");
        _echoNestListStylesURL = new URL("http://developer.echonest.com/api/v4/artist/list_terms?api_key=" + _apiKey + "&format=json&type=style");

    }

    /**
     *
     * @return
     * @throws Exception
     */
    public List<Style> retrieveStyles() throws Exception
    {
        Gson gson = new Gson();

        HttpClient httpClient = new DefaultHttpClient();

        final HttpGet httpGet = new HttpGet(_echoNestListStylesURL.toString());
        final HttpResponse execute = httpClient.execute(httpGet);

        if (execute.getStatusLine().getStatusCode() == 200)
        {
            final EchoNestBasicResponse echoNestListTermsResponse = gson.fromJson(new JsonReader(new InputStreamReader(execute.getEntity().getContent())), EchoNestBasicResponse.class);

            List<Style> styles = new ArrayList<Style>();
            for (EchoNestListTerm term : echoNestListTermsResponse.response.terms)
            {
                styles.add(new Style(term.name,0.0f));
            }

            return styles;

        }


        return Collections.emptyList();
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public List<Mood> retrievesMoods() throws Exception
    {
        Gson gson = new Gson();

        HttpClient httpClient = new DefaultHttpClient();

        final HttpGet httpGet = new HttpGet(_echoNestListMoodsURL.toString());
        final HttpResponse execute = httpClient.execute(httpGet);

        if (execute.getStatusLine().getStatusCode() == 200)
        {
            final EchoNestBasicResponse echoNestListTermsResponse = gson.fromJson(new JsonReader(new InputStreamReader(execute.getEntity().getContent())), EchoNestBasicResponse.class);

            List<Mood> moods = new ArrayList<Mood>();
            for (EchoNestListTerm term : echoNestListTermsResponse.response.terms)
            {
                moods.add(new Mood(term.name,0.0f));
            }

            return moods;

        }


        return Collections.emptyList();
    }

    /**
     *
     * @param artists
     * @param moods
     * @param styles
     * @return
     * @throws EchoNestException
     */
    public Playlist retrieveDynamicPlaylist(List<String> artists, List<Mood> moods, List<Style> styles) throws EchoNestException
    {
        final DynamicPlaylistParams p = new DynamicPlaylistParams();
        for (String artist : artists)
        {
            p.addArtist(artist);
        }
        p.setType(PlaylistParams.PlaylistType.SONG_RADIO);
        if (moods != null)
        {
            for (Mood mood : moods)
            {
                p.addMood(mood.echoNestStyle());
            }
        }
        if (moods != null)
        {
            for (Style style : styles)
            {
                p.addMood(style.echoNestStyle());
            }
        }

        return _echoNestApi.createDynamicPlaylist(p);
    }

    /**
     *
     * @param session
     * @return
     * @throws EchoNestException
     */
    public Playlist updateDynamicPlaylist(String session) throws EchoNestException
    {


        return _echoNestApi.getNextInDynamicPlaylist(session);
    }

}

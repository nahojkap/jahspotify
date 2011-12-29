package jahspotify.service.nuances;

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
import javax.annotation.PostConstruct;

import jahspotify.service.echonest.EchoNestService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Manages all the available 'nuances' within Jah'Spotify.  A nuance is the combination of a style and a mood setting.
 * Parties interested in changes to the nuance of the system can add a <code>NuanceChangeListener</code> via this API.
 *
 * @author Johan Lindquist
 */
public class NuanceManager
{
    private List<Style> _availableStyles;
    private List<Mood> _availableMoods;
    private NuanceConfiguration _currentNuanceConfiguration;

    @Autowired
    private EchoNestService _echoNestService;

    @PostConstruct
    private void initialize()
    {
        try
        {
            _availableMoods = _echoNestService.retrievesMoods();
            _availableStyles = _echoNestService.retrieveStyles();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public List<Style> getAvailableStyles()
    {
        return cloneStyles(_availableStyles);
    }

    private List<Style> cloneStyles(List<Style> styles)
    {
        List<Style> clonedStyles = new ArrayList<Style>();
        for (Style availableStyle : styles)
        {
            clonedStyles.add(new Style(availableStyle._style, availableStyle._weight));
        }
        return clonedStyles;
    }

    public List<Mood> getAvailableMoods()
    {
        return cloneMoods(_availableMoods);
    }

    private List<Mood> cloneMoods(final List<Mood> moods)
    {
        List<Mood> clonedMoods = new ArrayList<Mood>();
        for (Mood availableMood : moods)
        {
            clonedMoods.add(new Mood(availableMood._mood, availableMood._weight));
        }
        return clonedMoods;
    }

    public NuanceConfiguration getCurrentNuanceConfiguration()
    {
        return clonceCurrentNuanceConfiguration(_currentNuanceConfiguration);
    }

    private NuanceConfiguration clonceCurrentNuanceConfiguration(final NuanceConfiguration nuanceConfiguration)
    {
        NuanceConfiguration clonedNuanceConfiguration = new NuanceConfiguration();
        clonedNuanceConfiguration._name = nuanceConfiguration._name;
        clonedNuanceConfiguration._moods = cloneMoods(nuanceConfiguration._moods);
        clonedNuanceConfiguration._styles = cloneStyles(nuanceConfiguration._styles);
        return clonedNuanceConfiguration;
    }

    public void setCurrentNuanceConfiguration(final String nuanceConfiguarationName)
    {
    }

    public void storeNuanceConfiguration(final NuanceConfiguration nuanceConfiguration)
    {
    }

    public List<NuanceConfiguration> getAvailableNuanceConfigurations()
    {
        return Collections.emptyList();
    }

    public void addNuanceChangeListener(final NuanceChangeListener nuanceChangeListener)
    {
    }

    public void removeNuanceChangeListener(final NuanceChangeListener nuanceChangeListener)
    {

    }

}

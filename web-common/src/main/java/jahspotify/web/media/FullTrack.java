package jahspotify.web.media;

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

import java.util.List;

/**
 * @author Johan Lindquist
 */
public class FullTrack extends Media
{
    private String title;
    
    private List<String> artistNames;
    private List<Link> artistLinks;
    
    private String albumName;
    private Link albumLink;

    private Link albumCoverLink;

    /**
     * Track number on a certain disk.
     */
    private int trackNumber;

    /**
     * Length of this track in seconds.
     */
    private int length;

    /**
     * If this track is explicit.
     */
    private boolean explicit;

    public boolean isExplicit()
    {
        return explicit;
    }

    public void setExplicit(final boolean explicit)
    {
        this.explicit = explicit;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(final int length)
    {
        this.length = length;
    }

    public int getTrackNumber()
    {
        return trackNumber;
    }

    public void setTrackNumber(final int trackNumber)
    {
        this.trackNumber = trackNumber;
    }

    public String getAlbumName()
    {
        return albumName;
    }

    public void setAlbumName(final String albumName)
    {
        this.albumName = albumName;
    }

    public Link getAlbumCoverLink()
    {
        return albumCoverLink;
    }

    public void setAlbumCoverLink(final Link albumCoverLink)
    {
        this.albumCoverLink = albumCoverLink;
    }

    public Link getAlbumLink()
    {
        return albumLink;
    }

    public void setAlbumLink(final Link albumLink)
    {
        this.albumLink = albumLink;
    }

    public List<Link> getArtistLinks()
    {
        return artistLinks;
    }

    public void setArtistLinks(final List<Link> artistLinks)
    {
        this.artistLinks = artistLinks;
    }

    public List<String> getArtistNames()
    {
        return artistNames;
    }

    public void setArtistNames(final List<String> artistNames)
    {
        this.artistNames = artistNames;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(final String title)
    {
        this.title = title;
    }
}

package jahspotify.media;

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

/**
 * @author Johan Lindquist
 */
public class LibraryEntry implements Comparable<LibraryEntry>
{

    private String parentID;
    private String id;
    private String name;
    private String type;
    private int numEntries;
    private Collection<LibraryEntry> subEntries = new ArrayList<LibraryEntry>();

    public static final String FOLDER_ENTRY_TYPE="FOLDER";
    public static final String PLAYLIST_ENTRY_TYPE="PLAYLIST";
    public static final String TRACK_ENTRY_TYPE="TRACK";

    public LibraryEntry(final String parentID, final String id, final String name, final String type)
    {
        this.parentID = parentID;
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public int getNumEntries()
    {
        return numEntries;
    }

    public void setNumEntries(final int numEntries)
    {
        this.numEntries = numEntries;
    }

    public String getParentID()
    {
        return parentID;
    }

    public void setParentID(final String parentID)
    {
        this.parentID = parentID;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public Collection<LibraryEntry> getSubEntries()
    {
        return subEntries;
    }

    public void setSubEntries(final Collection<LibraryEntry> subEntries)
    {
        this.subEntries = subEntries;
    }

    public static LibraryEntry createPlaylistLibraryEntry(final String parentID, final String id, final String name)
    {
        return new LibraryEntry(parentID,id,name,PLAYLIST_ENTRY_TYPE);
    }

    public static LibraryEntry createTrackLibraryEntry(final String parentID, final String id, final String name)
    {
        return new LibraryEntry(parentID, id,name,TRACK_ENTRY_TYPE);
    }

    public static LibraryEntry createFolderLibraryEntry(final String parentID, final String id, final String name)
    {
        return new LibraryEntry(parentID, id,name,FOLDER_ENTRY_TYPE);
    }

    public void addSubEntry(final LibraryEntry subEntry)
    {
        if (subEntries == null)
        {
            subEntries = new ArrayList<LibraryEntry>();
        }
        numEntries++;
        subEntries.add(subEntry);
    }

    @Override
    public int compareTo(final LibraryEntry o)
    {
        return name.compareTo(o.getName());
    }
}

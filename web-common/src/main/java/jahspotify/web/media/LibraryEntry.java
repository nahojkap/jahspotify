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

import java.util.*;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Johan Lindquist
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class LibraryEntry
{
    private Link id;
    private String name;
    private String type;
    private List<LibraryEntry> subEntries;
    private int numSubEntries;

    public static final String FOLDER_ENTRY_TYPE = "folder";
    public static final String PLAYLIST_ENTRY_TYPE = "playlist";
    public static final String TRACK_ENTRY_TYPE = "track";

    private String parentID;

    public LibraryEntry(final Link id, final String name, final String type)
    {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public Link getId()
    {
        return id;
    }

    public void setId(final Link id)
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

    public List<LibraryEntry> getSubEntries()
    {
        return subEntries;
    }

    public void setSubEntries(final List<LibraryEntry> subEntries)
    {
        this.subEntries = subEntries;
    }

    public static LibraryEntry createPlaylistEntry(final Link id, final String name)
    {
        return new LibraryEntry(id, name, PLAYLIST_ENTRY_TYPE);
    }

    public static LibraryEntry createTrackEntry(final Link id, final String name)
    {
        return new LibraryEntry(id, name, TRACK_ENTRY_TYPE);
    }

    public static LibraryEntry createFolderEntry(final Link id, final String name)
    {
        return new LibraryEntry(id, name, FOLDER_ENTRY_TYPE);
    }

    public void addSubEntry(final LibraryEntry entry)
    {
        if (subEntries == null)
        {
            subEntries = new ArrayList<LibraryEntry>();
        }
        subEntries.add(entry);
    }


    public void setParentID(final String parentID)
    {
        this.parentID = parentID;
    }

    public String getParentID()
    {
        return parentID;
    }

    public void setNumSubEntries(final int numSubEntries)
    {
        this.numSubEntries = numSubEntries;
    }

    public int getNumSubEntries()
    {
        return numSubEntries;
    }

    @Override
    public String toString()
    {
        return "Entry{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", subEntries=" + subEntries +
                ", numSubEntries=" + numSubEntries +
                ", parentID='" + parentID + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o)
    {
        if (null == o)
        {
            return true;
        }
        if (!(o instanceof LibraryEntry))
        {
            return false;
        }

        final LibraryEntry entry = (LibraryEntry) o;

        if (numSubEntries != entry.numSubEntries)
        {
            return false;
        }
        if (id != null ? !id.equals(entry.id) : entry.id != null)
        {
            return false;
        }
        if (name != null ? !name.equals(entry.name) : entry.name != null)
        {
            return false;
        }
        if (parentID != null ? !parentID.equals(entry.parentID) : entry.parentID != null)
        {
            return false;
        }
        if (subEntries != null ? !subEntries.equals(entry.subEntries) : entry.subEntries != null)
        {
            return false;
        }
        if (type != null ? !type.equals(entry.type) : entry.type != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (subEntries != null ? subEntries.hashCode() : 0);
        result = 31 * result + numSubEntries;
        result = 31 * result + (parentID != null ? parentID.hashCode() : 0);
        return result;
    }
}
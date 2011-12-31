package jahspotify.media;

import java.util.*;


public class Library
{
    public static final Library EMPTY = new Library();

    private String owner;
    private List<Entry> entries;

    public Library()
    {
        this.owner = null;
        this.entries = new ArrayList<Entry>();
    }

    public String getOwner()
    {
        return this.owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public List<Entry> getEntries()
    {
        return this.entries;
    }

    public void addEntry(Entry content)
    {
        this.entries.add(content);
    }

    public void setEntries(List<Entry> entries)
    {
        this.entries = entries;
    }

    public String toString()
    {
        return String.format("[RootFolder: %s, %d]", this.owner,0);
    }

    public static class Entry
    {
        private String parentID;
        private String id;
        private String name;
        private String type;
        private int numEntries;
        private List<Entry> subEntries = new ArrayList<Entry>();

        public static final String FOLDER_ENTRY_TYPE="FOLDER";
        public static final String PLAYLIST_ENTRY_TYPE="PLAYLIST";
        public static final String TRACK_ENTRY_TYPE="TRACK";

        public Entry(final String parentID, final String id, final String name, final String type)
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

        public List<Entry> getSubEntries()
        {
            return subEntries;
        }

        public void setSubEntries(final List<Entry> subEntries)
        {
            this.subEntries = subEntries;
        }

        public static Entry createPlaylistEntry(final String parentID, final String id, final String name)
        {
            return new Entry(parentID,id,name,PLAYLIST_ENTRY_TYPE);
        }

        public static Entry createTrackEntry(final String parentID, final String id, final String name)
        {
            return new Entry(parentID, id,name,TRACK_ENTRY_TYPE);
        }

        public static Entry createFolderEntry(final String parentID, final String id, final String name)
        {
            return new Entry(parentID, id,name,FOLDER_ENTRY_TYPE);
        }

        public void addSubEntry(final Entry entry)
        {
            if (subEntries == null)
            {
                subEntries = new ArrayList<Entry>();
            }
            numEntries++;
            subEntries.add(entry);
        }
    }

}

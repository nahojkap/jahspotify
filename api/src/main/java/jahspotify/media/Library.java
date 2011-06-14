package jahspotify.media;

import java.util.ArrayList;
import java.util.List;


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
        return String.format("[RootFolder: %s, %d]", this.owner);
    }

    public static class Entry
    {
        private String id;
        private String name;
        private String type;
        private List<Entry> subEntries;

        public static final String FOLDER_ENTRY_TYPE="folder";
        public static final String PLAYLIST_ENTRY_TYPE="playlist";
        public static final String TRACK_ENTRY_TYPE="track";

        public Entry(final String id, final String name, final String type)
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

        public String getId()
        {
            return id;
        }

        public void setId(final String id)
        {
            this.id = id;
        }

        public List<Entry> getSubEntries()
        {
            return subEntries;
        }

        public void setSubEntries(final List<Entry> subEntries)
        {
            this.subEntries = subEntries;
        }

        public static Entry createPlaylistEntry(final String id, final String name)
        {
            return new Entry(id,name,PLAYLIST_ENTRY_TYPE);
        }

        public static Entry createTrackEntry(final String id, final String name)
        {
            return new Entry(id,name,TRACK_ENTRY_TYPE);
        }

        public static Entry createFolderEntry(final String id, final String name)
        {
            return new Entry(id,name,FOLDER_ENTRY_TYPE);
        }

        public void addSubEntry(final Entry entry)
        {
            if (subEntries == null)
            {
                subEntries = new ArrayList<Entry>();
            }
            subEntries.add(entry);
        }
    }

}

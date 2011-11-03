package jahspotify.web.media;

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

    @Override
    public String toString()
    {
        return "Library{" +
                "entries=" + entries +
                ", owner='" + owner + '\'' +
                '}';
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

        private String parentID;

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


        public void setParentID(final String parentID)
        {
            this.parentID = parentID;
        }

        public String getParentID()
        {
            return parentID;
        }

        @Override
        public String toString()
        {
            return "Entry{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", subEntries=" + subEntries +
                    ", parentID=" + parentID +
                    '}';
        }

        @Override
        public boolean equals(final Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (!(o instanceof Entry))
            {
                return false;
            }

            final Entry entry = (Entry) o;

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
            result = 31 * result + (parentID != null ? parentID.hashCode() : 0);
            return result;
        }
    }

}

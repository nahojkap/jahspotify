package jahspotify.web.media;

import java.util.*;


public class Library
{
    public static final Library EMPTY = new Library();

    private String owner;
    private List<LibraryEntry> entries;

    public Library()
    {
        this.owner = null;
        this.entries = new ArrayList<LibraryEntry>();
    }

    public String getOwner()
    {
        return this.owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public List<LibraryEntry> getEntries()
    {
        return this.entries;
    }

    public void addEntry(LibraryEntry content)
    {
        this.entries.add(content);
    }

    public void setEntries(List<LibraryEntry> entries)
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

}

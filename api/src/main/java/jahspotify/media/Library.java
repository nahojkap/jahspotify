package jahspotify.media;

import java.util.ArrayList;
import java.util.List;


public class Library
{
    public static final Library EMPTY = new Library();

    private String author;
    private List<Container> children;

    public Library()
    {
        this.author = null;
        this.children = new ArrayList<Container>();
    }

    public String getAuthor()
    {
        return this.author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public List<Container> getChildren()
    {
        return this.children;
    }

    public void addChild(Container child)
    {
        this.children.add(child);
    }

    public void setChildren(List<Container> children)
    {
        this.children = children;
    }

    public String toString()
    {
        return String.format("[RootFolder: %s, %d]", this.author);
    }
}

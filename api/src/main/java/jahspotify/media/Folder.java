package jahspotify.media;

import java.util.*;

/** Holds information about a playlist folder
 */
public class Folder extends Container
{
    private String name;
    private List<Container> children = new ArrayList<Container>();

    public List<Container> getChildren()
    {
        return children;
    }

    public void setChildren(final List<Container> children)
    {
        this.children = children;
    }

    public void addChild(final Container child)
    {
        children.add(child);
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }
}

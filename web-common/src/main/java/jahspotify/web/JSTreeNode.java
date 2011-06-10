package jahspotify.web;

import java.util.*;

/**
 * @author Johan Lindquist
 */
public class JSTreeNode
{
    private String data;
    private String type;
    private Map<String, String> attr;
    private String state;
    private List<JSTreeNode> children = new ArrayList<JSTreeNode>();

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public Map<String,String> getAttr()
    {
        return attr;
    }

    public void setAttr(final Map<String, String> attr)
    {
        this.attr = attr;
    }

    public void addChild(JSTreeNode child)
    {
        children.add(child);
    }

    public List<JSTreeNode> getChildren()
    {
        return children;
    }

    public void setChildren(final List<JSTreeNode> children)
    {
        this.children = children;
    }

    public String getData()
    {
        return data;
    }

    public void setData(final String data)
    {
        this.data = data;
    }

    public String getState()
    {
        return state;
    }

    public void setState(final String state)
    {
        this.state = state;
    }
}

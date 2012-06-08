package jahspotify.web.api;

import java.util.*;

/**
 * @author Johan Lindquist
 */
public class JSTreeNode
{
    private JSTreeNodeData data;
    private String title;
    private Map<String, String> attr;
    private String state;
    private List<JSTreeNode> children = new ArrayList<JSTreeNode>();

    public Map<String,String> getAttr()
    {
        return attr;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(final String title)
    {
        this.title = title;
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

    public JSTreeNodeData getData()
    {
        return data;
    }

    public void setData(final JSTreeNodeData data)
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

    public static class JSTreeNodeData
    {
        private String title;
        private String icon;
        private Map<String, String> attr;

        public Map<String, String> getAttr()
        {
            return attr;
        }

        public void setAttr(final Map<String, String> attr)
        {
            this.attr = attr;
        }

        public String getIcon()
        {
            return icon;
        }

        public void setIcon(final String icon)
        {
            this.icon = icon;
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
}

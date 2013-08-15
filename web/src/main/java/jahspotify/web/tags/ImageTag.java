package jahspotify.web.tags;

import javax.servlet.jsp.tagext.Tag;

import jahspotify.JahSpotify;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * @author
 */
public class ImageTag extends RequestContextAwareTag
{
    private String _link;
    private String _baseurl;
    private String _context;

    public void setLink(final String link)
    {
        _link = link;
    }

    public void setBaseurl(final String baseurl)
    {
        _baseurl = baseurl;
    }

    public void setContext(final String context)
    {
        _context = context;
    }

    @Override
    protected int doStartTagInternal() throws Exception
    {
        final JahSpotify jahSpotify = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext()).getBean(JahSpotify.class);

        String baseURL = _baseurl;

        if (baseURL == null || baseURL.isEmpty())
        {
            baseURL = "/media";
        }

        String context = _context;

        if (context == null || context.isEmpty())
        {
            context = pageContext.getServletContext().getServletContextName();
        }

        String fullURL = context + baseURL + "/" + _link;

        pageContext.getOut().println("<img id=\"" + _link + "\" src=\"" + fullURL +"\"/>");

        return Tag.SKIP_BODY;
    }
}

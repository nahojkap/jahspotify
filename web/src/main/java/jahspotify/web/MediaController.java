package jahspotify.web;

import javax.servlet.http.*;

import jahspotify.media.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/** Simple controller which accepts any media related URL and will forward the request to the appropriate controller.
 *
 * @author Johan Lindquist
 */
@Controller
public class MediaController extends BaseController
{

    @RequestMapping(value = "/media/*", method = RequestMethod.GET)
    public String retrievePlaylist(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            final Link uri = retrieveLink(httpServletRequest);

            switch (uri.getType())
            {
                case IMAGE:
                    return "forward:/image/" + uri.asString() + (httpServletRequest.getQueryString() != null ? "?" + httpServletRequest.getQueryString() : "");
                case ALBUM:
                    return "forward:/album/" + uri.asString() + (httpServletRequest.getQueryString() != null ? "?" + httpServletRequest.getQueryString() : "");
                case ARTIST:
                    return "forward:/artist/" + uri.asString() + (httpServletRequest.getQueryString() != null ? "?" + httpServletRequest.getQueryString() : "");
                case FOLDER:
                    return "forward:/folder/" + uri.asString() + (httpServletRequest.getQueryString() != null ? "?" + httpServletRequest.getQueryString() : "");
                case PLAYLIST:
                    return "forward:/playlist/" + uri.asString() + (httpServletRequest.getQueryString() != null ? "?" + httpServletRequest.getQueryString() : "");
                case TRACK:
                    return "forward:/track/" + uri.asString() + (httpServletRequest.getQueryString() != null ? "?" + httpServletRequest.getQueryString() : "");
                default:
                    writeResponseGeneric(httpServletResponse,"URI_NOT_HANDLED");
            }
        }
        catch (Exception e)
        {
            _log.error("Error while retrieving playlist: " + e.getMessage(), e);
            super.writeErrorResponse(httpServletResponse, e);
        }
        return null;
    }


}

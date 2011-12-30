package jahspotify.web;

import javax.servlet.http.*;

import jahspotify.*;
import jahspotify.query.TokenQuery;
import jahspotify.services.SearchEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
public class SearchController extends BaseController
{
    @Autowired
    SearchEngine _searchEngine;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public void searchAsGetRoot(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        searchAsGet(httpServletRequest,httpServletResponse);
    }

    @RequestMapping(value = "/search/", method = RequestMethod.GET)
    public void searchAsGet(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        // Read in search parameters
        String query = httpServletRequest.getParameter("query");
        if (query == null)
        {
            final SimpleStatusResponse simpleStatusResponse = SimpleStatusResponse.createParameterMissingErrorResponse("query");
            super.writeResponse(httpServletResponse, simpleStatusResponse);
            return;
        }

        int trackOffset = getIntegerParameter(httpServletRequest,"trackOffset",0);
        int numTracks = getIntegerParameter(httpServletRequest,"numTracks",255);
        int albumOffset = getIntegerParameter(httpServletRequest,"albumOffset",0);
        int numAlbums = getIntegerParameter(httpServletRequest,"numAlbums",255);
        int artistOffset = getIntegerParameter(httpServletRequest,"artistOffset",0);
        int numArtists = getIntegerParameter(httpServletRequest,"numArtists",255);

        Search search = new Search(TokenQuery.token(query));
        search.setNumTracks(numTracks);
        search.setNumAlbums(numAlbums);
        search.setNumArtists(numArtists);
        search.setTrackOffset(trackOffset);
        search.setAlbumOffset(albumOffset);
        search.setArtistOffset(artistOffset);
        final SearchResult searchResult = _searchEngine.search(search);

        super.writeResponseGeneric(httpServletResponse, searchResult);
    }

    @RequestMapping(value = "/search/", method = RequestMethod.POST)
    public void searchAsPost(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        // Suck in the JSON representing search
        // Read in search parameters
        String query = httpServletRequest.getParameter("query");

        int trackOffset = getIntegerParameter(httpServletRequest,"trackOffset",0);
        int numTracks = getIntegerParameter(httpServletRequest,"numTracks",255);
        int albumOffset = getIntegerParameter(httpServletRequest,"albumOffset",0);
        int numAlbums = getIntegerParameter(httpServletRequest,"numAlbums",255);
        int artistOffset = getIntegerParameter(httpServletRequest,"artistOffset",0);
        int numArtists = getIntegerParameter(httpServletRequest,"numArtists",255);

        Search search = new Search(TokenQuery.token(query));
        final SearchResult searchResult = _searchEngine.search(search);

        super.writeResponseGeneric(httpServletResponse, searchResult);
    }

    private int getIntegerParameter(HttpServletRequest httpServletRequest, final String parameterName, final int defaultValue)
    {
        String parameterValue = httpServletRequest.getParameter(parameterName);
        if (parameterValue != null)
        {
            try
            {
                return Integer.parseInt(parameterValue);
            }
            catch (NumberFormatException e)
            {
                _log.error("Error parsing numerical value for parameter: " + parameterName + " (" + parameterValue + ")");
            }
        }
        return defaultValue;
    }
}

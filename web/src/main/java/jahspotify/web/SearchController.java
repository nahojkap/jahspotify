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
@RequestMapping(value = "/search")
public class SearchController extends BaseController
{
    @Autowired
    SearchEngine _searchEngine;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public SearchResult searchAsGet(@RequestParam String query, @RequestParam(defaultValue = "0") int trackOffset, @RequestParam(defaultValue = "0") int numTracks, @RequestParam(defaultValue = "255") int albumOffset, @RequestParam(defaultValue = "255") int numAlbums, @RequestParam(defaultValue = "0") int artistOffset, @RequestParam(defaultValue = "255") int numArtists)
    {
        final Search search = new Search(TokenQuery.token(query));
        search.setNumTracks(numTracks);
        search.setNumAlbums(numAlbums);
        search.setNumArtists(numArtists);
        search.setTrackOffset(trackOffset);
        search.setAlbumOffset(albumOffset);
        search.setArtistOffset(artistOffset);
        final SearchResult searchResult = _searchEngine.search(search);
        return searchResult;

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

package jahspotify.web.ui;

import jahspotify.*;
import jahspotify.query.TokenQuery;
import jahspotify.services.SearchEngine;
import jahspotify.web.media.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Johan Lindquist
 */
@Controller
@RequestMapping(value = "/ui/search")
public class SearchMediaUIController
{
    @Autowired
    SearchEngine _searchEngine;

    @RequestMapping(value = {"/", ""})
    public ModelAndView loadSearchPage()
    {
        final ModelAndView modelAndView = new ModelAndView("/jsp/search.jsp");
        return modelAndView;
    }

    @RequestMapping(value = {"/execute"})
    public ModelAndView executeSearchQuery(@RequestParam String query, @RequestParam(defaultValue = "0") int trackOffset, @RequestParam(defaultValue = "0") int numTracks, @RequestParam(defaultValue = "255") int albumOffset, @RequestParam(defaultValue = "255") int numAlbums, @RequestParam(defaultValue = "0") int artistOffset, @RequestParam(defaultValue = "255") int numArtists)
    {
        final ModelAndView modelAndView = new ModelAndView("/jsp/search-result.jsp");

        final Search search = new Search(TokenQuery.token(query));
        search.setNumTracks(numTracks);
        search.setNumAlbums(numAlbums);
        search.setNumArtists(numArtists);
        search.setTrackOffset(trackOffset);
        search.setAlbumOffset(albumOffset);
        search.setArtistOffset(artistOffset);

        final jahspotify.SearchResult searchResult = _searchEngine.search(search);
        modelAndView.addObject("queryResult", searchResult);

        return modelAndView;
    }



}

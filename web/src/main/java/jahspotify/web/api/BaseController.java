package jahspotify.web.api;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.*;

import com.google.gson.Gson;
import jahspotify.JahSpotify;
import jahspotify.media.*;
import jahspotify.media.Album;
import jahspotify.media.Artist;
import jahspotify.media.Disc;
import jahspotify.media.Link;
import jahspotify.media.Track;
import jahspotify.web.*;
import jahspotify.web.media.*;
import org.apache.commons.logging.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Johan Lindquist
 */
public class BaseController
{
    protected Log _log = LogFactory.getLog(BaseController.class);

    @Autowired
    protected JahSpotify _jahSpotify;

    @Value(value = "${jahspotify.web.controller.default-media-expires-duration}")
    private int _defaultMediaExpirationTime;

    public static jahspotify.web.media.Link toWebLink(final Link link)
    {
        if (link != null)
        {
            return new jahspotify.web.media.Link(link.asString(), jahspotify.web.media.Link.Type.valueOf(link.getType().name()));
        }
        return null;
    }

    public static List<jahspotify.web.media.Link> toWebLinksAsList(final List<Link> links)
    {
        if (links == null || links.isEmpty())
        {
            return null;
        }

        List<jahspotify.web.media.Link> stringLinks = new ArrayList<jahspotify.web.media.Link>(links.size());

        for (Link track : links)
        {
            stringLinks.add(toWebLink(track));
        }

        return stringLinks;
    }

    public static Set<jahspotify.web.media.Link> toWebLinksAsSet(final Set<Link> links)
    {
        if (links == null || links.isEmpty())
        {
            return null;
        }

        final Set<jahspotify.web.media.Link> stringLinks = new HashSet<jahspotify.web.media.Link>(links.size());

        for (Link track : links)
        {
            stringLinks.add(toWebLink(track));
        }

        return stringLinks;
    }


    protected void writeResponse(final HttpServletResponse httpServletResponse, final SimpleStatusResponse simpleStatusResponse)
    {
        Gson gson = new Gson();
        try
        {
            httpServletResponse.setContentType("application/json; charset=utf-8");
            _log.debug("Serializing: " + simpleStatusResponse);
            final PrintWriter writer = httpServletResponse.getWriter();
            gson.toJson(simpleStatusResponse, writer);
            writer.flush();
            writer.close();
        }
        catch (Exception e)
        {
            _log.error("Error while writing response: " + e.getMessage(), e);
        }
    }

    protected void writeMediaNotReadable(final HttpServletResponse httpServletResponse)
    {
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(ResponseStatus.RESOURCE_NOT_FOUND);
        writeResponse(httpServletResponse, simpleStatusResponse);
    }

    protected void writeResponseGeneric(final HttpServletResponse httpServletResponse, final Object object)
    {
        this.writeResponseGenericWithDate(httpServletResponse, null, object);
    }


    protected void writeResponseGenericWithDate(final HttpServletResponse httpServletResponse, final Date lastModified, final Object object)
    {
        writeResponseGenericWithDate(httpServletResponse, lastModified, _defaultMediaExpirationTime, object);

    }

    protected void writeResponseGenericWithDate(final HttpServletResponse httpServletResponse, final Date lastModified, final int expirationTime, final Object object)
    {
        Gson gson = new Gson();
        try
        {
            httpServletResponse.setContentType("application/json; charset=utf-8");
            if (lastModified != null)
            {
                httpServletResponse.addHeader("Expires", createDateHeader(expirationTime));
                httpServletResponse.addHeader("Last-Modified", toHttpDate(lastModified));
            }
            _log.debug("Serializing: " + object);
            final PrintWriter writer = httpServletResponse.getWriter();
            gson.toJson(object, writer);
            writer.flush();
            writer.close();
        }
        catch (Exception e)
        {
            _log.error("Error while writing response: " + e.getMessage(), e);
        }
    }


    protected String createDateHeader(int expires)
    {
        final Calendar utc = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.add(Calendar.SECOND, expires);
        return toHttpDate(utc.getTime());
    }

    protected String toHttpDate(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
        return simpleDateFormat.format(date);
    }

    protected Link retrieveLink(final HttpServletRequest httpServletRequest)
    {
        String uri = httpServletRequest.getRequestURI().substring(httpServletRequest.getRequestURI().lastIndexOf("/") + 1);
        _log.debug("Extracted URI: " + uri);
        return Link.create(uri);
    }

    protected <T> T readRequest(final HttpServletRequest httpServletRequest, final Class<T> classOfT) throws IOException
    {
        final BufferedReader br = new BufferedReader(httpServletRequest.getReader());
        Gson gson = new Gson();
        return gson.fromJson(br, classOfT);
    }

    public void writeErrorResponse(final HttpServletResponse httpServletResponse, final Exception e)
    {
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(ResponseStatus.INTERNAL_ERROR);
        writeResponse(httpServletResponse, simpleStatusResponse);
    }


    @ExceptionHandler(JahSpotifyWebException.class)
    public void handleJahSpotifyWebException(JahSpotifyWebException ex, HttpServletRequest request, HttpServletResponse response)
    {
        writeErrorResponse(response, ex);
    }

    public static jahspotify.web.media.Track convertToWebTrack(final jahspotify.media.Track track)
    {
        jahspotify.web.media.Track webTrack = new jahspotify.web.media.Track();

        webTrack.setAlbum(toWebLink(track.getAlbum()));
        webTrack.setArtists(toWebLinksAsSet(track.getArtists()));
        webTrack.setId(toWebLink(track.getId()));
        // webTrack.setRestrictions(toWebRestrictions(track.getRestrictions()));

        BeanUtils.copyProperties(track, webTrack, new String[]{"id", "restrictions", "album", "artists"});

        return webTrack;

    }



    public static jahspotify.web.media.FullTrack createFullTrack(final JahSpotify jahSpotify, final jahspotify.media.Track track)
    {
        jahspotify.web.media.FullTrack fullTrack = new jahspotify.web.media.FullTrack();
        fullTrack.setId(toWebLink(track.getId()));
        fullTrack.setTitle(track.getTitle());
        fullTrack.setLength(track.getLength());
        fullTrack.setTrackNumber(track.getTrackNumber());
        fullTrack.setExplicit(track.isExplicit());

        final Set<Link> artists = track.getArtists();
        final List<jahspotify.web.media.Link> webArtistLinks = new ArrayList<jahspotify.web.media.Link>();

        final List<String> webArtistNames = new ArrayList<String>();
        fullTrack.setArtistLinks(webArtistLinks);
        fullTrack.setArtistNames(webArtistNames);

        for (jahspotify.media.Link artistLink : artists)
        {
            final Artist artist = jahSpotify.readArtist(artistLink);

            if (artist != null)
            {
                webArtistLinks.add(toWebLink(artistLink));
                webArtistNames.add(artist.getName());
            }
            else
            {
                // at least one artist has not loaded yet - let the client know
                fullTrack.setComplete(false);
            }
        }

        final jahspotify.media.Album album = jahSpotify.readAlbum(track.getAlbum());
        if (album != null)
        {
            fullTrack.setAlbumName(album.getName());
            fullTrack.setAlbumLink(toWebLink(album.getId()));
            fullTrack.setAlbumCoverLink(toWebLink(album.getCover()));
        }
        else
        {
            fullTrack.setComplete(false);
        }




        return fullTrack;
    }

    protected jahspotify.web.media.Album convertToWebAlbum(final Album album)
    {

        jahspotify.web.media.Album webAlbum = new jahspotify.web.media.Album();

        webAlbum.setId(toWebLink(album.getId()));
        webAlbum.setCover(toWebLink(album.getCover()));
        webAlbum.setArtist(toWebLink(album.getArtist()));
        webAlbum.setDiscs(toWebDiscs(album.getDiscs()));
        webAlbum.setType(jahspotify.web.media.AlbumType.valueOf(album.getType().name()));
        // webAlbum.setRestrictions(toWebRestrictions(album.getRestrictions()));

        BeanUtils.copyProperties(album, webAlbum, new String[]{"id", "restrictions", "artist", "cover", "type", "discs"});
        return webAlbum;
    }

    protected List<jahspotify.web.media.Disc> toWebDiscs(final List<Disc> discs)
    {
        List<jahspotify.web.media.Disc> webDiscs = new ArrayList<jahspotify.web.media.Disc>();
        for (Disc disc : discs)
        {
            webDiscs.add(toWebDisc(disc));
        }
        return webDiscs;
    }

    protected jahspotify.web.media.Disc toWebDisc(final Disc disc)
    {
        jahspotify.web.media.Disc webDisc = new jahspotify.web.media.Disc();
        webDisc.setTracks(toWebLinksAsList(disc.getTracks()));
        return webDisc;
    }

    protected jahspotify.web.media.Artist convertToWebArtist(final Artist artist)
    {
        jahspotify.web.media.Artist webArtist = new jahspotify.web.media.Artist();
        webArtist.setTopHitTracks(toWebLinksAsSet(artist.getTopHitTracks()));
        webArtist.setAlbums(toWebLinksAsSet(artist.getAlbums()));
        webArtist.setPortraits(toWebLinksAsList(artist.getPortraits()));
        webArtist.setSimilarArtists(toWebLinksAsSet(artist.getSimilarArtists()));
        webArtist.setId(toWebLink(artist.getId()));
        webArtist.setBioParagraphs(splitIntoList(artist.getBios()));
        BeanUtils.copyProperties(artist, webArtist, new String[]{"id", "restrictions", "albums", "similarArtists", "topHitTracks", "portraits", "bios"});
        return webArtist;
    }

    private List<String> splitIntoList(final String bios)
    {
        if (bios == null || bios.trim().isEmpty())
        {
            return Collections.emptyList();
        }
        String[] paragraphs = bios.split("\n");
        return Arrays.asList(paragraphs);
    }


}

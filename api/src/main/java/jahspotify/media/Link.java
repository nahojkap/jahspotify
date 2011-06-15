package jahspotify.media;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.regex.*;

import jahspotify.util.BaseConvert;

/**
 * Represents a link (Spotify URI) to a media object.
 * <p/>
 * Provides methods to convert different objects to
 * {@link Link} objects.
 * <p/>
 * <b>Note:</b> Offset in track link not supported yet.
 *
 * @author Felix Bruns <felixbruns@web.de>
 */
public class Link
{
    /**
     * Different possible link types.
     */
    public enum Type
    {
        ARTIST, ALBUM, TRACK, PLAYLIST, SEARCH;

        /**
         * Returns the lower-case name of this enum constant.
         *
         * @return The lower-case name of this enum constant.
         */
        public String toString()
        {
            return this.name().toLowerCase();
        }
    }

    /**
     * An exception that is thrown if parsing a Spotify URI fails.
     */
    public class InvalidSpotifyURIException extends Exception
    {
        private static final long serialVersionUID = 1L;
    }

    /**
     * A regular expression to match artist, album and track URIs:
     * <p/>
     * <pre>spotify:(artist|album|track):([0-9A-Za-z]{22})</pre>
     */
    private static final Pattern mediaPattern = Pattern.compile("spotify:(artist|album|track):([0-9A-Za-z]{22})");

    /**
     * A regular expression to match playlist URIs:
     * <p/>
     * <pre>spotify:user:([^:]+):playlist:([0-9A-Za-z]{22})</pre>
     */
    private static final Pattern playlistPattern = Pattern.compile("spotify:user:([^:]+):playlist:([0-9A-Za-z]{22})");

    /**
     * A regular expression to match search URIs:
     * <p/>
     * <pre>spotify:search:([^\\s]+)</pre>
     */
    private static final Pattern searchPattern = Pattern.compile("spotify:search:([^\\s]+)");

    /**
     * The {@link Link.Type} of this link.
     */
    private Type type;

    /**
     * The id of this link, if it's an artist, album or track link.
     */
    private String id;

    /**
     * The user of a playlist link.
     */
    private String user;

    /**
     * The search query of a search link.
     */
    private String query;

    /**
     * Create a {@link Link} using the given parameters.
     *
     * @param type  The {@link Link.Type} to use.
     * @param id    The id to set (for artists, albums and tracks) or {@code null}.
     * @param user  The user to set (for playlists) or {@code null}.
     * @param query The search query to set (for search) or {@code null}.
     * @return A {@link Link} object.
     * @throws InvalidSpotifyURIException If the Spotify URI is invalid.
     */
    private Link(Type type, String id, String user, String query)
    {
        this.type = type;
        this.id = id;
        this.user = user;
        this.query = query;
    }

    /**
     * Create a {@link Link} from a Spotify URI.
     *
     * @param uri A Spotify URI to parse.
     * @return A {@link Link} object.
     * @throws InvalidSpotifyURIException If the Spotify URI is invalid.
     */
    private Link(String uri) throws InvalidSpotifyURIException
    {
        /* Regex for matching Spotify URIs. */
        Matcher mediaMatcher = mediaPattern.matcher(uri);
        Matcher playlistMatcher = playlistPattern.matcher(uri);
        Matcher searchMatcher = searchPattern.matcher(uri);

        /* Check if URI matches artist/album/track pattern. */
        if (mediaMatcher.matches())
        {
            String type = mediaMatcher.group(1);

            if (type.equals("artist"))
            {
                this.type = Type.ARTIST;
            }
            else if (type.equals("album"))
            {
                this.type = Type.ALBUM;
            }
            else if (type.equals("track"))
            {
                this.type = Type.TRACK;
            }
            else
            {
                throw new InvalidSpotifyURIException();
            }

            this.id = Link.toHex(mediaMatcher.group(2));
            this.user = null;
            this.query = null;
        }
        /* Check if URI matches playlist pattern. */
        else if (playlistMatcher.matches())
        {
            this.type = Type.PLAYLIST;
            this.user = playlistMatcher.group(1);
            this.id = Link.toHex(playlistMatcher.group(2));
            this.query = null;
        }
        /* Check if URI matches search pattern. */
        else if (searchMatcher.matches())
        {
            this.type = Type.SEARCH;
            this.id = null;
            this.user = null;

            try
            {
                this.query = URLDecoder.decode(searchMatcher.group(1), "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                throw new InvalidSpotifyURIException();
            }
        }
        /* If nothing was matched. */
        else
        {
            throw new InvalidSpotifyURIException();
        }
    }

    /**
     * Get the {@link Link.Type} of this link.
     *
     * @return A {@link Link.Type}.
     */
    public Type getType()
    {
        return this.type;
    }

    /**
     * Check if this link is an artist link.
     *
     * @return true if this link is an artist link, false otherwise.
     */
    public boolean isArtistLink()
    {
        return this.type.equals(Type.ARTIST);
    }

    /**
     * Check if this link is an album link.
     *
     * @return true if this link is an album link, false otherwise.
     */
    public boolean isAlbumLink()
    {
        return this.type.equals(Type.ALBUM);
    }

    /**
     * Check if this link is a track link.
     *
     * @return true if this link is a track link, false otherwise.
     */
    public boolean isTrackLink()
    {
        return this.type.equals(Type.TRACK);
    }

    /**
     * Check if this link is a playlist link.
     *
     * @return true if this link is a playlist link, false otherwise.
     */
    public boolean isPlaylistLink()
    {
        return this.type.equals(Type.PLAYLIST);
    }

    /**
     * Check if this link is a search link.
     *
     * @return true if this link is a search link, false otherwise.
     */
    public boolean isSearchLink()
    {
        return this.type.equals(Type.SEARCH);
    }

    /**
     * Get the id of this link.
     *
     * @return A 32-character hex id.
     * @throws IllegalStateException If the link doesn't have an id.
     */
    public String getId()
    {
        if (this.id == null)
        {
            throw new IllegalStateException("Link doesn't have an id!");
        }

        return this.id;
    }

    /**
     * Get the user of this playlist link.
     *
     * @return A username.
     * @throws IllegalStateException If the link is not a playlist link.
     */
    public String getUser()
    {
        if (!this.isPlaylistLink())
        {
            throw new IllegalStateException("Link is not a playlist link!");
        }

        return this.user;
    }

    /**
     * Get the query of this search link.
     *
     * @return A search query.
     * @throws IllegalStateException If the link is not a search link.
     */
    public String getQuery()
    {
        if (!this.isSearchLink())
        {
            throw new IllegalStateException("Link is not a search link!");
        }

        return this.query;
    }

    /**
     * Returns the Spotify URI representation of this link.
     *
     * @return A Spotify URI (or {@code null} if an
     *         {@link UnsupportedEncodingException} occurs).
     */
    public String asString()
    {
        if (this.isPlaylistLink())
        {
            return String.format(
                    "spotify:user:%s:playlist:%s",
                    this.user, Link.toBase62(this.id)
            );
        }
        else if (this.isSearchLink())
        {
            try
            {
                return String.format(
                        "spotify:search:%s",
                        URLEncoder.encode(this.query, "UTF-8")
                );
            }
            catch (UnsupportedEncodingException e)
            {
                return null;
            }
        }
        else
        {
            return String.format(
                    "spotify:%s:%s", this.type,
                    Link.toBase62(this.id)
            );
        }
    }

    /**
     * Returns the HTTP Spotify URI representation of this link.
     *
     * @return A HTTP Spotify URI (or {@code null} if an
     *         {@link UnsupportedEncodingException} occurs).
     */
    public String asHTTPLink()
    {
        if (this.isPlaylistLink())
        {
            return String.format(
                    "http://open.spotify.com/user/%s/playlist/%s",
                    this.user, Link.toBase62(this.id)
            );
        }
        else if (this.isSearchLink())
        {
            try
            {
                return String.format(
                        "http://open.spotify.com/search/%s",
                        URLEncoder.encode(this.query, "UTF-8")
                );
            }
            catch (UnsupportedEncodingException e)
            {
                return null;
            }
        }
        else
        {
            return String.format(
                    "http://open.spotify.com/%s/%s",
                    this.type, Link.toBase62(this.id)
            );
        }
    }

    /**
     * Returns the string representation of this link.
     *
     * @return A Spotify URI string (or {@code null} if an
     *         {@link UnsupportedEncodingException} occurs).
     */
    public String toString()
    {
        return this.asString();
    }

    /**
     * Create a {@link Link} from a Spotify URI.
     *
     * @param uri A Spotify URI to parse.
     * @return A {@link Link} object.
     * @throws InvalidSpotifyURIException If the Spotify URI is invalid.
     */
    public static Link create(String uri) throws InvalidSpotifyURIException
    {
        return new Link(uri);
    }

    /**
     * Convert a base-62 encoded id into a hexadecimal id.
     *
     * @param base62 A base-62 encoded id.
     * @return A hexadecimal id.
     */
    private static String toHex(String base62)
    {
        StringBuffer hex = new StringBuffer(BaseConvert.convert(base62, 62, 16));

        /* Prepend zeroes until hexadecimal string length is 32. */
        while (hex.length() < 32)
        {
            hex.insert(0, '0');
        }

        return hex.toString();
    }

    /**
     * Convert a hexadecimal id into a base-62 encoded id.
     *
     * @param hex A hexadecimal id.
     * @return A base-62 encoded id.
     */
    private static String toBase62(String hex)
    {
        StringBuffer uri = new StringBuffer(BaseConvert.convert(hex, 16, 62));

        /* Prepend zeroes until base-62 string length is 22. */
        while (uri.length() < 22)
        {
            uri.insert(0, '0');
        }

        return uri.toString();
    }
}

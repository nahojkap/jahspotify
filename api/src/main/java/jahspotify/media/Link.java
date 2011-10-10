package jahspotify.media;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.regex.*;

import jahspotify.util.*;

/**
 * Represents a link (Spotify or Jah'Spotify URI) to a media object.
 * <p/>
 * Provides methods to convert different objects to {@link Link} objects.
 * <p/>
 * <b>Note:</b> Offset in track link not supported yet.
 * <p/>
 * The following Spotify URIs are supported:<br/>
 * <ul>
 *     <li>spotify:album:&gt;album-identifier&lt;</li>
 *     <li>spotify:artist:&gt;artist-identifier&lt;</li>
 *     <li>spotify:track:&gt;track-identifier&lt;</li>
 *     <li>spotify:image:&gt;image-indentifier&lt;</li>
 *     <li>spotify:playlist:&gt;username&lt;:&gt;playlist-identifier&lt;</li>
 * </ul>
 * <p/>
 * In addition to the above Spotify URIs, the following Jah'Spotify URIs are used:
 *
 * <ul>
 *     <li>jahspotify:folder:&gtfolder-id&lt;</li>
 *     <li>jahspotify:queue:&gtqueue-name&lt;</li>
 *     <li>jahspotify:queue:&gtqueue-name&lt;:&gt;queue-entry-identifier&lt;</li>
 * </ul>
 *
 *
 * @author Felix Bruns <felixbruns@web.de>
 * @author Johan Lindquist
 */
public class Link
{
    public String getUri()
    {
        return uri;
    }

    /**
     * Different possible link types.
     */
    public enum Type
    {
        ARTIST, ALBUM, TRACK, PLAYLIST, FOLDER, IMAGE, SEARCH, QUEUE, PODCAST, MP3;

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
    public class InvalidSpotifyURIException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        public InvalidSpotifyURIException(final String s)
        {
            super(s);
        }
    }

    /**
     * A regular expression to match artist, album and track URIs:
     * <p/>
     * <pre>spotify:(artist|album|track):([0-9A-Za-z]{22})</pre>
     */
    private static final Pattern mediaPattern = Pattern.compile("spotify:(artist|album|track):([0-9A-Za-z]{22})");

    /**
     * A regular expression to match artist, album and track URIs:
     * <p/>
     * <pre>spotify:image:([0-9A-Za-z]{40})</pre>
     */
    private static final Pattern imagePattern = Pattern.compile("spotify:image:([0-9A-Za-z]{40})");

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
     * A regular expression to match search URIs:
     * <p/>
     * <pre>jahspotify:queue:([^\\s]+)</pre>
     */
    private static final Pattern jahQueuePattern = Pattern.compile("jahspotify:queue((:)([^\\s]+))");

    /**
     * A regular expression to match podcast URIs:
     * <p/>
     * <pre>jahspotify:podcast:([^\\s]+)</pre>
     */
    private static final Pattern jahPodcastPattern = Pattern.compile("jahspotify:podcast:([^\\s]+)");

    /**
     * A regular expression to match MP3 URIs:
     * <p/>
     * <pre>jahspotify:podcast:([^\\s]+)</pre>
     */
    private static final Pattern jahMP3Pattern = Pattern.compile("jahspotify:mp3:([^\\s]+)");

    /**
     * A regular expression to match Spotify folder URIs:
     * <p/>
     * <pre>jahspotify:folder:([^\\s]+)</pre>
     */
    private static final Pattern jahFolderPattern = Pattern.compile("jahspotify:folder:(ROOT|([0-9A-Za-z]{16}))");

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

    private String queue;

    private String queueId;

    private String uri;

    private Long folderId;

    /**
     * Create a {@link Link} using the given parameters.
     *
     * @param type  The {@link Link.Type} to use.
     * @param id    The id to set (for artists, albums and tracks) or {@code null}.
     * @param user  The user to set (for playlists) or {@code null}.
     * @param query The search query to set (for search) or {@code null}.
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
     * @throws InvalidSpotifyURIException If the Spotify URI is invalid.
     */
    private Link(String uri) throws InvalidSpotifyURIException
    {
        /* Regex for matching Spotify URIs. */
        Matcher mediaMatcher = mediaPattern.matcher(uri);
        Matcher imageMatcher = imagePattern.matcher(uri);
        Matcher playlistMatcher = playlistPattern.matcher(uri);
        Matcher searchMatcher = searchPattern.matcher(uri);
        Matcher jahQueueMatcher = jahQueuePattern.matcher(uri);
        Matcher jahPodcastMatcher = jahPodcastPattern.matcher(uri);
        Matcher jahMp3Matcher = jahMP3Pattern.matcher(uri);
        Matcher jahFolderMatcher = jahFolderPattern.matcher(uri);

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
                throw new InvalidSpotifyURIException("Invalid type: " + type);
            }

            this.id = uri;
            this.user = null;
            this.query = null;
        }
        else if (imageMatcher.matches())
        {
            this.type = Type.IMAGE;
            this.id = uri;
            this.user = null;
            this.query = null;
        }
        /* Check if URI matches playlist pattern. */
        else if (playlistMatcher.matches())
        {
            this.type = Type.PLAYLIST;
            this.user = playlistMatcher.group(1);
            this.id = uri;
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
                throw new InvalidSpotifyURIException("Invalid encoding of query");
            }
        }
        else if (jahQueueMatcher.matches())
        {
            this.type = Type.QUEUE;
            this.id = uri;
            this.queue = "default";
            if (searchMatcher.groupCount() > 0)
            {
                this.queue = jahQueueMatcher.group(3);
            }
        }
        else if (jahPodcastMatcher.matches())
        {
            this.type = Type.PODCAST;
            this.id = uri;
            this.uri = jahPodcastMatcher.group(1);
        }
        else if (jahMp3Matcher.matches())
        {
            this.type = Type.MP3;
            this.id = uri;
            this.uri = jahMp3Matcher.group(1);
        }
        else if (jahFolderMatcher.matches())
        {
            this.type = Type.FOLDER;
            this.id = uri;
            final String group = jahFolderMatcher.group(1);
            if (group.equals("ROOT"))
            {
                this.folderId = 0l;
            }
            else
            {
                this.folderId = Hex.hexToLong(group);
            }
        }
        /* If nothing was matched. */
        else
        {
            throw new InvalidSpotifyURIException("Invalid URI: " + uri);
        }
    }

    public static Link createFolderLink(final long folderID)
    {
        if (folderID == 0)
        {
            return new Link("jahspotify:folder:ROOT");
        }
        return new Link("jahspotify:folder:" + Hex.toHex(folderID,8));
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

    public String getQueueId()
    {
        return queue;
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

    public long getFolderId()
    {
        return folderId;
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
     * Check if this link is an image link.
     *
     * @return true if this link is an image link, false otherwise.
     */
    public boolean isImageLink()
    {
        return this.type.equals(Type.IMAGE);
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
     * Check if this link is a queue link.
     *
     * @return true if this link is a queue link, false otherwise.
     */
    public boolean isQueueLink()
    {
        return this.type.equals(Type.QUEUE);
    }

    /**
     * Check if this link is a folder link.
     *
     * @return true if this link is a folder link, false otherwise.
     */
    public boolean isFolderLink()
    {
        return this.type.equals(Type.FOLDER);
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

    public String getQueue()
    {
        if (!this.isQueueLink())
        {
            throw new IllegalStateException("Link is not a queue link!");
        }
        return queue;
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
        return id;
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

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Link))
        {
            return false;
        }

        final Link link = (Link) o;

        if (id != null ? !id.equals(link.id) : link.id != null)
        {
            return false;
        }
        if (query != null ? !query.equals(link.query) : link.query != null)
        {
            return false;
        }
        if (type != link.type)
        {
            return false;
        }
        if (user != null ? !user.equals(link.user) : link.user != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (query != null ? query.hashCode() : 0);
        return result;
    }
}

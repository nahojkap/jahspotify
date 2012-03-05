package jahspotify.web.media;

/**
 * @author Johan Lindquist
 */
public class Link
{
    private String id;
    private Type type;

    public Link()
    {
    }

    public Link(final String id, final Type type)
    {
        this.id = id;
        this.type = type;
    }

     /**
     * Different possible link types.
     */
    public static enum Type
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

    public String getId()
    {
        return id;
    }

    public void setId(final String id)
    {
        this.id = id;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(final Type type)
    {
        this.type = type;
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
        if (type != link.type)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Link{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

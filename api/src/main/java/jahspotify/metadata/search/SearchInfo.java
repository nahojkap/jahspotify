package jahspotify.metadata.search;

/**
 * @author Johan Lindquist
 */
public class SearchInfo
{
    int num_results;
    int limit;
    int offset;
    String query;
    String type;
    int page;

    public int getLimit()
    {
        return limit;
    }

    public void setLimit(final int limit)
    {
        this.limit = limit;
    }

    public int getNum_results()
    {
        return num_results;
    }

    public void setNum_results(final int num_results)
    {
        this.num_results = num_results;
    }

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(final int offset)
    {
        this.offset = offset;
    }

    public int getPage()
    {
        return page;
    }

    public void setPage(final int page)
    {
        this.page = page;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(final String query)
    {
        this.query = query;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "Info{" +
                "limit=" + limit +
                ", num_results=" + num_results +
                ", offset=" + offset +
                ", query='" + query + '\'' +
                ", type='" + type + '\'' +
                ", page=" + page +
                '}';
    }
}

package jahspotify.web;

/**
 * @author Johan Lindquist
 */
public class BasicResponse
{
    private ResponseStatus responseStatus;

    public ResponseStatus getResponseStatus()
    {
        return responseStatus;
    }

    public void setResponseStatus(final ResponseStatus responseStatus)
    {
        this.responseStatus = responseStatus;
    }
}

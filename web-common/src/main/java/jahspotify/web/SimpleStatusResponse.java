package jahspotify.web;

/**
 * @author Johan Lindquist
 */
public class SimpleStatusResponse
{
    private ResponseStatus responseStatus;
    private String detail;

    public ResponseStatus getResponseStatus()
    {
        return responseStatus;
    }

    public void setResponseStatus(final ResponseStatus responseStatus)
    {
        this.responseStatus = responseStatus;
    }

    public static SimpleStatusResponse createParameterMissingErrorResponse(final String name)
    {
        final SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(ResponseStatus.MISSING_PARAMETER);
        simpleStatusResponse.setDetail(name);
        return simpleStatusResponse;
    }

    public String getDetail()
    {
        return detail;
    }

    private void setDetail(final String detail)
    {
        this.detail = detail;
    }

    @Override
    public String toString()
    {
        return "SimpleStatusResponse{" +
                "detail='" + detail + '\'' +
                ", responseStatus=" + responseStatus +
                '}';
    }
}

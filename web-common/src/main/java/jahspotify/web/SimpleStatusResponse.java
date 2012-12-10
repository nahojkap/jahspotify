package jahspotify.web;

import java.util.*;

/**
 * @author Johan Lindquist
 */
public class SimpleStatusResponse
{
    private ResponseStatus responseStatus;
    private String detail;
    private Map<String,Object> response;

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

    public static SimpleStatusResponse createSimpleSuccess(final String msg)
    {
        final SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(ResponseStatus.OK);
        simpleStatusResponse.setDetail(msg);
        return simpleStatusResponse;
    }

    public String getDetail()
    {
        return detail;
    }

    public void setDetail(final String detail)
    {
        this.detail = detail;
    }

    public Map<String, Object> getResponse()
    {
        if (response == null)
        {
            response = new HashMap<String, Object>();
        }
        return response;
    }

    public void setResponse(final Map<String, Object> response)
    {
        this.response = response;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof SimpleStatusResponse))
        {
            return false;
        }

        final SimpleStatusResponse that = (SimpleStatusResponse) o;

        if (detail != null ? !detail.equals(that.detail) : that.detail != null)
        {
            return false;
        }
        if (response != null ? !response.equals(that.response) : that.response != null)
        {
            return false;
        }
        if (responseStatus != that.responseStatus)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = responseStatus != null ? responseStatus.hashCode() : 0;
        result = 31 * result + (detail != null ? detail.hashCode() : 0);
        result = 31 * result + (response != null ? response.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "SimpleStatusResponse{" +
                "detail='" + detail + '\'' +
                ", responseStatus=" + responseStatus +
                ", response=" + response +
                '}';
    }
}

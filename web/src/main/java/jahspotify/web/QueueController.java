package jahspotify.web;

import java.util.*;
import javax.servlet.http.*;

import jahspotify.media.Link;
import jahspotify.services.*;
import jahspotify.services.Queue;
import jahspotify.services.QueueConfiguration;
import jahspotify.services.QueueStatus;
import jahspotify.web.queue.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
public class QueueController extends BaseController
{
    @Autowired
    QueueManager _queueManager;

    private Log _log = LogFactory.getLog(QueueController.class);

    @RequestMapping(value = "/queue/", method = RequestMethod.POST)
    public void addEntry(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        // FIXME: Verify content type application/json ??

        try
        {
            final QueueTracksRequest queueTracksRequest = readRequest(httpServletRequest, QueueTracksRequest.class);

            final List<Link> uriQueue = convertToLinkList(queueTracksRequest.getURIQueue());
            _queueManager.addToQueue(QueueManager.DEFAULT_QUEUE_LINK,uriQueue);

            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(ResponseStatus.OK);
            simpleStatusResponse.setDetail("TRACKS_ADDED");

            writeResponse(httpServletResponse, simpleStatusResponse);


        }
        catch (Exception e)
        {
            _log.error("Error while adding to queue: " + e.getMessage(), e);
            super.writeErrorResponse(httpServletResponse, e);
        }
    }

    private List<Link> convertToLinkList(final List<String> uriQueue)
    {
        List<Link> linkList = new ArrayList<Link>();
        for (String uri : uriQueue)
        {
            linkList.add(Link.create(uri));
        }
        return linkList;
    }

    @RequestMapping(value = "/add-to-queue/*", method = RequestMethod.GET)
    public void addEntryViaGet(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            Link uri = retrieveLink(httpServletRequest);
            _queueManager.addToQueue(QueueManager.DEFAULT_QUEUE_LINK,uri);

            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(ResponseStatus.OK);
            simpleStatusResponse.setDetail("TRACKS_ADDED");

            writeResponse(httpServletResponse, simpleStatusResponse);
        }
        catch (Exception e)
        {
            _log.error("Error while adding to queue: " + e.getMessage(), e);
            super.writeErrorResponse(httpServletResponse, e);
        }

    }

    @RequestMapping(value = "/queue/*", method = RequestMethod.DELETE)
    public void deleteEntry(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        // Either:
        // - track (or in fact, all occurences of that track in the queue)
        // - id in the form of a jahspotify uri

        try
        {
            Link uri = retrieveLink(httpServletRequest);
            _queueManager.deleteQueuedTrack(QueueManager.DEFAULT_QUEUE_LINK, uri);
            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(ResponseStatus.OK);
            simpleStatusResponse.setDetail("QUEUED_TRACK_DELETED");
        }
        catch (Exception e)
        {
            _log.error("Error while retrieving queue: " + e.getMessage(), e);
            super.writeErrorResponse(httpServletResponse, e);
        }
    }

    @RequestMapping(value = "/queue/shuffle/*", method = RequestMethod.GET)
    public void shuffle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            Link uri = retrieveLink(httpServletRequest);
            Queue queue = _queueManager.shuffle(uri);
            writeCurrentQueue(httpServletResponse, queue);
        }
        catch (Exception e)
        {
            _log.error("Error shuffling queue: " + e.getMessage(), e);
            super.writeErrorResponse(httpServletResponse, e);
        }
    }


    @RequestMapping(value = "/queue/", method = RequestMethod.GET)
    public void getQueueRoot(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        getQueue(httpServletRequest, httpServletResponse);
    }

    @RequestMapping(value = "/queue", method = RequestMethod.GET)
    public void getQueue(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _log.debug("Request for the queue");
        int count = 0;
        final String countStr = httpServletRequest.getParameter("count");
        if (countStr != null)
        {
            try
            {
                count = Integer.parseInt(countStr);
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
        }
        final Queue queue = _queueManager.getCurrentQueue(count);
        writeCurrentQueue(httpServletResponse, queue);
    }

    private void writeCurrentQueue(final HttpServletResponse httpServletResponse, final Queue queue)
    {
        final CurrentQueue currentQueue = QueueWebHelper.convertToWebQueue(queue, _queueManager.getQueueStatus());
        writeResponseGeneric(httpServletResponse, currentQueue);
    }

    @RequestMapping(value = "/queue/configuration", method = RequestMethod.GET)
    public void readQueueConfigurationDefaultURI(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        final QueueConfiguration queueConfiguration = _queueManager.getQueueConfiguration(Link.create("jahspotify:queue:default"));
        writeResponseGeneric(httpServletResponse, QueueWebHelper.convertToWebQueueConfiguration(queueConfiguration));
    }

    @RequestMapping(value = "/queue/configuration/*", method = RequestMethod.GET)
    public void readQueueConfiguration(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        Link uri = retrieveLink(httpServletRequest);
        final QueueConfiguration queueConfiguration = _queueManager.getQueueConfiguration(uri);
        writeResponseGeneric(httpServletResponse, QueueWebHelper.convertToWebQueueConfiguration(queueConfiguration));
    }

    @RequestMapping(value = "/queue/configuration", method = RequestMethod.POST)
    public void updateQueueConfigurationDefaultURI(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            final jahspotify.web.queue.QueueConfiguration webQueueConfiguration = readRequest(httpServletRequest, jahspotify.web.queue.QueueConfiguration.class);
            final QueueConfiguration currentQueueConfiguration = _queueManager.getQueueConfiguration(Link.create("jahspotify:queue:default"));
            _queueManager.setQueueConfiguration(Link.create("jahspotify:queue:default"), QueueWebHelper.mergeConfigurations(webQueueConfiguration, currentQueueConfiguration));
            final QueueConfiguration queueConfiguration = _queueManager.getQueueConfiguration(Link.create("jahspotify:queue:default"));
            writeResponseGeneric(httpServletResponse, QueueWebHelper.convertToWebQueueConfiguration(queueConfiguration));
        }
        catch (Exception e)
        {
            _log.error("Error while configuring queue: " + e.getMessage(), e);
            super.writeErrorResponse(httpServletResponse, e);
        }
    }

    @RequestMapping(value = "/queue/status", method = RequestMethod.GET)
    public void readQueueStatus(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        final QueueStatus queueStatus = _queueManager.getQueueStatus();
        writeResponseGeneric(httpServletResponse, QueueWebHelper.convertToWebQueueStatus(queueStatus));
    }

}

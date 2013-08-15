package jahspotify.web.api;

import java.util.*;
import javax.servlet.http.*;

import jahspotify.media.Link;
import jahspotify.services.*;
import jahspotify.services.Queue;
import jahspotify.services.QueueConfiguration;
import jahspotify.services.QueueStatus;
import jahspotify.web.*;
import jahspotify.web.queue.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Johan Lindquist
 */
@Controller
@RequestMapping(value = "/queue")
public class QueueController extends BaseController
{
    @Autowired
    QueueManager _queueManager;

    private Log _log = LogFactory.getLog(QueueController.class);

    @RequestMapping(method = RequestMethod.GET, headers = "Accept: application/json")
    @ResponseBody
    public List<jahspotify.web.media.Link> listQueues()
    {
        final List<Link> links = _queueManager.retrieveQueues();
        return toWebLinksAsList(links);
    }

    @RequestMapping(value="/{queue}/add", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public SimpleStatusResponse addEntry(@PathVariable String queue, @RequestBody QueueTracksRequest queueTracksRequest)
    {
        try
        {
            Link qLink = Link.create(queue);
            final List<Link> uriQueue = convertToLinkList(queueTracksRequest.getURIQueue());
            _queueManager.addToQueue(qLink,uriQueue);

            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(jahspotify.web.ResponseStatus.OK);
            simpleStatusResponse.setDetail("TRACKS_ADDED");

            return simpleStatusResponse;
        }
        catch (Exception e)
        {
            _log.error("Error while adding to queue: " + e.getMessage(), e);
            throw new JahSpotifyWebException();
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

    @RequestMapping(value = "/{queue}/add/{track}", method = RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public SimpleStatusResponse addEntryViaGet(@PathVariable String queue, @PathVariable String track)
    {
        try
        {
            Link qLink = Link.create(queue);
            Link tLink = Link.create(track);
            _queueManager.addToQueue(qLink, tLink);

            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(jahspotify.web.ResponseStatus.OK);
            simpleStatusResponse.setDetail("TRACKS_ADDED");

            return simpleStatusResponse;
        }
        catch (Exception e)
        {
            _log.error("Error while adding to queue: " + e.getMessage(), e);
            throw new JahSpotifyWebException();
        }

    }

    @RequestMapping(value="/{link}/remove/{entry}",method = RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public SimpleStatusResponse deleteEntry(@PathVariable(value = "jahspotify:queue:default") String queue,@PathVariable String entry)
    {
        try
        {
            Link qEntryLink = Link.create(queue + ":" + entry);
            _queueManager.deleteQueuedTrack(qEntryLink);
            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(jahspotify.web.ResponseStatus.OK);
            simpleStatusResponse.setDetail("QUEUED_TRACK_DELETED");
            return simpleStatusResponse;
        }
        catch (Exception e)
        {
            _log.error("Error while deleting track from queue: " + e.getMessage(), e);
            throw new JahSpotifyWebException();
        }
    }

    @RequestMapping(value="/{link}",method = RequestMethod.GET,produces = "application/json")
    public void getQueue(@PathVariable String queue, @RequestParam(defaultValue = "0", required = false) int count, final HttpServletResponse httpServletResponse)
    {
        final Link queueLink = Link.create(queue);
        final Queue currentQueue = _queueManager.getCurrentQueue(count);
        writeCurrentQueue(httpServletResponse, currentQueue);
    }

    private void writeCurrentQueue(final HttpServletResponse httpServletResponse, final Queue queue)
    {
        final CurrentQueue currentQueue = QueueWebHelper.convertToWebQueue(queue, _queueManager.getQueueStatus(queue.getId()));
        writeResponseGeneric(httpServletResponse, currentQueue);
    }

    @RequestMapping(value = "/{link}/configuration", method = RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public jahspotify.web.queue.QueueConfiguration readQueueConfigurationDefaultURI(@PathVariable String link)
    {
        final Link queueLink = Link.create(link);
        final QueueConfiguration queueConfiguration = _queueManager.getQueueConfiguration(queueLink);
        return QueueWebHelper.convertToWebQueueConfiguration(queueConfiguration);
    }

    @RequestMapping(value = "/{link}/configuration", method = RequestMethod.POST)
    @ResponseBody
    public jahspotify.web.queue.QueueConfiguration updateQueueConfigurationDefaultURI(@PathVariable String link, @RequestBody jahspotify.web.queue.QueueConfiguration webQueueConfiguration)
    {
        try
        {
            final Link queueLink = Link.create(link);
            final QueueConfiguration currentQueueConfiguration = _queueManager.getQueueConfiguration(queueLink);
            _queueManager.setQueueConfiguration(queueLink, QueueWebHelper.mergeConfigurations(webQueueConfiguration, currentQueueConfiguration));
            final QueueConfiguration queueConfiguration = _queueManager.getQueueConfiguration(queueLink);
            return QueueWebHelper.convertToWebQueueConfiguration(queueConfiguration);
        }
        catch (Exception e)
        {
            _log.error("Error while configuring queue: " + e.getMessage(), e);
            throw new JahSpotifyWebException();
        }
    }

    @RequestMapping(value = "/{link}/status", method = RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public jahspotify.web.queue.QueueStatus readQueueStatus(@PathVariable String link)
    {
        final Link queue = Link.create(link);
        final QueueStatus queueStatus = _queueManager.getQueueStatus(queue);
        return QueueWebHelper.convertToWebQueueStatus(queueStatus);
    }

    @RequestMapping(value = "/{link}/shuffle", method = RequestMethod.GET, produces = "application/json")
    public void shuffleQueue(@PathVariable String link, final HttpServletResponse httpServletResponse)
    {
        try
        {
            Link queueLink = Link.create(link);
            Queue queue = _queueManager.shuffle(queueLink);
            writeCurrentQueue(httpServletResponse, queue);
        }
        catch (Exception e)
        {
            _log.error("Error shuffling queue: " + e.getMessage(), e);
            super.writeErrorResponse(httpServletResponse, e);
        }
    }

    @RequestMapping(value = "/{link}/clear", method = RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public SimpleStatusResponse clearQueue(@PathVariable String link, final HttpServletResponse httpServletResponse)
    {
        try
        {
            Link uri = Link.create(link);
            int count = _queueManager.clearQueue(uri);
            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(jahspotify.web.ResponseStatus.OK);
            simpleStatusResponse.setDetail("QUEUE_CLEARED");

            return simpleStatusResponse;
        }
        catch (Exception e)
        {
            _log.error("Error clearing queue: " + e.getMessage(), e);
            throw new JahSpotifyWebException();
        }
    }
}

package jahspotify.web;

import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import javax.servlet.http.*;

import jahspotify.media.Link;
import jahspotify.services.*;
import jahspotify.services.Queue;
import jahspotify.services.QueueConfiguration;
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

    private long _upSince = System.currentTimeMillis();

    public QueueController()
    {
    }

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
        final jahspotify.web.queue.CurrentQueue currentCurrentQueue = new jahspotify.web.queue.CurrentQueue();

        currentCurrentQueue.setQueueState(QueueWebHelper.convertToQueueStatus(_queueManager.getQueueStatus().getMediaPlayerState()));
        currentCurrentQueue.setId(queue.getId().getId());
        jahspotify.web.queue.QueueConfiguration currentQueueConfiguration = new jahspotify.web.queue.QueueConfiguration();
        currentQueueConfiguration.setRepeatCurrentQueue(queue.getQueueConfiguration().isRepeatCurrentQueue());
        currentQueueConfiguration.setRepeatCurrentTrack(queue.getQueueConfiguration().isRepeatCurrentTrack());
        currentQueueConfiguration.setShuffle(queue.getQueueConfiguration().isShuffle());
        currentQueueConfiguration.setAutoRefill(queue.getQueueConfiguration().isAutoRefill());
        currentCurrentQueue.setQueueConfiguration(currentQueueConfiguration);

        final QueueTrack currentlyPlaying = queue.getCurrentlyPlaying();
        if (currentlyPlaying != null)
        {
            currentCurrentQueue.setCurrentlyPlaying(new CurrentTrack(currentlyPlaying.getId(), currentlyPlaying.getTrackUri().asString()));
        }

        currentCurrentQueue.setQueuedTracks(convertToWeb(queue.getQueuedTracks()));
        writeResponseGeneric(httpServletResponse, currentCurrentQueue);
    }

    private List<QueuedTrack> convertToWeb(final BlockingDeque<QueueTrack> queuedTracks)
    {
        if (queuedTracks.isEmpty())
        {
            return Collections.emptyList();
        }

        List<QueuedTrack> queuedWebTracks = new ArrayList<QueuedTrack>();
        for (QueueTrack queuedTrack : queuedTracks)
        {
            queuedWebTracks.add(new QueuedTrack(queuedTrack.getId(), queuedTrack.getTrackUri().asString()));
        }

        return queuedWebTracks;
    }

    @RequestMapping(value = "/queue/configuration", method = RequestMethod.GET)
    public void readQueueConfigurationDefaultURI(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        final QueueConfiguration queueConfiguration = _queueManager.getQueueConfiguration(Link.create("jahspotify:queue:default"));
        final jahspotify.web.queue.QueueConfiguration webQueueConfiguration = new jahspotify.web.queue.QueueConfiguration();
        webQueueConfiguration.setRepeatCurrentTrack(queueConfiguration.isRepeatCurrentTrack());
        webQueueConfiguration.setRepeatCurrentQueue(queueConfiguration.isRepeatCurrentQueue());
        webQueueConfiguration.setShuffle(queueConfiguration.isShuffle());
        webQueueConfiguration.setAutoRefill(queueConfiguration.isAutoRefill());

        webQueueConfiguration.setCallbackURL(queueConfiguration.getCallbackURL() == null ? null : queueConfiguration.getCallbackURL().toString());
        webQueueConfiguration.setReportEmptyQueue(queueConfiguration.isReportEmptyQueue());
        webQueueConfiguration.setReportTrackChanges(queueConfiguration.isReportTrackChanges());

        writeResponseGeneric(httpServletResponse, webQueueConfiguration);

    }

    @RequestMapping(value = "/queue/configuration", method = RequestMethod.POST)
    public void updateQueueConfigurationDefaultURI(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            final jahspotify.web.queue.QueueConfiguration webQueueConfiguration = readRequest(httpServletRequest, jahspotify.web.queue.QueueConfiguration.class);
            final QueueConfiguration currentQueueConfiguration = _queueManager.getQueueConfiguration(Link.create("jahspotify:queue:default"));


            _queueManager.setQueueConfiguration(Link.create("jahspotify:queue:default"), mergeConfigurations(webQueueConfiguration, currentQueueConfiguration));

            final QueueConfiguration queueConfiguration = _queueManager.getQueueConfiguration(Link.create("jahspotify:queue:default"));
            final jahspotify.web.queue.QueueConfiguration newWebQueueConfiguration = new jahspotify.web.queue.QueueConfiguration();
            newWebQueueConfiguration.setRepeatCurrentTrack(queueConfiguration.isRepeatCurrentTrack());
            newWebQueueConfiguration.setRepeatCurrentQueue(queueConfiguration.isRepeatCurrentQueue());
            newWebQueueConfiguration.setShuffle(queueConfiguration.isShuffle());
            newWebQueueConfiguration.setAutoRefill(queueConfiguration.isAutoRefill());
            newWebQueueConfiguration.setReportEmptyQueue(queueConfiguration.isReportEmptyQueue());
            newWebQueueConfiguration.setReportTrackChanges(queueConfiguration.isReportTrackChanges());
            newWebQueueConfiguration.setCallbackURL(queueConfiguration.getCallbackURL() == null ? null : queueConfiguration.getCallbackURL().toString());

            writeResponseGeneric(httpServletResponse, newWebQueueConfiguration);


        }
        catch (Exception e)
        {
            _log.error("Error while configuring queue: " + e.getMessage(), e);
            super.writeErrorResponse(httpServletResponse, e);
        }
    }

    private QueueConfiguration mergeConfigurations(final jahspotify.web.queue.QueueConfiguration webQueueConfiguration, final QueueConfiguration currentQueueConfiguration) throws MalformedURLException
    {
        final QueueConfiguration queueConfiguration = new QueueConfiguration();
        queueConfiguration.setRepeatCurrentQueue(webQueueConfiguration.isRepeatCurrentQueue());
        queueConfiguration.setRepeatCurrentTrack(webQueueConfiguration.isRepeatCurrentTrack());
        queueConfiguration.setShuffle(webQueueConfiguration.isShuffle());
        queueConfiguration.setAutoRefill(webQueueConfiguration.isAutoRefill());
        queueConfiguration.setReportEmptyQueue(webQueueConfiguration.isReportEmptyQueue());
        queueConfiguration.setReportTrackChanges(webQueueConfiguration.isReportTrackChanges());
        queueConfiguration.setCallbackURL(webQueueConfiguration.getCallbackURL() == null ? null : new URL(webQueueConfiguration.getCallbackURL()));
        return queueConfiguration;
    }

    @RequestMapping(value = "/queue/configuration/*", method = RequestMethod.GET)
    public void readQueueConfiguration(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        Link uri = retrieveLink(httpServletRequest);
        final QueueConfiguration queueConfiguration = _queueManager.getQueueConfiguration(uri);
        final jahspotify.web.queue.QueueConfiguration webQueueConfiguration = new jahspotify.web.queue.QueueConfiguration();
        webQueueConfiguration.setRepeatCurrentTrack(queueConfiguration.isRepeatCurrentTrack());
        webQueueConfiguration.setRepeatCurrentQueue(queueConfiguration.isRepeatCurrentQueue());
        webQueueConfiguration.setShuffle(queueConfiguration.isShuffle());
        writeResponseGeneric(httpServletResponse, webQueueConfiguration);
    }

}

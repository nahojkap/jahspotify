package jahspotify.web;

import java.util.*;
import javax.servlet.http.*;

import jahspotify.media.Link;
import jahspotify.service.*;
import jahspotify.service.QueueConfiguration;
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
            _queueManager.addToQueue(uriQueue);

            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(ResponseStatus.OK);

            writeResponse(httpServletResponse, simpleStatusResponse);


        }
        catch (Exception e)
        {
            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(ResponseStatus.INTERNAL_ERROR);
            writeResponse(httpServletResponse, simpleStatusResponse);
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
            _queueManager.addToQueue(uri);

            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(ResponseStatus.OK);

            writeResponse(httpServletResponse, simpleStatusResponse);
        }
        catch (Exception e)
        {
            _log.error("Error processing request: " + e.getMessage(), e);
            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(ResponseStatus.INTERNAL_ERROR);
            writeResponse(httpServletResponse, simpleStatusResponse);
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
            _queueManager.deleteQueuedTrack(uri);
            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(ResponseStatus.OK);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/queue/shuffle/*", method = RequestMethod.GET)
    public void shuffle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        Link uri = retrieveLink(httpServletRequest);
        jahspotify.service.CurrentQueue currentQueue = _queueManager.shuffle(uri);
        writeCurrentQueue(httpServletResponse, currentQueue);
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

        final jahspotify.service.CurrentQueue currentQueue = _queueManager.getCurrentQueue(count);
        writeCurrentQueue(httpServletResponse, currentQueue);
    }

    private void writeCurrentQueue(final HttpServletResponse httpServletResponse, final jahspotify.service.CurrentQueue currentQueue)
    {
        final CurrentQueue currentCurrentQueue = new CurrentQueue();

        final QueueTrack currentlyPlaying = currentQueue.getCurrentlyPlaying();
        if (currentlyPlaying != null)
        {
            currentCurrentQueue.setCurrentlyPlaying(new CurrentTrack(currentlyPlaying.getId(), currentlyPlaying.getTrackUri().asString()));
        }

        currentCurrentQueue.setQueuedTracks(convertToWeb(currentQueue.getQueuedTracks()));
        writeResponseGeneric(httpServletResponse, currentCurrentQueue);
    }

    private List<QueuedTrack> convertToWeb(final List<QueueTrack> queuedTracks)
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

    @RequestMapping(value = "/system/status", method = RequestMethod.GET)
    public void status(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _log.debug("Request for the system status");

        final jahspotify.service.QueueStatus queueStatus = _queueManager.getQueueStatus();

        SystemStatus systemStatus = new SystemStatus();
        systemStatus.setQueueStatus(convertToWeb(queueStatus));

        systemStatus.setUpSince(_upSince);

        systemStatus.setTotalMemory(Runtime.getRuntime().totalMemory());
        systemStatus.setMaxMemory(Runtime.getRuntime().maxMemory());
        systemStatus.setFreeMemory(Runtime.getRuntime().freeMemory());

        systemStatus.setNumberProcessors(Runtime.getRuntime().availableProcessors());

        writeResponseGeneric(httpServletResponse, systemStatus);

    }


    @RequestMapping(value = "/queue/configuration", method = RequestMethod.GET)
    public void readQueueConfigurationDefaultURI(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        final QueueConfiguration queueConfiguration = _queueManager.getQueueConfiguration(Link.create("jahspotify:queue:default"));
        final jahspotify.web.QueueConfiguration webQueueConfiguration = new jahspotify.web.QueueConfiguration();
        webQueueConfiguration.setRepeatCurrentTrack(queueConfiguration.isRepeatCurrentTrack());
        webQueueConfiguration.setRepeatCurrentQueue(queueConfiguration.isRepeatCurrentQueue());
        webQueueConfiguration.setShuffle(queueConfiguration.isShuffle());
        writeResponseGeneric(httpServletResponse, webQueueConfiguration);

    }

    @RequestMapping(value = "/queue/configuration", method = RequestMethod.POST)
    public void updateQueueConfigurationDefaultURI(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        try
        {
            final jahspotify.web.QueueConfiguration webQueueConfiguration = readRequest(httpServletRequest, jahspotify.web.QueueConfiguration.class);
            final QueueConfiguration currentQueueConfiguration = _queueManager.getQueueConfiguration(Link.create("jahspotify:queue:default"));


            _queueManager.setQueueConfiguration(Link.create("jahspotify:queue:default"), mergeConfigurations(webQueueConfiguration, currentQueueConfiguration));

            final QueueConfiguration queueConfiguration = _queueManager.getQueueConfiguration(Link.create("jahspotify:queue:default"));
            final jahspotify.web.QueueConfiguration newWebQueueConfiguration = new jahspotify.web.QueueConfiguration();
            newWebQueueConfiguration.setRepeatCurrentTrack(queueConfiguration.isRepeatCurrentTrack());
            newWebQueueConfiguration.setRepeatCurrentQueue(queueConfiguration.isRepeatCurrentQueue());
            newWebQueueConfiguration.setShuffle(queueConfiguration.isShuffle());
            writeResponseGeneric(httpServletResponse, newWebQueueConfiguration);


        }
        catch (Exception e)
        {
            _log.error("Error processing request: " + e.getMessage(), e);
            SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
            simpleStatusResponse.setResponseStatus(ResponseStatus.INTERNAL_ERROR);
            writeResponse(httpServletResponse, simpleStatusResponse);
        }
    }

    private QueueConfiguration mergeConfigurations(final jahspotify.web.QueueConfiguration webQueueConfiguration, final QueueConfiguration currentQueueConfiguration)
    {
        final QueueConfiguration queueConfiguration = new QueueConfiguration();
        queueConfiguration.setRepeatCurrentQueue(webQueueConfiguration.isRepeatCurrentQueue());
        queueConfiguration.setRepeatCurrentTrack(webQueueConfiguration.isRepeatCurrentTrack());
        queueConfiguration.setShuffle(webQueueConfiguration.isShuffle());
        return queueConfiguration;
    }

    @RequestMapping(value = "/queue/configuration/*", method = RequestMethod.GET)
    public void readQueueConfiguration(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        Link uri = retrieveLink(httpServletRequest);
        final QueueConfiguration queueConfiguration = _queueManager.getQueueConfiguration(uri);
        final jahspotify.web.QueueConfiguration webQueueConfiguration = new jahspotify.web.QueueConfiguration();
        webQueueConfiguration.setRepeatCurrentTrack(queueConfiguration.isRepeatCurrentTrack());
        webQueueConfiguration.setRepeatCurrentQueue(queueConfiguration.isRepeatCurrentQueue());
        webQueueConfiguration.setShuffle(queueConfiguration.isShuffle());
        writeResponseGeneric(httpServletResponse, webQueueConfiguration);
    }

    private QueueStatus convertToWeb(final jahspotify.service.QueueStatus queueStatus)
    {
        QueueStatus webQueueStatus = new QueueStatus();

        webQueueStatus.setCurrentQueueSize(queueStatus.getCurrentQueueSize());
        webQueueStatus.setMaxQueueSize(queueStatus.getMaxQueueSize());
        webQueueStatus.setQueueState(QueueState.valueOf(queueStatus.getMediaPlayerState().name()));
        webQueueStatus.setTotalPlaytime(queueStatus.getTotalPlaytime());
        webQueueStatus.setTotalTracksCompleted(queueStatus.getTotalTracksCompleted());
        webQueueStatus.setTotalTracksPlayed(queueStatus.getTotalTracksPlayed());
        webQueueStatus.setTotalTracksSkipped(queueStatus.getTotalTracksSkipped());

        return webQueueStatus;
    }

    @RequestMapping(value = "/system/intialize", method = RequestMethod.POST)
    public void initialize(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        SimpleStatusResponse simpleStatusResponse = new SimpleStatusResponse();
        simpleStatusResponse.setResponseStatus(ResponseStatus.OK);
        writeResponse(httpServletResponse, simpleStatusResponse);
    }

}

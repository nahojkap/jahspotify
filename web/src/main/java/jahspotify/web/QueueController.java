package jahspotify.web;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;

import com.google.gson.Gson;
import jahspotify.media.Link;
import jahspotify.service.*;
import jahspotify.service.QueueStatus;
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
            final QueueRequest queueRequest = readRequest(httpServletRequest, QueueRequest.class);

            final List<Link> uriQueue = convertToLinkList(queueRequest.getURIQueue());
            _queueManager.addToQueue(uriQueue);
            if (queueRequest.isAutoPlay() && _queueManager.getQueueState() != jahspotify.service.QueueState.PLAYING)
            {
                // _queueManager.play();
            }

            BasicResponse basicResponse = new BasicResponse();
            basicResponse.setResponseStatus(ResponseStatus.OK);

            writeResponse(httpServletResponse, basicResponse);


        }
        catch (Exception e)
        {
            BasicResponse basicResponse = new BasicResponse();
            basicResponse.setResponseStatus(ResponseStatus.INTERNAL_ERROR);
            writeResponse(httpServletResponse, basicResponse);
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

            BasicResponse basicResponse = new BasicResponse();
            basicResponse.setResponseStatus(ResponseStatus.OK);

            writeResponse(httpServletResponse, basicResponse);
        }
        catch (Exception e)
        {
            BasicResponse basicResponse = new BasicResponse();
            basicResponse.setResponseStatus(ResponseStatus.INTERNAL_ERROR);
            writeResponse(httpServletResponse, basicResponse);
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
            BasicResponse basicResponse = new BasicResponse();
            basicResponse.setResponseStatus(ResponseStatus.OK);
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
        final CurrentQueue currentQueue = _queueManager.getCurrentQueue();
        final QueueResponse currentQueueResponse = new QueueResponse();

        final Link currentlyPlaying = currentQueue.getCurrentlyPlaying();
        if (currentlyPlaying != null)
        {
            currentQueueResponse.setCurrentlyPlaying(currentlyPlaying.asString());
        }

        currentQueueResponse.setQueuedTracks(convertToWeb(currentQueue.getQueuedTracks()));
        writeResponse(httpServletResponse, currentQueueResponse);
    }

    private List<QueuedTrack> convertToWeb(final List<jahspotify.service.QueuedTrack> queuedTracks)
    {
        if (queuedTracks.isEmpty())
        {
            return Collections.emptyList();
        }

        List<QueuedTrack> queuedWebTracks = new ArrayList<QueuedTrack>();
        for (jahspotify.service.QueuedTrack queuedTrack : queuedTracks)
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

        SystemStatusResponse systemStatusResponse = new SystemStatusResponse();
        systemStatusResponse.setQueueStatusResponse(convertToWeb(queueStatus));

        systemStatusResponse.setUpSince(_upSince);

        systemStatusResponse.setTotalMemory(Runtime.getRuntime().totalMemory());
        systemStatusResponse.setMaxMemory(Runtime.getRuntime().maxMemory());
        systemStatusResponse.setFreeMemory(Runtime.getRuntime().freeMemory());

        systemStatusResponse.setNumberProcessors(Runtime.getRuntime().availableProcessors());

        writeResponse(httpServletResponse, systemStatusResponse);

    }

    private QueueStatusResponse convertToWeb(final QueueStatus queueStatus)
    {
        QueueStatusResponse webQueueStatusResponse = new QueueStatusResponse();

        webQueueStatusResponse.setCurrentQueueSize(queueStatus.getCurrentQueueSize());
        webQueueStatusResponse.setMaxQueueSize(queueStatus.getMaxQueueSize());
        webQueueStatusResponse.setQueueState(QueueState.valueOf(queueStatus.getQueueState().name()));
        webQueueStatusResponse.setTotalPlaytime(queueStatus.getTotalPlaytime());
        webQueueStatusResponse.setTotalTracksCompleted(queueStatus.getTotalTracksCompleted());
        webQueueStatusResponse.setTotalTracksPlayed(queueStatus.getTotalTracksPlayed());
        webQueueStatusResponse.setTotalTracksSkipped(queueStatus.getTotalTracksSkipped());

        return webQueueStatusResponse;
    }

    @RequestMapping(value = "/system/intialize", method = RequestMethod.POST)
    public void initialize(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setResponseStatus(ResponseStatus.OK);
        writeResponse(httpServletResponse, basicResponse);
    }

    @RequestMapping(value = "/controller/pause", method = RequestMethod.GET)
    public void pause(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _queueManager.pause();
        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setResponseStatus(ResponseStatus.OK);
        writeResponse(httpServletResponse, basicResponse);
    }

    @RequestMapping(value = "/controller/resume", method = RequestMethod.GET)
    public void resume(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _queueManager.play();
        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setResponseStatus(ResponseStatus.OK);
        writeResponse(httpServletResponse, basicResponse);
    }

    @RequestMapping(value = "/controller/play", method = RequestMethod.GET)
    public void play(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _queueManager.play();
        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setResponseStatus(ResponseStatus.OK);
        writeResponse(httpServletResponse, basicResponse);
    }

    @RequestMapping(value = "/controller/skip", method = RequestMethod.GET)
    public void skip(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
    {
        _queueManager.skip();
        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setResponseStatus(ResponseStatus.OK);
        writeResponse(httpServletResponse, basicResponse);
    }

}

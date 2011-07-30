package jahspotify.mp3;

import java.io.*;
import java.net.URL;
import java.util.concurrent.*;

import jahspotify.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author Johan Lindquist
 */
public class MPG123Player
{
    private Thread _playingThread;
    private BlockingQueue<Integer> _resultReportQueue;

    private boolean _keepRunning = true;
    private Process _mpg123Player;
    private int _downloadBufferSize = 16000;

    public void playMP3(final URL url, final File cacheFile) throws Exception
    {
        if (_playingThread != null || _resultReportQueue != null)
        {
            System.out.println("Already running, exiting");
        }

        _keepRunning = true;
        _resultReportQueue = new ArrayBlockingQueue<Integer>(1);
        _playingThread = new Thread()
        {
            @Override
            public void run()
            {
                boolean downloadComplete = false;
                _mpg123Player = null;
                try
                {
                    final HttpClient httpClient = new DefaultHttpClient();

                    final HttpGet httpPost = new HttpGet(url.toString());
                    final HttpResponse execute = httpClient.execute(httpPost);

                    if (execute.getStatusLine().getStatusCode() == 200)
                    {
                        _mpg123Player = Runtime.getRuntime().exec(new String[]{"mpg123", "-"});
                        final OutputStream audioOutputStream = new BufferedOutputStream(_mpg123Player.getOutputStream());
                        final InputStream remoteData;
                        if (cacheFile != null)
                        {
                            remoteData = new WritingInputStream(new BufferedInputStream(execute.getEntity().getContent()), new DuplicatingOutputStream(audioOutputStream));
                        }
                        else
                        {
                            remoteData = new WritingInputStream(new BufferedInputStream(execute.getEntity().getContent()), new DuplicatingOutputStream(audioOutputStream, new BufferedOutputStream(new FileOutputStream("/home/johan/downloadedfile.mp3"))));
                        }
                        byte[] buffer = new byte[_downloadBufferSize];

                        try
                        {
                            while (_keepRunning && remoteData.read(buffer, 0, _downloadBufferSize) != -1);
                        }
                        finally
                        {
                            remoteData.close();
                        }

                        System.out.println("_keepRunning = " + _keepRunning);
                        downloadComplete = _keepRunning;
                    }

                    performOptionalCleanup(downloadComplete, cacheFile);

                }
                catch (Exception e)
                {
                    performOptionalCleanup(downloadComplete, cacheFile);
                }

            }
        };
        _playingThread.start();
    }

    private void performOptionalCleanup(final boolean downloadComplete, final File cacheFile)
    {
        System.out.println("Performing cleanup: " + (downloadComplete ? "No" : "Yes"));
        if (_mpg123Player != null)
        {
            _mpg123Player.destroy();
        }
        if (!downloadComplete && cacheFile != null)
        {
            System.out.println("Download did not complete, deleting cached file: " + cacheFile);
            // Download not finished - delete the file
            if (!cacheFile.delete())
            {
                System.out.println("Could not delete cached file: " + cacheFile);
            }
        }
        setReturnValue(downloadComplete ? 0 : 1);
    }

    private void setReturnValue(final int result)
    {
        try
        {
            _resultReportQueue.put(result);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void stop()
    {
        if (isCompleted())
        {
            return;
        }

        _keepRunning = false;
        try
        {
            Integer result =_resultReportQueue.poll(10000,TimeUnit.MILLISECONDS);
            if (result != null)
            {
                System.out.println("result = " + result);
                _playingThread = null;
                _resultReportQueue = null;
            }
            else
            {
                System.out.println("Timeout reached while waiting for shutdown of thread");
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }

    public boolean isCompleted()
    {
        Integer result = _resultReportQueue.peek();
        return result != null;
    }

    public void playMP3(final URL url) throws Exception
    {
        playMP3(url, null);
    }


}

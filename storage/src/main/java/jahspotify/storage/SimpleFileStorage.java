package jahspotify.storage;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import com.google.gson.Gson;
import jahspotify.media.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
@Qualifier(value = "in-memory")
public class SimpleFileStorage implements JahStorage
{
    @Value(value="${jahspotify.storage.simple-file-based.directory}")
    private String _baseDirectoryStr = "/tmp/jahspotify/simple-file-storage/";

    private File _baseDirectory;
    private File _trackBaseDirectory;
    private File _albumBaseDirectory;
    private File _artistBaseDirectory;
    private File _imageBaseDirectory;

    @PostConstruct
    public void initialize()
    {

    }

    @Override
    public void deleteTrack(final Link uri)
    {
    }

    @Override
    public void store(final Track track)
    {
        File trackFile = new File("");
    }

    @Override
    public Track readTrack(final Link uri)
    {
        final String substring = uri.asString().substring(uri.asString().lastIndexOf(":") + 1);
        File trackFile = new File(_trackBaseDirectory,substring);

        byte[] bytes;
        if (trackFile.exists())
        {
            // _log.debug("Image found in cache: " + uri);

            try
            {
                FileInputStream fileInputStream = new FileInputStream(trackFile);


                Gson gson = new Gson();
                return gson.fromJson(new InputStreamReader(fileInputStream),Track.class);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void store(final Artist artist)
    {
    }

    @Override
    public Artist readArtist(final Link uri)
    {
        return null;
    }

    @Override
    public void store(final Album album)
    {
    }

    @Override
    public Album readAlbum(final Link uri)
    {
        return null;
    }

    @Override
    public void store(final Playlist playlist)
    {
    }

    @Override
    public void store(final Image image)
    {
    }

    @Override
    public Image readImage(final Link uri)
    {
        return null;
    }
}

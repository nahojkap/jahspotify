package jahspotify.storage;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import com.google.gson.Gson;
import jahspotify.media.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
@Qualifier(value = "simple-file")
public class SimpleFileStorage implements JahStorage
{
    @Value(value="${jahspotify.storage.simple-file-based.directory}")
    private String _baseDirectoryStr = "/var/lib/jahspotify/simple-file-storage/";

    private File _trackBaseDirectory;
    private File _albumBaseDirectory;
    private File _artistBaseDirectory;
    private File _imageBaseDirectory;
    private File _playlistBaseDirectory;

    private Log _log = LogFactory.getLog(SimpleFileStorage.class);

    @PostConstruct
    public void initialize()
    {
        final File baseDirectory = new File(_baseDirectoryStr);

        _trackBaseDirectory = new File(baseDirectory,  "tracks");
        _trackBaseDirectory.mkdirs();

        _albumBaseDirectory = new File(baseDirectory,  "albums");
        _albumBaseDirectory.mkdirs();

        _artistBaseDirectory = new File(baseDirectory,  "artists");
        _artistBaseDirectory.mkdirs();

        _imageBaseDirectory = new File(baseDirectory,  "images");
        _imageBaseDirectory.mkdirs();

        _playlistBaseDirectory = new File(baseDirectory,  "playlists");
        _playlistBaseDirectory.mkdirs();
    }

    @Override
    public void deleteTrack(final Link uri)
    {
        deleteObject(_trackBaseDirectory, uri);
    }

    private void deleteObject(final File baseDirectory, final Link uri)
    {
        final String substring = extractFilename(uri);
        File f = new File(baseDirectory,substring);
        if (f.exists())
        {
            if (!f.delete())
            {
                // Warn someone?
            }
        }
    }

    private String extractFilename(final Link uri)
    {
        return uri.asString().substring(uri.asString().lastIndexOf(":") + 1);
    }

    @Override
    public void store(final Track track)
    {
        writeObject(_trackBaseDirectory,track.getId(), track);
    }

    @Override
    public Track readTrack(final Link uri)
    {
        return readObject(_trackBaseDirectory, uri, Track.class);
    }

    private void writeObject(final File baseDirectory, final Link uri, Object obj)
    {
        final String substring = extractFilename(uri);
        try
        {
            final OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(baseDirectory,substring)));
            Gson gson = new Gson();
            final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            gson.toJson(obj, outputStreamWriter);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        }
        catch (Exception e)
        {
            _log.error("Error while writing out object: " + e.getMessage());
        }
    }

    private <T> T readObject(final File baseDirectory, final Link uri, final Class<T> objClass)
    {
        final String substring = extractFilename(uri);
        final File dataFile = new File(baseDirectory,substring);

        if (dataFile.exists())
        {
            try
            {
                final InputStream inputStream = new BufferedInputStream(new FileInputStream(dataFile));
                Gson gson = new Gson();
                return gson.fromJson(new InputStreamReader(inputStream),objClass);
            }
            catch (Exception e)
            {
                _log.error("Error while reading in object: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public void store(final Artist artist)
    {
        writeObject(_artistBaseDirectory,artist.getId(), artist);
    }

    @Override
    public Artist readArtist(final Link uri)
    {
        return readObject(_artistBaseDirectory, uri, Artist.class);
    }

    @Override
    public void store(final Album album)
    {
        writeObject(_albumBaseDirectory,album.getId(), album);
    }

    @Override
    public Album readAlbum(final Link uri)
    {
        return readObject(_albumBaseDirectory, uri, Album.class);
    }

    @Override
    public void store(final Playlist playlist)
    {
        writeObject(_playlistBaseDirectory,playlist.getId(), playlist);
    }

    @Override
    public Playlist readPlaylist(final Link uri)
    {
        return readObject(_playlistBaseDirectory,uri,Playlist.class);
    }

    @Override
    public void store(final Image image)
    {
        writeObject(_imageBaseDirectory,image.getId(), image);
    }

    @Override
    public Image readImage(final Link uri)
    {
        return readObject(_imageBaseDirectory, uri, Image.class);
    }
}

package jahspotify.storage.media;

import javax.annotation.PostConstruct;

import com.mongodb.*;
import jahspotify.media.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
@Qualifier(value = "mongodb")
public class MongDBMediaStorage implements MediaStorage
{
    private Log _log = LogFactory.getLog(MongDBMediaStorage.class);

    @Value(value="${jahspotify.storage.mongodb.host")
    private String _dbHost = "localhost";
    @Value(value="${jahspotify.storage.mongodb.port")
    private int _dbPort = 27017;
    @Value(value="${jahspotify.storage.mongodb.db-name")
    private String _dbName = "JahSpotify";

    private Mongo _mongoDBInstance;
    private DB _db;

    @PostConstruct
    public void initialize()
    {
        try
        {
            _mongoDBInstance = new Mongo();
            _db = _mongoDBInstance.getDB(_dbName);
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public void store(final Track track)
    {
        final DBCollection tracks = _db.getCollection("tracks");
        final BasicDBObject basicDBObject = new BasicDBObject();
        tracks.insert(basicDBObject);
    }

    @Override
    public Track readTrack(final Link uri)
    {
        return null;
    }

    @Override
    public void deleteTrack(final Link uri)
    {
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
    public Playlist readPlaylist(final Link uri)
    {
        return null;
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

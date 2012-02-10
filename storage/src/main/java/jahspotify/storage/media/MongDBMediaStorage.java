package jahspotify.storage.media;

import java.io.FileReader;
import javax.annotation.PostConstruct;

import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.util.JSON;
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

    public static void main(String[] args) throws Exception
    {
        MongDBMediaStorage mongDBMediaStorage = new MongDBMediaStorage();
        mongDBMediaStorage.initialize();
        Track track = new Gson().fromJson(new FileReader("/var/lib/jahspotify/simple-file-storage/tracks/3ZE8LPdngaax7rboeT7vtl"),Track.class );
        mongDBMediaStorage.store(track);
        Track readTrack = mongDBMediaStorage.readTrack(Link.create("spotify:track:3ZE8LPdngaax7rboeT7vtl"));
    }

    @Override
    public void store(final Track track)
    {
        final DBCollection tracks = _db.getCollection("tracks");
        final BasicDBObject basicDBObject = (BasicDBObject) JSON.parse(new Gson().toJson(track));
        basicDBObject.put("_id",track.getId().getId());
        final WriteResult insert = tracks.insert(basicDBObject);
    }

    @Override
    public Track readTrack(final Link uri)
    {
        final DBCollection tracks = _db.getCollection("tracks");

        BasicDBObject query = new BasicDBObject();
        query.put("_id", uri.getId());

        final DBObject one = tracks.findOne(query);
        
        if (one != null)
        {
            return new Gson().fromJson(JSON.serialize(one),Track.class);
        }

        return null;
    }

    @Override
    public void deleteTrack(final Link uri)
    {
        final DBCollection tracks = _db.getCollection("tracks");
        BasicDBObject query = new BasicDBObject();
        query.put("_id", uri.getId());
        final DBObject andRemove = tracks.findAndRemove(query);
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

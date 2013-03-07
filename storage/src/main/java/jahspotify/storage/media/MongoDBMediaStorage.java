package jahspotify.storage.media;

import javax.annotation.*;

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
public class MongoDBMediaStorage implements MediaStorage
{
    private Log _log = LogFactory.getLog(MongoDBMediaStorage.class);

    @Value(value = "${jahspotify.storage.mongodb.host}")
    private String _dbHost = "localhost";
    @Value(value = "${jahspotify.storage.mongodb.port}")
    private int _dbPort = 27017;
    @Value(value = "${jahspotify.storage.mongodb.db-name}")
    private String _dbName = "JahSpotify";

    private Mongo _mongoDBInstance;
    private DB _db;

    @PreDestroy
    public void shutdown()
    {
        if (_mongoDBInstance != null)
        {
            _log.debug("Shutting down Mongo DB instance");
            _mongoDBInstance.close();
            _mongoDBInstance = null;

        }
    }

    @PostConstruct
    public void initialize()
    {
        try
        {
            _log.debug("Initializing Mongo DB instance");
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
        final BasicDBObject basicDBObject = (BasicDBObject) JSON.parse(new Gson().toJson(track));
        basicDBObject.put("_id", track.getId().getId());
        final WriteResult insert = tracks.insert(basicDBObject);
    }

    @Override
    public Track readTrack(final Link uri)
    {
        final DBCollection tracks = _db.getCollection("tracks");

        final BasicDBObject query = new BasicDBObject();
        query.put("_id", uri.getId());

        final DBObject one = tracks.findOne(query);

        if (one != null)
        {
            return new Gson().fromJson(JSON.serialize(one), Track.class);
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
        final DBCollection tracks = _db.getCollection("artists");
        final BasicDBObject basicDBObject = (BasicDBObject) JSON.parse(new Gson().toJson(artist));
        basicDBObject.put("_id", artist.getId().getId());
        final WriteResult insert = tracks.insert(basicDBObject);
    }

    @Override
    public Artist readArtist(final Link uri)
    {
        final DBCollection tracks = _db.getCollection("artists");

        final BasicDBObject query = new BasicDBObject();
        query.put("_id", uri.getId());

        final DBObject one = tracks.findOne(query);

        if (one != null)
        {
            return new Gson().fromJson(JSON.serialize(one), Artist.class);
        }

        return null;
    }

    @Override
    public void store(final Album album)
    {
        final DBCollection tracks = _db.getCollection("albums");
        final BasicDBObject basicDBObject = (BasicDBObject) JSON.parse(new Gson().toJson(album));
        basicDBObject.put("_id", album.getId().getId());
        final WriteResult insert = tracks.insert(basicDBObject);
    }

    @Override
    public Album readAlbum(final Link uri)
    {
        final DBCollection tracks = _db.getCollection("albums");

        final BasicDBObject query = new BasicDBObject();
        query.put("_id", uri.getId());

        final DBObject one = tracks.findOne(query);

        if (one != null)
        {
            return new Gson().fromJson(JSON.serialize(one), Album.class);
        }
        return null;
    }

    @Override
    public void store(final Playlist playlist)
    {
        final DBCollection tracks = _db.getCollection("playlists");
        final BasicDBObject basicDBObject = (BasicDBObject) JSON.parse(new Gson().toJson(playlist));
        if (playlist.getId() != null)
        {
            basicDBObject.put("_id", playlist.getId().getId());
            final WriteResult insert = tracks.insert(basicDBObject);
        }
    }

    @Override
    public Playlist readPlaylist(final Link uri)
    {
        final DBCollection tracks = _db.getCollection("playlists");

        final BasicDBObject query = new BasicDBObject();
        query.put("_id", uri.getId());

        final DBObject one = tracks.findOne(query);

        if (one != null)
        {
            return new Gson().fromJson(JSON.serialize(one), Playlist.class);
        }
        return null;
    }

    @Override
    public void store(final Image image)
    {
        final DBCollection tracks = _db.getCollection("images");
        final BasicDBObject basicDBObject = (BasicDBObject) JSON.parse(new Gson().toJson(image));
        basicDBObject.put("_id", image.getId().getId());
        final WriteResult insert = tracks.insert(basicDBObject);
    }

    @Override
    public Image readImage(final Link uri)
    {
        final DBCollection tracks = _db.getCollection("images");

        final BasicDBObject query = new BasicDBObject();
        query.put("_id", uri.getId());

        final DBObject one = tracks.findOne(query);

        if (one != null)
        {
            return new Gson().fromJson(JSON.serialize(one), Image.class);
        }
        return null;
    }

    @Override
    public void deletePlaylist(final Link uri)
    {
        final DBCollection tracks = _db.getCollection("playlists");
        BasicDBObject query = new BasicDBObject();
        query.put("_id", uri.getId());
        final DBObject andRemove = tracks.findAndRemove(query);
    }
}

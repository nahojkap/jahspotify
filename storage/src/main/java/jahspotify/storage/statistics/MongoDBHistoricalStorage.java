package jahspotify.storage.statistics;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *        or more contributor license agreements.  See the NOTICE file
 *        distributed with this work for additional information
 *        regarding copyright ownership.  The ASF licenses this file
 *        to you under the Apache License, Version 2.0 (the
 *        "License"); you may not use this file except in compliance
 *        with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing,
 *        software distributed under the License is distributed on an
 *        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *        KIND, either express or implied.  See the License for the
 *        specific language governing permissions and limitations
 *        under the License.
 */

import java.util.*;

import javax.annotation.PostConstruct;

import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.util.JSON;
import jahspotify.media.Link;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
@Qualifier(value = "mongodb")
public class MongoDBHistoricalStorage implements HistoricalStorage
{
    private Log _log = LogFactory.getLog(MongoDBHistoricalStorage.class);

    @Value(value="${jahspotify.storage.mongodb.host}")
    private String _dbHost = "localhost";
    @Value(value="${jahspotify.storage.mongodb.port}")
    private int _dbPort = 27017;
    @Value(value="${jahspotify.storage.mongodb.db-name}")
    private String _dbName = "JahSpotify";

    private Mongo _mongoDBInstance;
    private DB _db;
    private static Gson _gson = new Gson();

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
    public TrackHistory getHistory(final Link trackLink)
    {
        final DBCollection tracks = _db.getCollection("history");

        final DBObject query = new BasicDBObject( "id", trackLink.getUri() );

        final DBCursor dbObjects = tracks.find(query);

        if (dbObjects.size() == 1)
        {
            return _gson.fromJson(JSON.serialize(dbObjects.next()), TrackHistory.class);
        }

        return null;
    }

    @Override
    public Collection<TrackHistory> getHistory(final int index, final int count, final HistoryCriteria... historyCriterias)
    {
        final DBCollection tracks = _db.getCollection("history");

        final DBCursor dbObjects = tracks.find();
        dbObjects.skip(index);
        dbObjects.limit(count);
        final BasicDBObject orderBy = new BasicDBObject();
        orderBy.put("startTime",-1);
        dbObjects.sort(orderBy);

        return new AbstractCollection<TrackHistory>()
        {
            @Override
            public Iterator<TrackHistory> iterator()
            {
                return new MongoDBHistoryCursor(dbObjects);
            }

            @Override
            public int size()
            {
                return dbObjects.size();
            }
        };
    }

    @Override
    public void addHistory(final TrackHistory trackHistory)
    {
        final DBCollection history = _db.getCollection("history");
        final BasicDBObject basicDBObject = (BasicDBObject) JSON.parse(new Gson().toJson(trackHistory));
        final WriteResult insert = history.insert(basicDBObject);
    }

    @Override
    public TrackStatistics getTrackStatistics(final Link trackLink)
    {
        final DBCollection tracks = _db.getCollection("history");

        final BasicDBObject query = new BasicDBObject();
        query.put("trackLink.id",trackLink.getId());
        final DBCursor dbObjects = tracks.find(query);
        final BasicDBObject orderBy = new BasicDBObject();
        orderBy.put("startTime",-1);

        dbObjects.sort(orderBy);

        TrackStatistics trackStatistics = new TrackStatistics();

        int numCompleted = 0;
        int numSkipped = 0;
        int totalSecondsPlayed = 0;

        if (dbObjects.hasNext())
        {
            final TrackHistory trackHistory = _gson.fromJson(JSON.serialize(dbObjects.next()), TrackHistory.class);
            trackStatistics.setTrackLink( trackHistory.getTrackLink() );
            trackStatistics.setLastPlayed(trackHistory.getStartTime());
            totalSecondsPlayed += trackHistory.getSecondsPlayed();
            numSkipped += trackHistory.isCompleteTrackPlayed() ? 0 : 1;
            numCompleted += trackHistory.isCompleteTrackPlayed() ? 1 : 0;

            if (!dbObjects.hasNext())
            {
                trackStatistics.setFirstPlayed(trackHistory.getStartTime());
            }
        }

        while (dbObjects.hasNext())
        {
            final TrackHistory trackHistory = _gson.fromJson(JSON.serialize(dbObjects.next()), TrackHistory.class);
            totalSecondsPlayed += trackHistory.getSecondsPlayed();
            numSkipped += trackHistory.isCompleteTrackPlayed() ? 0 : 1;
            numCompleted += trackHistory.isCompleteTrackPlayed() ? 1 : 0;

            if (!dbObjects.hasNext())
            {
                trackStatistics.setFirstPlayed(trackHistory.getStartTime());
            }

        }

        trackStatistics.setNumTimesCompleted(numCompleted);
        trackStatistics.setNumTimesSkipped(numSkipped);
        trackStatistics.setNumTimesPlayed(numCompleted+numSkipped);
        trackStatistics.setTotalPlaytime(totalSecondsPlayed);

        return trackStatistics;
    }

    @Override
    public int getHistoryCount(final HistoryCriteria... historyCriterias)
    {
        return 0;
    }
}

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
    public List<TrackHistory> getHistory(final int index, final int count, final HistoryCriteria... historyCriterias)
    {
        final DBCollection tracks = _db.getCollection("history");

        final DBCursor dbObjects = tracks.find();
        dbObjects.skip(index);
        dbObjects.limit(count);
        final BasicDBObject orderBy = new BasicDBObject();
        orderBy.put("_startTime",1);
        dbObjects.sort(orderBy);

        List<TrackHistory> trackHistories = new ArrayList<TrackHistory>();
        
        while (dbObjects.hasNext())
        {
            trackHistories.add(new Gson().fromJson(JSON.serialize(dbObjects.next()),TrackHistory.class));
        }

        return trackHistories;
    }

    @Override
    public void addHistory(final TrackHistory trackHistory)
    {
        final DBCollection history = _db.getCollection("history");
        final BasicDBObject basicDBObject = (BasicDBObject) JSON.parse(new Gson().toJson(trackHistory));
        final WriteResult insert = history.insert(basicDBObject);
    }

    @Override
    public AggregatedTrackStatistics aggregatedTrackStatistics(final Link trackLink)
    {
        return null;
    }

    @Override
    public List<TrackStatistics> trackStatistics(final Link trackLink, final int startFrom, final int count)
    {
        return null;
    }
}

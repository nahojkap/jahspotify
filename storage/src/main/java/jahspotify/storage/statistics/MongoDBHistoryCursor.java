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

import com.google.gson.Gson;
import com.mongodb.DBCursor;
import com.mongodb.util.JSON;

/**
 * @author Johan Lindquist
 */
public class MongoDBHistoryCursor implements HistoryCursor
{

    private DBCursor _dbObjects;
    private static Gson _gson = new Gson();
    
    MongoDBHistoryCursor(final DBCursor dbObjects)
    {
        _dbObjects = dbObjects;
    }

    @Override
    public int getCount()
    {
        return _dbObjects.count();
    }

    @Override
    public boolean hasNext()
    {
        return _dbObjects.hasNext();
    }

    @Override
    public TrackHistory next()
    {
        return _gson.fromJson(JSON.serialize(_dbObjects.next()), TrackHistory.class);
    }
}

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

/**
 * @author Johan Lindquist
 */
public class InMemoryHistoryCursor implements HistoryCursor
{
    private List<TrackHistory> _trackHistoryList;
    private Iterator<TrackHistory> _trackHistoryListIterator;

    public InMemoryHistoryCursor(final List<TrackHistory> trackHistoryList)
    {
        _trackHistoryList = trackHistoryList;
    }

    @Override
    public int getCount()
    {
        return _trackHistoryList.size();
    }

    @Override
    public boolean hasNext()
    {
        if (_trackHistoryListIterator == null)
        {
            _trackHistoryListIterator = _trackHistoryList.iterator();
        }
        return _trackHistoryListIterator.hasNext();
    }

    @Override
    public TrackHistory next()
    {
        if (_trackHistoryListIterator == null)
        {
            _trackHistoryListIterator = _trackHistoryList.iterator();
        }
        return _trackHistoryListIterator.next();
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Remove is not supported");
    }
}

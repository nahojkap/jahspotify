package jahspotify.services;

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

import jahspotify.media.Link;

/**
 * @author Johan Lindquist
 */
public class QueueNextTrack extends QueueTrack implements Comparable<QueueNextTrack>
{
    int _weight;

    public QueueNextTrack(final String id, final Link trackUri, final int weight, final Link queue)
    {
        super(id,trackUri, queue);
        _weight = weight;
    }

    public int getWeight()
    {
        return _weight;
    }

    public void setWeight(final int weight)
    {
        _weight = weight;
    }

    @Override
    public String toString()
    {
        return "QueueNextTrack{" +
                "_weight=" + _weight +
                "} " + super.toString();
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof QueueNextTrack))
        {
            return false;
        }
        if (!super.equals(o))
        {
            return false;
        }

        final QueueNextTrack that = (QueueNextTrack) o;

        if (_weight != that._weight)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + _weight;
        return result;
    }

    @Override
    public int compareTo(final QueueNextTrack other)
    {
        return _weight - other._weight;
    }
}

package jahspotify.web.system;

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

import jahspotify.web.queue.*;

/**
 * @author Johan Lindquist
 */
public class SystemStatus
{
    private long upSince;

    private long freeMemory;
    private long totalMemory;
    private long maxMemory;

    private int numberProcessors;

    private QueueConfiguration queueConfiguration;
    private String currentQueue;
    private QueueStatus queueStatus;

    public String getCurrentQueue()
    {
        return currentQueue;
    }

    public void setCurrentQueue(final String currentQueue)
    {
        this.currentQueue = currentQueue;
    }

    public QueueConfiguration getQueueConfiguration()
    {
        return queueConfiguration;
    }

    public void setQueueConfiguration(final QueueConfiguration queueConfiguration)
    {
        this.queueConfiguration = queueConfiguration;
    }

    public long getMaxMemory()
    {
        return maxMemory;
    }

    public void setMaxMemory(final long maxMemory)
    {
        this.maxMemory = maxMemory;
    }

    public long getFreeMemory()
    {
        return freeMemory;
    }

    public void setFreeMemory(final long freeMemory)
    {
        this.freeMemory = freeMemory;
    }

    public long getTotalMemory()
    {
        return totalMemory;
    }

    public void setTotalMemory(final long totalMemory)
    {
        this.totalMemory = totalMemory;
    }

    public long getUpSince()
    {
        return upSince;
    }

    public void setUpSince(final long upSince)
    {
        this.upSince = upSince;
    }

    public void setNumberProcessors(final int numberProcessors)
    {
        this.numberProcessors = numberProcessors;
    }

    public int getNumberProcessors()
    {
        return numberProcessors;
    }

    public void setQueueStatus(final QueueStatus queueStatus)
    {
        this.queueStatus = queueStatus;
    }

    public QueueStatus getQueueStatus()
    {
        return queueStatus;
    }
}

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

import java.util.List;

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
    private List<String> availableQueues;
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

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof SystemStatus))
        {
            return false;
        }

        final SystemStatus that = (SystemStatus) o;

        if (freeMemory != that.freeMemory)
        {
            return false;
        }
        if (maxMemory != that.maxMemory)
        {
            return false;
        }
        if (numberProcessors != that.numberProcessors)
        {
            return false;
        }
        if (totalMemory != that.totalMemory)
        {
            return false;
        }
        if (upSince != that.upSince)
        {
            return false;
        }
        if (availableQueues != null ? !availableQueues.equals(that.availableQueues) : that.availableQueues != null)
        {
            return false;
        }
        if (currentQueue != null ? !currentQueue.equals(that.currentQueue) : that.currentQueue != null)
        {
            return false;
        }
        if (queueConfiguration != null ? !queueConfiguration.equals(that.queueConfiguration) : that.queueConfiguration != null)
        {
            return false;
        }
        if (queueStatus != null ? !queueStatus.equals(that.queueStatus) : that.queueStatus != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int) (upSince ^ (upSince >>> 32));
        result = 31 * result + (int) (freeMemory ^ (freeMemory >>> 32));
        result = 31 * result + (int) (totalMemory ^ (totalMemory >>> 32));
        result = 31 * result + (int) (maxMemory ^ (maxMemory >>> 32));
        result = 31 * result + numberProcessors;
        result = 31 * result + (queueConfiguration != null ? queueConfiguration.hashCode() : 0);
        result = 31 * result + (currentQueue != null ? currentQueue.hashCode() : 0);
        result = 31 * result + (availableQueues != null ? availableQueues.hashCode() : 0);
        result = 31 * result + (queueStatus != null ? queueStatus.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "SystemStatus{" +
                "availableQueues=" + availableQueues +
                ", upSince=" + upSince +
                ", freeMemory=" + freeMemory +
                ", totalMemory=" + totalMemory +
                ", maxMemory=" + maxMemory +
                ", numberProcessors=" + numberProcessors +
                ", queueConfiguration=" + queueConfiguration +
                ", currentQueue='" + currentQueue + '\'' +
                ", queueStatus=" + queueStatus +
                '}';
    }
}

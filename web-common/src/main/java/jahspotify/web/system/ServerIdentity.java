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

/** Minimal class representing a Jahspotify server identity.
 *
 * @author Johan Lindquist
 */
public class ServerIdentity
{
    String serverId;
    String type = "MASTER";
    String apiVersion;
    String serverWebAddress;
    int webPort;
    private long upSince;

    public ServerIdentity()
    {
    }

    public ServerIdentity(final String serverId, final String apiVersion, final int webPort, final String master, final long upSince)
    {
        this.webPort = webPort;
        this.apiVersion = apiVersion;
        this.serverId = serverId;
        this.type = type;
        this.upSince = upSince;
    }

    public String getApiVersion()
    {
        return apiVersion;
    }

    public void setApiVersion(final String apiVersion)
    {
        this.apiVersion = apiVersion;
    }

    public String getServerId()
    {
        return serverId;
    }

    public void setServerId(final String serverId)
    {
        this.serverId = serverId;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public int getWebPort()
    {
        return webPort;
    }

    public void setWebPort(final int webPort)
    {
        this.webPort = webPort;
    }

    public String getServerWebAddress()
    {
        return serverWebAddress;
    }

    public void setServerWebAddress(final String serverWebAddress)
    {
        this.serverWebAddress = serverWebAddress;
    }

    public long getUpSince()
    {
        return upSince;
    }

    public void setUpSince(final long upSince)
    {
        this.upSince = upSince;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof ServerIdentity))
        {
            return false;
        }

        final ServerIdentity that = (ServerIdentity) o;

        if (upSince != that.upSince)
        {
            return false;
        }
        if (webPort != that.webPort)
        {
            return false;
        }
        if (apiVersion != null ? !apiVersion.equals(that.apiVersion) : that.apiVersion != null)
        {
            return false;
        }
        if (serverId != null ? !serverId.equals(that.serverId) : that.serverId != null)
        {
            return false;
        }
        if (serverWebAddress != null ? !serverWebAddress.equals(that.serverWebAddress) : that.serverWebAddress != null)
        {
            return false;
        }
        if (type != null ? !type.equals(that.type) : that.type != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = serverId != null ? serverId.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (apiVersion != null ? apiVersion.hashCode() : 0);
        result = 31 * result + (serverWebAddress != null ? serverWebAddress.hashCode() : 0);
        result = 31 * result + webPort;
        result = 31 * result + (int) (upSince ^ (upSince >>> 32));
        return result;
    }

    @Override
    public String toString()
    {
        return "ServerIdentity{" +
                "apiVersion='" + apiVersion + '\'' +
                ", serverId='" + serverId + '\'' +
                ", type='" + type + '\'' +
                ", serverWebAddress='" + serverWebAddress + '\'' +
                ", webPort=" + webPort +
                ", upSince=" + upSince +
                '}';
    }
}

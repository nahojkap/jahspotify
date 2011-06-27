package jahspotify.impl;

import jahspotify.ConnectionListener;

/**
 * @author Johan Lindquist
 */
public abstract class AbstractConnectionListener implements ConnectionListener
{
    @Override
    public void connected()
    {
    }

    @Override
    public void disconnected()
    {
    }

    @Override
    public void loggedIn()
    {
    }

    @Override
    public void loggedOut()
    {
    }
}

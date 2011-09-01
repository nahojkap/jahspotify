package jahspotify.service;

import javax.annotation.*;

import jahspotify.JahSpotify;
import jahspotify.impl.JahSpotifyImpl;
import jahspotify.storage.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
public class JahSpotifyService
{
    private JahSpotifyImpl _jahSpotifyImpl;

    @Autowired(required = false)
    @Qualifier(value ="in-memory")
    private MediaStorage _mediaStorage;

    @PostConstruct
    public void initialize()
    {
        if (_jahSpotifyImpl == null)
        {
            _jahSpotifyImpl = JahSpotifyImpl.getInstance();
            if (_mediaStorage != null)
            {
                _jahSpotifyImpl.setMediaStorage(_mediaStorage);
            }
            if (!_jahSpotifyImpl.isStarted())
            {
                _jahSpotifyImpl.login(System.getProperty("spotify.username"), System.getProperty("spotify.password"));
            }
        }
    }

    @PreDestroy
    public void shutdown()
    {
        if (_jahSpotifyImpl != null)
        {
            _jahSpotifyImpl.pause();
            _jahSpotifyImpl.stop();
        }
    }

    public JahSpotify getJahSpotify()
    {
        return _jahSpotifyImpl;
    }

}

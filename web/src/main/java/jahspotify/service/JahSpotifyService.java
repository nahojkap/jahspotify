package jahspotify.service;

import javax.annotation.*;

import jahspotify.JahSpotify;
import jahspotify.impl.JahSpotifyImpl;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
public class JahSpotifyService
{
    private JahSpotifyImpl _jahSpotifyImpl;

    @PostConstruct
    public void initialize()
    {
        if (_jahSpotifyImpl == null)
        {
            _jahSpotifyImpl = JahSpotifyImpl.getInstance();
            if (!_jahSpotifyImpl.isStarted())
            {
                _jahSpotifyImpl.start(System.getProperty("spotify.username"), System.getProperty("spotify.password"));
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

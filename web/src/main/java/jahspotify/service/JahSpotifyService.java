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
    private JahStorage _jahStorage;

    @PostConstruct
    public void initialize()
    {
        if (_jahSpotifyImpl == null)
        {
            _jahSpotifyImpl = JahSpotifyImpl.getInstance();
            if (_jahStorage != null)
            {
                _jahSpotifyImpl.setJahStorage(_jahStorage);
            }
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

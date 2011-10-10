package jahspotify.service;

import javax.annotation.*;

import jahspotify.JahSpotify;
import jahspotify.impl.JahSpotifyImpl;
import jahspotify.storage.media.MediaStorage;
import jahspotify.storage.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Service
public class JahSpotifyService
{
    private JahSpotify _jahSpotify;

    @Autowired(required = false)
    @Qualifier(value ="in-memory")
    private MediaStorage _mediaStorage;

    @PostConstruct
    public void initialize()
    {
        if (_jahSpotify == null)
        {
            if (_mediaStorage != null)
            {
                _jahSpotify = StorageAwareJahspotify.getInstance();
                ((StorageAwareJahspotify)_jahSpotify).setMediaStorage(_mediaStorage);
            }
            else
            {
                _jahSpotify = JahSpotifyImpl.getInstance();
            }
            if (!_jahSpotify.isStarted())
            {
                _jahSpotify.login(System.getProperty("spotify.username"), System.getProperty("spotify.password"));
            }
        }
    }

    @PreDestroy
    public void shutdown()
    {
        if (_jahSpotify != null)
        {
            _jahSpotify.pause();
            _jahSpotify.stop();
        }
    }

    public JahSpotify getJahSpotify()
    {
        return _jahSpotify;
    }

}

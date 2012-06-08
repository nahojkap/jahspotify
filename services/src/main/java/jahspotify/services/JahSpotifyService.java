package jahspotify.services;

import javax.annotation.*;

import jahspotify.JahSpotify;
import jahspotify.impl.JahSpotifyImpl;
import jahspotify.storage.media.MediaStorage;
import jahspotify.storage.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Service;

/**
 * @author Johan Lindquist
 */
@Configuration
public class JahSpotifyService
{
    private JahSpotify _jahSpotify;

    @Autowired(required = false)
    @Qualifier(value ="mongodb")
    private MediaStorage _mediaStorage;

    @Bean(name="JahSpotify")
    public JahSpotify createJahSpotify()
    {
        return _jahSpotify;
    }

    @PostConstruct
    public void initialize()
    {
        if (_jahSpotify == null)
        {
            if (_mediaStorage != null)
            {
                _jahSpotify = StorageAwareJahspotify.getInstanceStorageBasedInstance();
                ((StorageAwareJahspotify)_jahSpotify).setMediaStorage(_mediaStorage);
            }
            else
            {
                _jahSpotify = JahSpotifyImpl.getInstance();
            }
            if (!_jahSpotify.isStarted())
            {
                _jahSpotify.login(System.getProperty("jahspotify.spotify.username"), System.getProperty("jahspotify.spotify.password"));
            }
        }
    }

    @PreDestroy
    public void shutdown()
    {
        if (_jahSpotify != null)
        {
            // Stop playback and then shutdown the instance
            _jahSpotify.stop();
            _jahSpotify.shutdown();
        }
    }

    public JahSpotify getJahSpotify()
    {
        return _jahSpotify;
    }

}

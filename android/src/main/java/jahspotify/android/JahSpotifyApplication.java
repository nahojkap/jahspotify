package jahspotify.android;

import java.io.File;

import android.app.Application;
import jahspotify.android.data.ImageCache;

/**
 * @author Johan Lindquist
 */
public class JahSpotifyApplication extends Application
{
    @Override
    public void onCreate()
    {
        File cacheDir = getApplicationContext().getCacheDir();
        File imageCache = new File(cacheDir,"image-cache");
        imageCache.mkdirs();
        ImageCache.cachePath = imageCache;
    }
}

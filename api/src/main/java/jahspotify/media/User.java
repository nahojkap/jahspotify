package jahspotify.media;

import java.net.*;

public class User
{
    private String _fullName;
    private String _userName;
    private String _displayName;
    private String _country;
    private String _imageURL;
    // FIXME: image format!

    public String getCountry()
    {
        return _country;
    }

    public User()
    {
    }

    public String getDisplayName()
    {
        return _displayName;
    }

    public String getFullName()
    {
        return _fullName;
    }

    public URL getImageURL()
    {
        if (_imageURL != null)
        {
            try
            {
                return new URL(_imageURL);
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getUserName()
    {
        return _userName;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "_country='" + _country + '\'' +
                ", _fullName='" + _fullName + '\'' +
                ", _userName='" + _userName + '\'' +
                ", _displayName='" + _displayName + '\'' +
                ", _imageURL=" + _imageURL +
                '}';
    }
}

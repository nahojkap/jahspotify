package jahspotify.web.media;

import java.net.*;

public class User
{
    private String fullName;
    private String userName;
    private String displayName;
    private String country;
    private String imageURL;

    public String getCountry()
    {
        return country;
    }

    public User()
    {
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getFullName()
    {
        return fullName;
    }

    public URL getImageURL()
    {
        if (imageURL != null)
        {
            try
            {
                return new URL(imageURL);
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
        return userName;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "country='" + country + '\'' +
                ", fullName='" + fullName + '\'' +
                ", userName='" + userName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", imageURL=" + imageURL +
                '}';
    }
}

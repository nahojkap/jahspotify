package jahspotify.android.data;

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

import java.io.*;

import jahspotify.web.media.Link;

/**
 * @author Johan Lindquist
 */
public class ImageCache
{
    public static File cachePath;
    
    public static InputStream readImage(Link uri)
    {
        try
        {
            String id = uri.getId();

            File cachedImage = new File(cachePath,id);
            if (cachedImage.exists())
            {
                return new FileInputStream(cachedImage);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
    
    public static void storeImage(Link uri, byte[] data)
    {
        try
        {
            String id = uri.getId();
            File cachedImage = new File(cachePath,id);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cachedImage));
            bos.write(data);
            bos.flush();
            bos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}

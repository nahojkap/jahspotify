package jahspotify.android.activities;

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

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.*;
import jahspotify.android.R;
import jahspotify.android.data.LibraryRetriever;
import jahspotify.web.media.*;

/**
 * @author Johan Lindquist
 */
public class ListItemLoader
{

    public static void loadListItem(final Link trackLink, final View listItem) throws Exception
    {
        Thread t = new Thread()

        {
            @Override
            public void run()
            {
                try
                {
                    final Track track = LibraryRetriever.getTrack(trackLink);
                    final Activity a = (Activity) listItem.getContext();
                    a.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            TextView text = (TextView) listItem.findViewById(R.id.result_name);
                            text.setText(track.getTitle());
                        }
                    });
                    final Link albumLink = track.getAlbum();
                    final Album album = LibraryRetriever.getAlbum(albumLink);
                    a.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            TextView text = (TextView) listItem.findViewById(R.id.result_second_line);
                            text.setText(album.getName());
                            text.setVisibility(View.VISIBLE);
                        }
                    });

                    final InputStream image1 = LibraryRetriever.getImage(album.getCover());
                    final Drawable jahSpotify = Drawable.createFromStream(image1, "JahSpotify");
                    a.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ImageView image = (ImageView)listItem.findViewById(R.id.result_icon);
                            image.setImageDrawable(jahSpotify);
                            listItem.setTag(null);
                        }
                    });
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        };
        t.start();

    }

}

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
import java.util.*;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
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

    private static final String TAG = "ListItemLoader";

    public static class ListItemToLoad
    {
        public Link mTrackLink;
        public View mListItem;
    }

    public static void loadListItem(final List<ListItemToLoad> items) throws Exception
    {
        final Thread t = new Thread()
        {
            @Override
            public void run()
            {
                final List<ListItemToLoad> failedToLoad = new ArrayList<ListItemToLoad>();

                while (!items.isEmpty())
                {
                    final ListItemToLoad item = items.remove(0);

                    final Link trackLink = item.mTrackLink;
                    final View listItem = item.mListItem;
                    final Activity a = (Activity) listItem.getContext();

                    loadIt(failedToLoad, item, trackLink, listItem, a);
                }

                while (!failedToLoad.isEmpty())
                {
                    final ListItemToLoad item = failedToLoad.remove(0);
                    final Link trackLink = item.mTrackLink;
                    final View listItem = item.mListItem;
                    final Activity a = (Activity) listItem.getContext();
                    loadIt(failedToLoad, item, trackLink, listItem, a);

                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

            }
        };
        t.start();

    }

    private static void loadIt(final List<ListItemToLoad> failedToLoad, final ListItemToLoad item, final Link trackLink, final View listItem, final Activity a)
    {
        try
        {

            a.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (listItem.getTag() != null)
                    {
                        ImageView image = (ImageView) listItem.findViewById(R.id.result_icon);
                        image.setVisibility(View.INVISIBLE);

                        TextView text = (TextView) listItem.findViewById(R.id.result_name);
                        text.setText("Loading...");
                        text = (TextView) listItem.findViewById(R.id.result_second_line);
                        text.setVisibility(View.GONE);
                    }
                }
            });


            final FullTrack track = LibraryRetriever.getFullTrack(trackLink);
            if (track != null)
            {
                a.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        TextView text = (TextView) listItem.findViewById(R.id.result_name);
                        listItem.setTag(null);
                        text.setText(track.getTitle());

                        text = (TextView) listItem.findViewById(R.id.result_second_line);
                        if (track.getAlbumName() != null)
                        {
                            text.setText(track.getAlbumName());
                            text.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            failedToLoad.add(item);
                        }
                    }

                });

                final ImageView image = (ImageView) listItem.findViewById(R.id.result_icon);
                if (image.getVisibility() != View.VISIBLE)
                {
                    if (track.getAlbumCoverLink() != null)
                    {
                        final InputStream image1 = LibraryRetriever.getImage(track.getAlbumCoverLink());
                        final Drawable jahSpotify = Drawable.createFromStream(image1, "JahSpotify");
                        a.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                image.setImageDrawable(jahSpotify);
                                image.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else
                    {
                        failedToLoad.add(item);
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "Error retrieving image for link: " + trackLink, e);
            failedToLoad.add(item);
        }
    }

}

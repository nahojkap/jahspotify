package jahspotify.android.activities;

import java.io.IOException;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import jahspotify.android.R;
import jahspotify.android.data.LibraryRetriever;
import jahspotify.web.media.*;

/**
 * @author Johan Lindquist
 */
public class PlaylistBrowseActivity extends ListActivity implements ListView.OnScrollListener
{

    private TextView mStatus;

    private boolean mBusy = false;
    public PlaylistBrowseActivity.SlowAdapter _adapter;


    /**
     * Will not bind views while the list is scrolling
     */
    private class SlowAdapter extends BaseAdapter
    {
        private LayoutInflater mInflater;

        public SlowAdapter(Context context)
        {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * The number of items in the list is determined by the number of speeches
         * in our array.
         *
         * @see android.widget.ListAdapter#getCount()
         */
        public int getCount()
        {
            return _currentPlaylist.getTracks().size();
        }

        /**
         * Since the data comes from an array, just returning the index is
         * sufficent to get at the data. If we were using a more complex data
         * structure, we would return whatever object represents one row in the
         * list.
         *
         * @see android.widget.ListAdapter#getItem(int)
         */
        public Object getItem(int position)
        {
            return _currentPlaylist.getTracks().get(position);
        }

        /**
         * Use the array index as a unique id.
         *
         * @see android.widget.ListAdapter#getItemId(int)
         */
        public long getItemId(int position)
        {
            return position;
        }

        /**
         * Make a view to hold each row.
         *
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            TextView text;

            if (convertView == null)
            {
                text = (TextView) mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            else
            {
                text = (TextView) convertView;
            }

            text.setClickable(true);
            text.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View view)
                {
                    final Link link = _currentPlaylist.getTracks().get(position);
                    try
                    {
                        LibraryRetriever.queue(link);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            if (!mBusy)
            {
                try
                {
                    final Link trackLink = _currentPlaylist.getTracks().get(position);
                    Track track = LibraryRetriever.getTrack(trackLink);
                    text.setText(track.getTitle());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                // Null tag means the view has the correct data
                text.setTag(null);
            }
            else
            {
                text.setText("Loading...");
                // Non-null tag means the view still needs to load it's data
                text.setTag(this);
            }

            return text;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_browse);
        mStatus = (TextView) findViewById(R.id.status);
        mStatus.setText("Idle");

        final String uri = getIntent().getStringExtra("URI");
        try
        {
            Log.d("PlaylistBrowseActivity", "Retriving URI: " + uri);
            _currentPlaylist = LibraryRetriever.getPlaylist(new Link(uri,"playlist"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        _adapter = new SlowAdapter(this);
        setListAdapter(_adapter);

        getListView().setOnScrollListener(this);
    }


    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount)
    {
    }


    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        switch (scrollState)
        {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                mBusy = false;

                int first = view.getFirstVisiblePosition();
                int count = view.getChildCount();
                for (int i = 0; i < count; i++)
                {
                    TextView t = (TextView) view.getChildAt(i);
                    if (t.getTag() != null)
                    {
                        final Link trackLink = _currentPlaylist.getTracks().get(first + i);

                        try
                        {
                            Track track = LibraryRetriever.getTrack(trackLink);
                            t.setText(track.getTitle());
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }

                        t.setTag(null);
                    }
                }

                mStatus.setText("Idle");
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                mBusy = true;
                mStatus.setText("Touch scroll");
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                mBusy = true;
                mStatus.setText("Fling");
                break;
        }
    }

    private Playlist _currentPlaylist;


}

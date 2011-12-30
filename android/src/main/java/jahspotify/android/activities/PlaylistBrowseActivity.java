package jahspotify.android.activities;

import java.io.IOException;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import jahspotify.android.R;
import jahspotify.android.data.LibraryRetriever;
import jahspotify.client.JahSpotifyClient;
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
            View listItem;

            if (convertView == null)
            {
                listItem = mInflater.inflate(R.layout.list_entry,parent, false);
            }
            else
            {
                listItem  = convertView;
            }

            listItem.setClickable(true);
            listItem.setVisibility(View.VISIBLE);
            listItem.setOnClickListener(new View.OnClickListener()
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
                    updateListItem(listItem, trackLink);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                // Null tag means the view has the correct data
            }
            else
            {
                TextView text = (TextView) listItem.findViewById(R.id.result_name);
                text.setText("Loading...");
                text = (TextView) listItem.findViewById(R.id.result_second_line);
                text.setVisibility(View.GONE);
                // Non-null tag means the view still needs to load it's data
                listItem.setTag(this);
            }

            return listItem;
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
            _currentPlaylist = LibraryRetriever.getPlaylist(new Link(uri, Link.Type.PLAYLIST));
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
                    View t = view.getChildAt(i);
                    if (t.getTag() != null)
                    {
                        final Link trackLink = _currentPlaylist.getTracks().get(first + i);

                        try
                        {
                            updateListItem(t, trackLink);
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

    private void updateListItem(final View listItem, final Link trackLink) throws IOException
    {
        Track track = LibraryRetriever.getTrack(trackLink);
        ImageView image = (ImageView)listItem.findViewById(R.id.result_icon);
        Link albumLink = track.getAlbum();
        Album album = LibraryRetriever.getAlbum(albumLink);
        image.setImageDrawable(Drawable.createFromStream(LibraryRetriever.getImage(album.getCover()), "JahSpotify"));

        TextView text = (TextView) listItem.findViewById(R.id.result_name);
        text.setText(track.getTitle());

        text = (TextView) listItem.findViewById(R.id.result_second_line);
        text.setText(album.getName());
        text.setVisibility(View.VISIBLE);

        listItem.setTag(null);

    }

    private Playlist _currentPlaylist;


}

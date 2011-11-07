package jahspotify.android.activities;

import java.io.IOException;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import jahspotify.android.R;
import jahspotify.android.data.LibraryRetriever;
import jahspotify.web.media.*;

/**
 * @author Johan Lindquist
 */
public class FolderBrowseActivity extends ListActivity implements ListView.OnScrollListener
{

    private TextView mStatus;

    private boolean mBusy = false;
    public FolderBrowseActivity.SlowAdapter _adapter;


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
            return _currentFolder.getSubEntries().size();
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
            return _currentFolder.getSubEntries().get(position);
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
                    try
                    {
                        final Library.Entry clickedEntry = _currentFolder.getSubEntries().get(position);
                        if ("FOLDER".equals(clickedEntry.getType()))
                        {
                            _currentFolder = LibraryRetriever.getEntry(clickedEntry.getId(), 2);
                            _adapter.notifyDataSetInvalidated();
                        }
                        else if ("PLAYLIST".equals(clickedEntry.getType()))
                        {
                            // Display a playlist!
                            Intent intent = new Intent(FolderBrowseActivity.this, PlaylistBrowseActivity.class);
                            intent.putExtra("URI",clickedEntry.getId().getId());
                            startActivity(intent);
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            text.setLongClickable(true);
            text.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(final View view)
                {
                    final Link link = _currentFolder.getSubEntries().get(position).getId();
                    try
                    {
                        LibraryRetriever.queue(link);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    return true;
                }
            });

            if (!mBusy)
            {
                text.setText(_currentFolder.getSubEntries().get(position).getName());
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

        try
        {
            _currentFolder = LibraryRetriever.getRoot(1);
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
                        t.setText(_currentFolder.getSubEntries().get(first + i).getName());
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

    private Library.Entry _currentFolder;


}

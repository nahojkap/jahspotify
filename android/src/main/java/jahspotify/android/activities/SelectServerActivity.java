package jahspotify.android.activities;

import java.util.*;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import jahspotify.android.R;
import jahspotify.client.ServerBroadcasterClient;
import jahspotify.web.system.ServerIdentity;

/**
 * @author Johan Lindquist
 */
public class SelectServerActivity extends ListActivity implements ListView.OnScrollListener
{
    private List<ServerIdentity> _serverSet = new ArrayList<ServerIdentity>();
    private boolean mBusy;
    private SlowAdapter _adapter;
    private static final String TAG = "SelectServerActivity";

    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        switch (scrollState)
        {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                mBusy = false;

                int first = view.getFirstVisiblePosition();
                int count = view.getChildCount();
                for (int i = first; i < count; i++)
                {
                    View t = view.getChildAt(i);
                    if (t.getTag() != null)
                    {
                        ((TextView) t).setText(_serverSet.get(i).getServerId());

                    }
                }
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                mBusy = true;
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                mBusy = true;
                break;
        }
    }


    @Override
    public void onScroll(final AbsListView absListView, final int i, final int i1, final int i2)
    {
    }

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
            return _serverSet.size();
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
            return _serverSet.get(position);
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

            final ServerIdentity serverIdentity = _serverSet.get(position);

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
                    Intent intent = new Intent(SelectServerActivity.this,BrowserActivity.class);
                    intent.putExtra("ServerIdentity",serverIdentity);
                    startActivity(intent);
                    finish();
                }
            });

            text.setLongClickable(true);
            text.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(final View view)
                {
                    // TODO: Display server information
                    return true;
                }
            });

            if (!mBusy)
            {
                text.setText(serverIdentity.getServerId());
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
        setContentView(R.layout.select_server);

        _adapter = new SlowAdapter(this);

        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        setListAdapter(_adapter);

        getListView().setOnScrollListener(this);

        _serverSet = new ArrayList<ServerIdentity>();

        final AsyncTask<Object, ServerIdentity, List<ServerIdentity>> serverFinderTask = new AsyncTask<Object, ServerIdentity, List<ServerIdentity>>()
        {
            @Override
            protected List<ServerIdentity> doInBackground(final Object... objects)
            {
                try
                {
                    Set<ServerIdentity> identities = ServerBroadcasterClient.discoverServers("224.0.0.1", 9764, 40000, new ServerBroadcasterClient.ServerFoundListener()
                    {
                        @Override
                        public void serverFound(final ServerIdentity serverIdentity)
                        {
                            Log.d(TAG, "Server found: " + serverIdentity);
                            publishProgress(serverIdentity);
                        }
                    });
                    return new ArrayList<ServerIdentity>(identities);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onProgressUpdate(final ServerIdentity... values)
            {
                for (ServerIdentity value : values)
                {
                    _serverSet.add(value);
                }
                _adapter.notifyDataSetChanged();
            }
        };

        serverFinderTask.execute(null);

    }

}

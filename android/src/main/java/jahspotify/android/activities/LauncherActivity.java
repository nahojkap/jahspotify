package jahspotify.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.view.View;
import android.widget.TextView;
import jahspotify.android.R;

/**
 * @author Johan Lindquist
 */
public class LauncherActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);

        final Handler handler = new Handler(Looper.myLooper());

        final Runnable runnable = new Runnable()
        {
            public void run()
            {
                startActivity(new Intent(getBaseContext(), SelectServerActivity.class));
                finish();
            }
        };
        handler.postDelayed(runnable, 1000);


        final TextView text = (TextView) findViewById(R.id.init_messages);
        String formatted = String.format("%s", getText(R.string.jahspotify_dialog_title));
        text.setText(formatted);

        final View id = this.findViewById(R.id.launcher);

        id.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(final View view)
            {
                handler.removeCallbacks(runnable);
                startActivity(new Intent(getBaseContext(), SelectServerActivity.class));
                finish();
            }
        });
        id.setClickable(true);
    }
}

package jahspotify.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import jahspotify.android.R;
import jahspotify.web.system.ServerIdentity;

/**
 * @author Johan Lindquist
 */
public class BrowserActivity extends Activity
{

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Intent intent = this.getIntent();
        ServerIdentity serverIdentity = (ServerIdentity) intent.getExtras().get("ServerIdentity");

        WebView engine = (WebView) findViewById(R.id.web_engine);
        engine.getSettings().setJavaScriptEnabled(true);
        engine.loadUrl("http://" + serverIdentity.getServerWebAddress() + ":" + serverIdentity.getWebPort() + "/jahspotify/index.html");


    }

}

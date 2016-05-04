package com.usedashnow.dasher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

public class LoginActivity extends AppCompatActivity implements TurbolinksAdapter {
    private Toolbar mToolbar;
    private Menu mMenu;
    private String dashID;
    private String resourceID;
    private String location;
    private TurbolinksView turbolinksView;

    private static final String INTENT_URL = "intentUrl";
    private static final String RESOURCE_ID = "resourceID";
    private static final String DASH_ID = "dashID";
    public static final String IS_APP_ACTIVE = "isAppActive";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);

        // Find the custom TurbolinksView object in your layout
        turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_login_view);
        turbolinksView.setBackgroundColor(Color.rgb(238, 238, 238));

        TurbolinksSession.getDefault(this).addJavascriptInterface(new DashWebService(this), "Android");

        // Use passed in intentURL else, just stick with the base url...
        location = getIntent().getStringExtra(INTENT_URL) != null ? getIntent().getStringExtra(INTENT_URL) : (getResources().getString(R.string.base_url) + "/dashers/dashes");
        dashID = getIntent().getStringExtra(DASH_ID);
        resourceID = getIntent().getStringExtra(RESOURCE_ID);

        // Execute the visit
        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visit(location);

        // Make app active (used for push notifications)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(IS_APP_ACTIVE, true).apply();
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(IS_APP_ACTIVE, false).apply();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);

        this.mMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_login_about_dash:
                openChromeBrowserWithURL("https://usedashnow.com");
                return true;
            case R.id.action_login_forgot_password:
                openChromeBrowserWithURL("https://secure.usedashnow.com/dashers/password/new");
                return true;
            case R.id.action_login_sign_up:
                openChromeBrowserWithURL("https://secure.usedashnow.com/dashers/sign_up");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void openChromeBrowserWithURL(String url) {
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        // Since the webView is shared between activities, we need to tell Turbolinks
        // to load the location from the previous activity upon restarting
        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .restoreWithCachedSnapshot(true)
                .view(turbolinksView)
                .visit(location);
    }

    @Override
    public void onPageFinished() {
    }

    @Override
    public void onReceivedError(int errorCode) {

    }

    @Override
    public void pageInvalidated() {

    }

    @Override
    public void requestFailedWithStatusCode(int statusCode) {
        handleError(statusCode);
    }

    @Override
    public void visitCompleted() {

    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        Intent intent = null;

        if (location.contains("messages")) {
            intent = new Intent(this, ActiveDashActivity.class);
            intent.putExtra(INTENT_URL, location);

            intent.putExtra(DASH_ID, location.replaceAll("\\D+",""));

            this.startActivity(intent);
            this.finish();
        } else if (location.contains("dashes")) {
            intent = new Intent(this, DashesActivity.class);
            intent.putExtra(INTENT_URL, location);

            this.startActivity(intent);
            this.finish();
        } else if (location.contains("deliveries")) {
            intent = new Intent(this, FinishDashActivity.class);
            intent.putExtra(INTENT_URL, location);

            intent.putExtra(RESOURCE_ID, location.replaceAll("\\D+",""));

            this.startActivity(intent);
            this.finish();
        } else if (location.contains("sign_in")) {
            // nothing
        } else if (location.contains("locked")) {
            intent = new Intent(this, LockedActivity.class);
            intent.putExtra(INTENT_URL, location);

            this.startActivity(intent);
            this.finish();
        } else {
            intent = new Intent(this, DashesActivity.class);
            intent.putExtra(INTENT_URL, location);
            intent.putExtra(DASH_ID, dashID);
            intent.putExtra(RESOURCE_ID, resourceID);

            this.startActivity(intent);
            this.finish();
        }
    }

    private void handleError(int code) {
        if (code == 404) {
            TurbolinksSession.getDefault(this)
                    .activity(this)
                    .adapter(this)
                    .restoreWithCachedSnapshot(false)
                    .view(turbolinksView)
                    .visit(getResources().getString(R.string.base_url) + "/dashers/error");
        }
    }
}

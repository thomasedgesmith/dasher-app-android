package com.usedashnow.dasher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

public class ActiveDashActivity extends AppCompatActivity implements TurbolinksAdapter {
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
        setContentView(R.layout.activity_active_dash);

        mToolbar = (Toolbar) findViewById(R.id.active_dash_toolbar);
        mToolbar.setTitle(R.string.title_activity_active_dash);
        setSupportActionBar(mToolbar);

        // Find the custom TurbolinksView object in your layout
        turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_active_dash_view);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_active_dash, menu);

        this.mMenu = menu;

        return true;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_active_dash_finish:
                Intent intentFinish = new Intent(ActiveDashActivity.this, ActiveDashFinishActivity.class);
                intentFinish.putExtra(DASH_ID, dashID);
                intentFinish.putExtra(RESOURCE_ID, resourceID);
                intentFinish.putExtra(INTENT_URL, (getResources().getString(R.string.base_url) + "/dashers/dashes/"+dashID+"/finish"));
                this.startActivity(intentFinish);
                return true;
            case R.id.action_active_dash_help:
                Intent intentHelp = new Intent(ActiveDashActivity.this, ActiveDashHelpActivity.class);
                intentHelp.putExtra(DASH_ID, dashID);
                intentHelp.putExtra(RESOURCE_ID, resourceID);
                intentHelp.putExtra(INTENT_URL, (getResources().getString(R.string.base_url) + "/dashers/dashes/"+dashID+"/help"));
                this.startActivity(intentHelp);
                return true;
            case R.id.action_active_dash_details:
                Intent intentDetails = new Intent(ActiveDashActivity.this, ActiveDashDetailsActivity.class);
                intentDetails.putExtra(INTENT_URL, (getResources().getString(R.string.base_url) + "/dashers/dashes/"+dashID));
                intentDetails.putExtra(DASH_ID, dashID);
                intentDetails.putExtra(RESOURCE_ID, resourceID);
                this.startActivity(intentDetails);
                return true;
            case R.id.action_active_dash_change_bid:
                Intent intentChangeBid = new Intent(ActiveDashActivity.this, ActiveDashChangeBidActivity.class);
                intentChangeBid.putExtra(INTENT_URL, (getResources().getString(R.string.base_url) + "/dashers/dashes/"+dashID+"/change_bid"));
                intentChangeBid.putExtra(DASH_ID, dashID);
                intentChangeBid.putExtra(RESOURCE_ID, resourceID);
                this.startActivity(intentChangeBid);
                return true;
            case R.id.action_active_dash_report:
                Intent intentReport = new Intent(ActiveDashActivity.this, ActiveDashReportActivity.class);
                intentReport.putExtra(INTENT_URL, (getResources().getString(R.string.base_url) + "/dashers/dashes/"+dashID+"/file_report"));
                intentReport.putExtra(RESOURCE_ID, resourceID);
                intentReport.putExtra(DASH_ID, dashID);
                this.startActivity(intentReport);
                return true;
            case R.id.action_active_dash_cancel:
                Intent intentCancel = new Intent(ActiveDashActivity.this, ActiveDashCancelActivity.class);
                intentCancel.putExtra(INTENT_URL, (getResources().getString(R.string.base_url) + "/dashers/dashes/"+dashID+"/cancel"));
                intentCancel.putExtra(RESOURCE_ID, resourceID);
                intentCancel.putExtra(DASH_ID, dashID);
                this.startActivity(intentCancel);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        Intent intent = new Intent(this, DashesActivity.class);
        intent.putExtra(INTENT_URL, location);
        intent.putExtra(RESOURCE_ID, dashID);

        this.startActivity(intent);
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

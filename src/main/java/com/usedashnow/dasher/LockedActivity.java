package com.usedashnow.dasher;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

public class LockedActivity extends AppCompatActivity implements TurbolinksAdapter {
    private Toolbar mToolbar;
    private Menu mMenu;
    private String dashID;
    private String resourceID;
    private String location;
    private TurbolinksView turbolinksView;

    private static final String INTENT_URL = "intentUrl";
    private static final String RESOURCE_ID = "resourceID";
    private static final String DASH_ID = "dashID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked);

        mToolbar = (Toolbar) findViewById(R.id.locked_toolbar);
        mToolbar.setTitle(R.string.title_activity_locked_dash);
        setSupportActionBar(mToolbar);

        // Find the custom TurbolinksView object in your layout
        turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_locked_view);
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

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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
            intent = new Intent(this, LoginActivity.class);
            intent.putExtra(INTENT_URL, location);

            intent.putExtra(RESOURCE_ID, location.replaceAll("\\D+",""));

            this.startActivity(intent);
            this.finish();
        }  else if (location.contains("locked")) {
            // do nothing
        } else if (location.contains("mailto")) {
            intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, location);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Locked Dasher Account");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
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

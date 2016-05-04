package com.usedashnow.dasher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.basecamp.turbolinks.TurbolinksSession;

import java.util.Objects;

/**
 * Created by tamcgoey on 16-03-15.
 */
public class DashWebService {
    Activity mContext;
    private static final String BASE_URL = "https://secure.usedashnow.com";
    private static final String INTENT_URL = "intentUrl";
    private static final String RESOURCE_ID = "resourceID";
    private static final String DASH_ID = "dashID";
    private static final String DASH_DASHER_TOKEN = "dasherToken";
    public static final String GCM_TOKEN = "gcmToken";


    /** Instantiate the interface and set the context */
    DashWebService(Activity c) {
        mContext = c;
    }

    @JavascriptInterface
    public void enablePushNotifications(String apiToken) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreferences.edit().putString(DASH_DASHER_TOKEN, apiToken).apply();

        // Call GCM to register tokens in the background (only if we need to...
        if (sharedPreferences.getString(GCM_TOKEN, "") == "") {
            mContext.startService(new Intent(mContext, RegistrationIntentService.class));
        }
    }

    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    public void visit(String location) {
        TurbolinksSession.getDefault(mContext).visit(location);
    }

    @JavascriptInterface
    public void launchActivity(String activityName, String path, String finishPrevious) {
        String packageName = "com.usedashnow.dasher";

        if (!activityName.equals(mContext.getClass().getSimpleName())) {
            try {
                Class activityClass = Class.forName(packageName + "." + activityName);

                Intent intent = new Intent(mContext, activityClass);
                intent.putExtra(INTENT_URL, (BASE_URL + path));

                mContext.startActivity(intent);

                if (finishPrevious.equals("true")) {
                    mContext.finish();
                }
            } catch (Exception ex) {
                Toast.makeText(mContext, "invalid activity name: " + activityName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @JavascriptInterface
    public void launchActivityWithResourceID(String activityName, String path, String resourceID, String finishPrevious) {
        String packageName = "com.usedashnow.dasher";

        if (!activityName.equals(mContext.getClass().getSimpleName())) {
            try {
                Class activityClass = Class.forName(packageName + "." + activityName);

                Intent intent = new Intent(mContext, activityClass);
                intent.putExtra(INTENT_URL, (BASE_URL + path));
                intent.putExtra(RESOURCE_ID, resourceID);

                mContext.startActivity(intent);

                if (finishPrevious.equals("true")) {
                    mContext.finish();
                }
            } catch (Exception ex) {
                Toast.makeText(mContext, "invalid activity name: " + activityName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @JavascriptInterface
    public void launchActivityWithDashID(String activityName, String path, String dashID, String finishPrevious) {
        String packageName = "com.usedashnow.dasher";

        if (!activityName.equals(mContext.getClass().getSimpleName())) {
            try {
                Class activityClass = Class.forName(packageName + "." + activityName);

                Intent intent = new Intent(mContext, activityClass);
                intent.putExtra(INTENT_URL, (BASE_URL + path));
                intent.putExtra(DASH_ID, dashID);

                mContext.startActivity(intent);

                if (finishPrevious.equals("true")) {
                    mContext.finish();
                }
            } catch (Exception ex) {
                Toast.makeText(mContext, "invalid activity name: " + activityName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @JavascriptInterface
    public void launchActivityWithResourceIDAndDashID(String activityName, String path, String resourceID, String dashID, String finishPrevious) {
        String packageName = "com.usedashnow.dasher";

        if (!activityName.equals(mContext.getClass().getSimpleName())) {
            try {
                Class activityClass = Class.forName(packageName + "." + activityName);

                Intent intent = new Intent(mContext, activityClass);
                intent.putExtra(INTENT_URL, (BASE_URL + path));
                intent.putExtra(RESOURCE_ID, resourceID);
                intent.putExtra(DASH_ID, dashID);

                mContext.startActivity(intent);

                if (finishPrevious.equals("true")) {
                    mContext.finish();
                }
            } catch (Exception ex) {
                Toast.makeText(mContext, "invalid activity name: " + activityName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @JavascriptInterface
    public void closeCurrentActivity(String activityName) {
        String packageName = "com.usedashnow.dasher";

        if (activityName.equals(mContext.getClass().getSimpleName())) {
            mContext.finish();
        }
    }
}

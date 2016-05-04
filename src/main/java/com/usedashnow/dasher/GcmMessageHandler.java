package com.usedashnow.dasher;

import com.google.android.gms.gcm.GcmListenerService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.renderscript.RenderScript;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * Created by tamcgoey on 16-03-22.
 */
public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;
    public static final String IS_APP_ACTIVE = "isAppActive";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String title = data.getString("title");
        String body = data.getString("body");
        String type = data.getString("type");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isAppActive = sharedPreferences.getBoolean(IS_APP_ACTIVE, true);

        if (!isAppActive) {
            createNotification(title, body, type);
        }
    }

    // Creates notification based on title and body received
    private void createNotification(String title, String body, String type) {
        Context context = getBaseContext();

        Intent notificationIntent = null;

        if (type.contains("messages")) {
            notificationIntent = new Intent(context, ActiveDashActivity.class);
        } else if (type.contains("dashes")) {
            notificationIntent = new Intent(context, DashesActivity.class);
        } else if (type.contains("finish")) {
            notificationIntent = new Intent(context, FinishDashActivity.class);
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_dash_push_icon)
                .setColor(Color.rgb(0, 0, 0))
                .setContentTitle(title)
                .setContentIntent(intent)
                .setContentText(body)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

    }

}
package com.usedashnow.dasher;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by tamcgoey on 16-03-22.
 */
public class RegistrationIntentService extends IntentService {

    // abbreviated tag name
    private static final String TAG = "RegIntentService";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String GCM_TOKEN = "gcmToken";
    private static final String DASH_DASHER_TOKEN = "dasherToken";
    private static final String API_URL = "https://secure.usedashnow.com/api/v1/";


    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Make a call to Instance API
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getResources().getString(R.string.gcm_defaultSenderId);
        try {
            // request token that will be used by the server to send push notifications
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);

            // pass along this data
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putString(GCM_TOKEN, token).apply();

            sendRegistrationToServer(token);

        } catch (IOException e) {}
    }

    private void sendRegistrationToServer(String deviceToken) {
        // Add custom implementation, as needed.

        // if registration sent was successful, store a boolean that indicates whether the generated token has been sent to server
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();

        String dashToken = sharedPreferences.getString(DASH_DASHER_TOKEN, "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        // Create an instance of our GitHub API interface.
        DashAPI dashAPI = retrofit.create(DashAPI.class);

        Call <PushNotification> pushCall = dashAPI.createPushNotification(dashToken, new PushNotification(deviceToken, "1"));

        try {
            pushCall.execute().body();
        } catch (IOException e) {}

    }

    // Since we're just going to be posting data this one time, just keep things here... (Should be moved out if we have time)
    public static class PushNotification {
        public final String notify_background_token;
        public final String device_type;

        public PushNotification(String notify_background_token, String device_type) {
            this.notify_background_token = notify_background_token;
            this.device_type = device_type;
        }
    }

    public interface DashAPI {
        @POST("notifications")
        Call<PushNotification> createPushNotification(@Header("X-AUTH-USEDASHNOW") String authorization, @Body PushNotification pushNotification);
    }
}
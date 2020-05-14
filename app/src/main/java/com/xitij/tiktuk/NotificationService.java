package com.xitij.tiktuk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.xitij.tiktuk.activity.BannerActivity;
import com.xitij.tiktuk.activity.MainActivity;

import org.json.JSONObject;

import java.util.Date;

public class NotificationService extends FirebaseMessagingService {

    private static final String TAG = NotificationService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                JSONObject payload = json.getJSONObject("message");
                //handleDataMessage(json);
                Log.i("JSON",""+json);
                String message = payload.getString("message");
                //Log.i("JSON",""+message);

                /*Video video = new Video(obj.getString("language"),obj.getString("category"),
                        obj.getString("thumbnail"),obj.getString("title"),
                        obj.getString("id"),obj.getString("view"),
                        obj.getString("url"));*/

                if (json.getJSONObject("flag").getString("flag").equals("cron")) {

                    sendNotificationVideo(message,json.getString("data"));

                }
                if (json.getJSONObject("flag").getString("flag").equals("admin")) {
                    sendNotification(message,json.getJSONObject("data").getString("title"));
                }

                /*JSONArray jsonArray = new JSONArray();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("language",obj.getString("language"));
                jsonObject.put("category",obj.getString("category"));
                jsonObject.put("thumbnail",obj.getString("thumbnail"));
                jsonObject.put("title",obj.getString("title"));
                jsonObject.put("id",obj.getString("id"));
                jsonObject.put("view",obj.getString("views"));
                jsonObject.put("url",obj.getString("url"));

                jsonArray.put(jsonObject);*/

                //sendNotification(message,jsonArray.toString());

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e);
            }
        }


    }

    @Override
    public void onNewToken(String s) {
        Log.d(TAG, "Refreshed token: " + s);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String token) {
        getSharedPreferences("_",MODE_PRIVATE).edit().putString("u_t",token).apply();
    }

    public static void saveToken(Context context, String token) {
        context.getSharedPreferences("_",MODE_PRIVATE).edit().putString("u_t",token).apply();
    }

    private void sendNotificationVideo(String messageBody, String video) {
        Intent intent = new Intent(this, BannerActivity.class);
        intent.putExtra("live_noti",true);
        intent.putExtra("data",video);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_notification_large))
                        .setColor(getResources().getColor(R.color.pink_A400))
                        .setContentTitle("TikTuk")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "TikTuk Notification",
                    NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(m /* ID of notification */, notificationBuilder.build());
        }
    }

    private void sendNotification(String messageBody, String data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_notification_large))
                        .setColor(getResources().getColor(R.color.pink_A400))
                        .setContentTitle(data)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "TikTuk Notification",
                    NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(m /* ID of notification */, notificationBuilder.build());
        }
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_",MODE_PRIVATE).getString("u_t","null");
    }

}

package com.aktivo.hamster.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.aktivo.hamster.R;
import com.aktivo.hamster.dashboard.NotificationActivity;
import com.aktivo.hamster.data.database.AppDatabase;
import com.aktivo.hamster.data.database.NotificationEntity;
import com.aktivo.hamster.utils.SessionManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.concurrent.Executors;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FIREBASE_DEBUG";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "==> onNewToken DIPANGGIL! TOKEN BARU DITERIMA: " + token);
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.saveFcmToken(token);
    }

    private void saveNotificationToDb(String title, String body) {
        NotificationEntity notification = new NotificationEntity(
                title,
                body,
                System.currentTimeMillis(),
                false
        );

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getDatabase(getApplicationContext()).notificationDao().insert(notification);
            Log.d(TAG, "Notifikasi berhasil disimpan ke database.");
        });
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "=======================================");
        Log.d(TAG, "==> NOTIFIKASI DITERIMA!");
        Log.d(TAG, "==> Dari: " + remoteMessage.getFrom());

        String notificationTitle = null;
        String notificationBody = null;

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();

            notificationTitle = data.get("title");
            notificationBody = data.get("body");
        }

        if (remoteMessage.getNotification() != null) {
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
        }

        if (notificationTitle != null && notificationBody != null) {
            sendNotification(notificationTitle, notificationBody);
            saveNotificationToDb(notificationTitle, notificationBody);
        }
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
        Log.i(TAG, "Notifikasi sistem berhasil ditampilkan.");
    }
}
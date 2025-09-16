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
import java.util.UUID;
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

    private void saveNotificationToDb(String id, String title, String body, String link, String copyString) {
        NotificationEntity notification = new NotificationEntity(
                id,
                title,
                body,
                System.currentTimeMillis(),
                false,
                link,
                copyString
        );

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getDatabase(getApplicationContext()).notificationDao().insert(notification);
            Log.d(TAG, "Notifikasi berhasil disimpan ke database.");
        });
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "=======================================================");
        Log.d(TAG, "==> NOTIFIKASI DITERIMA!");
        Log.d(TAG, "==> Dari: " + remoteMessage.getFrom());
        Log.d(TAG, "==> Message ID: " + remoteMessage.getMessageId());
        Log.d(TAG, "==> Waktu Pengiriman: " + remoteMessage.getSentTime());


        String notificationTitle = null;
        String notificationBody = null;
        String link = null;
        String copyString = null;

        // Log data payload
        if (!remoteMessage.getData().isEmpty()) {
            Map<String, String> data = remoteMessage.getData();
            Log.d(TAG, "==> Data Payload: " + data.toString());
            notificationTitle = data.get("title");
            notificationBody = data.get("body");
            link = data.get("link");
            copyString = data.get("copyString");
        }

        // Log notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "==> Notification Body: " + remoteMessage.getNotification().getBody());
            Log.d(TAG, "==> Notification Title: " + remoteMessage.getNotification().getTitle());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
        }

        if (notificationTitle != null && notificationBody != null) {
            Log.d(TAG, "Notifikasi valid. Memproses untuk ditampilkan...");
            String id = remoteMessage.getMessageId();
            if (id == null) {
                id = UUID.randomUUID().toString();
                Log.w(TAG, "Message ID null, membuat ID acak: " + id);
            }
            sendNotification(notificationTitle, notificationBody);
            saveNotificationToDb(id, notificationTitle, notificationBody, link, copyString);
        } else {
            Log.w(TAG, "Gagal memproses notifikasi karena title atau body null.");
        }
        Log.d(TAG, "=======================================================");
    }

    private void sendNotification(String title, String messageBody) {
        Log.d(TAG, "Membuat notifikasi sistem...");
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
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
        Log.i(TAG, "Notifikasi sistem berhasil ditampilkan.");
    }
}
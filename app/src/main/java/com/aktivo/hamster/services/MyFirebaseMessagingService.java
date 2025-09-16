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
import com.aktivo.hamster.dashboard.DashboardActivity;
import com.aktivo.hamster.dashboard.NotificationActivity;
import com.aktivo.hamster.data.model.request.RegisterDeviceRequest;
import com.aktivo.hamster.data.model.request.UnregisterDeviceRequest;
import com.aktivo.hamster.data.network.ApiClient;
import com.aktivo.hamster.data.network.ApiService;
import com.aktivo.hamster.utils.SessionManager;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "=======================================");
        Log.d(TAG, "==> NOTIFIKASI DITERIMA!");
        Log.d(TAG, "==> Dari: " + remoteMessage.getFrom());

        String notificationTitle = null;
        String notificationBody = null;

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "==> Tipe: Pesan DATA");
            Log.d(TAG, "==> Payload Data: " + remoteMessage.getData());
            notificationTitle = remoteMessage.getData().get("title");
            notificationBody = remoteMessage.getData().get("body");
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "==> Tipe: Pesan NOTIFICATION");
            Log.d(TAG, "==> Judul: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "==> Body: " + remoteMessage.getNotification().getBody());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
        }

        if (notificationTitle != null && notificationBody != null) {
            sendNotification(notificationTitle, notificationBody);
        } else {
            Log.w(TAG, "Gagal menampilkan notifikasi karena judul atau body kosong.");
        }
        Log.d(TAG, "=======================================");
    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM token received: " + token);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        sessionManager.saveFcmToken(token);
    }

    private void sendRegistrationToServer(String token) {
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "FCM Token is null or empty. Cannot register.");
            return;
        }

        RegisterDeviceRequest requestBody = new RegisterDeviceRequest(token, "ANDROID");

        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        apiService.registerDeviceToken(requestBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Device token registered successfully to server.");
                } else {
                    Log.w(TAG, "Failed to register token. Server responded with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to register token due to network error or exception.", t);
            }
        });
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
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }


}
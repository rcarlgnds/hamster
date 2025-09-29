package com.aktivo.hamster;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.aktivo.hamster.dashboard.DashboardActivity;
import com.aktivo.hamster.login.LoginActivity;
import com.aktivo.hamster.utils.SessionManager;
import com.aktivo.hamster.utils.managers.FcmTokenManager;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "FIREBASE_DEBUG";
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "Izin notifikasi DIBERIKAN.");
                } else {
                    Log.w(TAG, "Izin notifikasi DITOLAK.");
                }
                proceedToNextActivity();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fetchFcmToken();
        askNotificationPermission();
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Izin notifikasi dh ada.");
                proceedToNextActivity();
            } else {
                Log.d(TAG, "Asking for izin notifikasi...");
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            proceedToNextActivity();
        }
    }


    private void fetchFcmToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "=======================================================");
                        Log.w(TAG, "==> GAGAL mendapatkan FCM token:", task.getException());
                        Log.w(TAG, "=======================================================");
                        return;
                    }

                    String token = task.getResult();
                    Log.d(TAG, "==> FCM Token didapatkan: " + token);
                    SessionManager sessionManager = new SessionManager(this);
                    sessionManager.saveFcmToken(token);
                });
    }

    private void proceedToNextActivity() {
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getAuthToken() != null) {
            FcmTokenManager.sendTokenToServerIfNeeded(this);
            navigateTo(DashboardActivity.class);
        } else {
            navigateTo(LoginActivity.class);
        }
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(SplashActivity.this, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
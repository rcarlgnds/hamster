package com.aktivo.hamster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.aktivo.hamster.dashboard.DashboardActivity;
import com.aktivo.hamster.login.LoginActivity;
import com.aktivo.hamster.utils.SessionManager;
import com.aktivo.hamster.utils.managers.FcmTokenManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "FIREBASE_DEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fetchFcmToken();

        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getAuthToken() != null) {
            FcmTokenManager.sendTokenToServerIfNeeded(this);
            navigateTo(DashboardActivity.class);
        } else {
            navigateTo(LoginActivity.class);
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
                    SessionManager sessionManager = new SessionManager(this);
                    sessionManager.saveFcmToken(token);
                });
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(SplashActivity.this, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
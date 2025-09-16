package com.aktivo.hamster;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.aktivo.hamster.dashboard.DashboardActivity;
import com.aktivo.hamster.login.LoginActivity;
import com.aktivo.hamster.utils.SessionManager;
import com.aktivo.hamster.utils.managers.FcmTokenManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
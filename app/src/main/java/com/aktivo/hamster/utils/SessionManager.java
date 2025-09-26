package com.aktivo.hamster.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.aktivo.hamster.data.database.AppDatabase;
import com.aktivo.hamster.data.model.User;
import com.aktivo.hamster.data.model.request.UnregisterDeviceRequest;
import com.aktivo.hamster.data.network.ApiClient;
import com.aktivo.hamster.data.network.ApiService;
import com.aktivo.hamster.login.LoginActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionManager {
    private static final String PREF_NAME = "HamsterAppSession";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_DATA = "userDataJson";
    private static final String KEY_LANGUAGE = "app_language";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final String KEY_FCM_TOKEN = "fcmToken";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Gson gson;
    private final Context context;

    public SessionManager(Context context) {
        this.context = context.getApplicationContext();
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();
    }

    public void saveAuthToken(String token) {
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
    }

    public String getAuthToken() {
        return pref.getString(KEY_ACCESS_TOKEN, null);
    }

    public void saveRefreshToken(String refreshToken) {
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public String getRefreshToken() {
        return pref.getString(KEY_REFRESH_TOKEN, null);
    }


    public void saveUser(User user) {
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER_DATA, userJson);
        editor.apply();
    }

    public User getUser() {
        String userJson = pref.getString(KEY_USER_DATA, null);
        if (userJson == null) {
            return null;
        }
        return gson.fromJson(userJson, User.class);
    }

    public void setLanguage(String languageCode) {
        editor.putString(KEY_LANGUAGE, languageCode);
        editor.apply();
    }

    public String getLanguage() {
        return pref.getString(KEY_LANGUAGE, "en");
    }

    public void saveThemeMode(int themeMode) {
        editor.putInt(KEY_THEME_MODE, themeMode);
        editor.apply();
    }

    public int getThemeMode() {
        return pref.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void saveFcmToken(String token) {
        editor.putString(KEY_FCM_TOKEN, token);
        editor.apply();
    }

    public String getFcmToken() {
        return pref.getString(KEY_FCM_TOKEN, null);
    }

    public void createLoginSession(String accessToken, String refreshToken, User user) {
        String userJson = gson.toJson(user);
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_USER_DATA, userJson);
        editor.apply();
    }

    private void unregisterDeviceFromServerAndLogout() {
        String jwt = getAuthToken();
        if (jwt == null || jwt.trim().isEmpty()) {
            proceedToClearAndNavigate();
            return;
        }

        ApiClient.getClient(context)
            .create(ApiService.class)
            .unregisterDeviceToken()
            .enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.i("SessionManager", "Token unregistered successfully from server.");
                    } else {
                        Log.w("SessionManager", "Unregister failed. Code: " + response.code());
                    }
                    proceedToClearAndNavigate();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e("SessionManager", "Error unregistering token.", t);
                    proceedToClearAndNavigate();
                }
            });
    }

    private void proceedToClearAndNavigate() {
        editor.clear();
        editor.apply();

        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public void logout() {
        unregisterDeviceFromServerAndLogout();
    }
}
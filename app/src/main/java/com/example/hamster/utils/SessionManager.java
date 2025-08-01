package com.example.hamster.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.hamster.data.model.User;
import com.example.hamster.login.LoginActivity;
import com.google.gson.Gson;

public class SessionManager {
    private static final String PREF_NAME = "HamsterAppSession";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_DATA = "userDataJson";

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

    public void createLoginSession(String accessToken, String refreshToken, User user) {
        String userJson = gson.toJson(user);
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_USER_DATA, userJson);
        editor.apply();
    }


    public void logout() {
        editor.clear();
        editor.apply();

        Intent i = new Intent(context, LoginActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(i);
    }
}
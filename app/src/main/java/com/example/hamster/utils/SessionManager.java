package com.example.hamster.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.hamster.data.model.User;
import com.google.gson.Gson;

public class SessionManager {
    private static final String PREF_NAME = "HamsterAppSession";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_USER_DATA = "userDataJson";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Gson gson;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();
    }

    public void saveAuthToken(String token) {
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
    }

    public String fetchAuthToken() {
        return pref.getString(KEY_ACCESS_TOKEN, null);
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

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
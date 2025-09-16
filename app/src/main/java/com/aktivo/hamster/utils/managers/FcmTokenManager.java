package com.aktivo.hamster.utils.managers;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.aktivo.hamster.data.model.request.RegisterDeviceRequest;
import com.aktivo.hamster.data.network.ApiClient;
import com.aktivo.hamster.data.network.ApiService;
import com.aktivo.hamster.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FcmTokenManager {
    private static final String TAG = "FcmTokenManager";
    public static void sendTokenToServerIfNeeded(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        String fcmToken = sessionManager.getFcmToken();

        if (fcmToken != null && !fcmToken.isEmpty()) {
            Log.d(TAG, "Found a pending FCM token to send: " + fcmToken);

            RegisterDeviceRequest requestBody = new RegisterDeviceRequest(fcmToken, "ANDROID");
            ApiService apiService = ApiClient.getClient(context).create(ApiService.class);

            apiService.registerDeviceToken(requestBody).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "FCM token registered successfully.");
                        sessionManager.saveFcmToken(null);
                    } else {
                        Log.w(TAG, "Failed to register FCM token. Code: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e(TAG, "Error registering FCM token.", t);
                }
            });
        } else {
            Log.d(TAG, "No pending FCM token to send.");
        }
    }
}
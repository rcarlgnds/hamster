package com.example.hamster.data.network;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hamster.data.model.request.RefreshTokenRequest;
import com.example.hamster.data.model.token.RefreshTokenResponse;
import com.example.hamster.utils.SessionManager;
import java.io.IOException;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

public class TokenAuthenticator implements Authenticator {

    private final SessionManager sessionManager;
    private final Context context;

    public TokenAuthenticator(Context context) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        String oldRefreshToken = sessionManager.getRefreshToken();

        if (oldRefreshToken == null) {
            sessionManager.logout();
            return null;
        }

        ApiService cleanApiService = ApiClient.createService(ApiService.class);
        Call<RefreshTokenResponse> call = cleanApiService.refreshToken(new RefreshTokenRequest(oldRefreshToken));

        try {
            retrofit2.Response<RefreshTokenResponse> refreshResponse = call.execute();

            if (refreshResponse.isSuccessful() && refreshResponse.body() != null && refreshResponse.body().getData() != null) {
                String newAccessToken = refreshResponse.body().getData().getAccessToken();
                String newRefreshToken = refreshResponse.body().getData().getRefreshToken();


                if (newAccessToken != null && !newAccessToken.isEmpty()) {
                    sessionManager.saveAuthToken(newAccessToken);
                    if(newRefreshToken != null && !newRefreshToken.isEmpty()){
                        sessionManager.saveRefreshToken(newRefreshToken);
                    }


                    return response.request().newBuilder()
                            .header("Authorization", "Bearer " + newAccessToken)
                            .build();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sessionManager.logout();

        return null;
    }
}
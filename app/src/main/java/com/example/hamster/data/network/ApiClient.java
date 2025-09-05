package com.example.hamster.data.network;

import android.content.Context;

import com.example.hamster.utils.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Untuk panggilan API
    public static final String BASE_URL = "https://hamster-be.spira.id/api/";

    // Untuk mengakses file media
    public static final String BASE_MEDIA_URL = "https://hamster-be.spira.id";

    /**
     * Membuat instance Retrofit utama yang digunakan di seluruh aplikasi.
     * Instance ini sudah dikonfigurasi dengan:
     * 1. Interceptor untuk menambahkan token otentikasi.
     * 2. Authenticator untuk menangani token yang kedaluwarsa (error 401).
     * 3. Logging untuk debugging.
     * 4. Timeout settings.
     *
     * @param context Context aplikasi, dibutuhkan oleh SessionManager dan TokenAuthenticator.
     * @return Instance Retrofit yang sudah dikonfigurasi.
     */
    public static Retrofit getClient(Context context) {
        Context applicationContext = context.getApplicationContext();
        SessionManager sessionManager = new SessionManager(applicationContext);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            String token = sessionManager.getAuthToken();
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder();

            if (token != null) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        httpClient.authenticator(new TokenAuthenticator(applicationContext));

        httpClient.addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }
}
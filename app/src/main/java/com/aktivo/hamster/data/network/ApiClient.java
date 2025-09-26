package com.aktivo.hamster.data.network;

import android.content.Context;

import com.aktivo.hamster.utils.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Untuk panggilan API
//    public static final String BASE_URL = "http://195.35.20.102:9180/api/";
    public static final String BASE_URL = "https://hamster-be.spira.id/api/";

    // Untuk mengakses file media
//    public static final String BASE_MEDIA_URL = "http://195.35.20.102:9180";
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

        OkHttpClient httpClient = new OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();

                String jwt = new SessionManager(context).getAuthToken();
                if (jwt != null && !jwt.trim().isEmpty()) {
                    builder.header("Authorization", "Bearer " + jwt);
                }

                return chain.proceed(builder.build());
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }
}
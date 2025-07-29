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
    // Alamat dasar untuk panggilan API
    public static final String BASE_URL = "http://195.35.20.102:9180/api/";

    // Alamat dasar untuk mengakses file media
    public static final String BASE_MEDIA_URL = "http://195.35.20.102:9180";

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
        // Gunakan ApplicationContext untuk mencegah memory leak
        Context applicationContext = context.getApplicationContext();
        SessionManager sessionManager = new SessionManager(applicationContext);

        // Setup logging untuk melihat detail request dan response di Logcat
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // 1. Menambahkan Interceptor untuk menyisipkan token ke setiap request
        httpClient.addInterceptor(chain -> {
            // Ambil token terbaru dari SharedPreferences SETIAP KALI ada request baru.
            // Ini penting agar token yang sudah di-refresh bisa langsung digunakan.
            String token = sessionManager.getAuthToken();
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder();

            if (token != null) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        // 2. Menambahkan Authenticator untuk menangani error 401 (token expired)
        httpClient.authenticator(new TokenAuthenticator(applicationContext));

        // 3. Menambahkan logging dan mengatur timeout
        httpClient.addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        // 4. Membangun instance Retrofit dengan client yang sudah dikonfigurasi
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Membuat instance Retrofit "bersih" (tanpa interceptor atau authenticator).
     * Method ini KHUSUS digunakan oleh TokenAuthenticator untuk memanggil API refresh token.
     * Tujuannya untuk menghindari infinite loop jika request refresh token itu sendiri gagal.
     *
     * @param serviceClass Kelas interface dari service Retrofit (misal: ApiService.class).
     * @return Instance dari service yang diminta.
     */
    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }
}
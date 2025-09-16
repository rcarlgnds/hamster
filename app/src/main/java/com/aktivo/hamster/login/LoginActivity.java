package com.aktivo.hamster.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.aktivo.hamster.R;
import com.aktivo.hamster.dashboard.DashboardActivity;
import com.aktivo.hamster.data.model.request.RegisterDeviceRequest;
import com.aktivo.hamster.data.network.ApiClient;
import com.aktivo.hamster.data.network.ApiService;
import com.aktivo.hamster.databinding.ActivityLoginBinding;
import com.aktivo.hamster.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding.buttonLogin.setOnClickListener(v -> {
            String email = binding.editTextEmail.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();
            if (!email.isEmpty() && !password.isEmpty()) {
                loginViewModel.login(email, password);
            } else {
                Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        setupObservers();
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonLogin.setText("");
            binding.buttonLogin.setEnabled(false);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.buttonLogin.setText(getString(R.string.login_button));
            binding.buttonLogin.setEnabled(true);
        }
    }

    private void setupObservers() {
        loginViewModel.getLoginResult().observe(this, result -> {
            switch (result) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    break;
                case INVALID_CREDENTIALS:
                case API_ERROR:
                    showLoading(false);
                    String errorMessage = loginViewModel.getErrorMessage().getValue();
                    if (errorMessage == null) {
                        errorMessage = "An unknown error occurred.";
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        loginViewModel.getUser().observe(this, user -> {
            if (user != null) {
                sendFcmTokenToServer();

                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }

    private void sendFcmTokenToServer() {
        SessionManager sessionManager = new SessionManager(this);
        String fcmToken = sessionManager.getFcmToken();

        if (fcmToken != null && !fcmToken.isEmpty()) {
            Log.d("LoginActivity", "Found saved FCM token, sending to server: " + fcmToken);

            RegisterDeviceRequest requestBody = new RegisterDeviceRequest(fcmToken, "ANDROID");
            ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

            apiService.registerDeviceToken(requestBody).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.i("LoginActivity", "FCM token registered successfully.");
                        sessionManager.saveFcmToken(null);
                    } else {
                        Log.w("LoginActivity", "Failed to register FCM token. Code: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e("LoginActivity", "Error registering FCM token.", t);
                }
            });
        } else {
            Log.d("LoginActivity", "No saved FCM token to send.");
        }
    }
}
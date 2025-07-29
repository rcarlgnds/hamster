package com.example.hamster.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.hamster.R;
import com.example.hamster.dashboard.DashboardActivity;
import com.example.hamster.data.model.User;
import com.example.hamster.databinding.ActivityLoginBinding;

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
            // You can add validation here if you want
            if (!email.isEmpty() && !password.isEmpty()) {
                loginViewModel.login(email, password);
            } else {
                Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        setupObservers();
    }

    /**
     * Manages the UI state of the login button and progress bar.
     * @param isLoading true to show the loading indicator, false to show the button text.
     */
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
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }
}
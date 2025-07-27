package com.example.hamster.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.hamster.R;
import com.example.hamster.dashboard.DashboardActivity;
import com.example.hamster.data.model.User;
import com.example.hamster.databinding.ActivityLoginBinding;
import com.google.android.material.textfield.TextInputEditText;

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
            String email = binding.editTextEmail.getText().toString();
            String password = binding.editTextPassword.getText().toString();
            loginViewModel.login(email, password);
        });

        setupObservers();
    }

    private void setupObservers() {
        loginViewModel.getLoginResult().observe(this, result -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.buttonLogin.setEnabled(true);

            switch (result) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.buttonLogin.setEnabled(false);
                    break;
                case INVALID_CREDENTIALS:
                    Toast.makeText(this, "Email atau Password Salah", Toast.LENGTH_SHORT).show();
                    break;
                case API_ERROR:
                    Toast.makeText(this, "Gagal terhubung ke server.", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        loginViewModel.getUser().observe(this, user -> {
            if (user != null) {
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                intent.putExtra("USER_DATA", user);
                startActivity(intent);

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}
package com.example.hamster.dashboard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hamster.R;
import com.example.hamster.data.model.User;
import com.example.hamster.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView rvFeatures;
    private FeatureAdapter featureAdapter;
    private SessionManager sessionManager;
    private TextInputEditText etSearch;
    private MaterialButton btnLogout;
    private TextView tvWelcomeUser;
    private TextView tvUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);

        rvFeatures = findViewById(R.id.rv_features);
        etSearch = findViewById(R.id.et_search);
        btnLogout = findViewById(R.id.btn_logout);
        tvWelcomeUser = findViewById(R.id.tv_welcome_user);
        tvUserRole = findViewById(R.id.tv_user_role);

        setupUserProfile();
        setupFeatures();
        setupSearch();
        setupLogoutButton();
    }

    private void setupUserProfile() {
        User currentUser = sessionManager.getUser();
        if (currentUser != null) {
            String welcomeMessage = "Welcome, " + currentUser.getFirstName();
            tvWelcomeUser.setText(welcomeMessage);

            tvUserRole.setText(currentUser.getEmail());
        }
    }

    private void setupFeatures() {
        List<FeatureAdapter.Feature> features = new ArrayList<>();
        features.add(new FeatureAdapter.Feature("Inventory", R.drawable.ic_inventory));
        features.add(new FeatureAdapter.Feature("Activation", R.drawable.ic_activation));

        featureAdapter = new FeatureAdapter(this, features);
        rvFeatures.setAdapter(featureAdapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (featureAdapter != null) {
                    featureAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupLogoutButton() {
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> sessionManager.logout())
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}
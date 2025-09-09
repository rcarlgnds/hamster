package com.example.hamster.dashboard;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hamster.R;
import com.example.hamster.data.constant.Controls;
import com.example.hamster.data.constant.Permissions;
import com.example.hamster.data.model.Control;
import com.example.hamster.data.model.Permission;
import com.example.hamster.data.model.User;
import com.example.hamster.databinding.ActivityDashboardBinding;
import com.example.hamster.databinding.ActivityLoginBinding;
import com.example.hamster.inventory.InventoryActivity;
import com.example.hamster.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView rvFeatures;
    private FeatureAdapter featureAdapter;
    private SessionManager sessionManager;
    private TextInputEditText etSearch;
    private TextView tvWelcomeUser;
    private TextView tvUserRole;
    private TextView tvGoToProfile;
    private ShapeableImageView ivTheme;
    private ShapeableImageView ivNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        sessionManager = new SessionManager(this);

        setupViews();
        setupUserProfile();
        setupFeatures();
        setupSearch();
    }

    private void setupViews() {
        rvFeatures = findViewById(R.id.rv_features);
        etSearch = findViewById(R.id.et_search);
        tvWelcomeUser = findViewById(R.id.tv_welcome_user);
        tvUserRole = findViewById(R.id.tv_user_role);

        tvGoToProfile = findViewById(R.id.tv_go_to_profile);
        tvGoToProfile.setPaintFlags(tvGoToProfile.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        ivNotification = findViewById(R.id.iv_user_notification);
    }

    private void setupUserProfile() {
        User currentUser = sessionManager.getUser();
        if (currentUser != null) {
            String welcomeMessage = "Welcome, " + currentUser.getFirstName();
            tvWelcomeUser.setText(welcomeMessage);
            tvUserRole.setText(currentUser.getEmail());
        }

        tvGoToProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        ivNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });
    }

    private void setupFeatures() {
        List<FeatureAdapter.Feature> features = new ArrayList<>();

        if (userHasPermission(Permissions.PERMISSION_INVENTORY_VIEW_ALL_LIST) || userHasPermission(Permissions.PERMISSION_INVENTORY_VIEW_HOSPITAL_LIST)) {
            features.add(new FeatureAdapter.Feature("Inventory", R.drawable.ic_inventory));
        }
        if (userHasAnyOfControls(Controls.CONTROL_APPROVAL_STEP_0)) {
            features.add(new FeatureAdapter.Feature("Activation", R.drawable.ic_activation));
        }
        if (userHasAnyOfControls(Controls.CONTROL_APPROVAL_STEP_1)) {
            features.add(new FeatureAdapter.Feature("Confirmation", R.drawable.ic_approval));
        }
        if (userHasAnyOfControls(Controls.CONTROL_APPROVAL_STEP_2)) {
            features.add(new FeatureAdapter.Feature("Approval", R.drawable.ic_approval));
        }

        featureAdapter = new FeatureAdapter(this, features);
        rvFeatures.setAdapter(featureAdapter);
    }

    private boolean userHasAnyOfControls(String... requiredKeys) {
        User currentUser = sessionManager.getUser();
        if (currentUser == null || currentUser.getControls() == null || requiredKeys.length == 0) {
            return false;
        }

        Log.d(TAG, "--- Memeriksa Kumpulan Control untuk user: " + currentUser.getEmail() + " ---");

        for (Control userControl : currentUser.getControls()) {
            if (userControl != null && userControl.getKey() != null) {
                for (String requiredKey : requiredKeys) {
                    if (requiredKey.equals(userControl.getKey())) {
                        Log.i(TAG, "COCOK! Ditemukan control key yang valid: '" + requiredKey + "'. Akses diberikan.");
                        return true;
                    }
                }
            }
        }

        Log.w(TAG, "TIDAK COCOK. Tidak ada satupun control key yang valid ditemukan.");
        return false;
    }

    private boolean userHasPermission(String requiredPermissionKey) {
        User currentUser = sessionManager.getUser();

        if (currentUser == null) {
            Log.d("PermissionCheck", "Current user is null.");
            return false;
        }
        if (currentUser.getPermissions() == null) {
            Log.d("PermissionCheck", "Permissions list is null for user: " + currentUser.getEmail());
            return false;
        }

        Log.d("PermissionCheck", "--- Checking Permissions for user: " + currentUser.getEmail() + " ---");
        Log.d("PermissionCheck", "Looking for permission: '" + requiredPermissionKey + "'");

        for (Permission permission : currentUser.getPermissions()) {
            Log.d("PermissionCheck", "Found permission key: '" + permission.getKey() + "'");
            if (requiredPermissionKey.equals(permission.getKey())) {
                Log.d("PermissionCheck", "MATCH FOUND! Returning true.");
                return true;
            }
        }

        Log.d("PermissionCheck", "NO MATCH FOUND. Returning false.");
        return false;
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
}
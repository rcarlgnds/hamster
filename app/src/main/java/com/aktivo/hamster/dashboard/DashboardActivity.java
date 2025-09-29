package com.aktivo.hamster.dashboard;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import com.aktivo.hamster.R;
import com.aktivo.hamster.data.constant.Controls;
import com.aktivo.hamster.data.constant.Permissions;
import com.aktivo.hamster.data.constant.Routes;
import com.aktivo.hamster.data.model.Control;
import com.aktivo.hamster.data.model.Permission;
import com.aktivo.hamster.data.model.User;
import com.aktivo.hamster.databinding.ActivityDashboardBinding;
import com.aktivo.hamster.databinding.ActivityLoginBinding;
import com.aktivo.hamster.utils.SessionManager;
import com.aktivo.hamster.utils.ThemeSetup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Route;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView rvFeatures;
    private FeatureAdapter featureAdapter;
    private SessionManager sessionManager;
    private ActivityDashboardBinding binding;

    private TextInputEditText etSearch;
    private TextView tvWelcomeUser;
    private TextView tvWelcomeEmail;
    private TextView tvGoToProfile;
    private ShapeableImageView ivUserAvatar;
    private ShapeableImageView ivNotification;
    private ShapeableImageView ivSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        ThemeSetup.applyTheme(sessionManager.getThemeMode());
        ThemeSetup.setLocale(sessionManager.getLanguage());

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViews();
        setupUserProfile();
        setupFeatures();
        setupSearch();
    }

    private void setupViews() {
        rvFeatures = findViewById(R.id.rv_features);
        etSearch = findViewById(R.id.et_search);
        tvWelcomeUser = findViewById(R.id.tv_welcome_user);
        tvWelcomeEmail = findViewById(R.id.tv_welcome_email);

        tvGoToProfile = findViewById(R.id.tv_go_to_profile);

        ivUserAvatar = findViewById(R.id.iv_user_avatar);
        ivNotification = findViewById(R.id.iv_user_notification);
        ivSettings = findViewById(R.id.iv_settings);
    }


    private void setupUserProfile() {
        User currentUser = sessionManager.getUser();
        if (currentUser != null) {
            String welcomeText = "Welcome, " + currentUser.getFirstName() + "!";
            tvWelcomeUser.setText(welcomeText);
            tvWelcomeEmail.setText(currentUser.getEmail());
        }

        View.OnClickListener profileClickListener = v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        };

        ivNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationActivity.class));
        });

        ivSettings.setOnClickListener(v -> showSettingDialog());

        ivUserAvatar.setOnClickListener(profileClickListener);
        tvWelcomeUser.setOnClickListener(profileClickListener);
        tvWelcomeEmail.setOnClickListener(profileClickListener);

        findViewById(R.id.card_user_info).setOnClickListener(profileClickListener);
        tvGoToProfile.setOnClickListener(profileClickListener);
    }

    private void setupFeatures() {
        List<FeatureAdapter.Feature> features = new ArrayList<>();
        features.add(new FeatureAdapter.Feature("Scan QR", R.drawable.ic_qr_code, Routes.SCAN_QR));

        if (userHasAnyOfControls(Controls.CONTROL_APPROVAL_STEP_0) ||
                userHasAnyOfControls(Controls.CONTROL_APPROVAL_STEP_1) ||
                userHasAnyOfControls(Controls.CONTROL_APPROVAL_STEP_2)) {
            features.add(new FeatureAdapter.Feature(getString(R.string.menu_inventory), R.drawable.ic_inventory, Routes.INVENTORY));
        }
        if (userHasAnyOfControls(Controls.CONTROL_APPROVAL_STEP_0)) {
            features.add(new FeatureAdapter.Feature(getString(R.string.menu_activation), R.drawable.ic_activation, Routes.ACTIVATION));
        }
        if (userHasAnyOfControls(Controls.CONTROL_APPROVAL_STEP_1)) {
            features.add(new FeatureAdapter.Feature(getString(R.string.menu_confirmation), R.drawable.ic_check_circle, Routes.CONFIRMATION));
        }
        if (userHasAnyOfControls(Controls.CONTROL_APPROVAL_STEP_2)) {
            features.add(new FeatureAdapter.Feature(getString(R.string.menu_approval), R.drawable.ic_approval, Routes.APPROVAL));
        }
        if(userHasAnyOfControls(Controls.CONTROL_APPROVAL_STEP_0) ||
                userHasAnyOfControls(Controls.CONTROL_APPROVAL_STEP_2) ||
                userHasAnyOfControls(Controls.CONTROL_APPROVAL_STEP_3) ||
                userHasAnyOfControls(Controls.CONTROL_APPROVAL_STEP_4)) {
            features.add(new FeatureAdapter.Feature(getString(R.string.menu_rejection), R.drawable.ic_cancel, Routes.REJECTED));
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

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_application_settings, null);
        builder.setView(dialogView);

        RadioGroup rgTheme = dialogView.findViewById(R.id.radioGroupTheme);
        RadioButton rbLight = dialogView.findViewById(R.id.radioLight);
        RadioButton rbDark = dialogView.findViewById(R.id.radioDark);
        RadioButton rbSystem = dialogView.findViewById(R.id.radioSystem);

        RadioGroup rgLanguage = dialogView.findViewById(R.id.radioGroupLanguage);
        RadioButton rbLangEn = dialogView.findViewById(R.id.radioLangEn);
        RadioButton rbLangId = dialogView.findViewById(R.id.radioLangId);

        Button btnCancel = dialogView.findViewById(R.id.buttonCancel);
        Button btnSave = dialogView.findViewById(R.id.buttonSave);

        String currentLang = sessionManager.getLanguage();
        if ("in".equals(currentLang)) {
            rbLangId.setChecked(true);
        } else {
            rbLangEn.setChecked(true);
        }


        final AlertDialog dialog = builder.create();

        int currentTheme = sessionManager.getThemeMode();
        if (currentTheme == AppCompatDelegate.MODE_NIGHT_NO) {
            rbLight.setChecked(true);
        } else if (currentTheme == AppCompatDelegate.MODE_NIGHT_YES) {
            rbDark.setChecked(true);
        } else {
            rbSystem.setChecked(true);
        }

        final int[] selectedTheme = {currentTheme};
        final String[] selectedLang = {sessionManager.getLanguage()};


        rgTheme.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioLight) {
                selectedTheme[0] = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.radioDark) {
                selectedTheme[0] = AppCompatDelegate.MODE_NIGHT_YES;
            } else if (checkedId == R.id.radioSystem) {
                selectedTheme[0] = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }
        });

        rgLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioLangId) {
                selectedLang[0] = "in";
            } else {
                selectedLang[0] = "en";
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            sessionManager.saveThemeMode(selectedTheme[0]);
            ThemeSetup.applyTheme(selectedTheme[0]);

            sessionManager.setLanguage(selectedLang[0]);
            ThemeSetup.setLocale(selectedLang[0]);

            dialog.dismiss();
        });

        dialog.show();
    }
}
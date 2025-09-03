package com.example.hamster.dashboard;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hamster.R;
import com.example.hamster.activation.ActivationApprovalActivity;
import com.example.hamster.data.model.Control;
import com.example.hamster.data.model.Permission;
import com.example.hamster.data.model.User;
import com.example.hamster.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvFeatures;
    private FeatureAdapter featureAdapter;
    private SessionManager sessionManager;
    private TextInputEditText etSearch;
    private TextView tvWelcomeUser;
    private TextView tvUserRole;

    private static final String PERMISSION_INVENTORY_VIEW = "ASSETS.VIEW_ALL_LIST";
    private static final String PERMISSION_ACTIVATION_VIEW = "ASSET_ACTIVATION.VIEW_ALL_LIST";

    private static final String CONTROL_APPROVAL_STEP_1 = "asset_activation_step_1";
    private static final String CONTROL_APPROVAL_STEP_2 = "asset_activation_step_2";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvFeatures = view.findViewById(R.id.rv_features);
        etSearch = view.findViewById(R.id.et_search);
        tvWelcomeUser = view.findViewById(R.id.tv_welcome_user);
        tvUserRole = view.findViewById(R.id.tv_user_role);

        setupUserProfile();
        setupFeatures();
        setupSearch();

        return view;
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

        if (userHasPermission(PERMISSION_INVENTORY_VIEW)) {
            features.add(new FeatureAdapter.Feature("Inventory", R.drawable.ic_inventory));
        }
        if (userHasPermission(PERMISSION_ACTIVATION_VIEW)) {
            features.add(new FeatureAdapter.Feature("Activation", R.drawable.ic_activation));
        }
        if (userHasAnyOfControls(CONTROL_APPROVAL_STEP_1, CONTROL_APPROVAL_STEP_2)) {
            features.add(new FeatureAdapter.Feature("Approval", R.drawable.ic_approval));
        }

        featureAdapter = new FeatureAdapter(requireContext(), features);
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
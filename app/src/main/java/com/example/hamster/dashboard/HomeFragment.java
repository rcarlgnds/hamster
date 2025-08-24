package com.example.hamster.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

    private static final String PERMISSION_ACTIVATION_APPROVAL = "asset-activation:approve";

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
        features.add(new FeatureAdapter.Feature("Inventory", R.drawable.ic_inventory));
        features.add(new FeatureAdapter.Feature("Activation", R.drawable.ic_activation));

        if (userHasPermission(PERMISSION_ACTIVATION_APPROVAL)) {
            features.add(new FeatureAdapter.Feature("Activation Approval", R.drawable.ic_approval));
        }

        featureAdapter = new FeatureAdapter(requireContext(), features);
        rvFeatures.setAdapter(featureAdapter);
    }

    private boolean userHasPermission(String requiredPermissionKey) {
        User currentUser = sessionManager.getUser();
        if (currentUser == null || currentUser.getPermissions() == null) {
            return false;
        }
        for (Permission permission : currentUser.getPermissions()) {
            if (requiredPermissionKey.equals(permission.getKey())) {
                return true;
            }
        }
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
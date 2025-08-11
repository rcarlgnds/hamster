package com.example.hamster.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.hamster.R;
import com.example.hamster.data.model.User;
import com.example.hamster.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    private SessionManager sessionManager;
    private TextView tvProfileName;
    private TextView tvProfileEmail;
    private MaterialButton btnLogout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvProfileName = view.findViewById(R.id.tv_profile_name);
        tvProfileEmail = view.findViewById(R.id.tv_profile_email);
        btnLogout = view.findViewById(R.id.btn_logout_profile);

        setupUserProfile();
        setupLogoutButton();

        return view;
    }

    private void setupUserProfile() {
        User currentUser = sessionManager.getUser();
        if (currentUser != null) {
            tvProfileName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
            tvProfileEmail.setText(currentUser.getEmail());
        }
    }

    private void setupLogoutButton() {
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> sessionManager.logout())
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}
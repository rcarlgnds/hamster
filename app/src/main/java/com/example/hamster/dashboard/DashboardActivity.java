package com.example.hamster.dashboard;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.example.hamster.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    private NotificationViewModel notificationViewModel;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        navView = findViewById(R.id.bottom_navigation);

        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        setupBadgeObserver();

        navView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_notifications) {
                selectedFragment = new NotificationsFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }

            return true;
        });

        if (savedInstanceState == null) {
            navView.setSelectedItemId(R.id.nav_home);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        notificationViewModel.fetchUnreadCount();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void setupBadgeObserver() {
        notificationViewModel.getUnreadCount().observe(this, count -> {
            BadgeDrawable badge = navView.getOrCreateBadge(R.id.nav_notifications);
            if (count != null && count > 0) {
                badge.setVisible(true);
                badge.setNumber(count);
            } else {
                badge.setVisible(false);
                badge.clearNumber();
            }
        });
    }
}
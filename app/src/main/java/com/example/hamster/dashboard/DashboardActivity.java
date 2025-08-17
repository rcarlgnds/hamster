package com.example.hamster.dashboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.hamster.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    private NotificationViewModel notificationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_dashboard);

        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navView, navController);

        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        setupBadgeObserver(navView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        notificationViewModel.fetchUnreadCount();
    }

    private void setupBadgeObserver(BottomNavigationView navView) {
        notificationViewModel.getUnreadCount().observe(this, count -> {
            BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_notifications);
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
package com.example.hamster.dashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hamster.R;
import com.example.hamster.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private NotificationViewModel notificationViewModel;
    private NotificationAdapter notificationAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView emptyState;
    private Button markAllReadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        sessionManager = new SessionManager(this);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        setupToolbar();
        setupViews();
        setupObservers();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Always refresh
        notificationViewModel.fetchNotifications();
        notificationViewModel.fetchUnreadCount();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupViews() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationAdapter = new NotificationAdapter(this, new ArrayList<>(), (notification, position) -> {
            // Click notification, mark as read
            if (!notification.isRead()) {
                notificationViewModel.markNotificationAsRead(notification.getId(), position);
            }
            // TODO: Tambahkan navigasi ke detail, contoh:
            // Intent intent = new Intent(getContext(), DetailActivity.class);
            // intent.putExtra("some_id", notification.getData().getRelatedId());
            // startActivity(intent);
        });
        recyclerView.setAdapter(notificationAdapter);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            notificationViewModel.fetchNotifications();
            notificationViewModel.fetchUnreadCount();
        });

        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.text_empty_state);
        markAllReadButton = findViewById(R.id.button_mark_all_read);

        markAllReadButton.setOnClickListener(v -> {
            showConfirmationDialog();
        });
    }

    private void showConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Konfirmasi")
                .setMessage("Anda yakin ingin menandai semua notifikasi sebagai telah dibaca?")
                .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Ya", (dialog, which) -> {
                    notificationViewModel.markAllNotificationsAsRead();
                    Toast.makeText(this, "Menandai semua sebagai telah dibaca...", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void setupObservers() {
        notificationViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading && !swipeRefreshLayout.isRefreshing()) {
                progressBar.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        notificationViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        notificationViewModel.getNotifications().observe(this, notifications -> {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE); // Dismiss progress bar

            if (notifications != null && !notifications.isEmpty()) {
                notificationAdapter.updateData(notifications);
                emptyState.setVisibility(View.GONE);
                markAllReadButton.setVisibility(View.VISIBLE);
            } else {
                notificationAdapter.updateData(new ArrayList<>());
                emptyState.setVisibility(View.VISIBLE);
                markAllReadButton.setVisibility(View.GONE);
            }
        });
    }
}
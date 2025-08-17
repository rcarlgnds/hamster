package com.example.hamster.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hamster.R;
import com.example.hamster.data.model.Notification;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private NotificationViewModel notificationViewModel;
    private NotificationAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView emptyState;
    private Button markAllReadButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        setupObservers();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Selalu refresh data saat fragment kembali ditampilkan
        notificationViewModel.fetchNotifications();
        notificationViewModel.fetchUnreadCount();
    }

    private void setupViews(View root) {
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NotificationAdapter(requireContext(), new ArrayList<>(), (notification, position) -> {
            // Klik pada notifikasi akan menandainya sebagai dibaca
            if (!notification.isRead()) {
                notificationViewModel.markNotificationAsRead(notification.getId(), position);
            }
            // TODO: Tambahkan navigasi ke detail, contoh:
            // Intent intent = new Intent(getContext(), DetailActivity.class);
            // intent.putExtra("some_id", notification.getData().getRelatedId());
            // startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            notificationViewModel.fetchNotifications();
            notificationViewModel.fetchUnreadCount();
        });

        progressBar = root.findViewById(R.id.progress_bar);
        emptyState = root.findViewById(R.id.text_empty_state);
        markAllReadButton = root.findViewById(R.id.button_mark_all_read);

        markAllReadButton.setOnClickListener(v -> {
            showConfirmationDialog();
        });
    }

    private void showConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Konfirmasi")
                .setMessage("Anda yakin ingin menandai semua notifikasi sebagai telah dibaca?")
                .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Ya", (dialog, which) -> {
                    notificationViewModel.markAllNotificationsAsRead();
                    Toast.makeText(getContext(), "Menandai semua sebagai telah dibaca...", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void setupObservers() {
        notificationViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading && !swipeRefreshLayout.isRefreshing()) {
                progressBar.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        notificationViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        notificationViewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE); // Pastikan progress bar hilang

            if (notifications != null && !notifications.isEmpty()) {
                adapter.updateData(notifications);
                emptyState.setVisibility(View.GONE);
                markAllReadButton.setVisibility(View.VISIBLE);
            } else {
                adapter.updateData(new ArrayList<>());
                emptyState.setVisibility(View.VISIBLE);
                markAllReadButton.setVisibility(View.GONE);
            }
        });
    }
}
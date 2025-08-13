package com.example.hamster.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hamster.R;
import com.example.hamster.data.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationViewModel notificationViewModel;
    private RecyclerView rvNotifications;
    private NotificationAdapter notificationAdapter;
    private ProgressBar progressBar;
    private final List<Notification> notificationList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvNotifications = view.findViewById(R.id.rv_notifications);
        progressBar = view.findViewById(R.id.progressBar);

        setupRecyclerView();

        observeViewModel();

        notificationViewModel.fetchNotifications();
    }

    private void setupRecyclerView() {
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationAdapter = new NotificationAdapter(getContext(), notificationList);
        rvNotifications.setAdapter(notificationAdapter);
    }

    private void observeViewModel() {
        notificationViewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications != null) {
                notificationList.clear();
                notificationList.addAll(notifications);
                notificationAdapter.notifyDataSetChanged();
            }
        });

        notificationViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        notificationViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
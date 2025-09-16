package com.aktivo.hamster.dashboard;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aktivo.hamster.data.database.AppDatabase;
import com.aktivo.hamster.data.database.NotificationDao;
import com.aktivo.hamster.data.database.NotificationEntity;
import com.aktivo.hamster.data.model.Notification;
import com.aktivo.hamster.data.model.User;
import com.aktivo.hamster.data.model.request.UpdateNotificationRequest;
import com.aktivo.hamster.data.model.response.NotificationResponse;
import com.aktivo.hamster.data.model.response.UnreadCountResponse;
import com.aktivo.hamster.data.network.ApiClient;
import com.aktivo.hamster.data.network.ApiService;
import com.aktivo.hamster.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private final MutableLiveData<Integer> unreadCount = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final NotificationDao notificationDao;
    private LiveData<List<NotificationEntity>> userNotifications;
    private String currentUserId;

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        notificationDao = db.notificationDao();
        apiService = ApiClient.getClient(application).create(ApiService.class);

        SessionManager sessionManager = new SessionManager(application);
        User currentUser = sessionManager.getUser();
        if (currentUser != null) {
            this.currentUserId = currentUser.getId();
            userNotifications = notificationDao.getNotificationsForUser(currentUserId);
        } else {
            userNotifications = new MutableLiveData<>();
        }
    }

    public LiveData<List<NotificationEntity>> getNotifications() {
        return userNotifications;
    }

    public LiveData<Integer> getUnreadCount() { return unreadCount; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void fetchNotifications() {
        isLoading.setValue(true);
        apiService.getNotifications(1, 50, false).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        List<Notification> apiNotifications = response.body().getData().getNotifications();
                        List<NotificationEntity> notificationEntities = new ArrayList<>();
                        for (Notification n : apiNotifications) {
                            notificationEntities.add(new NotificationEntity(
                                    n.getId(),
                                    currentUserId,
                                    n.getTitle(),
                                    n.getMessage(),
                                    n.getCreatedAt().getTime(),
                                    n.isRead(),
                                    n.getLink(),
                                    n.getCopyString()
                            ));
                        }
                        notificationDao.insertAll(notificationEntities);
                    });
                } else {
                    errorMessage.setValue("Gagal memuat notifikasi");
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }

    public void fetchUnreadCount() {
        apiService.getUnreadNotificationCount().enqueue(new Callback<UnreadCountResponse>() {
            @Override
            public void onResponse(@NonNull Call<UnreadCountResponse> call, @NonNull Response<UnreadCountResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    unreadCount.setValue(response.body().getData().getCount());
                }
            }
            @Override
            public void onFailure(@NonNull Call<UnreadCountResponse> call, @NonNull Throwable t) { }
        });
    }

    public void markNotificationAsRead(String notificationId, int position) {
        if (currentUserId == null) return;
        apiService.markNotificationAsRead(notificationId, new UpdateNotificationRequest(true)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        notificationDao.markAsRead(notificationId, currentUserId);
                    });
                    fetchUnreadCount();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) { }
        });
    }

    public void markAllNotificationsAsRead() {
        if (currentUserId == null) return;
        apiService.markAllNotificationsAsRead().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        notificationDao.markAllAsReadForUser(currentUserId);
                    });
                    fetchUnreadCount();
                } else {
                    errorMessage.setValue("Gagal menandai semua notifikasi");
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }
}
package com.example.hamster.dashboard;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.hamster.data.model.Notification;
import com.example.hamster.data.model.request.UpdateNotificationRequest;
import com.example.hamster.data.model.response.NotificationResponse;
import com.example.hamster.data.model.response.UnreadCountResponse;
import com.example.hamster.data.network.ApiClient;
import com.example.hamster.data.network.ApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private final MutableLiveData<List<Notification>> notifications = new MutableLiveData<>();
    private final MutableLiveData<Integer> unreadCount = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient(application).create(ApiService.class);
    }

    public LiveData<List<Notification>> getNotifications() { return notifications; }
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
                    notifications.setValue(response.body().getData().getNotifications());
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
        apiService.markNotificationAsRead(notificationId, new UpdateNotificationRequest(true)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    List<Notification> currentList = notifications.getValue();
                    if (currentList != null && !currentList.get(position).isRead()) {
                        currentList.get(position).setRead(true);
                        notifications.postValue(currentList);
                        fetchUnreadCount();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) { }
        });
    }

    public void markAllNotificationsAsRead() {
        apiService.markAllNotificationsAsRead().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchNotifications();
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
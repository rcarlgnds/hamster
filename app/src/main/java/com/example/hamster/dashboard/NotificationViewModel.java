package com.example.hamster.dashboard;

import android.app.Application; // Import Application
import androidx.annotation.NonNull; // Import NonNull
import androidx.lifecycle.AndroidViewModel; // Ganti ke AndroidViewModel
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.hamster.data.model.Notification;
import com.example.hamster.data.model.response.NotificationResponse;
import com.example.hamster.data.network.ApiClient;
import com.example.hamster.data.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Notification>> notificationsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final ApiService apiService;

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient(application).create(ApiService.class);
    }

    public LiveData<List<Notification>> getNotificationsLiveData() {
        return notificationsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchNotifications() {
        isLoading.setValue(true);
        apiService.getNotifications().enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<NotificationResponse> call, @NonNull Response<NotificationResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    notificationsLiveData.setValue(response.body().getData());
                } else {
                    errorMessage.setValue("Failed to load notifications. Please try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network Error: " + t.getMessage());
            }
        });
    }
}
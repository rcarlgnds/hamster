package com.example.hamster.activation;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.hamster.data.model.AssetRejected;
import com.example.hamster.data.model.RejectedDataPayload;
import com.example.hamster.data.model.response.AssetRejectedResponse;
import com.example.hamster.data.model.response.RejectionResponse;
import com.example.hamster.data.network.ApiClient;
import com.example.hamster.data.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RejectedAssetViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private final MutableLiveData<List<AssetRejected>> rejectedList = new MutableLiveData<>();
//    private final MutableLiveData<AssetRejectedDetailData> rejectedDetails = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
//    private final MutableLiveData<Boolean> rejectionResult = new MutableLiveData<>();

    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoadingMore = false;
    private static final int PAGE_SIZE = 10;

    private final MutableLiveData<Boolean> actionResult = new MutableLiveData<>();

    public LiveData<Boolean> getActionResult() {
        return actionResult;
    }

    public void continueRejection(int id) {
        isLoading.setValue(true);
        apiService.continueRejection(id).enqueue(new Callback<RejectionResponse>() {
            @Override
            public void onResponse(Call<RejectionResponse> call, Response<RejectionResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    actionResult.setValue(true);
                } else {
                    handleApiError(response, "Failed to continue process.");
                    actionResult.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<RejectionResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network request failed: " + t.getMessage());
                actionResult.setValue(false);
            }
        });
    }
    public void confirmLocation(int id) {
        isLoading.setValue(true);
        apiService.confirmLocation(id).enqueue(new Callback<RejectionResponse>() {
            @Override
            public void onResponse(Call<RejectionResponse> call, Response<RejectionResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    actionResult.setValue(true);
                } else {
                    handleApiError(response, "Failed to confirm location.");
                    actionResult.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<RejectionResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network request failed: " + t.getMessage());
                actionResult.setValue(false);
            }
        });
    }

    public RejectedAssetViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient(application).create(ApiService.class);
    }

    public MutableLiveData<List<AssetRejected>> getRejectedList() {
        return rejectedList;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchRejectedAssets() {
        if (currentPage > totalPages) return;

        isLoading.setValue(true);
        isLoadingMore = true;
        apiService.getRejectedAssets(currentPage, PAGE_SIZE, "", "").enqueue(new Callback<AssetRejectedResponse>() {
            @Override
            public void onResponse(Call<AssetRejectedResponse> call, Response<AssetRejectedResponse> response) {
                isLoading.setValue(false);
                isLoadingMore = false;
                if(response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    totalPages = response.body().getData().getPagination().getTotalPages();
                    List<AssetRejected> newItems = response.body().getData().getData();
                    List<AssetRejected> currentList = rejectedList.getValue();
                    if (currentList == null) {
                        currentList = new ArrayList<>();
                    }
                    if (currentPage == 1) {
                        currentList.clear();
                    }
                    currentList.addAll(newItems);
                    rejectedList.setValue(currentList);
                    currentPage++;
                } else {
                    handleApiError(response, "Failed to load rejected assets.");
                }
            }

            @Override
            public void onFailure(Call<AssetRejectedResponse> call, Throwable t) {
                isLoading.setValue(false);
                isLoadingMore = false;
                errorMessage.setValue("Network request failed: " + t.getMessage());
            }
        });
    }

    public void loadMoreItems() {
        if (!isLoadingMore) {
            fetchRejectedAssets();
        }
    }

    public void refresh() {
        currentPage = 1;
        totalPages = 1;
        rejectedList.setValue(new ArrayList<>());
        fetchRejectedAssets();
    }

    private <T> void handleApiError(Response<T> response, String defaultMessage) {
        String errorBody = "";
        try {
            if (response.errorBody() != null) {
                errorBody = response.errorBody().string();
            }
        } catch (Exception e) {
            // Ignore
        }
        errorMessage.setValue(defaultMessage + " Code: " + response.code() + ". " + errorBody);
    }
}

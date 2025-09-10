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
import com.example.hamster.data.network.ApiClient;
import com.example.hamster.data.network.ApiService;

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
        isLoading.setValue(true);
        apiService.getRejectedAssets(1, 100, "", "").enqueue(new Callback<AssetRejectedResponse>() {
            @Override
            public void onResponse(Call<AssetRejectedResponse> call, Response<AssetRejectedResponse> response) {
                isLoading.setValue(false);
                if(response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    rejectedList.setValue(response.body().getData().getData());
                } else {
                    handleApiError(response, "Failed to load rejected assets.");
                }
            }

            @Override
            public void onFailure(Call<AssetRejectedResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network request failed: " + t.getMessage());
            }
        });
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

package com.example.hamster.activation;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.AssetDetailResponse;
import com.example.hamster.data.model.request.ApproveActivationRequest;
import com.example.hamster.data.model.response.PendingApprovalsResponse;
import com.example.hamster.data.network.ApiClient;
import com.example.hamster.data.network.ApiService;
import com.example.hamster.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivationApprovalViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private final SessionManager sessionManager;

    private final MutableLiveData<List<Asset>> approvalList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> approvalResult = new MutableLiveData<>();


    public ActivationApprovalViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient(application).create(ApiService.class);
        sessionManager = new SessionManager(application);
    }

    public LiveData<List<Asset>> getApprovalList() {
        return approvalList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getApprovalResult() { return approvalResult; }


    public void fetchPendingApprovals() {
        isLoading.setValue(true);
        apiService.getPendingApprovals(1, 100).enqueue(new Callback<PendingApprovalsResponse>() {
            @Override
            public void onResponse(@NonNull Call<PendingApprovalsResponse> call, @NonNull Response<PendingApprovalsResponse> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<String> assetIds = response.body().getAssetIds();

                    if (assetIds != null && !assetIds.isEmpty()) {
                        fetchAssetDetails(assetIds);
                    } else {
                        approvalList.setValue(new ArrayList<>());
                    }
                } else {
                    String errorBody = "No error body";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                    }
                    String detailedErrorMessage = "Failed to load approvals. Code: " + response.code() + ", Body: " + errorBody;
                    errorMessage.setValue(detailedErrorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PendingApprovalsResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network request failed: " + t.getMessage());
            }
        });
    }

    private void fetchAssetDetails(List<String> assetIds) {
        List<Asset> detailedAssets = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(assetIds.size());

        for (String id : assetIds) {
            apiService.getAssetById(id).enqueue(new Callback<AssetDetailResponse>() {
                @Override
                public void onResponse(@NonNull Call<AssetDetailResponse> call, @NonNull Response<AssetDetailResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        detailedAssets.add(response.body().getData());
                    }
                    if (counter.decrementAndGet() == 0) {
                        approvalList.setValue(detailedAssets);
                        isLoading.setValue(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AssetDetailResponse> call, @NonNull Throwable t) {
                    if (counter.decrementAndGet() == 0) {
                        approvalList.setValue(detailedAssets);
                        isLoading.setValue(false);
                    }
                }
            });
        }
    }

    public void submitApproval(String assetId, boolean isApproved, String remarks) {
        isLoading.setValue(true);
        String approverId = sessionManager.getUser().getId();
        ApproveActivationRequest request = new ApproveActivationRequest(assetId, approverId, isApproved, remarks);

        apiService.approveAssetActivation(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    approvalResult.setValue(true);
                } else {
                    approvalResult.setValue(false);
                    errorMessage.setValue("Failed to process. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                approvalResult.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }
}
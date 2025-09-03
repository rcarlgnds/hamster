package com.example.hamster.activation;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.hamster.data.model.ActivationDetailData;
import com.example.hamster.data.model.ApprovalItem;
import com.example.hamster.data.model.request.ApproveActivationRequest;
import com.example.hamster.data.model.response.ActivationDetailResponse;
import com.example.hamster.data.model.response.PendingApprovalsResponse;
import com.example.hamster.data.network.ApiClient;
import com.example.hamster.data.network.ApiService;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivationApprovalViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private final MutableLiveData<List<ApprovalItem>> approvalList = new MutableLiveData<>();
    private final MutableLiveData<ActivationDetailData> activationDetails = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> approvalResult = new MutableLiveData<>();

    public ActivationApprovalViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient(application).create(ApiService.class);
    }

    public LiveData<List<ApprovalItem>> getApprovalList() { return approvalList; }
    public LiveData<ActivationDetailData> getActivationDetails() { return activationDetails; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getApprovalResult() { return approvalResult; }

    public void fetchPendingApprovals() {
        isLoading.setValue(true);
        apiService.getPendingApprovals(1, 100).enqueue(new Callback<PendingApprovalsResponse>() {
            @Override
            public void onResponse(@NonNull Call<PendingApprovalsResponse> call, @NonNull Response<PendingApprovalsResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    approvalList.setValue(response.body().getData().getData());
                } else {
                    handleApiError(response, "Failed to load approvals.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PendingApprovalsResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network request failed: " + t.getMessage());
            }
        });
    }

    public void fetchActivationDetails(String transactionId) {
        isLoading.setValue(true);
        apiService.getAssetActivationById(transactionId).enqueue(new Callback<ActivationDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<ActivationDetailResponse> call, @NonNull Response<ActivationDetailResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    activationDetails.setValue(response.body().getData());
                } else {
                    handleApiError(response, "Failed to load activation details.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ActivationDetailResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network request failed: " + t.getMessage());
            }
        });
    }

    public void submitApproval(String transactionId, boolean isApproved, String remarks) {
        isLoading.setValue(true);
        String action = isApproved ? "APPROVED" : "REJECTED";
        ApproveActivationRequest request = new ApproveActivationRequest(transactionId, action, remarks);

        apiService.approveAssetActivation(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    approvalResult.setValue(true);
                } else {
                    approvalResult.setValue(false);
                    handleApiError(response, "Failed to process approval.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                approvalResult.setValue(false);
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
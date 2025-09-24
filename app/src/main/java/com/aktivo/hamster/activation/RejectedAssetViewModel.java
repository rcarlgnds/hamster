package com.aktivo.hamster.activation;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aktivo.hamster.data.model.AssetRejected;
import com.aktivo.hamster.data.model.response.AssetRejectedResponse;
import com.aktivo.hamster.data.model.response.RejectionResponse;
import com.aktivo.hamster.data.network.ApiClient;
import com.aktivo.hamster.data.network.ApiService;
import com.aktivo.hamster.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RejectedAssetViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private final MutableLiveData<List<AssetRejected>> rejectedList = new MutableLiveData<>();
    private List<AssetRejected> originalRejectedList = new ArrayList<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoadingMore = false;
    private static final int PAGE_SIZE = 10;
    private String currentSearchQuery = "";
    private String currentStatusFilter = "";

    private final MutableLiveData<Boolean> actionResult = new MutableLiveData<>();

    public LiveData<Boolean> getActionResult() {
        return actionResult;
    }

    public void continueRejection(String id, Uri photoUri) {
        isLoading.setValue(true);

        File photoFile = FileUtils.getFile(getApplication(), photoUri);
        if (photoFile == null) {
            errorMessage.setValue("File foto tidak valid.");
            actionResult.setValue(false);
            isLoading.setValue(false);
            return;
        }

        RequestBody transactionIdBody = RequestBody.create(MediaType.parse("text/plain"), id);
        RequestBody photoBody = RequestBody.create(MediaType.parse(getApplication().getContentResolver().getType(photoUri)), photoFile);
        MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo", photoFile.getName(), photoBody);

        apiService.continueRejection(id, transactionIdBody, photoPart).enqueue(new Callback<RejectionResponse>() {
            @Override
            public void onResponse(Call<RejectionResponse> call, Response<RejectionResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    actionResult.setValue(true);
                } else {
                    handleApiError(response, "Gagal melanjutkan proses.");
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

    public void confirmLocation(String id) {
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

                    if (currentPage == 1) {
                        originalRejectedList.clear();
                    }
                    originalRejectedList.addAll(newItems);
                    filterAssets();
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

    public void search(String query) {
        currentSearchQuery = query;
        filterAssets();
    }

    private void filterAssets() {
        if (originalRejectedList == null) return;

        List<AssetRejected> filteredList = new ArrayList<>(originalRejectedList);

        if (currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
            String lowerCaseQuery = currentSearchQuery.toLowerCase();
            filteredList = filteredList.stream()
                    .filter(asset -> (asset.getAssetName() != null && asset.getAssetName().toLowerCase().contains(lowerCaseQuery)) ||
                            (asset.getAssetCode() != null && asset.getAssetCode().toLowerCase().contains(lowerCaseQuery)) ||
                            (asset.getStatus() != null && asset.getStatus().toLowerCase().contains(lowerCaseQuery)))
                    .collect(Collectors.toList());
        }

        rejectedList.setValue(filteredList);
    }

    public void loadMoreItems() {
        if (!isLoadingMore) {
            fetchRejectedAssets();
        }
    }

    public void refresh() {
        currentPage = 1;
        totalPages = 1;
        originalRejectedList.clear();
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
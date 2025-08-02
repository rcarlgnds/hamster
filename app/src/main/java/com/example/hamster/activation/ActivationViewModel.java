package com.example.hamster.activation;

import android.app.Application;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.AssetActivationStatus;
import com.example.hamster.data.network.ApiClient;
import com.example.hamster.data.network.ApiService;
import com.example.hamster.utils.FileUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivationViewModel extends AndroidViewModel {

    public enum ActivationProcessState { IDLE, LOADING, SUCCESS, ERROR }
    public enum StatusCheckState { IDLE, LOADING, FOUND, NOT_FOUND, ERROR }

    private final ApiService apiService;
    private final MutableLiveData<ActivationProcessState> activationState = new MutableLiveData<>(ActivationProcessState.IDLE);
    private final MutableLiveData<StatusCheckState> statusCheckState = new MutableLiveData<>(StatusCheckState.IDLE);
    private final MutableLiveData<AssetActivationStatus> assetStatusData = new MutableLiveData<>();
    private String currentAssetId = null;

    public ActivationViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient(application).create(ApiService.class);
    }

    public LiveData<ActivationProcessState> getActivationState() { return activationState; }
    public LiveData<StatusCheckState> getStatusCheckState() { return statusCheckState; }
    public LiveData<AssetActivationStatus> getAssetStatusData() { return assetStatusData; }


    public void checkAssetStatus(String assetCode) {
        statusCheckState.setValue(StatusCheckState.LOADING);

        apiService.getAssetByCode(assetCode).enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(@NonNull Call<Asset> call, @NonNull Response<Asset> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentAssetId = response.body().getId();
                    fetchActivationStatusById(currentAssetId);
                } else {
                    statusCheckState.setValue(StatusCheckState.NOT_FOUND);
                    assetStatusData.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Asset> call, @NonNull Throwable t) {
                statusCheckState.setValue(StatusCheckState.ERROR);
                assetStatusData.setValue(null);
            }
        });
    }

    private void fetchActivationStatusById(String assetId) {
        apiService.getAssetActivationStatus(assetId).enqueue(new Callback<AssetActivationStatus>() {
            @Override
            public void onResponse(@NonNull Call<AssetActivationStatus> call, @NonNull Response<AssetActivationStatus> response) {
                if (response.isSuccessful() && response.body() != null) {
                    statusCheckState.setValue(StatusCheckState.FOUND);
                    assetStatusData.setValue(response.body());
                } else if (response.code() == 404) {
                    statusCheckState.setValue(StatusCheckState.NOT_FOUND);
                    assetStatusData.setValue(null);
                } else {
                    statusCheckState.setValue(StatusCheckState.ERROR);
                    assetStatusData.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AssetActivationStatus> call, @NonNull Throwable t) {
                statusCheckState.setValue(StatusCheckState.ERROR);
                assetStatusData.setValue(null);
            }
        });
    }

    public void startActivationProcess(String assetCode, Uri photoUri) {
        activationState.setValue(ActivationProcessState.LOADING);

        File photoFile = FileUtils.getFile(getApplication(), photoUri);
        if (photoFile == null) {
            activationState.setValue(ActivationProcessState.ERROR);
            return;
        }

        RequestBody assetCodeBody = RequestBody.create(MediaType.parse("multipart/form-data"), assetCode);
        RequestBody photoBody = RequestBody.create(MediaType.parse(getApplication().getContentResolver().getType(photoUri)), photoFile);
        MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo", photoFile.getName(), photoBody);

        apiService.startAssetActivation(assetCodeBody, photoPart).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    activationState.setValue(ActivationProcessState.SUCCESS);
                } else {
                    activationState.setValue(ActivationProcessState.ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                activationState.setValue(ActivationProcessState.ERROR);
            }
        });
    }
}
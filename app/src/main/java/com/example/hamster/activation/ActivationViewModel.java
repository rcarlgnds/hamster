package com.example.hamster.activation;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.AssetActivationStatus;
import com.example.hamster.data.model.response.AssetActivationStatusResponse;
import com.example.hamster.data.model.response.AssetByCodeResponse;
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

    private static final String TAG = "ActivationVM";

    public enum ActivationProcessState {IDLE, LOADING, SUCCESS, ERROR}

    public enum StatusCheckState {IDLE, LOADING, FOUND, NOT_FOUND, ERROR}

    private final ApiService apiService;
    private final MutableLiveData<ActivationProcessState> activationState = new MutableLiveData<>(ActivationProcessState.IDLE);
    private final MutableLiveData<StatusCheckState> statusCheckState = new MutableLiveData<>(StatusCheckState.IDLE);
    private final MutableLiveData<AssetActivationStatus> assetStatusData = new MutableLiveData<>();
    private String currentAssetId = null;

    public ActivationViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient(application).create(ApiService.class);
    }

    public LiveData<StatusCheckState> getStatusCheckState() {
        return statusCheckState;
    }

    public LiveData<AssetActivationStatus> getAssetStatusData() {
        return assetStatusData;
    }

    public LiveData<ActivationProcessState> getActivationState() {
        return activationState;
    }

    public void checkAssetStatus(String assetCode) {
        statusCheckState.setValue(StatusCheckState.LOADING);
        apiService.getAssetByCode(assetCode).enqueue(new Callback<AssetByCodeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AssetByCodeResponse> call, @NonNull Response<AssetByCodeResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Asset asset = response.body().getData();
                    currentAssetId = asset.getId();
                    fetchActivationStatusById(currentAssetId);
                } else {
                    statusCheckState.setValue(StatusCheckState.NOT_FOUND);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AssetByCodeResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure getAssetByCode", t);
                statusCheckState.setValue(StatusCheckState.ERROR);
            }
        });
    }

    private void fetchActivationStatusById(String assetId) {
        apiService.getAssetActivationStatus(assetId).enqueue(new Callback<AssetActivationStatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<AssetActivationStatusResponse> call, @NonNull Response<AssetActivationStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    assetStatusData.setValue(response.body().getData());
                    statusCheckState.setValue(StatusCheckState.FOUND);
                } else if (response.code() == 404) {
                    statusCheckState.setValue(StatusCheckState.NOT_FOUND);
                } else {
                    statusCheckState.setValue(StatusCheckState.ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AssetActivationStatusResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure getAssetActivationStatus", t);
                statusCheckState.setValue(StatusCheckState.ERROR);
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
                activationState.setValue(response.isSuccessful() ? ActivationProcessState.SUCCESS : ActivationProcessState.ERROR);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                activationState.setValue(ActivationProcessState.ERROR);
            }
        });
    }
}
package com.example.hamster.activation;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.hamster.data.model.AssetActivationStatus;
import com.example.hamster.data.model.request.StartActivationRequest;
import com.example.hamster.data.network.ApiClient;
import com.example.hamster.data.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivationViewModel extends AndroidViewModel {

    public enum ActivationProcessState {
        IDLE,       // Keadaan awal
        LOADING,    // Sedang mengirim request
        SUCCESS,    // Berhasil memulai aktivasi
        ERROR       // Terjadi kesalahan saat memulai
    }

    public enum StatusCheckState {
        IDLE,           // Keadaan awal
        LOADING,        // Sedang mengecek status
        FOUND,          // Aset ditemukan (sudah ada proses sebelumnya)
        NOT_FOUND,      // Aset tidak ditemukan (bisa diaktivasi)
        ERROR           // Gagal mengecek status
    }

    private final ApiService apiService;
    private final MutableLiveData<ActivationProcessState> activationState = new MutableLiveData<>(ActivationProcessState.IDLE);
    private final MutableLiveData<StatusCheckState> statusCheckState = new MutableLiveData<>(StatusCheckState.IDLE);
    private final MutableLiveData<AssetActivationStatus> assetStatusData = new MutableLiveData<>();


    public ActivationViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient(application).create(ApiService.class);
    }

    public LiveData<ActivationProcessState> getActivationState() {
        return activationState;
    }

    public LiveData<StatusCheckState> getStatusCheckState() {
        return statusCheckState;
    }

    public LiveData<AssetActivationStatus> getAssetStatusData() {
        return assetStatusData;
    }


    /**
     * Memeriksa status aktivasi aset yang ada berdasarkan kodenya.
     * @param assetCode Kode aset yang akan diperiksa.
     */
    public void checkAssetStatus(String assetCode) {
        statusCheckState.setValue(StatusCheckState.LOADING);

        apiService.getAssetActivationStatus(assetCode).enqueue(new Callback<AssetActivationStatus>() {
            @Override
            public void onResponse(@NonNull Call<AssetActivationStatus> call, @NonNull Response<AssetActivationStatus> response) {
                if (response.isSuccessful()) {
                    // HTTP 200 OK: Aset ditemukan, sudah ada proses aktivasi sebelumnya.
                    assetStatusData.setValue(response.body());
                    statusCheckState.setValue(StatusCheckState.FOUND);
                } else if (response.code() == 404) {
                    // HTTP 404 Not Found: Aset aman untuk diaktivasi.
                    assetStatusData.setValue(null);
                    statusCheckState.setValue(StatusCheckState.NOT_FOUND);
                } else {
                    // Error lain dari server (e.g., 500)
                    assetStatusData.setValue(null);
                    statusCheckState.setValue(StatusCheckState.ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AssetActivationStatus> call, @NonNull Throwable t) {
                // Gagal koneksi ke jaringan
                assetStatusData.setValue(null);
                statusCheckState.setValue(StatusCheckState.ERROR);
            }
        });
    }

    /**
     * Memulai proses aktivasi aset baru.
     * @param assetCode Kode aset yang akan diaktivasi.
     */
    public void startActivationProcess(String assetCode) {
        activationState.setValue(ActivationProcessState.LOADING);

        StartActivationRequest request = new StartActivationRequest(assetCode);

        apiService.startAssetActivation(request).enqueue(new Callback<Void>() {
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
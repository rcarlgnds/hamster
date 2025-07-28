// File: app/src/main/java/com/example/hamster/activation/ActivationViewModel.java
package com.example.hamster.activation;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.hamster.data.model.request.StartActivationRequest;
import com.example.hamster.data.network.ApiClient;
import com.example.hamster.data.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivationViewModel extends AndroidViewModel {

    public enum ActivationState {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR
    }

    private final MutableLiveData<ActivationState> activationState = new MutableLiveData<>(ActivationState.IDLE);
    private final ApiService apiService;

    public ActivationViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient(application).create(ApiService.class);
    }

    public LiveData<ActivationState> getActivationState() {
        return activationState;
    }

    public void startActivationProcess(String assetCode) {
        activationState.setValue(ActivationState.LOADING);

        // TODO: Nanti kita perlu mengirim foto dan notes juga, tapi untuk sekarang kita ikuti API `start`
        StartActivationRequest request = new StartActivationRequest(assetCode);

        apiService.startAssetActivation(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    activationState.setValue(ActivationState.SUCCESS);
                } else {
                    activationState.setValue(ActivationState.ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                activationState.setValue(ActivationState.ERROR);
            }
        });
    }
}
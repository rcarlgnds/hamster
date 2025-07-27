package com.example.hamster.inventory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.AssetsResponse;
import com.example.hamster.data.network.ApiClient;
import com.example.hamster.data.network.ApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Asset>> assetList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isError = new MutableLiveData<>();

    public LiveData<List<Asset>> getAssetList() { return assetList; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsError() { return isError; }

    public InventoryViewModel(@NonNull Application application) {
        super(application);
    }
    public void fetchAssets() {
        isLoading.setValue(true);
        isError.setValue(false);

        ApiService apiService = ApiClient.getClient(getApplication()).create(ApiService.class);
        Call<AssetsResponse> call = apiService.getAssets(1, 10);

        call.enqueue(new Callback<AssetsResponse>() {
            @Override
            public void onResponse(Call<AssetsResponse> call, Response<AssetsResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    assetList.setValue(response.body().getData().getData());
                } else {
                    isError.setValue(true);
                }
            }
            @Override
            public void onFailure(Call<AssetsResponse> call, Throwable t) {
                isLoading.setValue(false);
                isError.setValue(true);
            }
        });
    }
}
// di package com.example.hamster.inventory
package com.example.hamster.inventory;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.hamster.data.model.Asset;
import com.example.hamster.data.network.ApiClient;
import com.example.hamster.data.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetDetailViewModel extends AndroidViewModel {
    private final MutableLiveData<Asset> assetData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isError = new MutableLiveData<>();

    public AssetDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Asset> getAssetData() {
        return assetData;
    }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void fetchAssetById(String assetId) {
        isLoading.setValue(true);
        ApiService apiService = ApiClient.getClient(getApplication()).create(ApiService.class);
        Call<Asset> call = apiService.getAssetById(assetId);

        call.enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    assetData.setValue(response.body());
                } else {
                    isError.setValue(true);
                }
            }
            @Override
            public void onFailure(Call<Asset> call, Throwable t) {
                isLoading.setValue(false);
                isError.setValue(true);
            }
        });
    }
}
package com.example.hamster.inventory;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.OptionItem;
import com.example.hamster.data.network.ApiClient;
import com.example.hamster.data.network.ApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetDetailViewModel extends AndroidViewModel {
    private final MutableLiveData<Asset> assetData = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> categoryOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> subCategoryOptions = new MutableLiveData<>();

    public AssetDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Asset> getAssetData() { return assetData; }
    public LiveData<List<OptionItem>> getCategoryOptions() { return categoryOptions; }
    public LiveData<List<OptionItem>> getSubCategoryOptions() { return subCategoryOptions; }

    public void fetchAssetById(String assetId) {
        ApiService apiService = ApiClient.getClient(getApplication()).create(ApiService.class);
        apiService.getAssetById(assetId).enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                if (response.isSuccessful()) {
                    assetData.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<Asset> call, Throwable t) { /* Handle error */ }
        });
    }

    public void fetchCategoryOptions() {
        ApiService apiService = ApiClient.getClient(getApplication()).create(ApiService.class);
        apiService.getAssetCategoryOptions().enqueue(new Callback<List<OptionItem>>() {
            @Override
            public void onResponse(Call<List<OptionItem>> call, Response<List<OptionItem>> response) {
                if (response.isSuccessful()) {
                    categoryOptions.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<OptionItem>> call, Throwable t) { /* Handle error */ }
        });
    }

    public void fetchSubCategoryOptions(String categoryId) {
        ApiService apiService = ApiClient.getClient(getApplication()).create(ApiService.class);
        apiService.getAssetSubCategoryOptions(categoryId).enqueue(new Callback<List<OptionItem>>() {
            @Override
            public void onResponse(Call<List<OptionItem>> call, Response<List<OptionItem>> response) {
                if (response.isSuccessful()) {
                    subCategoryOptions.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<OptionItem>> call, Throwable t) { /* Handle error */ }
        });
    }
}
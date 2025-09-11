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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Asset>> assetList = new MutableLiveData<>();
    private final MutableLiveData<List<Asset>> filteredAssetList = new MutableLiveData<>();
    private List<Asset> originalAssetList = new ArrayList<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isError = new MutableLiveData<>();
    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoadingMore = false;
    private static final int PAGE_SIZE = 10;

    public LiveData<List<Asset>> getAssetList() { return filteredAssetList; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsError() { return isError; }

    public InventoryViewModel(@NonNull Application application) {
        super(application);
    }

    public void fetchAssets() {
        if (currentPage > totalPages) {
            return;
        }

        isLoading.setValue(true);
        isLoadingMore = true;
        isError.setValue(false);

        ApiService apiService = ApiClient.getClient(getApplication()).create(ApiService.class);
        Call<AssetsResponse> call = apiService.getAssets(currentPage, PAGE_SIZE);

        call.enqueue(new Callback<AssetsResponse>() {
            @Override
            public void onResponse(Call<AssetsResponse> call, Response<AssetsResponse> response) {
                isLoading.setValue(false);
                isLoadingMore = false;
                if (response.isSuccessful() && response.body() != null) {
                    totalPages = response.body().getData().getPagination().getTotalPages();
                    List<Asset> newAssets = response.body().getData().getData();

                    if (currentPage == 1) {
                        originalAssetList.clear();
                    }
                    originalAssetList.addAll(newAssets);
                    filteredAssetList.setValue(new ArrayList<>(originalAssetList));
                    currentPage++;

                } else {
                    isError.setValue(true);
                }
            }
            @Override
            public void onFailure(Call<AssetsResponse> call, Throwable t) {
                isLoading.setValue(false);
                isLoadingMore = false;
                isError.setValue(true);
            }
        });
    }

    public void loadMoreItems() {
        if (!isLoadingMore) {
            fetchAssets();
        }
    }

    public void refreshAssets() {
        currentPage = 1;
        totalPages = 1;
        originalAssetList.clear();
        filteredAssetList.setValue(new ArrayList<>());
        fetchAssets();
    }


    public void searchAssets(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredAssetList.setValue(originalAssetList);
            return;
        }

        String lowerCaseQuery = query.toLowerCase();
        List<Asset> filtered = originalAssetList.stream()
                .filter(asset -> (asset.getName() != null && asset.getName().toLowerCase().contains(lowerCaseQuery)) ||
                        (asset.getCode() != null && asset.getCode().toLowerCase().contains(lowerCaseQuery)))
                .collect(Collectors.toList());

        filteredAssetList.setValue(filtered);
    }

    public void filterByStatus(String status) {
        if (status == null || status.equalsIgnoreCase("All")) {
            filteredAssetList.setValue(originalAssetList);
            return;
        }

        String lowerCaseStatus = status.toLowerCase();
        List<Asset> filtered = originalAssetList.stream()
                .filter(asset -> asset.getStatus() != null && asset.getStatus().equalsIgnoreCase(lowerCaseStatus))
                .collect(Collectors.toList());

        filteredAssetList.setValue(filtered);
    }
}
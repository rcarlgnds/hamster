package com.aktivo.hamster.inventory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.aktivo.hamster.data.model.Asset;
import com.aktivo.hamster.data.model.AssetsResponse;
import com.aktivo.hamster.data.model.OptionItem;
import com.aktivo.hamster.data.model.response.OptionsResponse;
import com.aktivo.hamster.data.network.ApiClient;
import com.aktivo.hamster.data.network.ApiService;

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
    private String currentQuery = "";
    private String currentStatus = "All";

    // Properti  untuk Advanced Search
    private final ApiService apiService;
    private final MutableLiveData<List<OptionItem>> hospitalOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> buildingOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> floorOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> roomOptions = new MutableLiveData<>();

    public LiveData<List<Asset>> getAssetList() { return filteredAssetList; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsError() { return isError; }

    // Getter untuk data dropdown
    public LiveData<List<OptionItem>> getHospitalOptions() { return hospitalOptions; }
    public LiveData<List<OptionItem>> getBuildingOptions() { return buildingOptions; }
    public LiveData<List<OptionItem>> getFloorOptions() { return floorOptions; }
    public LiveData<List<OptionItem>> getRoomOptions() { return roomOptions; }

    public InventoryViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient(application).create(ApiService.class);
    }

    public void fetchHospitalOptions() {
        apiService.getHospitalOptions().enqueue(new Callback<OptionsResponse>() {
            @Override
            public void onResponse(Call<OptionsResponse> call, Response<OptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    hospitalOptions.setValue(response.body().getData());
                }
            }
            @Override
            public void onFailure(Call<OptionsResponse> call, Throwable t) {
                hospitalOptions.setValue(new ArrayList<>());
            }
        });
    }

    public void fetchBuildingOptions(String hospitalId) {
        if (hospitalId == null) {
            buildingOptions.setValue(new ArrayList<>());
            return;
        }
        apiService.getBuildingOptionsForHospital(hospitalId).enqueue(new Callback<OptionsResponse>() {
            @Override
            public void onResponse(Call<OptionsResponse> call, Response<OptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    buildingOptions.setValue(response.body().getData());
                }
            }
            @Override
            public void onFailure(Call<OptionsResponse> call, Throwable t) {
                buildingOptions.setValue(new ArrayList<>());
            }
        });
    }

    public void fetchFloorOptions(String buildingId) {
        if (buildingId == null) {
            floorOptions.setValue(new ArrayList<>());
            return;
        }
        apiService.getFloorOptionsForBuilding(buildingId).enqueue(new Callback<OptionsResponse>() {
            @Override
            public void onResponse(Call<OptionsResponse> call, Response<OptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    floorOptions.setValue(response.body().getData());
                }
            }
            @Override
            public void onFailure(Call<OptionsResponse> call, Throwable t) {
                floorOptions.setValue(new ArrayList<>());
            }
        });
    }

    public void fetchRoomOptions(String floorId) {
        if (floorId == null) {
            roomOptions.setValue(new ArrayList<>());
            return;
        }
        apiService.getRoomOptionsByFloor(floorId).enqueue(new Callback<OptionsResponse>() {
            @Override
            public void onResponse(Call<OptionsResponse> call, Response<OptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    roomOptions.setValue(response.body().getData());
                }
            }
            @Override
            public void onFailure(Call<OptionsResponse> call, Throwable t) {
                roomOptions.setValue(new ArrayList<>());
            }
        });
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

    public void advancedSearch(String name, String hospitalId, String buildingId, String floorId, String roomId) {
        List<Asset> results = originalAssetList.stream()
                .filter(asset -> {
                    boolean nameMatch = (name == null || name.isEmpty()) ||
                            (asset.getName() != null && asset.getName().toLowerCase().contains(name.toLowerCase()));

                    boolean locationMatch;
                    if (hospitalId == null || hospitalId.isEmpty()) {
                        locationMatch = true;
                    } else {
                        locationMatch = asset.getRoom() != null &&
                                asset.getRoom().getFloor() != null &&
                                asset.getRoom().getFloor().getBuilding() != null &&
                                asset.getRoom().getFloor().getBuilding().getHospitalId() != null &&
                                asset.getRoom().getFloor().getBuilding().getHospitalId().equals(hospitalId) &&
                                (buildingId == null || buildingId.isEmpty() || asset.getRoom().getFloor().getBuildingId().equals(buildingId)) &&
                                (floorId == null || floorId.isEmpty() || asset.getRoom().getFloorId().equals(floorId)) &&
                                (roomId == null || roomId.isEmpty() || asset.getRoomId().equals(roomId));
                    }

                    return nameMatch && locationMatch;
                })
                .collect(Collectors.toList());

        filteredAssetList.setValue(results);
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
        currentQuery = query;
        filterAssets();
    }

    public void filterByStatus(String status) {
        currentStatus = status;
        filterAssets();
    }

    private void filterAssets() {
        if (originalAssetList == null) return;

        List<Asset> fullyFilteredList = new ArrayList<>(originalAssetList);

        if (currentStatus != null && !currentStatus.equalsIgnoreCase("All")) {
            String lowerCaseStatus = currentStatus.toLowerCase();
            fullyFilteredList = fullyFilteredList.stream()
                    .filter(asset -> asset.getStatus() != null && asset.getStatus().equalsIgnoreCase(lowerCaseStatus))
                    .collect(Collectors.toList());
        }

        if (currentQuery != null && !currentQuery.trim().isEmpty()) {
            String lowerCaseQuery = currentQuery.toLowerCase();
            fullyFilteredList = fullyFilteredList.stream()
                    .filter(asset -> (asset.getCode() != null && asset.getCode().toLowerCase().contains(lowerCaseQuery)) ||
                            (asset.getName() != null && asset.getName().toLowerCase().contains(lowerCaseQuery)) ||
                            (asset.getRoom() != null && asset.getRoom().getFloor() != null && asset.getRoom().getFloor().getBuilding() != null && asset.getRoom().getFloor().getBuilding().getHospital() != null && asset.getRoom().getFloor().getBuilding().getHospital().getName().toLowerCase().contains(lowerCaseQuery)) ||
                            (asset.getRoom() != null && asset.getRoom().getFloor() != null && asset.getRoom().getFloor().getBuilding() != null && asset.getRoom().getFloor().getBuilding().getName().toLowerCase().contains(lowerCaseQuery)) ||
                            (asset.getRoom() != null && asset.getRoom().getFloor() != null && asset.getRoom().getFloor().getName().toLowerCase().contains(lowerCaseQuery)) ||
                            (asset.getRoom() != null && asset.getRoom().getName().toLowerCase().contains(lowerCaseQuery)) ||
                            (asset.getStatus() != null && asset.getStatus().toLowerCase().contains(lowerCaseQuery)) ||
                            (asset.getOwnership() != null && asset.getOwnership().toLowerCase().contains(lowerCaseQuery)) ||
                            (asset.getCategory() != null && asset.getCategory().getName().toLowerCase().contains(lowerCaseQuery)) ||
                            (asset.getSubcategory() != null && asset.getSubcategory().getName().toLowerCase().contains(lowerCaseQuery)) ||
                            (asset.getBrand() != null && asset.getBrand().getName().toLowerCase().contains(lowerCaseQuery)) ||
                            (asset.getCondition() != null && asset.getCondition().toLowerCase().contains(lowerCaseQuery)))
                    .collect(Collectors.toList());
        }

        filteredAssetList.setValue(fullyFilteredList);
    }
}
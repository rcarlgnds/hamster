package com.example.hamster.inventory;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.AssetDetailResponse;
import com.example.hamster.data.model.OptionItem;
import com.example.hamster.data.model.UpdateAssetRequest;
import com.example.hamster.data.network.ApiClient;
import com.example.hamster.data.network.ApiService;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetDetailViewModel extends AndroidViewModel {
    private final ApiService apiService;

    // --- LiveData untuk Status & Data Utama ---
    private final MutableLiveData<Asset> assetData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSaveSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isError = new MutableLiveData<>();

    // --- LiveData untuk Dropdown Options ---
    // Asset Info Fragment
    private final MutableLiveData<List<OptionItem>> categoryOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> subCategoryOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> brandOptions = new MutableLiveData<>();

    // Asset Location Fragment
    private final MutableLiveData<List<OptionItem>> hospitalOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> buildingOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> floorOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> roomOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> subRoomOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> divisionOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> workingUnitOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> userOptions = new MutableLiveData<>();
    private final MutableLiveData<List<String>> ownershipOptions = new MutableLiveData<>();
    private final MutableLiveData<List<String>> conditionOptions = new MutableLiveData<>();
    private final MutableLiveData<List<String>> unitOptions = new MutableLiveData<>();


    // Asset Maintenance Fragment
    private final MutableLiveData<List<OptionItem>> vendorOptions = new MutableLiveData<>();


    public AssetDetailViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient(application).create(ApiService.class);
    }

    // --- Getters untuk LiveData ---
    public LiveData<Asset> getAssetData() { return assetData; }
    public LiveData<Boolean> getIsSaveSuccess() { return isSaveSuccess; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsError() { return isError; }
    public LiveData<List<OptionItem>> getCategoryOptions() { return categoryOptions; }
    public LiveData<List<OptionItem>> getSubCategoryOptions() { return subCategoryOptions; }
    public LiveData<List<OptionItem>> getBrandOptions() { return brandOptions; }
    public LiveData<List<OptionItem>> getHospitalOptions() { return hospitalOptions; }
    public LiveData<List<OptionItem>> getBuildingOptions() { return buildingOptions; }
    public LiveData<List<OptionItem>> getFloorOptions() { return floorOptions; }
    public LiveData<List<OptionItem>> getRoomOptions() { return roomOptions; }
    public LiveData<List<OptionItem>> getSubRoomOptions() { return subRoomOptions; }
    public LiveData<List<OptionItem>> getDivisionOptions() { return divisionOptions; }
    public LiveData<List<OptionItem>> getWorkingUnitOptions() { return workingUnitOptions; }
    public LiveData<List<OptionItem>> getUserOptions() { return userOptions; }
    public LiveData<List<OptionItem>> getVendorOptions() { return vendorOptions; }
    public LiveData<List<String>> getOwnershipOptions() { return ownershipOptions; }
    public LiveData<List<String>> getConditionOptions() { return conditionOptions; }
    public LiveData<List<String>> getUnitOptions() { return unitOptions; }


    // --- Metode Fetch Data Utama ---
    public void fetchAssetById(String assetId) {
        isLoading.setValue(true);
        apiService.getAssetById(assetId).enqueue(new Callback<AssetDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<AssetDetailResponse> call, @NonNull Response<AssetDetailResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    assetData.setValue(response.body().getData());
                } else {
                    isError.setValue(true);
                }
            }
            @Override
            public void onFailure(@NonNull Call<AssetDetailResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                isError.setValue(true);
            }
        });
    }

    // --- Metode Fetch untuk Dropdown ---

    // -- Asset Info --
    public void fetchCategoryOptions() {
        apiService.getAssetCategoryOptions().enqueue(createCallback(categoryOptions));
    }

    public void fetchSubCategoryOptions(String categoryId) {
        if (categoryId == null || categoryId.isEmpty()) return;
        apiService.getAssetSubCategoryOptions(categoryId).enqueue(createCallback(subCategoryOptions));
    }

    public void fetchBrandOptions() {
        apiService.getBrandOptions().enqueue(createCallback(brandOptions));
    }

    // -- Asset Location --
    public void fetchHospitalOptions() {
        apiService.getHospitalOptions().enqueue(createCallback(hospitalOptions));
    }

    public void fetchBuildingOptions(String hospitalId) {
        if (hospitalId == null || hospitalId.isEmpty()) return;
        apiService.getBuildingOptionsForHospital(hospitalId).enqueue(createCallback(buildingOptions));
    }

    public void fetchFloorOptions(String buildingId) {
        if (buildingId == null || buildingId.isEmpty()) return;
        apiService.getFloorOptionsForBuilding(buildingId).enqueue(createCallback(floorOptions));
    }

    public void fetchRoomOptions(String floorId) {
        if (floorId == null || floorId.isEmpty()) return;
        apiService.getRoomOptionsByFloor(floorId).enqueue(createCallback(roomOptions));
    }

    public void fetchSubRoomOptions(String roomId) {
        if (roomId == null || roomId.isEmpty()) return;
        apiService.getSubRoomOptionsByRoom(roomId).enqueue(createCallback(subRoomOptions));
    }

    public void fetchDivisionOptions() {
        apiService.getDivisionOptions().enqueue(createCallback(divisionOptions));
    }

    public void fetchWorkingUnitOptions() {
        apiService.getWorkingUnitOptions().enqueue(createCallback(workingUnitOptions));
    }

    public void fetchUserOptions() {
        apiService.getUserOptions().enqueue(createCallback(userOptions));
    }

    // -- Asset Maintenance --
    public void fetchVendorOptions() {
        apiService.getVendorOptions().enqueue(createCallback(vendorOptions));
    }

    public void fetchOwnershipOptions() {
        // Contoh implementasi jika API sudah ada:
//         apiService.getOwnershipOptions().enqueue(createCallback(ownershipOptions));

        // Untuk sementara, kita bisa isi dengan data default agar tidak error
        ownershipOptions.setValue(java.util.Arrays.asList("Owned", "KSO", "Rent"));
    }

    public void fetchConditionOptions() {
        // apiService.getConditionOptions().enqueue(createCallback(conditionOptions));
        conditionOptions.setValue(java.util.Arrays.asList("Good", "Slightly Damaged", "Moderately Damaged", "Heavily Damaged"));
    }

    public void fetchUnitOptions() {
        // apiService.getUnitOptions().enqueue(createCallback(unitOptions));
        unitOptions.setValue(java.util.Arrays.asList("pieces", "unit", "set"));
    }

    // --- Metode Save ---
    public void saveAsset(String assetId, UpdateAssetRequest request) {
        isLoading.setValue(true);
        ApiService apiService = ApiClient.getClient(getApplication()).create(ApiService.class);

        // 1. Ubah objek request menjadi string JSON
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);

        // 2. Buat RequestBody dari string JSON
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonRequest);

        // 3. Siapkan daftar file (kosong untuk saat ini)
        // TODO: Tambahkan file-file yang sebenarnya di sini jika ada
        List<MultipartBody.Part> files = new ArrayList<>();

        // 4. Panggil API dengan parameter yang benar
        apiService.updateAsset(assetId, requestBody, files).enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                isLoading.setValue(false);
                isSaveSuccess.setValue(response.isSuccessful());
            }
            @Override
            public void onFailure(Call<Asset> call, Throwable t) {
                isLoading.setValue(false);
                isSaveSuccess.setValue(false);
                isError.setValue(true);
            }
        });
    }

    // --- Generic Callback Helper ---
    private <T> Callback<T> createCallback(final MutableLiveData<T> liveData) {
        return new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    isError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                isError.setValue(true);
            }
        };
    }
}
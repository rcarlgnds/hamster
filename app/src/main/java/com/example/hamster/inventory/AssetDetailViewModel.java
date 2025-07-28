// File: app/src/main/java/com/example/hamster/inventory/AssetDetailViewModel.java
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
import com.example.hamster.data.model.response.OptionsResponse;
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

    // Data utama dan status
    private final MutableLiveData<Asset> assetData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSaveSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isError = new MutableLiveData<>();

    // "Wadah" untuk semua perubahan dari semua fragment
    private final UpdateAssetRequest pendingUpdateRequest = new UpdateAssetRequest();

    // Data untuk dropdown
    private final MutableLiveData<List<OptionItem>> categoryOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> subCategoryOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> brandOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> parentAssetOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> hospitalOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> buildingOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> floorOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> roomOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> subRoomOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> divisionOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> workingUnitOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> userOptions = new MutableLiveData<>();
    private final MutableLiveData<List<OptionItem>> vendorOptions = new MutableLiveData<>();
    private final MutableLiveData<List<String>> ownershipOptions = new MutableLiveData<>();
    private final MutableLiveData<List<String>> conditionOptions = new MutableLiveData<>();
    private final MutableLiveData<List<String>> unitOptions = new MutableLiveData<>();

    public AssetDetailViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getClient(application).create(ApiService.class);
    }

    // --- Getters ---
    public LiveData<Asset> getAssetData() { return assetData; }
    public LiveData<Boolean> getIsSaveSuccess() { return isSaveSuccess; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getIsError() { return isError; }
    public LiveData<List<OptionItem>> getCategoryOptions() { return categoryOptions; }
    public LiveData<List<OptionItem>> getSubCategoryOptions() { return subCategoryOptions; }
    public LiveData<List<OptionItem>> getBrandOptions() { return brandOptions; }
    public LiveData<List<OptionItem>> getParentAssetOptions() { return parentAssetOptions; }
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


    // --- Metode spesifik untuk menerima data dari setiap fragment ---
    public void updateAssetInfoData(UpdateAssetRequest data) {
        pendingUpdateRequest.setName(data.getName());
        pendingUpdateRequest.setCode(data.getCode());
        pendingUpdateRequest.setCode2(data.getCode2());
        pendingUpdateRequest.setCode3(data.getCode3());
        pendingUpdateRequest.setType(data.getType());
        pendingUpdateRequest.setSerialNumber(data.getSerialNumber());
        pendingUpdateRequest.setDescription(data.getDescription());
        pendingUpdateRequest.setTotal(data.getTotal());
        pendingUpdateRequest.setUnit(data.getUnit());
        pendingUpdateRequest.setCondition(data.getCondition());
        pendingUpdateRequest.setOwnership(data.getOwnership());
        pendingUpdateRequest.setParentId(data.getParentId());
        pendingUpdateRequest.setCategoryId(data.getCategoryId());
        pendingUpdateRequest.setSubcategoryId(data.getSubcategoryId());
        pendingUpdateRequest.setBrandId(data.getBrandId());
    }

    public void updateLocationData(UpdateAssetRequest data) {
        pendingUpdateRequest.setRoomId(data.getRoomId());
        pendingUpdateRequest.setSubRoomId(data.getSubRoomId());
        pendingUpdateRequest.setResponsibleDivisionId(data.getResponsibleDivisionId());
        pendingUpdateRequest.setResponsibleWorkingUnitId(data.getResponsibleWorkingUnitId());
        pendingUpdateRequest.setResponsibleUserId(data.getResponsibleUserId());
    }

    public void updateMaintenanceData(UpdateAssetRequest data) {
        pendingUpdateRequest.setVendorId(data.getVendorId());
        pendingUpdateRequest.setProcurementDate(data.getProcurementDate());
        pendingUpdateRequest.setWarrantyExpirationDate(data.getWarrantyExpirationDate());
        pendingUpdateRequest.setPurchasePrice(data.getPurchasePrice());
        pendingUpdateRequest.setPoNumber(data.getPoNumber());
        pendingUpdateRequest.setInvoiceNumber(data.getInvoiceNumber());
        pendingUpdateRequest.setDepreciation(data.getDepreciation());
        pendingUpdateRequest.setDepreciationValue(data.getDepreciationValue());
        pendingUpdateRequest.setDepreciationStartDate(data.getDepreciationStartDate());
        pendingUpdateRequest.setDepreciationDurationMonth(data.getDepreciationDurationMonth());
    }


    // --- Metode Fetch ---
    public void fetchAllOptions() {
        fetchCategoryOptions();
        fetchBrandOptions();
        fetchParentAssetOptions();
        fetchHospitalOptions();
        fetchDivisionOptions();
        fetchWorkingUnitOptions();
        fetchUserOptions();
        fetchVendorOptions();
        fetchOwnershipOptions();
        fetchConditionOptions();
        fetchUnitOptions();
    }

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

    public void fetchCategoryOptions() { apiService.getAssetCategoryOptions().enqueue(createOptionsCallback(categoryOptions)); }
    public void fetchSubCategoryOptions(String categoryId) { if (categoryId != null) apiService.getAssetSubCategoryOptions(categoryId).enqueue(createOptionsCallback(subCategoryOptions)); }
    public void fetchBrandOptions() { apiService.getBrandOptions().enqueue(createOptionsCallback(brandOptions)); }
    public void fetchParentAssetOptions() { apiService.getAssetOptions().enqueue(createOptionsCallback(parentAssetOptions)); }
    public void fetchHospitalOptions() { apiService.getHospitalOptions().enqueue(createOptionsCallback(hospitalOptions)); }
    public void fetchBuildingOptions(String hospitalId) { if (hospitalId != null) apiService.getBuildingOptionsForHospital(hospitalId).enqueue(createOptionsCallback(buildingOptions)); }
    public void fetchFloorOptions(String buildingId) { if (buildingId != null) apiService.getFloorOptionsForBuilding(buildingId).enqueue(createOptionsCallback(floorOptions)); }
    public void fetchRoomOptions(String floorId) { if (floorId != null) apiService.getRoomOptionsByFloor(floorId).enqueue(createOptionsCallback(roomOptions)); }
    public void fetchSubRoomOptions(String roomId) { if (roomId != null) apiService.getSubRoomOptionsByRoom(roomId).enqueue(createOptionsCallback(subRoomOptions)); }
    public void fetchDivisionOptions() { apiService.getDivisionOptions().enqueue(createOptionsCallback(divisionOptions)); }
    public void fetchWorkingUnitOptions() { apiService.getWorkingUnitOptions().enqueue(createOptionsCallback(workingUnitOptions)); }
    public void fetchUserOptions() { apiService.getUserOptions().enqueue(createOptionsCallback(userOptions)); }
    public void fetchVendorOptions() { apiService.getVendorOptions().enqueue(createOptionsCallback(vendorOptions)); }
    public void fetchOwnershipOptions() { ownershipOptions.setValue(java.util.Arrays.asList("Owned", "KSO", "Rent")); }
    public void fetchConditionOptions() { conditionOptions.setValue(java.util.Arrays.asList("Good", "Slightly Damaged", "Moderately Damaged", "Heavily Damaged")); }
    public void fetchUnitOptions() { unitOptions.setValue(java.util.Arrays.asList("pieces", "unit", "set")); }

    // --- Metode Save ---
    public void saveChanges(String assetId) {
        // Hanya validasi code & name sebagai field wajib
        if (pendingUpdateRequest.getCode() == null || pendingUpdateRequest.getCode().trim().isEmpty()
                || pendingUpdateRequest.getName() == null || pendingUpdateRequest.getName().trim().isEmpty()) {
            isError.setValue(true);
            return;
        }

        isLoading.setValue(true);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(pendingUpdateRequest);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonRequest);
        List<MultipartBody.Part> files = new ArrayList<>();

        apiService.updateAsset(assetId, requestBody, files).enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(@NonNull Call<Asset> call, @NonNull Response<Asset> response) {
                isLoading.setValue(false);
                isSaveSuccess.setValue(response.isSuccessful());
                if (!response.isSuccessful()){
                    isError.setValue(true);
                }
            }
            @Override
            public void onFailure(@NonNull Call<Asset> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                isSaveSuccess.setValue(false);
                isError.setValue(true);
            }
        });
    }

    // Generic Callback Helper
    private Callback<OptionsResponse> createOptionsCallback(final MutableLiveData<List<OptionItem>> liveData) {
        return new Callback<OptionsResponse>() {
            @Override
            public void onResponse(@NonNull Call<OptionsResponse> call, @NonNull Response<OptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body().getData());
                }
            }
            @Override
            public void onFailure(@NonNull Call<OptionsResponse> call, @NonNull Throwable t) {
                isError.setValue(true);
            }
        };
    }
}
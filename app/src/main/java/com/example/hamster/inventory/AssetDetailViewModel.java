package com.example.hamster.inventory;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
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
import com.example.hamster.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel untuk mengelola data dan logika bisnis untuk detail aset.
 */
public class AssetDetailViewModel extends AndroidViewModel {

    private static final String TAG = "AssetDetailViewModel";

    private final ApiService apiService;

    // --- LiveData untuk UI State ---
    private final MutableLiveData<Asset> assetData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSaveSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // --- State untuk Perubahan ---
    private final UpdateAssetRequest pendingUpdateRequest = new UpdateAssetRequest();

    // Daftar URI untuk file-file BARU yang akan di-upload.
    private final List<Uri> newSerialNumberPhotoUris = new ArrayList<>();
    private final List<Uri> newAssetPhotoUris = new ArrayList<>();
    private final List<Uri> newPoDocumentUris = new ArrayList<>();
    private final List<Uri> newInvoiceDocumentUris = new ArrayList<>();
    private final List<Uri> newWarrantyDocumentUris = new ArrayList<>();
    private final List<Uri> newLicenseDocumentUris = new ArrayList<>();
    private final List<Uri> newUserManualDocumentUris = new ArrayList<>();
    private final List<Uri> newCustomDocumentUris = new ArrayList<>();
    private final List<String> newCustomDocumentNames = new ArrayList<>();

    // --- LiveData untuk Dropdown Options ---
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

    // --- Getters untuk LiveData ---
    public LiveData<Asset> getAssetData() { return assetData; }
    public LiveData<Boolean> getIsSaveSuccess() { return isSaveSuccess; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
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

    // --- Metode untuk Menerima Data dari Fragment ---

    public void updateAssetInfoData(UpdateAssetRequest data) {
        pendingUpdateRequest.setName(data.getName());
        pendingUpdateRequest.setCode(data.getCode());
        pendingUpdateRequest.setCode2(data.getCode2());
        pendingUpdateRequest.setCode3(data.getCode3());
        pendingUpdateRequest.setType(data.getType());
        pendingUpdateRequest.setSerialNumber(data.getSerialNumber());
        pendingUpdateRequest.setDescription(data.getDescription());
        if (data.getTotal() != null) pendingUpdateRequest.setTotal(data.getTotal());
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

    public void updateAllDocuments(
            List<Uri> newSerialUris, List<String> keepSerialIds,
            List<Uri> newAssetUris, List<String> keepAssetIds,
            List<Uri> newPoUris, List<String> keepPoIds,
            List<Uri> newInvoiceUris, List<String> keepInvoiceIds,
            List<Uri> newWarrantyUris, List<String> keepWarrantyIds,
            List<Uri> newLicenseUris, List<String> keepLicenseIds,
            List<Uri> newUserManualUris, List<String> keepUserManualIds,
            List<Uri> newCustomUris, List<String> customNames, List<String> keepCustomIds)
    {
        updateUriList(this.newSerialNumberPhotoUris, newSerialUris);
        updateUriList(this.newAssetPhotoUris, newAssetUris);
        updateUriList(this.newPoDocumentUris, newPoUris);
        updateUriList(this.newInvoiceDocumentUris, newInvoiceUris);
        updateUriList(this.newWarrantyDocumentUris, newWarrantyUris);
        updateUriList(this.newLicenseDocumentUris, newLicenseUris);
        updateUriList(this.newUserManualDocumentUris, newUserManualUris);
        updateUriList(this.newCustomDocumentUris, newCustomUris);
        updateStringList(this.newCustomDocumentNames, customNames);

        pendingUpdateRequest.setKeepSerialNumberPhotos(keepSerialIds);
        pendingUpdateRequest.setKeepAssetPhotos(keepAssetIds);
        pendingUpdateRequest.setKeepPoDocuments(keepPoIds);
        pendingUpdateRequest.setKeepInvoiceDocuments(keepInvoiceIds);
        pendingUpdateRequest.setKeepWarrantyDocuments(keepWarrantyIds);
        pendingUpdateRequest.setKeepLicenseDocuments(keepLicenseIds);
        pendingUpdateRequest.setKeepUserManualDocuments(keepUserManualIds);
        pendingUpdateRequest.setKeepCustomDocuments(keepCustomIds);
    }

    private void updateUriList(List<Uri> targetList, List<Uri> sourceList) {
        targetList.clear();
        if (sourceList != null) {
            targetList.addAll(sourceList);
        }
    }

    private void updateStringList(List<String> targetList, List<String> sourceList) {
        targetList.clear();
        if (sourceList != null) {
            targetList.addAll(sourceList);
        }
    }

    public void fetchAssetById(String assetId) {
        isLoading.setValue(true);
        apiService.getAssetById(assetId).enqueue(new Callback<AssetDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<AssetDetailResponse> call, @NonNull Response<AssetDetailResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    assetData.setValue(response.body().getData());
                } else {
                    errorMessage.setValue("Gagal memuat detail aset. Kode: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<AssetDetailResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Koneksi error: " + t.getMessage());
                Log.e(TAG, "fetchAssetById onFailure", t);
            }
        });
    }

    public void saveChanges(String assetId) {
        if (pendingUpdateRequest.getCode() == null || pendingUpdateRequest.getCode().isEmpty() ||
                pendingUpdateRequest.getName() == null || pendingUpdateRequest.getName().isEmpty()) {
            errorMessage.setValue("Kode Aset dan Nama Aset tidak boleh kosong.");
            return;
        }

        isLoading.setValue(true);
        Map<String, RequestBody> fields = buildFieldsMap();
        List<MultipartBody.Part> fileParts = buildFileParts();

        apiService.updateAsset(assetId, fields, fileParts).enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(@NonNull Call<Asset> call, @NonNull Response<Asset> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    isSaveSuccess.setValue(true);
                    resetPendingChanges();
                } else {
                    isSaveSuccess.setValue(false);
                    handleApiError(response);
                }
            }
            @Override
            public void onFailure(@NonNull Call<Asset> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                isSaveSuccess.setValue(false);
                errorMessage.setValue("Koneksi error saat menyimpan: " + t.getMessage());
                Log.e(TAG, "saveChanges onFailure", t);
            }
        });
    }

    private void resetPendingChanges() {
        newSerialNumberPhotoUris.clear();
        newAssetPhotoUris.clear();
        newPoDocumentUris.clear();
        newInvoiceDocumentUris.clear();
        newWarrantyDocumentUris.clear();
        newLicenseDocumentUris.clear();
        newUserManualDocumentUris.clear();
        newCustomDocumentUris.clear();
        newCustomDocumentNames.clear();
    }

    private Map<String, RequestBody> buildFieldsMap() {
        Map<String, RequestBody> fields = new HashMap<>();
        addPart(fields, "code", pendingUpdateRequest.getCode());
        addPart(fields, "name", pendingUpdateRequest.getName());
        addPart(fields, "ownership", pendingUpdateRequest.getOwnership());
        addPart(fields, "categoryId", pendingUpdateRequest.getCategoryId());
        addPart(fields, "subcategoryId", pendingUpdateRequest.getSubcategoryId());
        addPart(fields, "brandId", pendingUpdateRequest.getBrandId());
        addPart(fields, "condition", pendingUpdateRequest.getCondition());
        addPart(fields, "roomId", pendingUpdateRequest.getRoomId());
        addPart(fields, "parentId", pendingUpdateRequest.getParentId());

        addMultipleTextPart(fields, "keepSerialNumberPhotos", pendingUpdateRequest.getKeepSerialNumberPhotos());
        addMultipleTextPart(fields, "keepAssetPhotos", pendingUpdateRequest.getKeepAssetPhotos());
        addMultipleTextPart(fields, "keepPoDocuments", pendingUpdateRequest.getKeepPoDocuments());
        addMultipleTextPart(fields, "keepInvoiceDocuments", pendingUpdateRequest.getKeepInvoiceDocuments());
        addMultipleTextPart(fields, "keepWarrantyDocuments", pendingUpdateRequest.getKeepWarrantyDocuments());
        addMultipleTextPart(fields, "keepLicenseDocuments", pendingUpdateRequest.getKeepLicenseDocuments());
        addMultipleTextPart(fields, "keepUserManualDocuments", pendingUpdateRequest.getKeepUserManualDocuments());
        addMultipleTextPart(fields, "keepCustomDocuments", pendingUpdateRequest.getKeepCustomDocuments());
        addMultipleTextPart(fields, "customDocumentNames", this.newCustomDocumentNames);

        return fields;
    }

    private List<MultipartBody.Part> buildFileParts() {
        List<MultipartBody.Part> fileParts = new ArrayList<>();
        addFilesToParts(fileParts, "serialNumberPhoto", newSerialNumberPhotoUris);
        addFilesToParts(fileParts, "assetPhotos", newAssetPhotoUris);
        addFilesToParts(fileParts, "poDocument", newPoDocumentUris);
        addFilesToParts(fileParts, "invoiceDocument", newInvoiceDocumentUris);
        addFilesToParts(fileParts, "warrantyDocument", newWarrantyDocumentUris);
        addFilesToParts(fileParts, "licenseDocuments", newLicenseDocumentUris);
        addFilesToParts(fileParts, "userManualDocuments", newUserManualDocumentUris);
        addFilesToParts(fileParts, "customDocuments", newCustomDocumentUris);
        return fileParts;
    }

    private void addFilesToParts(List<MultipartBody.Part> fileParts, String partName, List<Uri> uris) {
        if (uris == null || uris.isEmpty()) return;
        for (Uri uri : uris) {
            MultipartBody.Part part = createFilePart(partName, uri);
            if (part != null) {
                fileParts.add(part);
            }
        }
    }

    private void addPart(Map<String, RequestBody> map, String key, String value) {
        if (value != null && !value.isEmpty()) {
            map.put(key, RequestBody.create(MediaType.parse("text/plain"), value));
        }
    }

    private void addMultipleTextPart(Map<String, RequestBody> map, String key, List<String> values) {
        if (values != null && !values.isEmpty()) {
            for (String value : values) {
                if (value != null && !value.isEmpty()) {
                    map.put(key, RequestBody.create(MediaType.parse("text/plain"), value));
                }
            }
        }
    }

    private MultipartBody.Part createFilePart(String partName, Uri fileUri) {
        try {
            File file = FileUtils.getFile(getApplication(), fileUri);
            if (file == null || !file.exists()) {
                Log.e(TAG, "File tidak ditemukan untuk URI: " + fileUri);
                errorMessage.postValue("File tidak ditemukan: " + fileUri.getPath());
                return null;
            }
            String mimeType = getApplication().getContentResolver().getType(fileUri);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
        } catch (Exception e) {
            Log.e(TAG, "Gagal membuat file part untuk: " + partName, e);
            errorMessage.postValue("Error saat menyiapkan file: " + e.getMessage());
            return null;
        }
    }

    public void fetchAllOptions() {
        fetchOptions(apiService.getAssetCategoryOptions(), categoryOptions);
        fetchOptions(apiService.getBrandOptions(), brandOptions);
        fetchOptions(apiService.getAssetOptions(), parentAssetOptions);
        fetchOptions(apiService.getHospitalOptions(), hospitalOptions);
        fetchOptions(apiService.getDivisionOptions(), divisionOptions);
        fetchOptions(apiService.getWorkingUnitOptions(), workingUnitOptions);
        fetchOptions(apiService.getUserOptions(), userOptions);
        fetchOptions(apiService.getVendorOptions(), vendorOptions);
        fetchStaticOptions();
    }

    private void fetchStaticOptions() {
        ownershipOptions.setValue(Arrays.asList("Owned", "KSO", "Rent"));
        conditionOptions.setValue(Arrays.asList("Good", "Slightly Damaged", "Moderately Damaged", "Heavily Damaged"));
        unitOptions.setValue(Arrays.asList("pieces", "unit", "set"));
    }

    public void fetchSubCategoryOptions(String categoryId) {
        if (categoryId != null && !categoryId.isEmpty()) {
            fetchOptions(apiService.getAssetSubCategoryOptions(categoryId), subCategoryOptions);
        }
    }

    public void fetchBuildingOptions(String hospitalId) {
        if (hospitalId != null && !hospitalId.isEmpty()) {
            fetchOptions(apiService.getBuildingOptionsForHospital(hospitalId), buildingOptions);
        }
    }

    public void fetchFloorOptions(String buildingId) {
        if (buildingId != null && !buildingId.isEmpty()) {
            fetchOptions(apiService.getFloorOptionsForBuilding(buildingId), floorOptions);
        }
    }

    public void fetchRoomOptions(String floorId) {
        if (floorId != null && !floorId.isEmpty()) {
            fetchOptions(apiService.getRoomOptionsByFloor(floorId), roomOptions);
        }
    }

    public void fetchSubRoomOptions(String roomId) {
        if (roomId != null && !roomId.isEmpty()) {
            fetchOptions(apiService.getSubRoomOptionsByRoom(roomId), subRoomOptions);
        }
    }

    private void fetchOptions(Call<OptionsResponse> call, final MutableLiveData<List<OptionItem>> liveData) {
        call.enqueue(new Callback<OptionsResponse>() {
            @Override
            public void onResponse(@NonNull Call<OptionsResponse> call, @NonNull Response<OptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body().getData());
                } else {
                    Log.w(TAG, "Gagal mengambil options: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<OptionsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Error jaringan saat mengambil options", t);
            }
        });
    }

    private void handleApiError(Response<?> response) {
        String errorBody = "Terjadi kesalahan.";
        try {
            if (response.errorBody() != null) {
                errorBody = response.errorBody().string();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saat parsing error body", e);
        }
        String errorMsg = "Gagal: " + response.code() + " - " + errorBody;
        errorMessage.setValue(errorMsg);
        Log.e(TAG, "API Error: " + errorMsg);
    }
}
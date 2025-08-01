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
import com.example.hamster.data.model.AssetMediaFile;
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

public class AssetDetailViewModel extends AndroidViewModel {

    private static final String TAG = "AssetDetailViewModel";
    private final ApiService apiService;

    private final MutableLiveData<Asset> assetData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSaveSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private UpdateAssetRequest pendingUpdateRequest = new UpdateAssetRequest();
    private final List<Uri> newSerialNumberPhotoUris = new ArrayList<>();
    private final List<Uri> newAssetPhotoUris = new ArrayList<>();

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

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    // --- Metode Update Real-time dari Fragment ---
    public void updateField(UpdateAction action) {
        action.update(pendingUpdateRequest);
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

    // --- Menerima data dokumen HANYA saat tombol save ditekan ---
    public void updateDocumentsData(List<Uri> newSerialUris, List<String> keepSerialIds, List<Uri> newAssetUris, List<String> keepAssetIds) {
        updateUriList(this.newSerialNumberPhotoUris, newSerialUris);
        updateUriList(this.newAssetPhotoUris, newAssetUris);
        pendingUpdateRequest.setKeepSerialNumberPhotos(keepSerialIds);
        pendingUpdateRequest.setKeepAssetPhotos(keepAssetIds);
    }

    private void updateUriList(List<Uri> targetList, List<Uri> sourceList) {
        targetList.clear();
        if (sourceList != null) {
            targetList.addAll(sourceList);
        }
    }


    // --- Logika Fetch & Save ---
    public void fetchAssetById(String assetId) {
        isLoading.setValue(true);
        apiService.getAssetById(assetId).enqueue(new Callback<AssetDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<AssetDetailResponse> call, @NonNull Response<AssetDetailResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Asset asset = response.body().getData();
                    assetData.setValue(asset);
                    initializePendingUpdate(asset);
                } else {
                    errorMessage.setValue("Gagal memuat detail aset. Kode: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<AssetDetailResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Koneksi error: " + t.getMessage());
            }
        });
    }

    private void initializePendingUpdate(Asset asset) {
        if (asset == null) {
            pendingUpdateRequest = new UpdateAssetRequest();
            return;
        }

        // 1. Info Aset
        pendingUpdateRequest.setName(asset.getName());
        pendingUpdateRequest.setCode(asset.getCode());
        pendingUpdateRequest.setOwnership(asset.getOwnership());
        pendingUpdateRequest.setCondition(asset.getCondition());
        pendingUpdateRequest.setType(asset.getType());
        pendingUpdateRequest.setSerialNumber(asset.getSerialNumber());
        pendingUpdateRequest.setDescription(asset.getDescription());
        pendingUpdateRequest.setTotal(asset.getTotal());
        pendingUpdateRequest.setUnit(asset.getUnit());

        if (asset.getCategory() != null) pendingUpdateRequest.setCategoryId(asset.getCategory().getId());
        if (asset.getSubcategory() != null) pendingUpdateRequest.setSubcategoryId(asset.getSubcategory().getId());
        if (asset.getBrand() != null) pendingUpdateRequest.setBrandId(asset.getBrand().getId());

        // 2. Lokasi Aset
        if (asset.getRoom() != null) pendingUpdateRequest.setRoomId(asset.getRoom().getId());
        if (asset.getSubRoom() != null) pendingUpdateRequest.setSubRoomId(asset.getSubRoom().getId());
        if (asset.getResponsibleDivision() != null) pendingUpdateRequest.setResponsibleDivisionId(asset.getResponsibleDivision().getId());
        if (asset.getResponsibleWorkingUnit() != null) pendingUpdateRequest.setResponsibleWorkingUnitId(asset.getResponsibleWorkingUnit().getId());
        if (asset.getResponsibleUser() != null) pendingUpdateRequest.setResponsibleUserId(asset.getResponsibleUser().getId());

        // 3. Maintenance Aset
        pendingUpdateRequest.setProcurementDate(asset.getProcurementDate());
        pendingUpdateRequest.setWarrantyExpirationDate(asset.getWarrantyExpirationDate());
        pendingUpdateRequest.setPurchasePrice(asset.getPurchasePrice());
        pendingUpdateRequest.setPoNumber(asset.getPoNumber());
        pendingUpdateRequest.setInvoiceNumber(asset.getInvoiceNumber());
        pendingUpdateRequest.setDepreciation(asset.getDepreciation());
        pendingUpdateRequest.setDepreciationValue(asset.getDepreciationValue());
        pendingUpdateRequest.setDepreciationStartDate(asset.getDepreciationStartDate());
        pendingUpdateRequest.setDepreciationDurationMonth(asset.getDepreciationDurationMonth());

        if (asset.getVendor() != null) pendingUpdateRequest.setVendorId(asset.getVendor().getId());

        if (asset.getMediaFiles() != null && !asset.getMediaFiles().isEmpty()) {
            List<String> serialNumberPhotoIds = new ArrayList<>();
            List<String> assetPhotoIds = new ArrayList<>();
            List<String> poDocumentIds = new ArrayList<>();
            List<String> invoiceDocumentIds = new ArrayList<>();
            List<String> otherDocumentIds = new ArrayList<>();

            for (AssetMediaFile media : asset.getMediaFiles()) {
                if (media == null || media.getId() == null) continue;

                switch (media.getType()) {
                    case "SERIAL_NUMBER_PHOTO":
                        serialNumberPhotoIds.add(media.getId());
                        break;
                    case "ASSET_PHOTO":
                        assetPhotoIds.add(media.getId());
                        break;
                    case "PO_DOCUMENT":
                        poDocumentIds.add(media.getId());
                        break;
                    case "INVOICE_DOCUMENT":
                        invoiceDocumentIds.add(media.getId());
                        break;
                    case "OTHER_DOCUMENT":
                        otherDocumentIds.add(media.getId());
                        break;
                }
            }

            // Set ID yang sudah ada ke pendingUpdateRequest agar tidak terhapus saat update
            pendingUpdateRequest.setKeepSerialNumberPhotos(serialNumberPhotoIds);
            pendingUpdateRequest.setKeepAssetPhotos(assetPhotoIds);
            pendingUpdateRequest.setKeepPoDocuments(poDocumentIds);
            pendingUpdateRequest.setKeepInvoiceDocuments(invoiceDocumentIds);
//            pendingUpdateRequest.setKeepOtherDocuments(otherDocumentIds);
        }
    }

    public void saveChanges(String assetId) {
        if (pendingUpdateRequest.getName() == null || pendingUpdateRequest.getName().trim().isEmpty()) {
            errorMessage.setValue("Nama Aset tidak boleh kosong.");
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
            }
        });
    }

    private Map<String, RequestBody> buildFieldsMap() {
        Map<String, RequestBody> fields = new HashMap<>();
        UpdateAssetRequest request = pendingUpdateRequest;

        // --- Asset Info Fragment ---
        addPart(fields, "code", request.getCode());
        addPart(fields, "code2", request.getCode2());
        addPart(fields, "code3", request.getCode3());
        addPart(fields, "name", request.getName());
        addPart(fields, "parentId", request.getParentId());
        addPart(fields, "type", request.getType());
        addPart(fields, "serialNumber", request.getSerialNumber());
        addPart(fields, "ownership", request.getOwnership());
        addPart(fields, "categoryId", request.getCategoryId());
        addPart(fields, "subcategoryId", request.getSubcategoryId());
        if (request.getTotal() != null) {
            addPart(fields, "total", String.valueOf(request.getTotal()));
        }
        addPart(fields, "unit", request.getUnit());
        addPart(fields, "description", request.getDescription());
        addPart(fields, "brandId", request.getBrandId());
        addPart(fields, "condition", request.getCondition());

        // --- Asset Location Fragment ---
        addPart(fields, "roomId", request.getRoomId());
        addPart(fields, "subRoomId", request.getSubRoomId());
        addPart(fields, "responsibleDivisionId", request.getResponsibleDivisionId());
        addPart(fields, "responsibleWorkingUnitId", request.getResponsibleWorkingUnitId());
        addPart(fields, "responsibleUserId", request.getResponsibleUserId());

        // --- Asset Maintenance Fragment ---
        addPart(fields, "vendorId", request.getVendorId());
        if (request.getProcurementDate() != null) {
            addPart(fields, "procurementDate", String.valueOf(request.getProcurementDate()));
        }
        if (request.getWarrantyExpirationDate() != null) {
            addPart(fields, "warrantyExpirationDate", String.valueOf(request.getWarrantyExpirationDate()));
        }
        if (request.getPurchasePrice() != null) {
            addPart(fields, "purchasePrice", String.valueOf(request.getPurchasePrice()));
        }
        addPart(fields, "poNumber", request.getPoNumber());
        addPart(fields, "invoiceNumber", request.getInvoiceNumber());
        if (request.getDepreciation() != null) {
            addPart(fields, "depreciation", String.valueOf(request.getDepreciation()));
        }
        if (request.getDepreciationValue() != null) {
            addPart(fields, "depreciationValue", String.valueOf(request.getDepreciationValue()));
        }
        if (request.getDepreciationStartDate() != null) {
            addPart(fields, "depreciationStartDate", String.valueOf(request.getDepreciationStartDate()));
        }
        if (request.getDepreciationDurationMonth() != null) {
            addPart(fields, "depreciationDurationMonth", String.valueOf(request.getDepreciationDurationMonth()));
        }

        // --- Asset Documents Fragment (Keep Existing Photos) ---
        addMultipleTextPart(fields, "keepSerialNumberPhotos", request.getKeepSerialNumberPhotos());
        addMultipleTextPart(fields, "keepAssetPhotos", request.getKeepAssetPhotos());

        return fields;
    }

    private List<MultipartBody.Part> buildFileParts() {
        List<MultipartBody.Part> fileParts = new ArrayList<>();
        addFilesToParts(fileParts, "serialNumberPhoto", newSerialNumberPhotoUris);
        addFilesToParts(fileParts, "assetPhotos", newAssetPhotoUris);
        return fileParts;
    }

    private void addFilesToParts(List<MultipartBody.Part> fileParts, String partName, List<Uri> uris) {
        if (uris == null) return;
        for (Uri uri : uris) {
            MultipartBody.Part part = createFilePart(partName, uri);
            if (part != null) fileParts.add(part);
        }
    }

    private void addPart(Map<String, RequestBody> map, String key, String value) {
        if (value != null) {
            map.put(key, RequestBody.create(MediaType.parse("text/plain"), value));
        }
    }

    private void addMultipleTextPart(Map<String, RequestBody> map, String key, List<String> values) {
        if (values == null) return;
        for (String value : values) {
            if (value != null && !value.isEmpty()) {
                map.put(key, RequestBody.create(MediaType.parse("text/plain"), value));
            }
        }
    }

    private MultipartBody.Part createFilePart(String partName, Uri fileUri) {
        try {
            File file = FileUtils.getFile(getApplication(), fileUri);
            if (file == null || !file.exists()) return null;
            String mimeType = getApplication().getContentResolver().getType(fileUri);
            if (mimeType == null) mimeType = "application/octet-stream";
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
        } catch (Exception e) {
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
        if (categoryId != null) fetchOptions(apiService.getAssetSubCategoryOptions(categoryId), subCategoryOptions);
    }
    public void fetchBuildingOptions(String hospitalId) {
        if (hospitalId != null) fetchOptions(apiService.getBuildingOptionsForHospital(hospitalId), buildingOptions);
    }
    public void fetchFloorOptions(String buildingId) {
        if (buildingId != null) fetchOptions(apiService.getFloorOptionsForBuilding(buildingId), floorOptions);
    }
    public void fetchRoomOptions(String floorId) {
        if (floorId != null) fetchOptions(apiService.getRoomOptionsByFloor(floorId), roomOptions);
    }
    public void fetchSubRoomOptions(String roomId) {
        if (roomId != null) fetchOptions(apiService.getSubRoomOptionsByRoom(roomId), subRoomOptions);
    }

    private void fetchOptions(Call<OptionsResponse> call, final MutableLiveData<List<OptionItem>> liveData) {
        call.enqueue(new Callback<OptionsResponse>() {
            @Override
            public void onResponse(@NonNull Call<OptionsResponse> call, @NonNull Response<OptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body().getData());
                }
            }
            @Override
            public void onFailure(@NonNull Call<OptionsResponse> call, @NonNull Throwable t) {
                // handle error silently
            }
        });
    }

    private void handleApiError(Response<?> response) {
        String errorBody = "Terjadi kesalahan.";
        try {
            if (response.errorBody() != null) errorBody = response.errorBody().string();
        } catch (Exception e) { /* ignore */ }
        errorMessage.setValue("Gagal: " + response.code() + " - " + errorBody);
    }

    // Interface fungsional untuk pembaruan yang bersih
    @FunctionalInterface
    public interface UpdateAction {
        void update(UpdateAssetRequest request);
    }
}
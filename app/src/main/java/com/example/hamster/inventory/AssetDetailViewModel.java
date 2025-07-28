package com.example.hamster.inventory;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;
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
    private final ApiService apiService;

    private final MutableLiveData<Asset> assetData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSaveSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final UpdateAssetRequest pendingUpdateRequest = new UpdateAssetRequest();

    // --- Data Khusus untuk Foto (dari AssetDocumentsFragment) ---
    private Uri newSerialNumberPhotoUri = null;
    private final List<Uri> newAssetPhotoUris = new ArrayList<>();
    private List<String> idsToKeepSerial = new ArrayList<>();
    private List<String> idsToKeepAsset = new ArrayList<>();

    // --- Data untuk Dokumen Lainnya (akan ditangani di Fragment lain) ---
    private final List<Uri> newPoDocumentUris = new ArrayList<>();
    private final List<Uri> newInvoiceDocumentUris = new ArrayList<>();
    private final List<Uri> newWarrantyDocumentUris = new ArrayList<>();
    private final List<Uri> newLicenseDocumentUris = new ArrayList<>();
    private final List<Uri> newUserManualDocumentUris = new ArrayList<>();
    private final List<Uri> newCustomDocumentUris = new ArrayList<>();
    private final List<String> newCustomDocumentNames = new ArrayList<>();

    private List<String> idsToKeepPoDocuments = new ArrayList<>();
    private List<String> idsToKeepInvoiceDocuments = new ArrayList<>();
    private List<String> idsToKeepWarrantyDocuments = new ArrayList<>();
    private List<String> idsToKeepLicenseDocuments = new ArrayList<>();
    private List<String> idsToKeepUserManualDocuments = new ArrayList<>();
    private List<String> idsToKeepCustomDocuments = new ArrayList<>();


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


    // --- Metode untuk Menerima Data dari Fragment Informasi Aset ---
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

    // --- Metode untuk Menerima Data dari Fragment Lokasi ---
    public void updateLocationData(UpdateAssetRequest data) {
        pendingUpdateRequest.setRoomId(data.getRoomId());
        pendingUpdateRequest.setSubRoomId(data.getSubRoomId());
        pendingUpdateRequest.setResponsibleDivisionId(data.getResponsibleDivisionId());
        pendingUpdateRequest.setResponsibleWorkingUnitId(data.getResponsibleWorkingUnitId());
        pendingUpdateRequest.setResponsibleUserId(data.getResponsibleUserId());
    }

    // --- Metode untuk Menerima Data dari Fragment Maintenance ---
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

    // --- Metode KHUSUS untuk Mengelola URI FOTO dari AssetDocumentsFragment ---
    public void updatePhotoUris(Uri serialUri, List<Uri> assetUris, List<String> keepSerialIds, List<String> keepAssetPhotoIds) {
        this.newSerialNumberPhotoUri = serialUri;
        this.newAssetPhotoUris.clear();
        if (assetUris != null) {
            this.newAssetPhotoUris.addAll(assetUris);
        }
        this.idsToKeepSerial = cleanIdList(keepSerialIds);
        this.idsToKeepAsset = cleanIdList(keepAssetPhotoIds);

        // Langsung set ke pendingUpdateRequest
        pendingUpdateRequest.setKeepSerialNumberPhotos(this.idsToKeepSerial);
        pendingUpdateRequest.setKeepAssetPhotos(this.idsToKeepAsset);

        Log.d("ViewModel", "Photos updated. New Serial URI: " + newSerialNumberPhotoUri + ", New Asset URIs: " + newAssetPhotoUris.size());
        Log.d("ViewModel", "Keep Serial IDs: " + idsToKeepSerial + ", Keep Asset IDs: " + idsToKeepAsset);
    }

    // --- Metode KHUSUS untuk Mengelola URI DOKUMEN dari Fragment Maintenance/Dokumen Lainnya ---
    public void updateDocumentUris(
            List<Uri> poUris, List<String> keepPoIds,
            List<Uri> invoiceUris, List<String> keepInvoiceIds,
            List<Uri> warrantyUris, List<String> keepWarrantyIds,
            List<Uri> licenseUris, List<String> keepLicenseIds,
            List<Uri> userManualUris, List<String> keepUserManualIds,
            List<Uri> customUris, List<String> customNames, List<String> keepCustomIds
    ) {
        this.newPoDocumentUris.clear();
        if (poUris != null) this.newPoDocumentUris.addAll(poUris);
        this.newInvoiceDocumentUris.clear();
        if (invoiceUris != null) this.newInvoiceDocumentUris.addAll(invoiceUris);
        this.newWarrantyDocumentUris.clear();
        if (warrantyUris != null) this.newWarrantyDocumentUris.addAll(warrantyUris);
        this.newLicenseDocumentUris.clear();
        if (licenseUris != null) this.newLicenseDocumentUris.addAll(licenseUris);
        this.newUserManualDocumentUris.clear();
        if (userManualUris != null) this.newUserManualDocumentUris.addAll(userManualUris);
        this.newCustomDocumentUris.clear();
        if (customUris != null) this.newCustomDocumentUris.addAll(customUris);
        this.newCustomDocumentNames.clear();
        if (customNames != null) this.newCustomDocumentNames.addAll(customNames);

        this.idsToKeepPoDocuments = cleanIdList(keepPoIds);
        this.idsToKeepInvoiceDocuments = cleanIdList(keepInvoiceIds);
        this.idsToKeepWarrantyDocuments = cleanIdList(keepWarrantyIds);
        this.idsToKeepLicenseDocuments = cleanIdList(keepLicenseIds);
        this.idsToKeepUserManualDocuments = cleanIdList(keepUserManualIds);
        this.idsToKeepCustomDocuments = cleanIdList(keepCustomIds);

        // Langsung set ke pendingUpdateRequest
        pendingUpdateRequest.setKeepPoDocuments(this.idsToKeepPoDocuments);
        pendingUpdateRequest.setKeepInvoiceDocuments(this.idsToKeepInvoiceDocuments);
        pendingUpdateRequest.setKeepWarrantyDocuments(this.idsToKeepWarrantyDocuments);
        pendingUpdateRequest.setKeepLicenseDocuments(this.idsToKeepLicenseDocuments);
        pendingUpdateRequest.setKeepUserManualDocuments(this.idsToKeepUserManualDocuments);
        pendingUpdateRequest.setKeepCustomDocuments(this.idsToKeepCustomDocuments);

        Log.d("ViewModel", "Documents updated. New PO URIs: " + newPoDocumentUris.size() + ", Keep PO IDs: " + idsToKeepPoDocuments);
    }

    private List<String> cleanIdList(List<String> rawList) {
        List<String> cleaned = new ArrayList<>();
        if (rawList != null) {
            for (String id : rawList) {
                if (id != null) {
                    cleaned.add(id.replace("\"", "").trim());
                }
            }
        }
        return cleaned;
    }

    // --- Metode Fetch Dropdown Options ---
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

    // --- Metode Fetch Data Aset berdasarkan ID ---
    public void fetchAssetById(String assetId) {
        isLoading.setValue(true);
        apiService.getAssetById(assetId).enqueue(new Callback<AssetDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<AssetDetailResponse> call, @NonNull Response<AssetDetailResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    assetData.setValue(response.body().getData());
                } else {
                    errorMessage.setValue("Gagal memuat detail aset.");
                }
            }
            @Override
            public void onFailure(@NonNull Call<AssetDetailResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Koneksi error: " + t.getMessage());
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


    // --- Metode UTAMA untuk Menyimpan Perubahan Aset ke API ---
    public void saveChanges(String assetId) {
        if (TextUtils.isEmpty(pendingUpdateRequest.getCode()) || TextUtils.isEmpty(pendingUpdateRequest.getName())) {
            errorMessage.setValue("Asset Code dan Asset Name wajib diisi.");
            return;
        }

        isLoading.setValue(true);

        Map<String, RequestBody> fields = new HashMap<>();

        // Menambahkan semua field teks dari pendingUpdateRequest
        addPart(fields, "code", pendingUpdateRequest.getCode());
        addPart(fields, "name", pendingUpdateRequest.getName());
        addPart(fields, "ownership", pendingUpdateRequest.getOwnership());
        addPart(fields, "categoryId", pendingUpdateRequest.getCategoryId());
        addPart(fields, "subcategoryId", pendingUpdateRequest.getSubcategoryId());
        addPart(fields, "brandId", pendingUpdateRequest.getBrandId());
        addPart(fields, "condition", pendingUpdateRequest.getCondition());
        addPart(fields, "roomId", pendingUpdateRequest.getRoomId());
        addPart(fields, "subRoomId", pendingUpdateRequest.getSubRoomId());
        addPart(fields, "responsibleDivisionId", pendingUpdateRequest.getResponsibleDivisionId());
        addPart(fields, "responsibleWorkingUnitId", pendingUpdateRequest.getResponsibleWorkingUnitId());
        addPart(fields, "responsibleUserId", pendingUpdateRequest.getResponsibleUserId());
        addPart(fields, "vendorId", pendingUpdateRequest.getVendorId());
        addPart(fields, "type", pendingUpdateRequest.getType());
        addPart(fields, "serialNumber", pendingUpdateRequest.getSerialNumber());
        addPart(fields, "code2", pendingUpdateRequest.getCode2());
        addPart(fields, "code3", pendingUpdateRequest.getCode3());
        addPart(fields, "parentId", pendingUpdateRequest.getParentId());
        addPart(fields, "description", pendingUpdateRequest.getDescription());
        addPart(fields, "procurementDate", pendingUpdateRequest.getProcurementDate());
        addPart(fields, "warrantyExpirationDate", pendingUpdateRequest.getWarrantyExpirationDate());
        addPart(fields, "purchasePrice", pendingUpdateRequest.getPurchasePrice());
        addPart(fields, "poNumber", pendingUpdateRequest.getPoNumber());
        addPart(fields, "invoiceNumber", pendingUpdateRequest.getInvoiceNumber());
        addPart(fields, "depreciation", pendingUpdateRequest.getDepreciation());
        addPart(fields, "depreciationValue", pendingUpdateRequest.getDepreciationValue());
        addPart(fields, "depreciationStartDate", pendingUpdateRequest.getDepreciationStartDate());
        addPart(fields, "depreciationDurationMonth", pendingUpdateRequest.getDepreciationDurationMonth());
        addPart(fields, "unit", pendingUpdateRequest.getUnit());

        // Menambahkan nama dokumen kustom sebagai part (jika ada)
        if (!newCustomDocumentNames.isEmpty()) {
            for (String name : newCustomDocumentNames) {
                addPart(fields, "customDocumentNames", name);
            }
        }


        List<MultipartBody.Part> fileParts = new ArrayList<>();

        // --- Menambahkan foto serial number baru (jika ada) ---
        if (newSerialNumberPhotoUri != null) {
            MultipartBody.Part part = createFilePart("serialNumberPhoto", newSerialNumberPhotoUri);
            if (part != null) fileParts.add(part);
        }

        // --- Menambahkan foto aset baru (jika ada) ---
        for (Uri uri : newAssetPhotoUris) {
            MultipartBody.Part part = createFilePart("assetPhotos", uri);
            if (part != null) fileParts.add(part);
        }

        // --- Menambahkan dokumen PO baru (jika ada) ---
        for (Uri uri : newPoDocumentUris) {
            MultipartBody.Part part = createFilePart("poDocument", uri);
            if (part != null) fileParts.add(part);
        }
        // --- Menambahkan dokumen Invoice baru (jika ada) ---
        for (Uri uri : newInvoiceDocumentUris) {
            MultipartBody.Part part = createFilePart("invoiceDocument", uri);
            if (part != null) fileParts.add(part);
        }
        // --- Menambahkan dokumen Warranty baru (jika ada) ---
        for (Uri uri : newWarrantyDocumentUris) {
            MultipartBody.Part part = createFilePart("warrantyDocument", uri);
            if (part != null) fileParts.add(part);
        }
        // --- Menambahkan dokumen License baru (jika ada) ---
        for (Uri uri : newLicenseDocumentUris) {
            MultipartBody.Part part = createFilePart("licenseDocuments", uri);
            if (part != null) fileParts.add(part);
        }
        // --- Menambahkan dokumen User Manual baru (jika ada) ---
        for (Uri uri : newUserManualDocumentUris) {
            MultipartBody.Part part = createFilePart("userManualDocuments", uri);
            if (part != null) fileParts.add(part);
        }
        // --- Menambahkan dokumen Custom baru (jika ada) ---
        for (Uri uri : newCustomDocumentUris) {
            MultipartBody.Part part = createFilePart("customDocuments", uri);
            if (part != null) fileParts.add(part);
        }


        // --- Menambahkan ID foto serial number yang ingin dipertahankan ---
        // Logikanya: jika TIDAK ada foto serial baru yang diambil, dan ada ID foto lama, kirim ID-nya.
        if (newSerialNumberPhotoUri == null && pendingUpdateRequest.getKeepSerialNumberPhotos() != null && !pendingUpdateRequest.getKeepSerialNumberPhotos().isEmpty()) {
            // Ubah menjadi string comma-separated untuk dikirim sebagai field biasa
            String ids = TextUtils.join(",", pendingUpdateRequest.getKeepSerialNumberPhotos());
            addPart(fields, "keepSerialNumberPhotos", ids); // Ditambahkan ke 'fields' (Map<String, RequestBody>)
        }

        if (pendingUpdateRequest.getKeepAssetPhotos() != null && !pendingUpdateRequest.getKeepAssetPhotos().isEmpty()) {
            // Ubah menjadi string comma-separated untuk dikirim sebagai field biasa
            String ids = TextUtils.join(",", pendingUpdateRequest.getKeepAssetPhotos());
            addPart(fields, "keepAssetPhotos", ids); // Ditambahkan ke 'fields' (Map<String, RequestBody>)
        }

        // --- Menambahkan ID dokumen PO yang ingin dipertahankan ---
        if (pendingUpdateRequest.getKeepPoDocuments() != null && !pendingUpdateRequest.getKeepPoDocuments().isEmpty()) {
            // Ubah menjadi string comma-separated untuk dikirim sebagai field biasa
            String ids = TextUtils.join(",", pendingUpdateRequest.getKeepPoDocuments());
            addPart(fields, "keepPoDocuments", ids);
        }
        // --- Menambahkan ID dokumen Invoice yang ingin dipertahankan ---
        if (pendingUpdateRequest.getKeepInvoiceDocuments() != null && !pendingUpdateRequest.getKeepInvoiceDocuments().isEmpty()) {
            String ids = TextUtils.join(",", pendingUpdateRequest.getKeepInvoiceDocuments());
            addPart(fields, "keepInvoiceDocuments", ids);
        }
        // --- Menambahkan ID dokumen Warranty yang ingin dipertahankan ---
        if (pendingUpdateRequest.getKeepWarrantyDocuments() != null && !pendingUpdateRequest.getKeepWarrantyDocuments().isEmpty()) {
            String ids = TextUtils.join(",", pendingUpdateRequest.getKeepWarrantyDocuments());
            addPart(fields, "keepWarrantyDocuments", ids);
        }
        // --- Menambahkan ID dokumen License yang ingin dipertahankan ---
        if (pendingUpdateRequest.getKeepLicenseDocuments() != null && !pendingUpdateRequest.getKeepLicenseDocuments().isEmpty()) {
            String ids = TextUtils.join(",", pendingUpdateRequest.getKeepLicenseDocuments());
            addPart(fields, "keepLicenseDocuments", ids);
        }
        // --- Menambahkan ID dokumen User Manual yang ingin dipertahankan ---
        if (pendingUpdateRequest.getKeepUserManualDocuments() != null && !pendingUpdateRequest.getKeepUserManualDocuments().isEmpty()) {
            String ids = TextUtils.join(",", pendingUpdateRequest.getKeepUserManualDocuments());
            addPart(fields, "keepUserManualDocuments", ids);
        }
        // --- Menambahkan ID dokumen Custom yang ingin dipertahankan ---
        if (pendingUpdateRequest.getKeepCustomDocuments() != null && !pendingUpdateRequest.getKeepCustomDocuments().isEmpty()) {
            String ids = TextUtils.join(",", pendingUpdateRequest.getKeepCustomDocuments());
            addPart(fields, "keepCustomDocuments", ids);
        }


        // Log untuk debug sebelum mengirim
        Log.d("SubmitCheck", "Submitting with fields: " + fields.keySet());
        Log.d("SubmitCheck", "Total file parts to send: " + fileParts.size());
        Log.d("SubmitCheck", "Keep Serial IDs for API: " + (fields.containsKey("keepSerialNumberPhotos") ? fields.get("keepSerialNumberPhotos") : "N/A"));
        Log.d("SubmitCheck", "Keep Asset IDs for API: " + (fields.containsKey("keepAssetPhotos") ? fields.get("keepAssetPhotos") : "N/A"));
        Log.d("SubmitCheck", "New PO Docs: " + newPoDocumentUris.size());
        Log.d("SubmitCheck", "Keep PO Docs: " + (fields.containsKey("keepPoDocuments") ? fields.get("keepPoDocuments") : "N/A"));


        // Panggil API untuk update aset
        apiService.updateAsset(
                assetId,
                fields,
                fileParts
        ).enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(@NonNull Call<Asset> call, @NonNull Response<Asset> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    isSaveSuccess.setValue(true);
                    // Setelah berhasil, kosongkan semua URI baru
                    newSerialNumberPhotoUri = null;
                    newAssetPhotoUris.clear();
                    newPoDocumentUris.clear();
                    newInvoiceDocumentUris.clear();
                    newWarrantyDocumentUris.clear();
                    newLicenseDocumentUris.clear();
                    newUserManualDocumentUris.clear();
                    newCustomDocumentUris.clear();
                    newCustomDocumentNames.clear();
                } else {
                    isSaveSuccess.setValue(false);
                    String errorBody = "Unknown error";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("API_Error", "Error parsing error body", e);
                    }
                    errorMessage.setValue("Gagal menyimpan: " + response.code() + " - " + response.message() + " | " + errorBody);
                    Log.e("API_Response", "Error response: " + response.code() + " " + response.message() + " " + errorBody);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Asset> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                isSaveSuccess.setValue(false);
                errorMessage.setValue("Koneksi error: " + t.getMessage());
                Log.e("API_Failure", "Network error: " + t.getMessage(), t);
            }
        });
    }

    // --- Helper Methods untuk Menambahkan Part RequestBody ---
    private void addPart(Map<String, RequestBody> map, String key, String value) {
        if (value != null && !value.isEmpty()) {
            map.put(key, RequestBody.create(MediaType.parse("text/plain"), value));
        }
    }

    private void addPart(Map<String, RequestBody> map, String key, Long value) {
        if (value != null) {
            map.put(key, RequestBody.create(MediaType.parse("text/plain"), String.valueOf(value)));
        }
    }

    private void addPart(Map<String, RequestBody> map, String key, Integer value) {
        if (value != null) {
            map.put(key, RequestBody.create(MediaType.parse("text/plain"), String.valueOf(value)));
        }
    }

    private void addPart(Map<String, RequestBody> map, String key, Double value) {
        if (value != null) {
            map.put(key, RequestBody.create(MediaType.parse("text/plain"), String.valueOf(value)));
        }
    }

    // --- Helper Method untuk Membuat MultipartBody.Part dari Uri File ---
    private MultipartBody.Part createFilePart(String partName, Uri fileUri) {
        try {
            File file = FileUtils.getFile(getApplication(), fileUri);
            if (file == null) {
                Log.e("FilePart", "File is null for Uri: " + fileUri);
                return null;
            }
            String mimeType = getApplication().getContentResolver().getType(fileUri);
            if (mimeType == null || mimeType.isEmpty()) {
                mimeType = "application/octet-stream";
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FilePart", "Error creating file part for " + partName + " and Uri " + fileUri + ": " + e.getMessage());
            return null;
        }
    }

    // --- Helper Method untuk Callback Opsi Dropdown ---
    private Callback<OptionsResponse> createOptionsCallback(final MutableLiveData<List<OptionItem>> liveData) {
        return new Callback<OptionsResponse>() {
            @Override
            public void onResponse(@NonNull Call<OptionsResponse> call, @NonNull Response<OptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body().getData());
                } else {
                    Log.e("OptionsAPI", "Failed to fetch options: " + response.code() + " - " + response.message());
                }
            }
            @Override
            public void onFailure(@NonNull Call<OptionsResponse> call, @NonNull Throwable t) {
                errorMessage.setValue("Koneksi error dropdown: " + t.getMessage());
                Log.e("OptionsAPI", "Network error fetching options: " + t.getMessage(), t);
            }
        };
    }
}
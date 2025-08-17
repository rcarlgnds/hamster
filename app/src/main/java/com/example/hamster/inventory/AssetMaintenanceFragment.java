package com.example.hamster.inventory;

import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hamster.R;
import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.AssetMediaFile;
import com.example.hamster.data.network.ApiClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;
import java.util.function.Consumer;

public class AssetMaintenanceFragment extends Fragment implements FragmentDataCollector {

    private AssetDetailViewModel viewModel;

    // View Components
    private TextInputEditText etProcurementDate, etWarrantyDate, etDepreciationStart, etEffectiveUsageDate;
    private TextView textPODocumentStatus, textInvoiceDocumentStatus, textWarrantyDocumentStatus;
    private ImageView iconPODocument, iconInvoiceDocument, iconWarrantyDocument;
    private Button buttonChoosePODocument, buttonChooseInvoiceDocument, buttonChooseWarrantyDocument;

    // File Handling State
    private ActivityResultLauncher<String[]> filePickerLauncher;
    private Consumer<Uri> currentFileCallback;
    private DocumentItem poDocument = new DocumentItem();
    private DocumentItem invoiceDocument = new DocumentItem();
    private DocumentItem warrantyDocument = new DocumentItem();

    // --- Lifecycle Methods ---
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset_maintenance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);

        initializeViews(view);
        initializeFilePicker();
        setupListeners();
        setupObservers();
    }

    // --- Initialization ---
    private void initializeViews(View view) {
        etProcurementDate = view.findViewById(R.id.etProcurementDate);
        etWarrantyDate = view.findViewById(R.id.editTextWarrantyDate);
        etDepreciationStart = view.findViewById(R.id.editTextDepreciationStart);
        etEffectiveUsageDate = view.findViewById(R.id.editTextEffectiveUsageDate);

        textPODocumentStatus = view.findViewById(R.id.textPODocumentStatus);
        textInvoiceDocumentStatus = view.findViewById(R.id.textInvoiceDocumentStatus);
        textWarrantyDocumentStatus = view.findViewById(R.id.textWarrantyDocumentStatus);

        iconPODocument = view.findViewById(R.id.iconPODocument);
        iconInvoiceDocument = view.findViewById(R.id.iconInvoiceDocument);
        iconWarrantyDocument = view.findViewById(R.id.iconWarrantyDocument);

        buttonChoosePODocument = view.findViewById(R.id.buttonChoosePODocument);
        buttonChooseInvoiceDocument = view.findViewById(R.id.buttonChooseInvoiceDocument);
        buttonChooseWarrantyDocument = view.findViewById(R.id.buttonChooseWarrantyDocument);
    }

    private void initializeFilePicker() {
        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri != null && currentFileCallback != null) {
                checkFileSizeAndProceed(uri, currentFileCallback);
            }
        });
    }

    private void setupListeners() {

        buttonChoosePODocument.setOnClickListener(v -> openFilePicker(uri -> {
            poDocument = new DocumentItem(uri);
            updateDocumentUI(poDocument, textPODocumentStatus, iconPODocument);
        }));

        buttonChooseInvoiceDocument.setOnClickListener(v -> openFilePicker(uri -> {
            invoiceDocument = new DocumentItem(uri);
            updateDocumentUI(invoiceDocument, textInvoiceDocumentStatus, iconInvoiceDocument);
        }));

        buttonChooseWarrantyDocument.setOnClickListener(v -> openFilePicker(uri -> {
            warrantyDocument = new DocumentItem(uri);
            updateDocumentUI(warrantyDocument, textWarrantyDocumentStatus, iconWarrantyDocument);
        }));
    }

    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), this::displayAssetData);
    }

    private void displayAssetData(Asset asset) {
        if (asset == null) return;


        if (asset.getMediaFiles() != null) {
            for (AssetMediaFile media : asset.getMediaFiles()) {
                if (media.getMediaFile() != null) {
                    String fullUrl = ApiClient.BASE_MEDIA_URL + media.getMediaFile().getUrl();
                    String fileName = media.getMediaFile().getOriginalName();
                    switch (media.getType()) {
                        case "PO_DOCUMENT":
                            poDocument = new DocumentItem(fullUrl, media.getId(), fileName);
                            break;
                        case "INVOICE_DOCUMENT":
                            invoiceDocument = new DocumentItem(fullUrl, media.getId(), fileName);
                            break;
                        case "WARRANTY_DOCUMENT":
                            warrantyDocument = new DocumentItem(fullUrl, media.getId(), fileName);
                            break;
                    }
                }
            }
        }
        updateAllDocumentUIs();
    }

    // --- UI Update Logic ---
    private void updateAllDocumentUIs() {
        updateDocumentUI(poDocument, textPODocumentStatus, iconPODocument);
        updateDocumentUI(invoiceDocument, textInvoiceDocumentStatus, iconInvoiceDocument);
        updateDocumentUI(warrantyDocument, textWarrantyDocumentStatus, iconWarrantyDocument);
    }

    private void updateDocumentUI(DocumentItem doc, TextView statusView, ImageView iconView) {
        if (doc.isSet()) {
            statusView.setText(doc.getFileName(requireContext()));
            iconView.setImageResource(R.drawable.ic_check_circle);
        } else {
            statusView.setText(R.string.status_no_file);
            iconView.setImageResource(R.drawable.ic_cancel);
        }
    }

    // --- File Picker Logic ---
    private void openFilePicker(Consumer<Uri> callback) {
        this.currentFileCallback = callback;
        String[] mimeTypes = {"image/jpeg", "image/png", "application/pdf"};
        filePickerLauncher.launch(mimeTypes);
    }

    private void checkFileSizeAndProceed(Uri uri, Consumer<Uri> callback) {
        if (getContext() == null) return;
        try (android.database.Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (!cursor.isNull(sizeIndex)) {
                    long fileSize = cursor.getLong(sizeIndex);
                    long maxSize = 10 * 1024 * 1024; // 10 MB

                    if (fileSize > maxSize) {
                        Toast.makeText(getContext(), "Ukuran file tidak boleh melebihi 10 MB", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("FileSizeCheck", "Gagal memeriksa ukuran file", e);
        }
        callback.accept(uri);
    }

    // --- Data Collection for Saving ---
    @Override
    public void collectDataForSave() {
        if (viewModel == null) return;
        viewModel.updateMaintenanceDocuments(
                poDocument.isNew() ? poDocument.localUri : null,
                invoiceDocument.isNew() ? invoiceDocument.localUri : null,
                warrantyDocument.isNew() ? warrantyDocument.localUri : null
        );

        viewModel.updateField(req -> {
            if(poDocument.isExisting()) req.getKeepPoDocuments().add(poDocument.mediaId);
            if(invoiceDocument.isExisting()) req.getKeepInvoiceDocuments().add(invoiceDocument.mediaId);
            if(warrantyDocument.isExisting()) req.getKeepWarrantyDocuments().add(warrantyDocument.mediaId);
        });
    }

    // --- Helper Class & Method ---
    private static class DocumentItem {
        Uri localUri;
        String remoteUrl;
        String mediaId;
        String originalName;

        DocumentItem() {}
        DocumentItem(Uri localUri) { this.localUri = localUri; }
        DocumentItem(String remoteUrl, String mediaId, String name) {
            this.remoteUrl = remoteUrl;
            this.mediaId = mediaId;
            this.originalName = name;
        }

        boolean isSet() { return localUri != null || remoteUrl != null; }
        boolean isNew() { return localUri != null; }
        boolean isExisting() { return mediaId != null && localUri == null; }

        String getFileName(android.content.Context context) {
            if (localUri != null) {
                try (android.database.Cursor cursor = context.getContentResolver().query(localUri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                    }
                }
            }
            return originalName != null ? originalName : "File lama";
        }
    }
}
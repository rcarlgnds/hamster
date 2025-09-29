package com.aktivo.hamster.inventory;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.aktivo.hamster.R;
import com.aktivo.hamster.data.model.Asset;
import com.aktivo.hamster.data.model.AssetMediaFile;
import com.aktivo.hamster.data.model.OptionItem;
import com.aktivo.hamster.data.model.UpdateAssetRequest;
import com.aktivo.hamster.data.network.ApiClient;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class AssetMaintenanceFragment extends Fragment implements FragmentDataCollector {

    private AssetDetailViewModel viewModel;

    // View Components
    private TextInputEditText etProcurementDate, etWarrantyDate, etDepreciationStart, etEffectiveActivationDate, etFunctionalTestingDate,
            etPurchasePrice, etPONumber, etInvoiceNumber, etDepreciationPercent, etDepreciationValue, etDepreciationDuration;
    private TextView textPODocumentStatus, textInvoiceDocumentStatus, textWarrantyDocumentStatus;
    private AutoCompleteTextView acVendor;
    private TextInputEditText etWarrantyStatus;
    private ImageView iconPODocument, iconInvoiceDocument, iconWarrantyDocument;
    private Button buttonChoosePODocument, buttonChooseInvoiceDocument, buttonChooseWarrantyDocument;
    private TextView tvDepreciation, tvInvoice, tvInvoiceDocument;
    private TextInputLayout tiInvoice;
    private MaterialCardView cvInvoice;
    private LinearLayout depreciation1, depreciation2;

    // File Handling State
    private ActivityResultLauncher<String[]> filePickerLauncher;
    private Consumer<Uri> currentFileCallback;
    private DocumentItem poDocument = new DocumentItem();
    private DocumentItem invoiceDocument = new DocumentItem();
    private DocumentItem warrantyDocument = new DocumentItem();
    private List<OptionItem> vendorList = new ArrayList<>();
    private boolean isProgrammaticChange = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset_maintenance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);
        viewModel.fetchAllOptions();

        initializeViews(view);
        initializeFilePicker();
        setupListeners();
        setupObservers();
    }

    private void initializeViews(View view) {
        etProcurementDate = view.findViewById(R.id.etProcurementDate);
        etWarrantyDate = view.findViewById(R.id.editTextWarrantyDate);
        etDepreciationStart = view.findViewById(R.id.editTextDepreciationStart);
        etEffectiveActivationDate = view.findViewById(R.id.editTextEffectiveActivationDate);
        etFunctionalTestingDate = view.findViewById(R.id.editTextFunctionalTestingDate);
        etPurchasePrice = view.findViewById(R.id.editTextPurchasePrice);
        etPONumber = view.findViewById(R.id.editTextPONumber);
        etInvoiceNumber = view.findViewById(R.id.editTextInvoiceNumber);
        etDepreciationPercent = view.findViewById(R.id.editTextDepreciationPercent);
        etDepreciationValue = view.findViewById(R.id.editTextDepreciationValue);
        etDepreciationDuration = view.findViewById(R.id.editTextDepreciationDuration);

        textPODocumentStatus = view.findViewById(R.id.textPODocumentStatus);
        textInvoiceDocumentStatus = view.findViewById(R.id.textInvoiceDocumentStatus);
        textWarrantyDocumentStatus = view.findViewById(R.id.textWarrantyDocumentStatus);

        iconPODocument = view.findViewById(R.id.iconPODocument);
        iconInvoiceDocument = view.findViewById(R.id.iconInvoiceDocument);
        iconWarrantyDocument = view.findViewById(R.id.iconWarrantyDocument);

        buttonChoosePODocument = view.findViewById(R.id.buttonChoosePODocument);
        buttonChooseInvoiceDocument = view.findViewById(R.id.buttonChooseInvoiceDocument);
        buttonChooseWarrantyDocument = view.findViewById(R.id.buttonChooseWarrantyDocument);
        acVendor = view.findViewById(R.id.autoCompleteVendor);
        etWarrantyStatus = view.findViewById(R.id.editTextWarrantyStatus);

        tvDepreciation = view.findViewById(R.id.depreciationTitle);
        depreciation1 = view.findViewById(R.id.depreciation1);
        depreciation2 = view.findViewById(R.id.depreciation2);
        tvInvoice = view.findViewById(R.id.tvInvoice);
        tvInvoiceDocument = view.findViewById(R.id.tvInvoiceDocument);
        tiInvoice = view.findViewById(R.id.tiInvoice);
        cvInvoice = view.findViewById(R.id.cvInvoice);
    }

    private void initializeFilePicker() {
        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri != null && currentFileCallback != null) {
                checkFileSizeAndProceed(uri, currentFileCallback);
            }
        });
    }

    private void setupListeners() {
        setupDatePickers();
        setupTextWatchers();

        acVendor.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < vendorList.size()) {
                OptionItem selected = vendorList.get(position);
                viewModel.updateField(req -> req.setVendorId(selected.getId()));
            }
        });

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

    private void setupDatePickers() {
        etProcurementDate.setOnClickListener(v -> showDatePickerDialog(etProcurementDate, time -> viewModel.updateField(req -> req.setProcurementDate(time))));
        etWarrantyDate.setOnClickListener(v -> showDatePickerDialog(etWarrantyDate, time -> {
            viewModel.updateField(req -> req.setWarrantyExpirationDate(time));
            updateWarrantyStatus(time);
        }));
        etDepreciationStart.setOnClickListener(v -> showDatePickerDialog(etDepreciationStart, time -> viewModel.updateField(req -> req.setDepreciationStartDate(time))));
    }

    private void showDatePickerDialog(TextInputEditText editText, Consumer<Long> onDateSet) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            editText.setText(sdf.format(calendar.getTime()));
            onDateSet.accept(calendar.getTimeInMillis() / 1000); // Convert to seconds
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setupTextWatchers() {
        etPurchasePrice.addTextChangedListener(new SimpleTextWatcher(text -> {
            if (!text.isEmpty()) viewModel.updateField(req -> req.setPurchasePrice(Double.parseDouble(text)));
        }));
        etPONumber.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setPoNumber(text))));
        etInvoiceNumber.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setInvoiceNumber(text))));
        etDepreciationPercent.addTextChangedListener(new SimpleTextWatcher(text -> {
            if (!text.isEmpty()) viewModel.updateField(req -> req.setDepreciation(Double.parseDouble(text)));
        }));
        etDepreciationValue.addTextChangedListener(new SimpleTextWatcher(text -> {
            if (!text.isEmpty()) viewModel.updateField(req -> req.setDepreciationValue(Double.parseDouble(text)));
        }));
        etDepreciationDuration.addTextChangedListener(new SimpleTextWatcher(text -> {
            if (!text.isEmpty()) viewModel.updateField(req -> req.setDepreciationDurationMonth(Integer.parseInt(text)));
        }));
    }

    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), this::displayAssetData);

        viewModel.getVendorOptions().observe(getViewLifecycleOwner(), options -> {
            if (options != null && getContext() != null) {
                vendorList.clear();
                vendorList.addAll(options);
                ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, vendorList);
                acVendor.setAdapter(adapter);
            }
        });

        viewModel.getIsViewFinancial().observe(getViewLifecycleOwner(), isViewFinancial -> {
            Log.d("viewfinancial", isViewFinancial.toString());
            if (Boolean.TRUE.equals(isViewFinancial)) {
                tvDepreciation.setVisibility(View.VISIBLE);
                depreciation1.setVisibility(View.VISIBLE);
                depreciation2.setVisibility(View.VISIBLE);
                etPurchasePrice.setVisibility(View.VISIBLE);
                etInvoiceNumber.setVisibility(View.VISIBLE);
                tvInvoice.setVisibility(View.VISIBLE);
                tvInvoiceDocument.setVisibility(View.VISIBLE);
                tiInvoice.setVisibility(View.VISIBLE);
                cvInvoice.setVisibility(View.VISIBLE);
            } else {
                tvDepreciation.setVisibility(View.GONE);
                depreciation1.setVisibility(View.GONE);
                depreciation2.setVisibility(View.GONE);
                etPurchasePrice.setVisibility(View.GONE);
                etInvoiceNumber.setVisibility(View.GONE);
                tvInvoice.setVisibility(View.GONE);
                tvInvoiceDocument.setVisibility(View.GONE);
                tiInvoice.setVisibility(View.GONE);
                cvInvoice.setVisibility(View.GONE);
            }
        });

        viewModel.getIsEditable().observe(getViewLifecycleOwner(), isEditable -> {
            if (Boolean.TRUE.equals(isEditable)) {
                etProcurementDate.setEnabled(isEditable);
                etPurchasePrice.setEnabled(isEditable);
                acVendor.setEnabled(isEditable);
                etPONumber.setEnabled(isEditable);
                buttonChoosePODocument.setEnabled(isEditable);
                etInvoiceNumber.setEnabled(isEditable);
                buttonChooseInvoiceDocument.setEnabled(isEditable);
                etWarrantyDate.setEnabled(isEditable);
                etWarrantyStatus.setEnabled(false);
                buttonChooseWarrantyDocument.setEnabled(isEditable);
                etDepreciationPercent.setEnabled(isEditable);
                etDepreciationValue.setEnabled(isEditable);
                etDepreciationStart.setEnabled(isEditable);
                etDepreciationDuration.setEnabled(isEditable);
                etEffectiveActivationDate.setEnabled(false);
                etFunctionalTestingDate.setEnabled(false);
            } else {
                etProcurementDate.setEnabled(isEditable);
                etPurchasePrice.setEnabled(isEditable);
                acVendor.setEnabled(isEditable);
                etPONumber.setEnabled(isEditable);
                buttonChoosePODocument.setEnabled(isEditable);
                etInvoiceNumber.setEnabled(isEditable);
                buttonChooseInvoiceDocument.setEnabled(isEditable);
                etWarrantyDate.setEnabled(isEditable);
                etWarrantyStatus.setEnabled(false);
                buttonChooseWarrantyDocument.setEnabled(isEditable);
                etDepreciationPercent.setEnabled(isEditable);
                etDepreciationValue.setEnabled(isEditable);
                etDepreciationStart.setEnabled(isEditable);
                etDepreciationDuration.setEnabled(isEditable);
                etEffectiveActivationDate.setEnabled(false);
                etFunctionalTestingDate.setEnabled(false);
            }
        });
    }

    private void displayAssetData(Asset asset) {
        if (asset == null) return;
        isProgrammaticChange = true;

        if (asset.getProcurementDate() != null) etProcurementDate.setText(formatDate(asset.getProcurementDate()));
        if (asset.getPurchasePrice() != null) etPurchasePrice.setText(String.valueOf(asset.getPurchasePrice()));
        if (asset.getVendor() != null) acVendor.setText(asset.getVendor().getName(), false);
        etPONumber.setText(asset.getPoNumber());

        etInvoiceNumber.setText(asset.getInvoiceNumber());
        if (asset.getWarrantyExpirationDate() != null) {
            etWarrantyDate.setText(formatDate(asset.getWarrantyExpirationDate()));
            updateWarrantyStatus(asset.getWarrantyExpirationDate());
        }

        if (asset.getDepreciation() != null) etDepreciationPercent.setText(String.valueOf(asset.getDepreciation()));
        if (asset.getDepreciationValue() != null) etDepreciationValue.setText(String.valueOf(asset.getDepreciationValue()));
        if (asset.getDepreciationStartDate() != null) etDepreciationStart.setText(formatDate(asset.getDepreciationStartDate()));
        if (asset.getDepreciationDurationMonth() != null) etDepreciationDuration.setText(String.valueOf(asset.getDepreciationDurationMonth()));
        if (asset.getEffectiveUsageDate() != null) etEffectiveActivationDate.setText(formatDate(asset.getEffectiveUsageDate()));

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

        isProgrammaticChange = false;
    }

    private String formatDate(Long timestamp) {
        if (timestamp == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp * 1000));
    }

    private void updateWarrantyStatus(Long expirationTimestamp) {
        if (expirationTimestamp == null) {
            etWarrantyStatus.setText("Inactive");
            return;
        }
        boolean isActive = (expirationTimestamp * 1000) > System.currentTimeMillis();
        etWarrantyStatus.setText(isActive ? "Active" : "Inactive");
    }

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

    @Override
    public void collectDataForSave() {
        if (viewModel == null) return;

        UpdateAssetRequest request = new UpdateAssetRequest();
        String purchasePriceStr = etPurchasePrice.getText().toString();
        if (!purchasePriceStr.isEmpty()) {
            request.setPurchasePrice(Double.parseDouble(purchasePriceStr));
        } else {
            request.setPurchasePrice(null);
        }
        request.setPoNumber(etPONumber.getText().toString());
        request.setInvoiceNumber(etInvoiceNumber.getText().toString());

        String depreciationPercentStr = etDepreciationPercent.getText().toString();
        if (!depreciationPercentStr.isEmpty()) {
            request.setDepreciation(Double.parseDouble(depreciationPercentStr));
        } else {
            request.setDepreciation(null);
        }

        String depreciationValueStr = etDepreciationValue.getText().toString();
        if (!depreciationValueStr.isEmpty()) {
            request.setDepreciationValue(Double.parseDouble(depreciationValueStr));
        } else {
            request.setDepreciationValue(null);
        }

        String depreciationDurationStr = etDepreciationDuration.getText().toString();
        if (!depreciationDurationStr.isEmpty()) {
            request.setDepreciationDurationMonth(Integer.parseInt(depreciationDurationStr));
        } else {
            request.setDepreciationDurationMonth(null);
        }

        viewModel.updateMaintenanceData(request);

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

    private class SimpleTextWatcher implements TextWatcher {
        private final Consumer<String> onTextChanged;
        SimpleTextWatcher(Consumer<String> onTextChanged) { this.onTextChanged = onTextChanged; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!isProgrammaticChange) {
                onTextChanged.accept(s.toString());
            }
        }
        @Override public void afterTextChanged(Editable s) {}
    }
}
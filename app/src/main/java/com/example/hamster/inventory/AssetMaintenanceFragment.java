package com.example.hamster.inventory;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hamster.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class AssetMaintenanceFragment extends Fragment {

    private TextInputEditText editTextProcurementDate, editTextPurchasePrice,
            editTextPONumber, editTextInvoiceNumber, editTextWarrantyDate,
            editTextDepreciationPercent, editTextDepreciationValue,
            editTextDepreciationStart, editTextDepreciationDuration;

    private AutoCompleteTextView autoCompleteVendor, autoCompleteWarrantyStatus;
    private TextView textPODocumentStatus, textInvoiceDocumentStatus, textWarrantyDocumentStatus;
    private Button buttonDownloadPO, buttonDownloadInvoice, buttonDownloadWarranty;

    private Uri selectedPODocumentUri, selectedInvoiceUri, selectedWarrantyUri;

    private final ActivityResultLauncher<String> choosePODocumentLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedPODocumentUri = uri;
                    textPODocumentStatus.setText("File selected: " + uri.getLastPathSegment());
                    buttonDownloadPO.setVisibility(View.VISIBLE);
                }
            });

    private final ActivityResultLauncher<String> chooseInvoiceDocumentLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedInvoiceUri = uri;
                    textInvoiceDocumentStatus.setText("File selected: " + uri.getLastPathSegment());
                    buttonDownloadInvoice.setVisibility(View.VISIBLE);
                }
            });

    private final ActivityResultLauncher<String> chooseWarrantyDocumentLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedWarrantyUri = uri;
                    textWarrantyDocumentStatus.setText("File selected: " + uri.getLastPathSegment());
                    buttonDownloadWarranty.setVisibility(View.VISIBLE);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asset_maintenance, container, false);

        // === Input fields ===
        editTextProcurementDate = view.findViewById(R.id.editTextProcurementDate);
        editTextPurchasePrice = view.findViewById(R.id.editTextPurchasePrice);
        editTextPONumber = view.findViewById(R.id.editTextPONumber);
        editTextInvoiceNumber = view.findViewById(R.id.editTextInvoiceNumber);
        editTextWarrantyDate = view.findViewById(R.id.editTextWarrantyDate);
        editTextDepreciationPercent = view.findViewById(R.id.editTextDepreciationPercent);
        editTextDepreciationValue = view.findViewById(R.id.editTextDepreciationValue);
        editTextDepreciationStart = view.findViewById(R.id.editTextDepreciationStart);
        editTextDepreciationDuration = view.findViewById(R.id.editTextDepreciationDuration);

        autoCompleteVendor = view.findViewById(R.id.autoCompleteVendor);
        autoCompleteWarrantyStatus = view.findViewById(R.id.autoCompleteWarrantyStatus);

        // === Document status text views ===
        textPODocumentStatus = view.findViewById(R.id.textPODocumentStatus);
        textInvoiceDocumentStatus = view.findViewById(R.id.textInvoiceDocumentStatus);
        textWarrantyDocumentStatus = view.findViewById(R.id.textWarrantyDocumentStatus);

        // === Choose buttons ===
        view.findViewById(R.id.buttonChoosePODocument).setOnClickListener(v -> choosePODocumentLauncher.launch("*/*"));
        view.findViewById(R.id.buttonChooseInvoiceDocument).setOnClickListener(v -> chooseInvoiceDocumentLauncher.launch("*/*"));
        view.findViewById(R.id.buttonChooseWarrantyDocument).setOnClickListener(v -> chooseWarrantyDocumentLauncher.launch("*/*"));

        // === Download buttons ===
        buttonDownloadPO = view.findViewById(R.id.buttonDownloadPODocument);
        buttonDownloadInvoice = view.findViewById(R.id.buttonDownloadInvoiceDocument);
        buttonDownloadWarranty = view.findViewById(R.id.buttonDownloadWarantyDocument);

        buttonDownloadPO.setOnClickListener(v -> openDocument(selectedPODocumentUri));
        buttonDownloadInvoice.setOnClickListener(v -> openDocument(selectedInvoiceUri));
        buttonDownloadWarranty.setOnClickListener(v -> openDocument(selectedWarrantyUri));

        setupDropdowns();

        return view;
    }

    private void openDocument(Uri uri) {
        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, requireContext().getContentResolver().getType(uri));
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "No document to open", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDropdowns() {
        String[] vendors = {"Vendor A", "Vendor B", "Vendor C"};
        String[] warrantyStatuses = {"Valid", "Expired", "None"};

        ArrayAdapter<String> vendorAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, vendors);
        ArrayAdapter<String> warrantyAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, warrantyStatuses);

        autoCompleteVendor.setAdapter(vendorAdapter);
        autoCompleteWarrantyStatus.setAdapter(warrantyAdapter);
    }
}

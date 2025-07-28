package com.example.hamster.inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.hamster.R;
import com.example.hamster.data.model.OptionItem;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssetMaintenanceFragment extends Fragment {
    private AssetDetailViewModel viewModel;
    private TextInputEditText etProcurementDate, etPurchasePrice, etPoNumber, etInvoiceNumber, etWarrantyDate;
    private AutoCompleteTextView acVendor, acWarrantyStatus;

    private List<OptionItem> vendorList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle s) {
        return inflater.inflate(R.layout.fragment_asset_maintenance, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);

        initializeViews(view);
        setupStaticDropdowns();
        setupObservers();

        viewModel.fetchVendorOptions();
    }

    private void initializeViews(View view) {
        etProcurementDate = view.findViewById(R.id.editTextProcurementDate);
        etPurchasePrice = view.findViewById(R.id.editTextPurchasePrice);
        etPoNumber = view.findViewById(R.id.editTextPONumber);
        etInvoiceNumber = view.findViewById(R.id.editTextInvoiceNumber);
        etWarrantyDate = view.findViewById(R.id.editTextWarrantyDate);
        acVendor = view.findViewById(R.id.autoCompleteVendor);
        acWarrantyStatus = view.findViewById(R.id.autoCompleteWarrantyStatus);
    }

    private void setupStaticDropdowns() {
        String[] warrantyStatus = {"Active", "Expired"};
        acWarrantyStatus.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, warrantyStatus));
    }

    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null) return;

            if (asset.getProcurementDate() != null) {
                etProcurementDate.setText(formatDate(asset.getProcurementDate()));
            }
            if (asset.getPurchasePrice() != null) {
                etPurchasePrice.setText(String.valueOf(asset.getPurchasePrice()));
            }
            etPoNumber.setText(asset.getPoNumber());
            etInvoiceNumber.setText(asset.getInvoiceNumber());
            if (asset.getWarrantyExpirationDate() != null) {
                etWarrantyDate.setText(formatDate(asset.getWarrantyExpirationDate()));
            }
            if (asset.getVendor() != null) {
                acVendor.setText(asset.getVendor().getName(), false);
            }
        });

        viewModel.getVendorOptions().observe(getViewLifecycleOwner(), options -> {
            if (options == null) return;
            vendorList.clear();
            vendorList.addAll(options);
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, vendorList);
            acVendor.setAdapter(adapter);
        });
    }

    private String formatDate(Long timestamp) {
        if (timestamp == null || timestamp == 0) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = new Date(timestamp * 1000);
            return sdf.format(date);
        } catch (Exception e) {
            return "Invalid Date";
        }
    }
}
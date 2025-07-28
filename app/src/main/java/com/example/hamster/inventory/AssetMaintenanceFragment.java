package com.example.hamster.inventory;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.hamster.data.model.UpdateAssetRequest;
import com.google.android.material.textfield.TextInputEditText;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssetMaintenanceFragment extends Fragment implements FragmentDataCollector {
    private AssetDetailViewModel viewModel;
    private TextInputEditText etProcurementDate, etPurchasePrice, etPoNumber, etInvoiceNumber, etWarrantyDate, etDepreciationPercent, etDepreciationValue, etDepreciationStart, etDepreciationDuration;
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
    }

    @Override
    public void collectDataForSave() {
        UpdateAssetRequest partialRequest = new UpdateAssetRequest();

        partialRequest.setVendorId(getSelectedId(acVendor, vendorList));
        partialRequest.setPoNumber(etPoNumber.getText().toString());
        partialRequest.setInvoiceNumber(etInvoiceNumber.getText().toString());

        try {
            if (!TextUtils.isEmpty(etPurchasePrice.getText()))
                partialRequest.setPurchasePrice(Double.parseDouble(etPurchasePrice.getText().toString()));
            if (!TextUtils.isEmpty(etDepreciationPercent.getText()))
                partialRequest.setDepreciation(Double.parseDouble(etDepreciationPercent.getText().toString()));
            if (!TextUtils.isEmpty(etDepreciationValue.getText()))
                partialRequest.setDepreciationValue(Double.parseDouble(etDepreciationValue.getText().toString()));
            if (!TextUtils.isEmpty(etDepreciationDuration.getText()))
                partialRequest.setDepreciationDurationMonth(Integer.parseInt(etDepreciationDuration.getText().toString()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        partialRequest.setProcurementDate(parseDateToTimestamp(etProcurementDate.getText().toString()));
        partialRequest.setWarrantyExpirationDate(parseDateToTimestamp(etWarrantyDate.getText().toString()));
        partialRequest.setDepreciationStartDate(parseDateToTimestamp(etDepreciationStart.getText().toString()));

        viewModel.updateMaintenanceData(partialRequest);
    }

    private void initializeViews(View view) {
        etProcurementDate = view.findViewById(R.id.editTextProcurementDate);
        etPurchasePrice = view.findViewById(R.id.editTextPurchasePrice);
        etPoNumber = view.findViewById(R.id.editTextPONumber);
        etInvoiceNumber = view.findViewById(R.id.editTextInvoiceNumber);
        etWarrantyDate = view.findViewById(R.id.editTextWarrantyDate);
        acVendor = view.findViewById(R.id.autoCompleteVendor);
        acWarrantyStatus = view.findViewById(R.id.autoCompleteWarrantyStatus);
        etDepreciationPercent = view.findViewById(R.id.editTextDepreciationPercent);
        etDepreciationValue = view.findViewById(R.id.editTextDepreciationValue);
        etDepreciationStart = view.findViewById(R.id.editTextDepreciationStart);
        etDepreciationDuration = view.findViewById(R.id.editTextDepreciationDuration);
    }

    private void setupStaticDropdowns() {
        String[] warrantyStatus = {"Active", "Expired"};
        acWarrantyStatus.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, warrantyStatus));
    }

    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null) return;

            etProcurementDate.setText(formatDate(asset.getProcurementDate()));
            if (asset.getPurchasePrice() != null) etPurchasePrice.setText(String.valueOf(asset.getPurchasePrice()));
            etPoNumber.setText(asset.getPoNumber());
            etInvoiceNumber.setText(asset.getInvoiceNumber());
            etWarrantyDate.setText(formatDate(asset.getWarrantyExpirationDate()));
            if (asset.getVendor() != null) acVendor.setText(asset.getVendor().getName(), false);
            if (asset.getDepreciation() != null) etDepreciationPercent.setText(String.valueOf(asset.getDepreciation()));
            if (asset.getDepreciationValue() != null) etDepreciationValue.setText(String.valueOf(asset.getDepreciationValue()));
            etDepreciationStart.setText(formatDate(asset.getDepreciationStartDate()));
            if (asset.getDepreciationDurationMonth() != null) etDepreciationDuration.setText(String.valueOf(asset.getDepreciationDurationMonth()));
        });

        viewModel.getVendorOptions().observe(getViewLifecycleOwner(), options -> {
            if (options == null || getContext() == null) return;
            vendorList.clear();
            vendorList.addAll(options);
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, vendorList);
            acVendor.setAdapter(adapter);
        });
    }

    private String getSelectedId(AutoCompleteTextView view, List<OptionItem> list) {
        String name = view.getText().toString();
        if (name.isEmpty() || list.isEmpty()) return null;
        for (OptionItem item : list) {
            if (item.getName().equals(name)) {
                return item.getId();
            }
        }
        return null;
    }

    private String formatDate(Long timestamp) {
        if (timestamp == null || timestamp == 0) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = new Date(timestamp * 1000);
            return sdf.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    private Long parseDateToTimestamp(String dateString) {
        if (dateString == null || dateString.isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(dateString);
            if (date != null) {
                return date.getTime() / 1000;
            }
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
package com.example.hamster.inventory;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hamster.R;

import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.OptionItem;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class AssetMaintenanceFragment extends Fragment {

    private AssetDetailViewModel viewModel;
    private TextInputEditText etPurchasePrice, etPoNumber, etInvoiceNumber, etDepreciationPercent, etDepreciationValue, etDepreciationDuration;
    private AutoCompleteTextView acVendor;

    private TextInputEditText etProcurementDate, etWarrantyDate, etDepreciationStart, etEffectiveUsageDate;

    private List<OptionItem> vendorList = new ArrayList<>();
    private boolean isUserAction = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset_maintenance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);

        initializeViews(view);
        setupListeners();
        setupObservers();
    }

    private void initializeViews(View view) {
        etProcurementDate = view.findViewById(R.id.etProcurementDate);
        etPurchasePrice = view.findViewById(R.id.editTextPurchasePrice);
        etPoNumber = view.findViewById(R.id.editTextPONumber);
        etInvoiceNumber = view.findViewById(R.id.editTextInvoiceNumber);
        etWarrantyDate = view.findViewById(R.id.editTextWarrantyDate);
        acVendor = view.findViewById(R.id.autoCompleteVendor);
        etDepreciationPercent = view.findViewById(R.id.editTextDepreciationPercent);
        etDepreciationValue = view.findViewById(R.id.editTextDepreciationValue);
        etDepreciationStart = view.findViewById(R.id.editTextDepreciationStart);
        etEffectiveUsageDate = view.findViewById(R.id.editTextEffectiveUsageDate);
        etDepreciationDuration = view.findViewById(R.id.editTextDepreciationDuration);

        etProcurementDate.setFocusable(false);
        etProcurementDate.setClickable(true);
        etWarrantyDate.setFocusable(false);
        etWarrantyDate.setClickable(true);
        etDepreciationStart.setFocusable(false);
        etDepreciationStart.setClickable(true);
        etEffectiveUsageDate.setFocusable(false);
        etEffectiveUsageDate.setClickable(true);
    }

    private void setupListeners() {
        // Listener untuk field teks biasa
        addTextWatcher(etPurchasePrice, text -> viewModel.updateField(req -> req.setPurchasePrice(parseDouble(text))));
        addTextWatcher(etPoNumber, text -> viewModel.updateField(req -> req.setPoNumber(text)));
        addTextWatcher(etInvoiceNumber, text -> viewModel.updateField(req -> req.setInvoiceNumber(text)));
        addTextWatcher(etDepreciationPercent, text -> viewModel.updateField(req -> req.setDepreciation(parseDouble(text))));
        addTextWatcher(etDepreciationValue, text -> viewModel.updateField(req -> req.setDepreciationValue(parseDouble(text))));
        addTextWatcher(etDepreciationDuration, text -> viewModel.updateField(req -> req.setDepreciationDurationMonth(parseInteger(text))));

        // Listener untuk dropdown (AutoCompleteTextView)
        acVendor.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selected = (OptionItem) parent.getItemAtPosition(position);
            if (selected != null) {
                viewModel.updateField(req -> req.setVendorId(selected.getId()));
            }
        });

        // Listener untuk field tanggal
        etProcurementDate.setOnClickListener(v -> showDatePickerDialog(etProcurementDate, timestamp -> viewModel.updateField(req -> req.setProcurementDate(timestamp))));
        etWarrantyDate.setOnClickListener(v -> showDatePickerDialog(etWarrantyDate, timestamp -> viewModel.updateField(req -> req.setWarrantyExpirationDate(timestamp))));
        etDepreciationStart.setOnClickListener(v -> showDatePickerDialog(etDepreciationStart, timestamp -> viewModel.updateField(req -> req.setDepreciationStartDate(timestamp))));
        etEffectiveUsageDate.setOnClickListener(v -> showDatePickerDialog(etEffectiveUsageDate, timestamp -> viewModel.updateField(req -> req.setEffectiveUsageDate(timestamp))));
    }

    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null) return;
            isUserAction = false;
            updateUI(asset);
            isUserAction = true;
        });

        viewModel.getVendorOptions().observe(getViewLifecycleOwner(), options -> {
            if (options == null || getContext() == null) return;
            vendorList.clear();
            vendorList.addAll(options);
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, vendorList);
            acVendor.setAdapter(adapter);

            if (viewModel.getAssetData().getValue() != null && viewModel.getAssetData().getValue().getVendor() != null) {
                acVendor.setText(viewModel.getAssetData().getValue().getVendor().getName(), false);
            }
        });
    }

    private void updateUI(Asset asset) {
        etProcurementDate.setText(formatDate(asset.getProcurementDate()));
        etWarrantyDate.setText(formatDate(asset.getWarrantyExpirationDate()));
        etDepreciationStart.setText(formatDate(asset.getDepreciationStartDate()));
        etEffectiveUsageDate.setText(formatDate(asset.getEffectiveUsageDate()));

        etPoNumber.setText(asset.getPoNumber());
        etInvoiceNumber.setText(asset.getInvoiceNumber());

        if (asset.getPurchasePrice() != null) etPurchasePrice.setText(String.valueOf(asset.getPurchasePrice()));
        if (asset.getDepreciation() != null) etDepreciationPercent.setText(String.valueOf(asset.getDepreciation()));
        if (asset.getDepreciationValue() != null) etDepreciationValue.setText(String.valueOf(asset.getDepreciationValue()));
        if (asset.getDepreciationDurationMonth() != null) etDepreciationDuration.setText(String.valueOf(asset.getDepreciationDurationMonth()));

        if (asset.getVendor() != null) {
            acVendor.setText(asset.getVendor().getName(), false);
        }
    }

    private void showDatePickerDialog(final EditText targetEditText, final Consumer<Long> onDateSelected) {
        final Calendar calendar = Calendar.getInstance();
        String existingDateStr = targetEditText.getText().toString();

        if (!existingDateStr.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date d = sdf.parse(existingDateStr);
                if (d != null) {
                    calendar.setTime(d);
                }
            } catch (Exception e) {
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.clear();
                    selectedDate.set(year, month, dayOfMonth);

                    long timestamp = selectedDate.getTimeInMillis();
                    targetEditText.setText(formatDate(timestamp));
                    onDateSelected.accept(timestamp);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    private void addTextWatcher(EditText editText, Consumer<String> onTextChanged) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUserAction) {
                    onTextChanged.accept(s.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private String formatDate(Long timestamp) {
        if (timestamp == null || timestamp == 0) return "";

        Date date = new Date(timestamp * 1000L);
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
    }

    private Double parseDouble(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
package com.example.hamster.inventory;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.ArrayList;
import java.util.List;

public class AssetInfoFragment extends Fragment {
    private AssetDetailViewModel viewModel;
    private TextInputEditText etCode, etName, etSerial, etDesc;
    private AutoCompleteTextView acOwnership, acCategory, acSubCategory, acBrand, acCondition;

    private List<OptionItem> categoryList = new ArrayList<>();
    private List<OptionItem> subCategoryList = new ArrayList<>();
    private List<OptionItem> brandList = new ArrayList<>();

    private boolean isProgrammaticChange = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup c, @Nullable Bundle s) {
        return inflater.inflate(R.layout.fragment_asset_info, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);
        initializeViews(view);
        setupObservers();
        setupInputListeners();
    }

    private void initializeViews(View view) {
        etCode = view.findViewById(R.id.editTextCode);
        etName = view.findViewById(R.id.editTextName);
        etSerial = view.findViewById(R.id.editTextSerial);
        etDesc = view.findViewById(R.id.editTextDescription);
        acOwnership = view.findViewById(R.id.autoCompleteOwnership);
        acCondition = view.findViewById(R.id.autoCompleteCondition);
        acCategory = view.findViewById(R.id.autoCompleteCategory);
        acSubCategory = view.findViewById(R.id.autoCompleteSubCategory);
        acBrand = view.findViewById(R.id.autoCompleteBrand);
    }

    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null) return;
            isProgrammaticChange = true;
            etCode.setText(asset.getCode());
            etName.setText(asset.getName());
            etSerial.setText(asset.getSerialNumber());
            etDesc.setText(asset.getDescription());
            acOwnership.setText(asset.getOwnership(), false);
            acCondition.setText(asset.getCondition(), false);
            if (asset.getCategory() != null) acCategory.setText(asset.getCategory().getName(), false);
            if (asset.getSubcategory() != null) acSubCategory.setText(asset.getSubcategory().getName(), false);
            if (asset.getBrand() != null) acBrand.setText(asset.getBrand().getName(), false);
            isProgrammaticChange = false;
        });

        viewModel.getCategoryOptions().observe(getViewLifecycleOwner(), options -> populateDropdown(acCategory, options, categoryList));
        viewModel.getSubCategoryOptions().observe(getViewLifecycleOwner(), options -> populateDropdown(acSubCategory, options, subCategoryList));
        viewModel.getBrandOptions().observe(getViewLifecycleOwner(), options -> populateDropdown(acBrand, options, brandList));

        viewModel.getOwnershipOptions().observe(getViewLifecycleOwner(), options -> populateStringDropdown(acOwnership, options));
        viewModel.getConditionOptions().observe(getViewLifecycleOwner(), options -> populateStringDropdown(acCondition, options));
    }

    private void setupInputListeners() {
        etName.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setName(text))));
        etCode.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setCode(text))));
        etSerial.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setSerialNumber(text))));
        etDesc.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setDescription(text))));

        acOwnership.setOnItemClickListener((p, v, pos, id) -> viewModel.updateField(req -> req.setOwnership(acOwnership.getText().toString())));
        acCondition.setOnItemClickListener((p, v, pos, id) -> viewModel.updateField(req -> req.setCondition(acCondition.getText().toString())));

        acCategory.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selected = categoryList.get(position);
            viewModel.updateField(req -> req.setCategoryId(selected.getId()));
            viewModel.fetchSubCategoryOptions(selected.getId());
            acSubCategory.setText("", false);
            viewModel.updateField(req -> req.setSubcategoryId(null));
        });

        acSubCategory.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selected = subCategoryList.get(position);
            viewModel.updateField(req -> req.setSubcategoryId(selected.getId()));
        });

        acBrand.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selected = brandList.get(position);
            viewModel.updateField(req -> req.setBrandId(selected.getId()));
        });
    }

    private void populateDropdown(AutoCompleteTextView view, List<OptionItem> options, List<OptionItem> localList) {
        if (options == null || getContext() == null) return;
        localList.clear();
        localList.addAll(options);
        ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, localList);
        view.setAdapter(adapter);
    }

    private void populateStringDropdown(AutoCompleteTextView view, List<String> options) {
        if (options == null || getContext() == null) return;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, options);
        view.setAdapter(adapter);
    }

    private class SimpleTextWatcher implements TextWatcher {
        private final java.util.function.Consumer<String> onTextChanged;
        SimpleTextWatcher(java.util.function.Consumer<String> onTextChanged) { this.onTextChanged = onTextChanged; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!isProgrammaticChange) {
                onTextChanged.accept(s.toString());
            }
        }
        @Override public void afterTextChanged(Editable s) {}
    }
}
package com.aktivo.hamster.inventory;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.aktivo.hamster.R;
import com.aktivo.hamster.data.model.OptionItem;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class AssetInfoFragment extends Fragment {
    private AssetDetailViewModel viewModel;
    private TextInputEditText etCode, etName, etSerial, etDesc, etAliasNameTeramedik, etAliasNameHamster;
    private AutoCompleteTextView acOwnership, acCategory, acSubCategory, acBrand, acCondition, acUnit;

    private List<OptionItem> categoryList = new ArrayList<>();
    private List<OptionItem> subCategoryList = new ArrayList<>();
    private List<OptionItem> brandList = new ArrayList<>();
    private List<OptionItem> unitList = new ArrayList<>();

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
        etAliasNameTeramedik = view.findViewById(R.id.editTextAliasNameTeramedik);
        etAliasNameHamster = view.findViewById(R.id.editTextAliasNameHamster);
        etSerial = view.findViewById(R.id.editTextSerial);
        etDesc = view.findViewById(R.id.editTextDescription);
        acOwnership = view.findViewById(R.id.autoCompleteOwnership);
        acCondition = view.findViewById(R.id.autoCompleteCondition);
        acCategory = view.findViewById(R.id.autoCompleteCategory);
        acSubCategory = view.findViewById(R.id.autoCompleteSubCategory);
        acBrand = view.findViewById(R.id.autoCompleteBrand);
        acUnit = view.findViewById(R.id.autoCompleteUnit);

    }

    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null) return;
            isProgrammaticChange = true;
            etCode.setText(asset.getCode());
            etName.setText(asset.getName());
            etAliasNameTeramedik.setText(asset.getAliasNameTeramedik());
            etAliasNameHamster.setText(asset.getAliasNameHamster());
            etSerial.setText(asset.getSerialNumber());
            etDesc.setText(asset.getDescription());
            acOwnership.setText(asset.getOwnership(), false);
            acCondition.setText(asset.getCondition(), false);
            if (asset.getCategory() != null) acCategory.setText(asset.getCategory().getName(), false);
            if (asset.getSubcategory() != null) acSubCategory.setText(asset.getSubcategory().getName(), false);
            if (asset.getBrand() != null) acBrand.setText(asset.getBrand().getName(), false);

            if (asset.getSubcategory() != null) {
                acSubCategory.setText(asset.getSubcategory().getName(), false);
                viewModel.fetchUnitOptions(asset.getSubcategory().getId());
            }
            acUnit.setText(asset.getUnit(), false);

            isProgrammaticChange = false;
        });

        viewModel.getCategoryOptions().observe(getViewLifecycleOwner(), options -> populateDropdown(acCategory, options, categoryList));
        viewModel.getSubCategoryOptions().observe(getViewLifecycleOwner(), options -> populateDropdown(acSubCategory, options, subCategoryList));
        viewModel.getBrandOptions().observe(getViewLifecycleOwner(), options -> populateDropdown(acBrand, options, brandList));
        viewModel.getUnitOptions().observe(getViewLifecycleOwner(), options -> populateDropdown(acUnit, options, unitList));

        viewModel.getOwnershipOptions().observe(getViewLifecycleOwner(), options -> populateStringDropdown(acOwnership, options));
        viewModel.getConditionOptions().observe(getViewLifecycleOwner(), options -> populateStringDropdown(acCondition, options));
    }

    private void setupInputListeners() {
        etName.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setName(text))));
        etAliasNameTeramedik.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setAliasNameTeramedik(text))));
        etAliasNameHamster.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setAliasNameHamster(text))));
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
            acUnit.setText("", false);
            viewModel.updateField(req -> req.setUnitId(null));
            viewModel.fetchUnitOptions(null);
        });

        acSubCategory.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selected = subCategoryList.get(position);
            viewModel.updateField(req -> req.setSubcategoryId(selected.getId()));

            viewModel.fetchUnitOptions(selected.getId());

            acUnit.setText("", false);
            viewModel.updateField(req -> req.setUnitId(null));
        });

        acUnit.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selected = unitList.get(position);
            viewModel.updateField(req -> req.setUnitId(selected.getId()));
        });

        acBrand.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selected = brandList.get(position);
            viewModel.updateField(req -> req.setBrandId(selected.getId()));
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
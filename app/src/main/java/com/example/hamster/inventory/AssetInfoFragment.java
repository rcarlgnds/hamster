// File: app/src/main/java/com/example/hamster/inventory/AssetInfoFragment.java
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
import java.util.ArrayList;
import java.util.List;

public class AssetInfoFragment extends Fragment implements FragmentDataCollector {
    private AssetDetailViewModel viewModel;
    private TextInputEditText etCode, etCode2, etCode3, etName, etType, etSerial, etTotal, etDesc;
    private AutoCompleteTextView acOwnership, acCategory, acSubCategory, acUnit, acBrand, acCondition, acParent;

    private List<OptionItem> categoryList = new ArrayList<>();
    private List<OptionItem> subCategoryList = new ArrayList<>();
    private List<OptionItem> brandList = new ArrayList<>();
    private List<OptionItem> parentAssetList = new ArrayList<>();

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
        setupDropdownListeners();
    }

    @Override
    public void collectDataForSave() {
        UpdateAssetRequest partialRequest = new UpdateAssetRequest();

        String name = etName.getText().toString();
        String code = etCode.getText().toString();

        // Validasi UI: hanya code & name wajib diisi
        if (TextUtils.isEmpty(code)) {
            etCode.setError("Asset Code wajib diisi");
        } else {
            etCode.setError(null);
        }
        if (TextUtils.isEmpty(name)) {
            etName.setError("Asset Name wajib diisi");
        } else {
            etName.setError(null);
        }

        partialRequest.setName(name);
        partialRequest.setCode(code);
        partialRequest.setCode2(etCode2.getText().toString());
        partialRequest.setCode3(etCode3.getText().toString());
        partialRequest.setType(etType.getText().toString());
        partialRequest.setSerialNumber(etSerial.getText().toString());
        partialRequest.setDescription(etDesc.getText().toString());

        if (!TextUtils.isEmpty(etTotal.getText().toString())) {
            try {
                partialRequest.setTotal(Integer.parseInt(etTotal.getText().toString()));
            } catch (NumberFormatException e) {
                partialRequest.setTotal(0);
            }
        }

        partialRequest.setOwnership(acOwnership.getText().toString());
        partialRequest.setUnit(acUnit.getText().toString());
        partialRequest.setCondition(acCondition.getText().toString());

        partialRequest.setCategoryId(getSelectedId(acCategory, categoryList));
        partialRequest.setSubcategoryId(getSelectedId(acSubCategory, subCategoryList));
        partialRequest.setBrandId(getSelectedId(acBrand, brandList));
        partialRequest.setParentId(getSelectedId(acParent, parentAssetList));

        viewModel.updateAssetInfoData(partialRequest);
    }

    private void initializeViews(View view) {
        etCode = view.findViewById(R.id.editTextCode);
        etCode2 = view.findViewById(R.id.editTextCode2);
        etCode3 = view.findViewById(R.id.editTextCode3);
        etName = view.findViewById(R.id.editTextName);
        etType = view.findViewById(R.id.editTextType);
        etSerial = view.findViewById(R.id.editTextSerial);
        etTotal = view.findViewById(R.id.editTextTotal);
        etDesc = view.findViewById(R.id.editTextDescription);
        acOwnership = view.findViewById(R.id.autoCompleteOwnership);
        acUnit = view.findViewById(R.id.autoCompleteUnit);
        acCondition = view.findViewById(R.id.autoCompleteCondition);
        acCategory = view.findViewById(R.id.autoCompleteCategory);
        acSubCategory = view.findViewById(R.id.autoCompleteSubCategory);
        acBrand = view.findViewById(R.id.autoCompleteBrand);
        acParent = view.findViewById(R.id.autoCompleteParent);
    }

    private void setupDropdownListeners() {
        acCategory.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selected = (OptionItem) parent.getItemAtPosition(position);
            viewModel.fetchSubCategoryOptions(selected.getId());
            acSubCategory.setText("", false);
        });
    }

    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null) return;
            etCode.setText(asset.getCode());
            etCode2.setText(asset.getCode2());
            etCode3.setText(asset.getCode3());
            etName.setText(asset.getName());
            etType.setText(asset.getType());
            etSerial.setText(asset.getSerialNumber());
            if(asset.getTotal() != null) etTotal.setText(String.valueOf(asset.getTotal()));
            etDesc.setText(asset.getDescription());
            acOwnership.setText(asset.getOwnership(), false);
            acUnit.setText(asset.getUnit(), false);
            acCondition.setText(asset.getCondition(), false);
            if (asset.getCategory() != null) acCategory.setText(asset.getCategory().getName(), false);
            if (asset.getSubcategory() != null) acSubCategory.setText(asset.getSubcategory().getName(), false);
            if (asset.getBrand() != null) acBrand.setText(asset.getBrand().getName(), false);
            if (asset.getParent() != null) acParent.setText(asset.getParent().getName(), false);
        });

        viewModel.getCategoryOptions().observe(getViewLifecycleOwner(), options -> populateDropdown(acCategory, options, categoryList));
        viewModel.getSubCategoryOptions().observe(getViewLifecycleOwner(), options -> populateDropdown(acSubCategory, options, subCategoryList));
        viewModel.getBrandOptions().observe(getViewLifecycleOwner(), options -> populateDropdown(acBrand, options, brandList));
        viewModel.getParentAssetOptions().observe(getViewLifecycleOwner(), options -> populateDropdown(acParent, options, parentAssetList));
        viewModel.getOwnershipOptions().observe(getViewLifecycleOwner(), options -> populateStringDropdown(acOwnership, options));
        viewModel.getConditionOptions().observe(getViewLifecycleOwner(), options -> populateStringDropdown(acCondition, options));
        viewModel.getUnitOptions().observe(getViewLifecycleOwner(), options -> populateStringDropdown(acUnit, options));
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
}
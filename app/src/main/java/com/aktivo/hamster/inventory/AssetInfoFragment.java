package com.aktivo.hamster.inventory;

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
import com.aktivo.hamster.R;
import com.aktivo.hamster.data.model.OptionItem;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class AssetInfoFragment extends Fragment {
    private AssetDetailViewModel viewModel;
    private TextInputEditText etCode, etCode2, etCode3, etName, etSerial, etDesc, etAliasNameTeramedik, etAliasNameHamster, etType, etTotal;
    private TextInputLayout tilOwnership, tilCategory, tilSubcategory, tilBrand, tilCondition, tilUnit, tilParent;
    private AutoCompleteTextView acOwnership, acCondition, acCategory, acSubCategory, acBrand, acUnit, acParent;
    private List<String> ownershipList = new ArrayList<>();
    private List<String> conditionList = new ArrayList<>();
    private List<OptionItem> categoryList = new ArrayList<>();
    private List<OptionItem> subCategoryList = new ArrayList<>();
    private List<OptionItem> brandList = new ArrayList<>();
    private List<OptionItem> unitList = new ArrayList<>();
    private List<OptionItem> parentAssetList = new ArrayList<>();

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
        etCode2 = view.findViewById(R.id.editTextCode2);
        etCode3 = view.findViewById(R.id.editTextCode3);
        etName = view.findViewById(R.id.editTextName);
        etAliasNameTeramedik = view.findViewById(R.id.editTextAliasNameTeramedik);
        etAliasNameHamster = view.findViewById(R.id.editTextAliasNameHamster);
        etSerial = view.findViewById(R.id.editTextSerial);
        etDesc = view.findViewById(R.id.editTextDescription);
        etType = view.findViewById(R.id.editTextType);
        etTotal = view.findViewById(R.id.editTextTotal);
        tilOwnership = view.findViewById(R.id.tilOwnership);
        acOwnership = view.findViewById(R.id.autoCompleteOwnership);
        tilCondition = view.findViewById(R.id.tilCondition);
        acCondition = view.findViewById(R.id.autoCompleteCondition);
        tilCategory = view.findViewById(R.id.tilCategory);
        acCategory = view.findViewById(R.id.autoCompleteCategory);
        tilSubcategory = view.findViewById(R.id.tilSubcategory);
        acSubCategory = view.findViewById(R.id.autoCompleteSubCategory);
        tilBrand = view.findViewById(R.id.tilBrand);
        acBrand = view.findViewById(R.id.autoCompleteBrand);
        tilUnit = view.findViewById(R.id.tilUnit);
        acUnit = view.findViewById(R.id.autoCompleteUnit);
        tilParent = view.findViewById(R.id.tilParent);
        acParent = view.findViewById(R.id.autoCompleteParent);
    }

    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null) return;
            isProgrammaticChange = true;
            etCode.setText(asset.getCode());
            etCode2.setText(asset.getCode2());
            etCode3.setText(asset.getCode3());
            etName.setText(asset.getName());
            etAliasNameTeramedik.setText(asset.getAliasNameTeramedik());
            etAliasNameHamster.setText(asset.getAliasNameHamster());
            etSerial.setText(asset.getSerialNumber());
            etDesc.setText(asset.getDescription());
            etType.setText(asset.getType());
            etTotal.setText(String.valueOf(asset.getTotal()));
            acOwnership.setText(asset.getOwnership(), false);
            acCondition.setText(asset.getCondition(), false);
            if (asset.getCategory() != null) acCategory.setText(asset.getCategory().getName(), false);
            if (asset.getSubcategory() != null) acSubCategory.setText(asset.getSubcategory().getName(), false);
            if (asset.getUnit() != null) acUnit.setText(asset.getUnit().getName(), false);
            if (asset.getBrand() != null) acBrand.setText(asset.getBrand().getName(), false);
            if (asset.getParent() != null) acParent.setText(asset.getParent().getName(), false);

            isProgrammaticChange = false;

            viewModel.getIsEditable().observe(getViewLifecycleOwner(), isEditable -> {
                if(Boolean.TRUE.equals(isEditable)) {
                    etCode.setEnabled(false);
                    etCode2.setEnabled(false);
                    etCode3.setEnabled(false);
                    etName.setEnabled(false);
                    etAliasNameTeramedik.setEnabled(false);
                    etAliasNameHamster.setEnabled(false);
                    etSerial.setEnabled(false);
                    etDesc.setEnabled(isEditable);
                    etType.setEnabled(isEditable);
                    etTotal.setEnabled(isEditable);

                    viewModel.getOwnershipOptions().observe(getViewLifecycleOwner(), options -> {
                        ownershipList.clear();
                        ownershipList.addAll(options);
                        populateStringDropdown(acOwnership, ownershipList);
                    });
                    tilOwnership.setEnabled(isEditable);
                    acOwnership.setEnabled(isEditable);
                    acOwnership.setFocusable(isEditable);
                    acOwnership.setClickable(isEditable);
                    acOwnership.setLongClickable(isEditable);
                    acOwnership.setCursorVisible(isEditable);
                    acOwnership.setOnTouchListener(null);

                    viewModel.getConditionOptions().observe(getViewLifecycleOwner(), options -> {
                        conditionList.clear();
                        conditionList.addAll(options);
                        populateStringDropdown(acCondition, conditionList);
                    });
                    tilCondition.setEnabled(isEditable);
                    acCondition.setEnabled(isEditable);
                    acCondition.setFocusable(isEditable);
                    acCondition.setClickable(isEditable);
                    acCondition.setLongClickable(isEditable);
                    acCondition.setCursorVisible(isEditable);
                    acCondition.setOnTouchListener(null);

                    viewModel.getCategoryOptions().observe(getViewLifecycleOwner(), options -> {
                        categoryList.clear();
                        categoryList.addAll(options);
                        populateDropdown(acCategory, options, categoryList);
                    });
                    tilCategory.setEnabled(isEditable);
                    acCategory.setEnabled(isEditable);
                    acCategory.setFocusable(isEditable);
                    acCategory.setFocusableInTouchMode(true);
                    acCategory.setClickable(isEditable);
                    acCategory.setLongClickable(isEditable);
                    acCategory.setCursorVisible(isEditable);
                    acCategory.setOnTouchListener(null);

                    viewModel.getSubCategoryOptions().observe(getViewLifecycleOwner(), options -> {
                        subCategoryList.clear();
                        subCategoryList.addAll(options);
                        populateDropdown(acSubCategory, options, subCategoryList);
                    });
                    tilSubcategory.setEnabled(isEditable);
                    acSubCategory.setEnabled(isEditable);
                    acSubCategory.setFocusable(isEditable);
                    acSubCategory.setClickable(isEditable);
                    acSubCategory.setLongClickable(isEditable);
                    acSubCategory.setCursorVisible(isEditable);
                    acSubCategory.setOnTouchListener(null);

                    viewModel.getBrandOptions().observe(getViewLifecycleOwner(), options -> {
                        brandList.clear();
                        brandList.addAll(options);
                        populateDropdown(acBrand, options, brandList);
                    });
                    tilBrand.setEnabled(isEditable);
                    acBrand.setEnabled(isEditable);
                    acBrand.setFocusable(isEditable);
                    acBrand.setClickable(isEditable);
                    acBrand.setLongClickable(isEditable);
                    acBrand.setCursorVisible(isEditable);
                    acBrand.setOnTouchListener(null);

                    viewModel.getUnitOptions().observe(getViewLifecycleOwner(), options -> {
                        unitList.clear();
                        unitList.addAll(options);
                        populateDropdown(acUnit, options, unitList);
                    });
                    tilUnit.setEnabled(isEditable);
                    acUnit.setEnabled(isEditable);
                    acUnit.setFocusable(isEditable);
                    acUnit.setClickable(isEditable);
                    acUnit.setLongClickable(isEditable);
                    acUnit.setCursorVisible(isEditable);
                    acUnit.setOnTouchListener(null);

                    viewModel.getParentAssetOptions().observe(getViewLifecycleOwner(), options -> {
                        parentAssetList.clear();
                        parentAssetList.addAll(options);
                        populateDropdown(acParent, options, parentAssetList);
                    });
                    tilParent.setEnabled(isEditable);
                    acParent.setEnabled(isEditable);
                    acParent.setFocusable(isEditable);
                    acParent.setClickable(isEditable);
                    acParent.setLongClickable(isEditable);
                    acParent.setCursorVisible(isEditable);
                    acParent.setOnTouchListener(null);

                } else {
                    etCode.setEnabled(isEditable);
                    etCode2.setEnabled(isEditable);
                    etCode3.setEnabled(isEditable);
                    etName.setEnabled(isEditable);
                    etAliasNameTeramedik.setEnabled(isEditable);
                    etAliasNameHamster.setEnabled(isEditable);
                    etSerial.setEnabled(isEditable);
                    etDesc.setEnabled(isEditable);
                    etType.setEnabled(isEditable);
                    etTotal.setEnabled(isEditable);

                    tilOwnership.setEnabled(isEditable);
                    acOwnership.setEnabled(isEditable);
                    acOwnership.setFocusable(isEditable);
                    acOwnership.setClickable(isEditable);
                    acOwnership.setLongClickable(isEditable);
                    acOwnership.setCursorVisible(isEditable);
                    acOwnership.setOnTouchListener((v, event) -> true);
                    acOwnership.setAdapter(null);
                    acOwnership.dismissDropDown();

                    tilCondition.setEnabled(isEditable);
                    acCondition.setEnabled(isEditable);
                    acCondition.setFocusable(isEditable);
                    acCondition.setClickable(isEditable);
                    acCondition.setLongClickable(isEditable);
                    acCondition.setCursorVisible(isEditable);
                    acCondition.setOnTouchListener((v, event) -> true);
                    acCondition.setAdapter(null);
                    acCondition.showDropDown();

                    tilCategory.setEnabled(isEditable);
                    acCategory.setEnabled(isEditable);
                    acCategory.setFocusable(isEditable);
                    acCategory.setClickable(isEditable);
                    acCategory.setLongClickable(isEditable);
                    acCategory.setCursorVisible(isEditable);
                    acCategory.setOnTouchListener((v, event) -> true);
                    acCategory.setAdapter(null);
                    acCategory.showDropDown();

                    tilSubcategory.setEnabled(isEditable);
                    acSubCategory.setEnabled(isEditable);
                    acSubCategory.setFocusable(isEditable);
                    acSubCategory.setClickable(isEditable);
                    acSubCategory.setLongClickable(isEditable);
                    acSubCategory.setCursorVisible(isEditable);
                    acSubCategory.setOnTouchListener((v, event) -> true);
                    acSubCategory.setAdapter(null);
                    acSubCategory.dismissDropDown();

                    tilBrand.setEnabled(isEditable);
                    acBrand.setEnabled(isEditable);
                    acBrand.setFocusable(isEditable);
                    acBrand.setClickable(isEditable);
                    acBrand.setLongClickable(isEditable);
                    acBrand.setCursorVisible(isEditable);
                    acBrand.setOnTouchListener((v, event) -> true);
                    acBrand.setAdapter(null);
                    acBrand.dismissDropDown();

                    tilUnit.setEnabled(isEditable);
                    acUnit.setEnabled(isEditable);
                    acUnit.setFocusable(isEditable);
                    acUnit.setClickable(isEditable);
                    acUnit.setLongClickable(isEditable);
                    acUnit.setCursorVisible(isEditable);
                    acUnit.setOnTouchListener((v, event) -> true);
                    acUnit.setAdapter(null);
                    acUnit.dismissDropDown();

                    tilParent.setEnabled(isEditable);
                    acParent.setEnabled(isEditable);
                    acParent.setFocusable(isEditable);
                    acParent.setClickable(isEditable);
                    acParent.setLongClickable(isEditable);
                    acParent.setCursorVisible(isEditable);
                    acParent.setOnTouchListener((v, event) -> true);
                    acParent.setAdapter(null);
                    acParent.dismissDropDown();
                }
            });
        });
    }

    private void setupInputListeners() {
        etName.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setName(text))));
        etAliasNameTeramedik.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setAliasNameTeramedik(text))));
        etAliasNameHamster.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setAliasNameHamster(text))));
        etCode.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setCode(text))));
        etSerial.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setSerialNumber(text))));
        etDesc.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setDescription(text))));
        etTotal.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setTotal(Integer.valueOf(text)))));
        etType.addTextChangedListener(new SimpleTextWatcher(text -> viewModel.updateField(req -> req.setType(text))));

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

        acParent.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selected = parentAssetList.get(position);
            viewModel.updateField(req -> req.setParentId(selected.getId()));
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
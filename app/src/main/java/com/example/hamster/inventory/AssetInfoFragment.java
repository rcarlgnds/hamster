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
import com.example.hamster.data.model.UpdateAssetRequest;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class AssetInfoFragment extends Fragment {
    private AssetDetailViewModel viewModel;
    private TextInputEditText editTextCode, editTextName, editTextSerial, editTextTotal, editTextDescription;
    private AutoCompleteTextView autoCompleteOwnership, autoCompleteCategory, autoCompleteSubCategory, autoCompleteUnit, autoCompleteBrand, autoCompleteCondition;

    private List<OptionItem> categoryList = new ArrayList<>();
    private List<OptionItem> subCategoryList = new ArrayList<>();
    private List<OptionItem> brandList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);

        initializeViews(view);
        setupObservers();
        setupDropdownListeners();

        // Panggil SEMUA fetcher untuk data dropdown
        viewModel.fetchCategoryOptions();
        viewModel.fetchBrandOptions();
        viewModel.fetchOwnershipOptions();
        viewModel.fetchConditionOptions();
        viewModel.fetchUnitOptions();
    }

    private void initializeViews(View view) {
        editTextCode = view.findViewById(R.id.editTextCode);
        editTextName = view.findViewById(R.id.editTextName);
        editTextSerial = view.findViewById(R.id.editTextSerial);
        editTextTotal = view.findViewById(R.id.editTextTotal);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        autoCompleteOwnership = view.findViewById(R.id.autoCompleteOwnership);
        autoCompleteUnit = view.findViewById(R.id.autoCompleteUnit);
        autoCompleteCondition = view.findViewById(R.id.autoCompleteCondition);
        autoCompleteCategory = view.findViewById(R.id.autoCompleteCategory);
        autoCompleteSubCategory = view.findViewById(R.id.autoCompleteSubCategory);
        autoCompleteBrand = view.findViewById(R.id.autoCompleteBrand);
    }

    private void setupDropdownListeners() {
        autoCompleteCategory.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selectedCategory = (OptionItem) parent.getItemAtPosition(position);
            if (selectedCategory != null) {
                viewModel.fetchSubCategoryOptions(selectedCategory.getId());
                autoCompleteSubCategory.setText("", false);
            }
        });
    }

    private void setupObservers() {
        // Observer untuk Data Aset Utama
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null) return;
            editTextCode.setText(asset.getCode());
            editTextName.setText(asset.getName());
            editTextSerial.setText(asset.getSerialNumber());
            editTextTotal.setText(String.valueOf(asset.getTotal()));
            editTextDescription.setText(asset.getDescription());

            autoCompleteOwnership.setText(asset.getOwnership(), false);
            autoCompleteUnit.setText(asset.getUnit(), false);
            autoCompleteCondition.setText(asset.getCondition(), false);

            if (asset.getCategory() != null) {
                autoCompleteCategory.setText(asset.getCategory().getName(), false);
                viewModel.fetchSubCategoryOptions(asset.getCategory().getId());
            }
            if (asset.getSubcategory() != null) {
                autoCompleteSubCategory.setText(asset.getSubcategory().getName(), false);
            }
            if (asset.getBrand() != null) {
                autoCompleteBrand.setText(asset.getBrand().getName(), false);
            }
        });

        // Observer untuk Opsi Kategori
        viewModel.getCategoryOptions().observe(getViewLifecycleOwner(), options -> {
            if (options == null) return;
            categoryList = options;
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryList);
            autoCompleteCategory.setAdapter(adapter);
        });

        // Observer untuk Opsi Sub-Kategori
        viewModel.getSubCategoryOptions().observe(getViewLifecycleOwner(), options -> {
            if (options == null) return;
            subCategoryList = options;
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, subCategoryList);
            autoCompleteSubCategory.setAdapter(adapter);
        });

        // Observer untuk Opsi Brand
        viewModel.getBrandOptions().observe(getViewLifecycleOwner(), options -> {
            if (options == null) return;
            brandList = options;
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, brandList);
            autoCompleteBrand.setAdapter(adapter);
        });

        // Observer untuk dropdown yang sebelumnya statis
        viewModel.getOwnershipOptions().observe(getViewLifecycleOwner(), options -> {
            if (options == null) return;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, options);
            autoCompleteOwnership.setAdapter(adapter);
        });

        viewModel.getConditionOptions().observe(getViewLifecycleOwner(), options -> {
            if (options == null) return;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, options);
            autoCompleteCondition.setAdapter(adapter);
        });

        viewModel.getUnitOptions().observe(getViewLifecycleOwner(), options -> {
            if (options == null) return;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, options);
            autoCompleteUnit.setAdapter(adapter);
        });
    }

    public UpdateAssetRequest getUpdatedAssetData() {
        // Implementasi untuk mengumpulkan data dari UI
        UpdateAssetRequest request = new UpdateAssetRequest();
        // ... (isi request dengan data dari semua field)
        return request;
    }
}
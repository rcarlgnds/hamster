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
import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.OptionItem;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AssetInfoFragment extends Fragment {
    private AssetDetailViewModel viewModel;

    private TextInputEditText editTextCode, editTextName, editTextSerial, editTextDescription, editTextTotal;
    private AutoCompleteTextView autoCompleteCategory, autoCompleteSubCategory, autoCompleteBrand, autoCompleteCondition, autoCompleteOwnership, autoCompleteUnit;
    private List<OptionItem> categoryList;

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

        viewModel.fetchCategoryOptions();
    }

    private void initializeViews(View view) {
        editTextName = view.findViewById(R.id.editTextName);
        editTextCode = view.findViewById(R.id.editTextCode);
        editTextSerial = view.findViewById(R.id.editTextSerial);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextTotal = view.findViewById(R.id.editTextTotal);
        autoCompleteCategory = view.findViewById(R.id.autoCompleteCategory);
        autoCompleteSubCategory = view.findViewById(R.id.autoCompleteSubCategory);
        autoCompleteBrand = view.findViewById(R.id.autoCompleteBrand);
        autoCompleteCondition = view.findViewById(R.id.autoCompleteCondition);
        autoCompleteOwnership = view.findViewById(R.id.autoCompleteOwnership);
        autoCompleteUnit = view.findViewById(R.id.autoCompleteUnit);
    }

    private void setupObservers() {
        // Observer untuk data utama aset
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset != null) {
                editTextName.setText(asset.getName());
                editTextCode.setText(asset.getCode());
                // editTextSerial.setText(asset.getSerialNumber()); // Lengkapi model Asset.java jika perlu
                // editTextDescription.setText(asset.getDescription());
                // editTextTotal.setText(String.valueOf(asset.getTotal()));
                autoCompleteCondition.setText(asset.getCondition(), false);
                if (asset.getCategory() != null) {
                    autoCompleteCategory.setText(asset.getCategory().getName(), false);
                    // Setelah kategori terisi, ambil data sub-kategori
                    viewModel.fetchSubCategoryOptions(asset.getCategory().getId());
                }
                if (asset.getBrand() != null) {
                    autoCompleteBrand.setText(asset.getBrand().getName(), false);
                }
            }
        });

        // Observer untuk mengisi dropdown kategori
        viewModel.getCategoryOptions().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                this.categoryList = categories; // Simpan daftar kategori
                ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categories);
                autoCompleteCategory.setAdapter(adapter);
            }
        });

        // Observer untuk mengisi dropdown sub-kategori
        viewModel.getSubCategoryOptions().observe(getViewLifecycleOwner(), subCategories -> {
            if (subCategories != null) {
                ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, subCategories);
                autoCompleteSubCategory.setAdapter(adapter);
                // Set nilai sub-kategori jika data aset sudah ada
                Asset currentAsset = viewModel.getAssetData().getValue();
                if (currentAsset != null && currentAsset.getSubcategory() != null) {
                    autoCompleteSubCategory.setText(currentAsset.getSubcategory().getName(), false);
                }
            }
        });

        // Listener untuk dropdown kategori
        autoCompleteCategory.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selectedCategory = (OptionItem) parent.getItemAtPosition(position);
            // Saat kategori diubah, ambil sub-kategori yang sesuai
            viewModel.fetchSubCategoryOptions(selectedCategory.getId());
            autoCompleteSubCategory.setText(""); // Kosongkan sub-kategori
        });
    }
}
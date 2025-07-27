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
import java.util.List;

public class AssetInfoFragment extends Fragment {
    private AssetDetailViewModel viewModel;
    private TextInputEditText editTextCode, editTextName, editTextSerial, editTextTotal, editTextDescription;
    private AutoCompleteTextView autoCompleteOwnership, autoCompleteCategory, autoCompleteSubCategory, autoCompleteUnit, autoCompleteBrand, autoCompleteCondition;

    private List<OptionItem> categoryList;
    private List<OptionItem> subCategoryList;
    private List<OptionItem> brandList; // Asumsi ada

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
        viewModel.fetchSubCategoryOptions();
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


    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset != null) {
                editTextCode.setText(asset.getCode());
                editTextName.setText(asset.getName());
                editTextSerial.setText(asset.getSerialNumber());
                editTextTotal.setText(String.valueOf(asset.getTotal()));
                editTextDescription.setText(asset.getDescription());
                autoCompleteOwnership.setText(asset.getOwnership(), false);
                autoCompleteUnit.setText(asset.getUnit(), false);
                autoCompleteCondition.setText(asset.getCondition(), false);
                if(asset.getCategory() != null) autoCompleteCategory.setText(asset.getCategory().getName(), false);
                if(asset.getSubcategory() != null) autoCompleteSubCategory.setText(asset.getSubcategory().getName(), false);
                if(asset.getBrand() != null) autoCompleteBrand.setText(asset.getBrand().getName(), false);
            }
        });

        viewModel.getCategoryOptions().observe(getViewLifecycleOwner(), options -> {
            categoryList = options;
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, options);
            autoCompleteCategory.setAdapter(adapter);
        });

        viewModel.getSubCategoryOptions().observe(getViewLifecycleOwner(), options -> {
            subCategoryList = options;
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, options);
            autoCompleteSubCategory.setAdapter(adapter);
        });
    }

    public UpdateAssetRequest getUpdatedAssetData() {
        UpdateAssetRequest request = new UpdateAssetRequest();
        request.setName(editTextName.getText().toString());
        request.setCode(editTextCode.getText().toString());
        request.setSerialNumber(editTextSerial.getText().toString());
        request.setCondition(autoCompleteCondition.getText().toString());
        request.setOwnership(autoCompleteOwnership.getText().toString());
        request.setTotal(Integer.parseInt(editTextTotal.getText().toString()));
        request.setUnit(autoCompleteUnit.getText().toString());
        request.setDescription(editTextDescription.getText().toString());

        // Dapatkan ID dari dropdown yang dipilih
        String selectedCategoryName = autoCompleteCategory.getText().toString();
        if (categoryList != null) {
            for (OptionItem item : categoryList) {
                if (item.getName().equals(selectedCategoryName)) {
                    request.setCategoryId(item.getId());
                    break;
                }
            }
        }
        // Lakukan hal yang sama untuk subCategoryList dan brandList
        return request;
    }
}
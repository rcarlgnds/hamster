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
import com.google.android.material.textfield.TextInputEditText;

public class AssetInfoFragment extends Fragment {
    private AssetDetailViewModel viewModel;
    private TextInputEditText editTextCode, editTextName, editTextSerial, editTextDescription;
    private AutoCompleteTextView autoCompleteCategory, autoCompleteBrand, autoCompleteCondition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);

        // Inisialisasi semua view
        editTextName = view.findViewById(R.id.editTextName);
        editTextCode = view.findViewById(R.id.editTextCode);
        autoCompleteCategory = view.findViewById(R.id.autoCompleteCategory);
        autoCompleteBrand = view.findViewById(R.id.autoCompleteBrand);
        autoCompleteCondition = view.findViewById(R.id.autoCompleteCondition);

        // Setup dropdowns (contoh dummy data)
        String[] categories = {"Medical", "IT", "Non Medical"};
        autoCompleteCategory.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categories));

        String[] conditions = {"Good", "Fair", "Broken"};
        autoCompleteCondition.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, conditions));

        // Mengisi data dari ViewModel ke UI
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset != null) {
                editTextName.setText(asset.getName());
                editTextCode.setText(asset.getCode());
                autoCompleteCondition.setText(asset.getCondition(), false);
                if (asset.getCategory() != null) {
                    autoCompleteCategory.setText(asset.getCategory().getName(), false);
                }
                if (asset.getBrand() != null) {
                    autoCompleteBrand.setText(asset.getBrand().getName(), false);
                }
            }
        });
    }
}
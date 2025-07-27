package com.example.hamster.inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.hamster.R;
import com.google.android.material.textfield.TextInputEditText;

public class AssetMaintenanceFragment extends Fragment {
    private AssetDetailViewModel viewModel;
    private TextInputEditText editTextProcurementDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle s) {
        return inflater.inflate(R.layout.fragment_asset_maintenance, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);
        editTextProcurementDate = view.findViewById(R.id.editTextProcurementDate);

        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset != null && asset.getProcurementDate() != null) {
                // Konversi dari Unix timestamp ke format yang lebih mudah dibaca jika perlu
                editTextProcurementDate.setText(String.valueOf(asset.getProcurementDate()));
            } else {
                editTextProcurementDate.setText("-");
            }
        });
    }
}
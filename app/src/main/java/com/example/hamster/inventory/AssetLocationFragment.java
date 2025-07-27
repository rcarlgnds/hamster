package com.example.hamster.inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.hamster.R;
import com.example.hamster.data.model.*;

public class AssetLocationFragment extends Fragment {
    private AssetDetailViewModel viewModel;
    private AutoCompleteTextView autoCompleteHospital, autoCompleteBuilding, autoCompleteFloor, autoCompleteRoom, autoCompleteSubRoom, autoCompleteDivision, autoCompleteUnit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle s) {
        return inflater.inflate(R.layout.fragment_asset_location, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);
        initializeViews(view);
        setupObserver();
    }

    private void initializeViews(View view) {
        autoCompleteHospital = view.findViewById(R.id.autoCompleteHospital);
        autoCompleteBuilding = view.findViewById(R.id.autoCompleteBuilding);
        autoCompleteFloor = view.findViewById(R.id.autoCompleteFloor);
        autoCompleteRoom = view.findViewById(R.id.autoCompleteRoom);
        autoCompleteSubRoom = view.findViewById(R.id.autoCompleteSubRoom);
        autoCompleteDivision = view.findViewById(R.id.autoCompleteDivision);
        autoCompleteUnit = view.findViewById(R.id.autoCompleteUnit);
    }

    private void setupObserver() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null) return;
            if (asset.getRoom() != null) {
                autoCompleteRoom.setText(asset.getRoom().getName(), false);
                if (asset.getRoom().getFloor() != null) {
                    autoCompleteFloor.setText(asset.getRoom().getFloor().getName(), false);
                    if (asset.getRoom().getFloor().getBuilding() != null) {
                        autoCompleteBuilding.setText(asset.getRoom().getFloor().getBuilding().getName(), false);
                        if (asset.getRoom().getFloor().getBuilding().getHospital() != null) {
                            autoCompleteHospital.setText(asset.getRoom().getFloor().getBuilding().getHospital().getName(), false);
                        }
                    }
                }
            }
            if (asset.getSubRoom() != null) autoCompleteSubRoom.setText(asset.getSubRoom().getName(), false);
            if (asset.getResponsibleDivision() != null) autoCompleteDivision.setText(asset.getResponsibleDivision().getName(), false);
            if (asset.getResponsibleWorkingUnit() != null) autoCompleteUnit.setText(asset.getResponsibleWorkingUnit().getName(), false);
        });
    }
}
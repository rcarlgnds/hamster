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
import com.example.hamster.data.model.Room;

public class AssetLocationFragment extends Fragment {
    private AssetDetailViewModel viewModel;
    private AutoCompleteTextView autoCompleteHospital;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);

        autoCompleteHospital = view.findViewById(R.id.autoCompleteHospital);

        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset != null && asset.getRoom() != null) {
                // Asumsi data hospital ada di dalam room. Anda bisa sesuaikan ini.
                // Room room = asset.getRoom();
                // if (room.getFloor() != null && room.getFloor().getBuilding() != null && room.getFloor().getBuilding().getHospital() != null) {
                //     autoCompleteHospital.setText(room.getFloor().getBuilding().getHospital().getName(), false);
                // }
            }
        });
    }
}
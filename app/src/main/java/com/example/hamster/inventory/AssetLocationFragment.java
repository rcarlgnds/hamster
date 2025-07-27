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
import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.Building;
import com.example.hamster.data.model.Division;
import com.example.hamster.data.model.Floor;
import com.example.hamster.data.model.Hospital;
import com.example.hamster.data.model.Room;
import com.example.hamster.data.model.SubRoom;
import com.example.hamster.data.model.WorkingUnit;

public class AssetLocationFragment extends Fragment {

    private AssetDetailViewModel viewModel;

    // Deklarasi untuk semua View dari XML Anda
    private AutoCompleteTextView autoCompleteHospital, autoCompleteBuilding, autoCompleteFloor;
    private AutoCompleteTextView autoCompleteRoom, autoCompleteSubRoom, autoCompleteDivision;
    private AutoCompleteTextView autoCompleteUnit, autoCompleteUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout yang sudah benar
        return inflater.inflate(R.layout.fragment_asset_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);



        // Inisialisasi semua View
        initializeViews(view);

        // Mengisi data dari ViewModel ke UI
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
        autoCompleteUser = view.findViewById(R.id.autoCompleteUser);
    }

    private void setupObserver() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null) return;

            // Mengisi data lokasi dengan aman (mengecek null di setiap level)
            Room room = asset.getRoom();
            if (room != null) {
                autoCompleteRoom.setText(room.getName(), false);
                Floor floor = room.getFloor();
                if (floor != null) {
                    autoCompleteFloor.setText(floor.getName(), false);
                    Building building = floor.getBuilding();
                    if (building != null) {
                        autoCompleteBuilding.setText(building.getName(), false);
                        Hospital hospital = building.getHospital();
                        if (hospital != null) {
                            autoCompleteHospital.setText(hospital.getName(), false);
                        }
                    }
                }
            }

            SubRoom subRoom = asset.getSubRoom();
            if (subRoom != null) {
                autoCompleteSubRoom.setText(subRoom.getName(), false);
            }

            Division division = asset.getResponsibleDivision();
            if (division != null) {
                autoCompleteDivision.setText(division.getName(), false);
            }

            WorkingUnit workingUnit = asset.getResponsibleWorkingUnit();
            if (workingUnit != null) {
                autoCompleteUnit.setText(workingUnit.getName(), false);
            }

            // `responsibleUser` null di API Anda, jadi kita kosongkan
            autoCompleteUser.setText("", false);
        });
    }
}
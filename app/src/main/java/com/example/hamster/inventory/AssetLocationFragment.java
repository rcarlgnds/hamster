// File: app/src/main/java/com/example/hamster/inventory/AssetLocationFragment.java
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import com.example.hamster.R;
import com.example.hamster.data.model.OptionItem;
import com.example.hamster.data.model.UpdateAssetRequest;
import java.util.ArrayList;
import java.util.List;

public class AssetLocationFragment extends Fragment implements FragmentDataCollector {
    private AssetDetailViewModel viewModel;
    private AutoCompleteTextView acHospital, acBuilding, acFloor, acRoom, acSubRoom, acDivision, acUnit, acUser;

    private List<OptionItem> hospitalList = new ArrayList<>();
    private List<OptionItem> buildingList = new ArrayList<>();
    private List<OptionItem> floorList = new ArrayList<>();
    private List<OptionItem> roomList = new ArrayList<>();
    private List<OptionItem> subRoomList = new ArrayList<>();
    private List<OptionItem> divisionList = new ArrayList<>();
    private List<OptionItem> workingUnitList = new ArrayList<>();
    private List<OptionItem> userList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle s) {
        return inflater.inflate(R.layout.fragment_asset_location, c, false);
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

        partialRequest.setRoomId(getSelectedId(acRoom, roomList));
        partialRequest.setSubRoomId(getSelectedId(acSubRoom, subRoomList));
        partialRequest.setResponsibleDivisionId(getSelectedId(acDivision, divisionList));
        partialRequest.setResponsibleWorkingUnitId(getSelectedId(acUnit, workingUnitList));
        partialRequest.setResponsibleUserId(getSelectedId(acUser, userList));

        viewModel.updateLocationData(partialRequest);
    }

    private void initializeViews(View view) {
        acHospital = view.findViewById(R.id.autoCompleteHospital);
        acBuilding = view.findViewById(R.id.autoCompleteBuilding);
        acFloor = view.findViewById(R.id.autoCompleteFloor);
        acRoom = view.findViewById(R.id.autoCompleteRoom);
        acSubRoom = view.findViewById(R.id.autoCompleteSubRoom);
        acDivision = view.findViewById(R.id.autoCompleteDivision);
        acUnit = view.findViewById(R.id.autoCompleteUnit);
        acUser = view.findViewById(R.id.autoCompleteUser);
    }

    private void setupDropdownListeners() {
        acHospital.setOnItemClickListener((p, v, pos, id) -> {
            OptionItem selected = (OptionItem) p.getItemAtPosition(pos);
            viewModel.fetchBuildingOptions(selected.getId());
            clearAndDisable(acBuilding, acFloor, acRoom, acSubRoom);
        });
        acBuilding.setOnItemClickListener((p, v, pos, id) -> {
            OptionItem selected = (OptionItem) p.getItemAtPosition(pos);
            viewModel.fetchFloorOptions(selected.getId());
            clearAndDisable(acFloor, acRoom, acSubRoom);
        });
        acFloor.setOnItemClickListener((p, v, pos, id) -> {
            OptionItem selected = (OptionItem) p.getItemAtPosition(pos);
            viewModel.fetchRoomOptions(selected.getId());
            clearAndDisable(acRoom, acSubRoom);
        });
        acRoom.setOnItemClickListener((p, v, pos, id) -> {
            OptionItem selected = (OptionItem) p.getItemAtPosition(pos);
            viewModel.fetchSubRoomOptions(selected.getId());
            clearAndDisable(acSubRoom);
        });
    }

    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null) return;
            // Pre-fill data dan fetch data turunan
            if (asset.getRoom() != null && asset.getRoom().getFloor() != null && asset.getRoom().getFloor().getBuilding() != null && asset.getRoom().getFloor().getBuilding().getHospital() != null) {
                acHospital.setText(asset.getRoom().getFloor().getBuilding().getHospital().getName(), false);
                acBuilding.setText(asset.getRoom().getFloor().getBuilding().getName(), false);
                acFloor.setText(asset.getRoom().getFloor().getName(), false);
                acRoom.setText(asset.getRoom().getName(), false);

                // Fetch data turunan berdasarkan data yang sudah ada
                viewModel.fetchBuildingOptions(asset.getRoom().getFloor().getBuilding().getHospital().getId());
                viewModel.fetchFloorOptions(asset.getRoom().getFloor().getBuilding().getId());
                viewModel.fetchRoomOptions(asset.getRoom().getFloor().getId());
                viewModel.fetchSubRoomOptions(asset.getRoom().getId());
            }
            if (asset.getSubRoom() != null) acSubRoom.setText(asset.getSubRoom().getName(), false);
            if (asset.getResponsibleDivision() != null) acDivision.setText(asset.getResponsibleDivision().getName(), false);
            if (asset.getResponsibleWorkingUnit() != null) acUnit.setText(asset.getResponsibleWorkingUnit().getName(), false);
            if (asset.getResponsibleUser() != null) acUser.setText(asset.getResponsibleUser().getFirstName(), false);
        });

        observeAndPopulate(viewModel.getHospitalOptions(), acHospital, hospitalList);
        observeAndPopulate(viewModel.getBuildingOptions(), acBuilding, buildingList);
        observeAndPopulate(viewModel.getFloorOptions(), acFloor, floorList);
        observeAndPopulate(viewModel.getRoomOptions(), acRoom, roomList);
        observeAndPopulate(viewModel.getSubRoomOptions(), acSubRoom, subRoomList);
        observeAndPopulate(viewModel.getDivisionOptions(), acDivision, divisionList);
        observeAndPopulate(viewModel.getWorkingUnitOptions(), acUnit, workingUnitList);
        observeAndPopulate(viewModel.getUserOptions(), acUser, userList);
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

    private void observeAndPopulate(LiveData<List<OptionItem>> liveData, AutoCompleteTextView autoComplete, List<OptionItem> dataList) {
        liveData.observe(getViewLifecycleOwner(), options -> {
            if (options == null || getContext() == null) return;
            dataList.clear();
            dataList.addAll(options);
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, dataList);
            autoComplete.setAdapter(adapter);
            autoComplete.setEnabled(true);
        });
    }

    private void clearAndDisable(AutoCompleteTextView... views) {
        for (AutoCompleteTextView view : views) {
            view.setText("", false);
            view.setAdapter(null);
            view.setEnabled(false);
        }
    }
}
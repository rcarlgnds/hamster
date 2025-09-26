package com.aktivo.hamster.inventory;

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
import com.aktivo.hamster.R;
import com.aktivo.hamster.data.model.OptionItem;

import java.util.ArrayList;
import java.util.List;

public class AssetLocationFragment extends Fragment {
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
    private boolean isProgrammaticChange = false;

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
            OptionItem selected = hospitalList.get(pos);
            viewModel.fetchBuildingOptions(selected.getId());
            clearAndDisable(acBuilding, acFloor, acRoom, acSubRoom);
            viewModel.updateField(req -> req.setRoomId(null));
        });
        acBuilding.setOnItemClickListener((p, v, pos, id) -> {
            OptionItem selected = buildingList.get(pos);
            viewModel.fetchFloorOptions(selected.getId());
            clearAndDisable(acFloor, acRoom, acSubRoom);
        });
        acFloor.setOnItemClickListener((p, v, pos, id) -> {
            OptionItem selected = floorList.get(pos);
            viewModel.fetchRoomOptions(selected.getId());
            clearAndDisable(acRoom, acSubRoom);
        });
        acRoom.setOnItemClickListener((p, v, pos, id) -> {
            OptionItem selected = roomList.get(pos);
            viewModel.updateField(req -> req.setRoomId(selected.getId()));
            viewModel.fetchSubRoomOptions(selected.getId());
            clearAndDisable(acSubRoom);
        });
        acSubRoom.setOnItemClickListener((p, v, pos, id) -> {
            OptionItem selected = subRoomList.get(pos);
            viewModel.updateField(req -> req.setSubRoomId(selected.getId()));
        });
        acDivision.setOnItemClickListener((p, v, pos, id) -> {
            OptionItem selected = divisionList.get(pos);
            viewModel.updateField(req -> req.setResponsibleDivisionId(selected.getId()));
        });
        acUnit.setOnItemClickListener((p, v, pos, id) -> {
            OptionItem selected = workingUnitList.get(pos);
            viewModel.updateField(req -> req.setResponsibleWorkingUnitId(selected.getId()));
        });
        acUser.setOnItemClickListener((p, v, pos, id) -> {
            OptionItem selected = userList.get(pos);
            viewModel.updateField(req -> req.setResponsibleUserId(selected.getId()));
        });
    }

    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null) return;
            isProgrammaticChange = true;
            if (asset.getRoom() != null && asset.getRoom().getFloor() != null && asset.getRoom().getFloor().getBuilding() != null && asset.getRoom().getFloor().getBuilding().getHospital() != null) {
                acHospital.setText(asset.getRoom().getFloor().getBuilding().getHospital().getName(), false);
                acBuilding.setText(asset.getRoom().getFloor().getBuilding().getName(), false);
                acFloor.setText(asset.getRoom().getFloor().getName(), false);
                acRoom.setText(asset.getRoom().getName(), false);

                viewModel.fetchBuildingOptions(asset.getRoom().getFloor().getBuilding().getHospital().getId());
                viewModel.fetchFloorOptions(asset.getRoom().getFloor().getBuilding().getId());
                viewModel.fetchRoomOptions(asset.getRoom().getFloor().getId());
                if (asset.getRoom() != null) viewModel.fetchSubRoomOptions(asset.getRoom().getId());
            }
            if (asset.getSubRoom() != null) acSubRoom.setText(asset.getSubRoom().getName(), false);
            if (asset.getResponsibleDivision() != null) acDivision.setText(asset.getResponsibleDivision().getName(), false);
            if (asset.getResponsibleWorkingUnit() != null) acUnit.setText(asset.getResponsibleWorkingUnit().getName(), false);
            if (asset.getResponsibleUser() != null) acUser.setText(asset.getResponsibleUser().getFirstName(), false);
            isProgrammaticChange = false;
        });

        viewModel.getIsEditable().observe(getViewLifecycleOwner(), isEditable -> {
            if (Boolean.TRUE.equals(isEditable)) {
                acHospital.setEnabled(isEditable);
                acBuilding.setEnabled(isEditable);
                acFloor.setEnabled(isEditable);
                acRoom.setEnabled(isEditable);
                acSubRoom.setEnabled(isEditable);
                acDivision.setEnabled(isEditable);
                acUnit.setEnabled(isEditable);
                acUser.setEnabled(isEditable);
            } else {
                acHospital.setEnabled(isEditable);
                acBuilding.setEnabled(isEditable);
                acFloor.setEnabled(isEditable);
                acRoom.setEnabled(isEditable);
                acSubRoom.setEnabled(isEditable);
                acDivision.setEnabled(isEditable);
                acUnit.setEnabled(isEditable);
                acUser.setEnabled(isEditable);
            }
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
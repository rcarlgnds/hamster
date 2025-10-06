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
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class AssetLocationFragment extends Fragment {
    private AssetDetailViewModel viewModel;
    private TextInputLayout tilHospital, tilBuilding, tilFloor, tilRoom, tilSubroom, tilRespDivision, tilRespUnit, tilRespUser;
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
        tilHospital = view.findViewById(R.id.tilHospital);
        tilBuilding = view.findViewById(R.id.tilBuilding);
        tilFloor = view.findViewById(R.id.tilFloor);
        tilRoom = view.findViewById(R.id.tilRoom);
        tilSubroom = view.findViewById(R.id.tilSubroom);
        tilRespDivision = view.findViewById(R.id.tilRespDivision);
        tilRespUnit = view.findViewById(R.id.tilRespUnit);
        tilRespUser = view.findViewById(R.id.tilRespUser);

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
            // can edit
            if (Boolean.TRUE.equals(isEditable)) {
                tilHospital.setEnabled(false);
                acHospital.setEnabled(false);
                acHospital.setFocusable(isEditable);
                acHospital.setClickable(isEditable);
                acHospital.setLongClickable(isEditable);
                acHospital.setCursorVisible(isEditable);
                acHospital.setOnTouchListener((v, event) -> true);
                acHospital.setAdapter(null);
                acHospital.dismissDropDown();
                acHospital.setEnabled(isEditable);

                observeAndPopulate(viewModel.getBuildingOptions(), acBuilding, buildingList);
                tilBuilding.setEnabled(isEditable);
                acBuilding.setEnabled(isEditable);
                acBuilding.setFocusable(isEditable);
                acBuilding.setClickable(isEditable);
                acBuilding.setLongClickable(isEditable);
                acBuilding.setCursorVisible(isEditable);
                acBuilding.setOnTouchListener(null);

                observeAndPopulate(viewModel.getFloorOptions(), acFloor, floorList);
                tilFloor.setEnabled(isEditable);
                acFloor.setEnabled(isEditable);
                acFloor.setFocusable(isEditable);
                acFloor.setClickable(isEditable);
                acFloor.setLongClickable(isEditable);
                acFloor.setCursorVisible(isEditable);
                acFloor.setOnTouchListener(null);

                observeAndPopulate(viewModel.getRoomOptions(), acRoom, roomList);
                tilRoom.setEnabled(isEditable);
                acRoom.setEnabled(isEditable);
                acRoom.setFocusable(isEditable);
                acRoom.setClickable(isEditable);
                acRoom.setLongClickable(isEditable);
                acRoom.setCursorVisible(isEditable);
                acRoom.setOnTouchListener(null);

                observeAndPopulate(viewModel.getSubRoomOptions(), acSubRoom, subRoomList);
                tilSubroom.setEnabled(isEditable);
                acSubRoom.setEnabled(isEditable);
                acSubRoom.setFocusable(isEditable);
                acSubRoom.setClickable(isEditable);
                acSubRoom.setLongClickable(isEditable);
                acSubRoom.setCursorVisible(isEditable);
                acSubRoom.setOnTouchListener(null);

                observeAndPopulate(viewModel.getDivisionOptions(), acDivision, divisionList);
                tilRespDivision.setEnabled(isEditable);
                acDivision.setEnabled(isEditable);
                acDivision.setFocusable(isEditable);
                acDivision.setClickable(isEditable);
                acDivision.setLongClickable(isEditable);
                acDivision.setCursorVisible(isEditable);
                acDivision.setOnTouchListener(null);

                observeAndPopulate(viewModel.getWorkingUnitOptions(), acUnit, workingUnitList);
                tilRespUnit.setEnabled(isEditable);
                acUnit.setEnabled(isEditable);
                acUnit.setFocusable(isEditable);
                acUnit.setClickable(isEditable);
                acUnit.setLongClickable(isEditable);
                acUnit.setCursorVisible(isEditable);
                acUnit.setOnTouchListener(null);

                observeAndPopulate(viewModel.getUserOptions(), acUser, userList);
                tilRespUser.setEnabled(isEditable);
                acUser.setEnabled(isEditable);
                acUser.setFocusable(isEditable);
                acUser.setClickable(isEditable);
                acUser.setLongClickable(isEditable);
                acUser.setCursorVisible(isEditable);
                acUser.setOnTouchListener(null);

            // can not edit
            } else {
                tilHospital.setEnabled(isEditable);
                acHospital.setEnabled(isEditable);
                acHospital.setFocusable(isEditable);
                acHospital.setClickable(isEditable);
                acHospital.setLongClickable(isEditable);
                acHospital.setCursorVisible(isEditable);
                acHospital.setOnTouchListener((v, event) -> true);
                acHospital.setAdapter(null);
                acHospital.dismissDropDown();

                tilBuilding.setEnabled(isEditable);
                acBuilding.setEnabled(isEditable);
                acBuilding.setFocusable(isEditable);
                acBuilding.setClickable(isEditable);
                acBuilding.setLongClickable(isEditable);
                acBuilding.setCursorVisible(isEditable);
                acBuilding.setOnTouchListener((v, event) -> true);
                acBuilding.setAdapter(null);
                acBuilding.dismissDropDown();

                tilFloor.setEnabled(isEditable);
                acFloor.setEnabled(isEditable);
                acFloor.setFocusable(isEditable);
                acFloor.setClickable(isEditable);
                acFloor.setLongClickable(isEditable);
                acFloor.setCursorVisible(isEditable);
                acFloor.setOnTouchListener((v, event) -> true);
                acFloor.setAdapter(null);
                acFloor.dismissDropDown();

                tilRoom.setEnabled(isEditable);
                acRoom.setEnabled(isEditable);
                acRoom.setFocusable(isEditable);
                acRoom.setClickable(isEditable);
                acRoom.setLongClickable(isEditable);
                acRoom.setCursorVisible(isEditable);
                acRoom.setOnTouchListener((v, event) -> true);
                acRoom.setAdapter(null);
                acRoom.dismissDropDown();

                tilSubroom.setEnabled(isEditable);
                acSubRoom.setEnabled(isEditable);
                acSubRoom.setFocusable(isEditable);
                acSubRoom.setClickable(isEditable);
                acSubRoom.setLongClickable(isEditable);
                acSubRoom.setCursorVisible(isEditable);
                acSubRoom.setOnTouchListener((v, event) -> true);
                acSubRoom.setAdapter(null);
                acSubRoom.dismissDropDown();

                tilRespDivision.setEnabled(isEditable);
                acDivision.setEnabled(isEditable);
                acDivision.setFocusable(isEditable);
                acDivision.setClickable(isEditable);
                acDivision.setLongClickable(isEditable);
                acDivision.setCursorVisible(isEditable);
                acDivision.setOnTouchListener((v, event) -> true);
                acDivision.setAdapter(null);
                acDivision.dismissDropDown();

                tilRespUnit.setEnabled(isEditable);
                acUnit.setEnabled(isEditable);
                acUnit.setFocusable(isEditable);
                acUnit.setClickable(isEditable);
                acUnit.setLongClickable(isEditable);
                acUnit.setCursorVisible(isEditable);
                acUnit.setOnTouchListener((v, event) -> true);
                acUnit.setAdapter(null);
                acUnit.dismissDropDown();

                tilRespUser.setEnabled(isEditable);
                acUser.setEnabled(isEditable);
                acUser.setFocusable(isEditable);
                acUser.setClickable(isEditable);
                acUser.setLongClickable(isEditable);
                acUser.setCursorVisible(isEditable);
                acUser.setOnTouchListener((v, event) -> true);
                acUser.setAdapter(null);
                acUser.dismissDropDown();
            }
        });
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
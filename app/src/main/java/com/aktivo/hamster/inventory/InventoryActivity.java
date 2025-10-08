package com.aktivo.hamster.inventory;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aktivo.hamster.R;
import com.aktivo.hamster.data.model.Asset;
import com.aktivo.hamster.data.model.OptionItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InventoryActivity extends AppCompatActivity implements InventoryAdapter.OnItemClickListener {

    private InventoryViewModel viewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private InventoryAdapter adapter;
    private LinearLayout layoutEmpty;
    private FloatingActionButton fabSearch;
    private TextView tvTotalInventories;

    private List<OptionItem> hospitalList = new ArrayList<>();
    private List<OptionItem> buildingList = new ArrayList<>();
    private List<OptionItem> floorList = new ArrayList<>();
    private List<OptionItem> roomList = new ArrayList<>();
    private List<OptionItem> categoryList = new ArrayList<>();
    private List<OptionItem> subCategoryList = new ArrayList<>();
    private List<OptionItem> brandList = new ArrayList<>();
    private List<String> ownershipList = new ArrayList<>();
    private List<String> conditionList = new ArrayList<>();
    private List<OptionItem> statusList = new ArrayList<>();
    private List<OptionItem> vendorList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupViews();
        setupObservers();
        viewModel.refreshAssets();
    }

    private void setupViews() {
        tvTotalInventories = findViewById(R.id.tvInventoryTotal);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerViewInventory);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (layoutManager == null || dy <= 0) return;

                int last = layoutManager.findLastVisibleItemPosition();
                int total = layoutManager.getItemCount();
                int threshold = 4;

                if (last >= total - 1 - threshold) {
                    viewModel.loadMoreItems();
                }
            }
        });

        fabSearch = findViewById(R.id.fab_add_inventory);
        fabSearch.setOnClickListener(v -> showSearchDialog());
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);
    }

    private void setupObservers() {
        viewModel.getAssetList().observe(this, assets -> {
            boolean empty = assets == null || assets.isEmpty();
            recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
            layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);

            if (adapter == null) {
                adapter = new InventoryAdapter(assets, this, viewModel.getCommissioningStatusMap().getValue());
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateData(assets, viewModel.getCommissioningStatusMap().getValue());
            }
        });

        viewModel.getCommissioningStatusMap().observe(this, statusMap -> {
            if (adapter != null) {
                adapter.updateData(viewModel.getAssetList().getValue(), statusMap);
            }
        });

        viewModel.getTotalCount().observe(this, total -> {
            if (total == null) total = 0;
            tvTotalInventories.setText("Total: " + total);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);

            if(isLoading) {
                layoutEmpty.setVisibility(View.GONE);
            } else {
                if(adapter != null){
                    boolean isEmpty = adapter.getItemCount() == 0;
                    recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                    layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                }
            }

        });

        viewModel.getIsError().observe(this, isError -> {
            if (isError) {
                progressBar.setVisibility(View.GONE);
                if(adapter != null) {
                    boolean isEmpty = adapter.getItemCount() == 0;
                    recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                    layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                }
                Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getToastMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getScheduleResult().observe(this, response -> {
            if (response != null) {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.searchAssets(newText);
                return true;
            }
        });

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_filter) {
            showFilterDialog();
            return true;
        } else if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_advanced_search) {
            showAdvancedSearchDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAdvancedSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_advanced_search, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        TextInputEditText etSearchQuery = dialogView.findViewById(R.id.editTextSearchQuery);
        AutoCompleteTextView spinnerHospital = dialogView.findViewById(R.id.spinnerHospital);
        AutoCompleteTextView spinnerBuilding = dialogView.findViewById(R.id.spinnerBuilding);
        AutoCompleteTextView spinnerFloor = dialogView.findViewById(R.id.spinnerFloor);
        AutoCompleteTextView spinnerRoom = dialogView.findViewById(R.id.spinnerRoom);
        AutoCompleteTextView spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);
        AutoCompleteTextView spinnerOwnership = dialogView.findViewById(R.id.spinnerOwnership);
        AutoCompleteTextView spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);
        AutoCompleteTextView spinnerSubCategory = dialogView.findViewById(R.id.spinnerSubCategory);
        AutoCompleteTextView spinnerBrand = dialogView.findViewById(R.id.spinnerBrand);
        AutoCompleteTextView spinnerCondition = dialogView.findViewById(R.id.spinnerCondition);
        TextInputLayout layoutBuilding = dialogView.findViewById(R.id.layoutSpinnerBuilding);
        TextInputLayout layoutFloor = dialogView.findViewById(R.id.layoutSpinnerFloor);
        TextInputLayout layoutRoom = dialogView.findViewById(R.id.layoutSpinnerRoom);
        TextInputLayout layoutSubCategory = dialogView.findViewById(R.id.layoutSpinnerSubCategory);
        Button btnCancel = dialogView.findViewById(R.id.buttonCancel);
        Button btnSearch = dialogView.findViewById(R.id.buttonSearch);

        final String[] selectedHospitalId = {null};
        final String[] selectedBuildingId = {null};
        final String[] selectedFloorId = {null};
        final String[] selectedRoomId = {null};
        final String[] selectedStatus = {null};
        final String[] selectedOwnership = {null};
        final String[] selectedCategoryId = {null};
        final String[] selectedSubCategoryId = {null};
        final String[] selectedBrandId = {null};
        final String[] selectedCondition = {null};

        viewModel.getHospitalOptions().observe(this, options -> {
            hospitalList = options;
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, hospitalList);
            spinnerHospital.setAdapter(adapter);
        });

        viewModel.getBuildingOptions().observe(this, options -> {
            buildingList = options;
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, buildingList);
            spinnerBuilding.setAdapter(adapter);
        });

        viewModel.getFloorOptions().observe(this, options -> {
            floorList = options;
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, floorList);
            spinnerFloor.setAdapter(adapter);
        });

        viewModel.getRoomOptions().observe(this, options -> {
            roomList = options;
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roomList);
            spinnerRoom.setAdapter(adapter);
        });

        viewModel.getCategoryOptions().observe(this, options -> {
            categoryList = options;
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categoryList);
            spinnerCategory.setAdapter(adapter);
        });

        viewModel.getSubCategoryOptions().observe(this, options -> {
            subCategoryList = options;
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subCategoryList);
            spinnerSubCategory.setAdapter(adapter);
        });

        viewModel.getBrandOptions().observe(this, options -> {
            brandList = options;
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, brandList);
            spinnerBrand.setAdapter(adapter);
        });

        viewModel.getStatusOptions().observe(this, options -> {
            statusList = options;
            ArrayAdapter<OptionItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statusList);
            spinnerStatus.setAdapter(adapter);
        });

        viewModel.getOwnershipOptions().observe(this, options -> {
            ownershipList = options;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ownershipList);
            spinnerOwnership.setAdapter(adapter);
        });

        viewModel.getConditionOptions().observe(this, options -> {
            conditionList = options;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, conditionList);
            spinnerCondition.setAdapter(adapter);
        });

        viewModel.fetchHospitalOptions();
        viewModel.fetchCategoryOptions();
        viewModel.fetchBrandOptions();
        viewModel.fetchDropdownOptions();

        spinnerHospital.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selected = hospitalList.get(position);
            selectedHospitalId[0] = selected.getId();
            viewModel.fetchBuildingOptions(selected.getId());

            spinnerBuilding.setText("", false);
            selectedBuildingId[0] = null;
            spinnerFloor.setText("", false);
            selectedFloorId[0] = null;
            spinnerRoom.setText("", false);
            selectedRoomId[0] = null;

            layoutBuilding.setVisibility(View.VISIBLE);
            layoutFloor.setVisibility(View.GONE);
            layoutRoom.setVisibility(View.GONE);
        });

        spinnerBuilding.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selected = buildingList.get(position);
            selectedBuildingId[0] = selected.getId();
            viewModel.fetchFloorOptions(selected.getId());

            spinnerFloor.setText("", false);
            selectedFloorId[0] = null;
            spinnerRoom.setText("", false);
            selectedRoomId[0] = null;

            layoutFloor.setVisibility(View.VISIBLE);
            layoutRoom.setVisibility(View.GONE);
        });

        spinnerFloor.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selected = floorList.get(position);
            selectedFloorId[0] = selected.getId();
            viewModel.fetchRoomOptions(selected.getId());

            spinnerRoom.setText("", false);
            selectedRoomId[0] = null;

            layoutRoom.setVisibility(View.VISIBLE);
        });

        spinnerRoom.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selected = roomList.get(position);
            selectedRoomId[0] = selected.getId();
        });

        spinnerStatus.setOnItemClickListener((parent, view, position, id) -> selectedStatus[0] = statusList.get(position).getId());
        spinnerOwnership.setOnItemClickListener((parent, view, position, id) -> selectedOwnership[0] = ownershipList.get(position));
        spinnerCondition.setOnItemClickListener((parent, view, position, id) -> selectedCondition[0] = conditionList.get(position));
        spinnerBrand.setOnItemClickListener((parent, view, position, id) -> selectedBrandId[0] = brandList.get(position).getId());

        spinnerCategory.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selected = categoryList.get(position);
            selectedCategoryId[0] = selected.getId();
            viewModel.fetchSubCategoryOptions(selected.getId());
            spinnerSubCategory.setText("", false);
            selectedSubCategoryId[0] = null;
            layoutSubCategory.setVisibility(View.VISIBLE);
        });

        spinnerSubCategory.setOnItemClickListener((parent, view, position, id) -> {
            OptionItem selected = subCategoryList.get(position);
            selectedSubCategoryId[0] = selected.getId();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSearch.setOnClickListener(v -> {
            String nameQuery = etSearchQuery.getText().toString().trim();
            viewModel.advancedSearch(nameQuery, selectedHospitalId[0], selectedBuildingId[0], selectedFloorId[0], selectedRoomId[0], selectedStatus[0], selectedOwnership[0], selectedCategoryId[0], selectedSubCategoryId[0], selectedBrandId[0], selectedCondition[0]);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showFilterDialog() {
        final String[] statuses = {"All", "Active", "Inactive"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter by Status")
                .setItems(statuses, (dialog, which) -> {
                    String selectedStatus = statuses[which];
                    viewModel.filterByStatus(selectedStatus);
                });
        builder.create().show();
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_search_inventory, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        TextInputEditText editTextCode = dialogView.findViewById(R.id.editTextInventoryCode);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonNext = dialogView.findViewById(R.id.buttonNext);

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        buttonNext.setOnClickListener(v -> {
            String code = editTextCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Silakan masukkan kode inventaris", Toast.LENGTH_SHORT).show();
                return;
            }

            if (adapter == null || adapter.getAssetList().isEmpty()) {
                Toast.makeText(this, "Daftar inventaris masih dimuat atau kosong.", Toast.LENGTH_SHORT).show();
                return;
            }

            Asset foundAsset = null;
            for (Asset asset : adapter.getAssetList()) {
                if (asset.getCode() != null && asset.getCode().equalsIgnoreCase(code)) {
                    foundAsset = asset;
                    break;
                }
            }

            if (foundAsset != null) {
                dialog.dismiss();
                Intent intent = new Intent(InventoryActivity.this, AssetDetailActivity.class);
                intent.putExtra("ASSET_ID", foundAsset.getId());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Aset dengan kode '" + code + "' tidak ditemukan.", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onWorkOrderClick(Asset asset) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Work Order")
                .setMessage("Are you sure you want to do registration for this inventory: " + asset.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    viewModel.registerAsset(asset.getCode());
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onNeedInstallationClick(Asset asset) {
        showInstallationDialog(asset);
    }

    private void showInstallationDialog(Asset asset) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_installation, null);
        builder.setView(dialogView);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextInputEditText etScheduleDate = dialogView.findViewById(R.id.etScheduleDate);
        AutoCompleteTextView spinnerVendor = dialogView.findViewById(R.id.spinnerVendor);
        CheckBox cbUserTraining = dialogView.findViewById(R.id.cbUserTraining);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);

        String title = "Create Inventory Installation Schedule for " + asset.getCode() + " - " + asset.getName();
        tvDialogTitle.setText(title);

        final AlertDialog dialog = builder.create();

        final String[] selectedVendorId = {null};
        final Calendar selectedDateTime = Calendar.getInstance();

        etScheduleDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(
                    InventoryActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDateTime.set(Calendar.YEAR, year);
                        selectedDateTime.set(Calendar.MONTH, month);
                        selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        new TimePickerDialog(
                                InventoryActivity.this,
                                (timeView, hourOfDay, minute) -> {
                                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    selectedDateTime.set(Calendar.MINUTE, minute);

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());
                                    etScheduleDate.setText(sdf.format(selectedDateTime.getTime()));
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                        ).show();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        viewModel.getVendorOptions().observe(this, vendors -> {
            if (vendors != null) {
                vendorList = vendors;
                ArrayAdapter<OptionItem> vendorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, vendorList);
                spinnerVendor.setAdapter(vendorAdapter);
            }
        });
        viewModel.fetchVendorOptions();

        spinnerVendor.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < vendorList.size()) {
                selectedVendorId[0] = vendorList.get(position).getId();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String dateTimeString = etScheduleDate.getText().toString();
            boolean isTrainingChecked = cbUserTraining.isChecked();

            if (dateTimeString.isEmpty()) {
                Toast.makeText(this, "Please select a schedule", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedVendorId[0] == null) {
                Toast.makeText(this, "Please select a vendor", Toast.LENGTH_SHORT).show();
                return;
            }

            long timestamp = selectedDateTime.getTimeInMillis() / 1000;

            viewModel.scheduleInstallation(asset.getId(), selectedVendorId[0], timestamp, isTrainingChecked);
            dialog.dismiss();
        });

        dialog.show();
    }
}
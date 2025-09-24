package com.aktivo.hamster.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.appcompat.widget.SearchView;

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

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private InventoryViewModel viewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private InventoryAdapter adapter;
    private LinearLayout layoutEmpty;
    private FloatingActionButton fabSearch;

    private List<OptionItem> hospitalList = new ArrayList<>();
    private List<OptionItem> buildingList = new ArrayList<>();
    private List<OptionItem> floorList = new ArrayList<>();
    private List<OptionItem> roomList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerViewInventory);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        viewModel.loadMoreItems();
                    }
                }
            }
        });

        fabSearch = findViewById(R.id.fab_add_inventory);
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);
        fabSearch.setOnClickListener(v -> showSearchDialog());

        setupObservers();
        viewModel.refreshAssets();
    }

    private void setupObservers() {
        viewModel.getAssetList().observe(this, assets -> {
            if (assets == null || assets.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
            }

            if (adapter == null) {
                adapter = new InventoryAdapter(assets);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateData(assets);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);

            if(isLoading) {
                layoutEmpty.setVisibility(View.GONE);
            } else {
                boolean isEmpty = adapter.getItemCount() == 0;
                recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            }

        });

        viewModel.getIsError().observe(this, isError -> {
            if (isError) {
                progressBar.setVisibility(View.GONE);
                boolean isEmpty = adapter.getItemCount() == 0;
                recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
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
        TextInputLayout layoutBuilding = dialogView.findViewById(R.id.layoutSpinnerBuilding);
        TextInputLayout layoutFloor = dialogView.findViewById(R.id.layoutSpinnerFloor);
        TextInputLayout layoutRoom = dialogView.findViewById(R.id.layoutSpinnerRoom);
        Button btnCancel = dialogView.findViewById(R.id.buttonCancel);
        Button btnSearch = dialogView.findViewById(R.id.buttonSearch);

        final String[] selectedHospitalId = {null};
        final String[] selectedBuildingId = {null};
        final String[] selectedFloorId = {null};
        final String[] selectedRoomId = {null};

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

        viewModel.fetchHospitalOptions();

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

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSearch.setOnClickListener(v -> {
            String nameQuery = etSearchQuery.getText().toString().trim();
            viewModel.advancedSearch(nameQuery, selectedHospitalId[0], selectedBuildingId[0], selectedFloorId[0], selectedRoomId[0]);
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
}
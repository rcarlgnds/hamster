package com.aktivo.hamster.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class InventoryActivity extends AppCompatActivity {

    private InventoryViewModel viewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private InventoryAdapter adapter;
    private LinearLayout layoutEmpty;
    private FloatingActionButton fabSearch;

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
        });

        viewModel.getIsError().observe(this, isError -> {
            if (isError) {
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
        }
        return super.onOptionsItemSelected(item);
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
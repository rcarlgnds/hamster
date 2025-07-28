package com.example.hamster.inventory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hamster.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class InventoryActivity extends AppCompatActivity {

    private InventoryViewModel viewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private InventoryAdapter adapter;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fabSearch = findViewById(R.id.fab_add_inventory);

        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        fabSearch.setOnClickListener(v -> showSearchDialog());

        setupObservers();
        viewModel.fetchAssets();
    }

    private void setupObservers() {
        viewModel.getAssetList().observe(this, assets -> {
            adapter = new InventoryAdapter(assets);
            recyclerView.setAdapter(adapter);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            showSearchDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            String code = editTextCode.getText().toString();
            Toast.makeText(this, "Mencari kode: " + code, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            // TODO: Implementasikan logika pencarian di sini
        });

        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
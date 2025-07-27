package com.example.hamster.inventory;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hamster.R;

public class InventoryActivity extends AppCompatActivity {

    private InventoryViewModel viewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private InventoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Menampilkan tombol back

        progressBar = findViewById(R.id.progressBar); // Pastikan ada ProgressBar di activity_inventory.xml
        recyclerView = findViewById(R.id.recyclerViewInventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        setupObservers();

        // Panggil API untuk mengambil data
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

    // Agar tombol back di toolbar berfungsi
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Kembali ke halaman sebelumnya
        return true;
    }
}
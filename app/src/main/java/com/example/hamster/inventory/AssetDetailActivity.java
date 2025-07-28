// File: app/src/main/java/com/example/hamster/inventory/AssetDetailActivity.java
package com.example.hamster.inventory;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.hamster.R;
import com.example.hamster.databinding.ActivityAssetDetailBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class AssetDetailActivity extends AppCompatActivity {

    private AssetDetailViewModel viewModel;
    private ActivityAssetDetailBinding binding;
    private String assetId;
    private AssetDetailPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssetDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        assetId = getIntent().getStringExtra("ASSET_ID");
        viewModel = new ViewModelProvider(this).get(AssetDetailViewModel.class);

        if (assetId != null) {
            viewModel.fetchAssetById(assetId);
            viewModel.fetchAllOptions(); // Muat semua data dropdown sekali saja
        }

        pagerAdapter = new AssetDetailPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Asset Info"); break;
                case 1: tab.setText("Location"); break;
                case 2: tab.setText("Maintenance"); break;
                case 3: tab.setText("Documents"); break;
                case 4: tab.setText("Review"); break;
            }
        }).attach();

        Button buttonSave = findViewById(R.id.buttonEdit);
        buttonSave.setOnClickListener(v -> saveChanges());

        viewModel.getIsSaveSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getIsError().observe(this, isError -> {
            if (isError != null && isError) {
                Toast.makeText(this, "Gagal menyimpan data. Periksa kembali isian Anda.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void saveChanges() {
        if (assetId == null) return;

        // 1. Panggil metode pengumpulan data di semua fragment yang aktif.
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof FragmentDataCollector && fragment.isAdded()) {
                ((FragmentDataCollector) fragment).collectDataForSave();
            }
        }

        // 2. Setelah semua data dijamin terkumpul di ViewModel, panggil save.
        viewModel.saveChanges(assetId);
    }

    // Adapter dibuat sebagai inner class non-statis
    private class AssetDetailPagerAdapter extends FragmentStateAdapter {
        public AssetDetailPagerAdapter(FragmentActivity fa) { super(fa); }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new AssetInfoFragment();
                case 1: return new AssetLocationFragment();
                case 2: return new AssetMaintenanceFragment();
                case 3: return new AssetDocumentsFragment();
                default: return new Fragment(); // Untuk tab "Review"
            }
        }

        @Override
        public int getItemCount() { return 5; }
    }
}
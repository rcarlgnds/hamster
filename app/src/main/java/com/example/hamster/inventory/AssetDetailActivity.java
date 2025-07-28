package com.example.hamster.inventory;

import android.os.Bundle;
import android.util.SparseArray;
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
            viewModel.fetchAllOptions();
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

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
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

        // 1. Panggil metode pengumpulan data di semua fragment yang sudah dibuat.
        for (int i = 0; i < pagerAdapter.getItemCount(); i++) {
            Fragment fragment = pagerAdapter.getFragment(i);
            if (fragment instanceof FragmentDataCollector) {
                ((FragmentDataCollector) fragment).collectDataForSave();
            }
        }

        // 2. Setelah semua data terkumpul di ViewModel, panggil save.
        viewModel.saveChanges(assetId);
    }

    // --- [DIUBAH] Adapter sekarang menyimpan referensi ke fragment ---
    private class AssetDetailPagerAdapter extends FragmentStateAdapter {
        // Gunakan SparseArray untuk menyimpan referensi fragment yang sudah dibuat
        private final SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public AssetDetailPagerAdapter(FragmentActivity fa) { super(fa); }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            final Fragment fragment;
            switch (position) {
                case 0:
                    fragment = new AssetInfoFragment();
                    break;
                case 1:
                    fragment = new AssetLocationFragment();
                    break;
                case 2:
                    fragment = new AssetMaintenanceFragment();
                    break;
                case 3:
                    fragment = new AssetDocumentsFragment();
                    break;
                default:
                    fragment = new Fragment();
                    break;
            }
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getItemCount() { return 5; }

        // Metode untuk mengambil fragment yang sudah disimpan
        public Fragment getFragment(int position) {
            return registeredFragments.get(position);
        }
    }
}
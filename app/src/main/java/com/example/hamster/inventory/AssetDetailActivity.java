package com.example.hamster.inventory;

import android.os.Bundle;
import android.util.Log;
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

    private static final String TAG = "AssetDetailActivity";
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

        if (assetId != null && savedInstanceState == null) { // Hanya fetch jika pertama kali dibuat
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
            }
        }).attach();

        Button buttonSave = findViewById(R.id.buttonEdit);
        buttonSave.setOnClickListener(v -> saveAllChanges());

        setupObservers();
    }

    private void setupObservers() {
        viewModel.getIsSaveSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                viewModel.clearErrorMessage();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void saveAllChanges() {
        if (assetId == null) return;

        Log.d(TAG, "Tombol simpan ditekan. Memulai proses pengumpulan data.");

        for (int i = 0; i < pagerAdapter.getItemCount(); i++) {
            Fragment fragment = pagerAdapter.getFragment(i);
            if (fragment instanceof FragmentDataCollector) {
                Log.d(TAG, "Mengumpulkan data dari fragment: " + fragment.getClass().getSimpleName());
                ((FragmentDataCollector) fragment).collectDataForSave();
            }
        }

        Log.d(TAG, "Semua data terkumpul. Memanggil saveChanges di ViewModel.");
        viewModel.saveChanges(assetId);
    }

    private static class AssetDetailPagerAdapter extends FragmentStateAdapter {
        private final SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public AssetDetailPagerAdapter(FragmentActivity fa) { super(fa); }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            final Fragment fragment;
            switch (position) {
                case 0: fragment = new AssetInfoFragment(); break;
                case 1: fragment = new AssetLocationFragment(); break;
                case 2: fragment = new AssetMaintenanceFragment(); break;
                case 3: fragment = new AssetDocumentsFragment(); break;
                default: fragment = new Fragment();
            }
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getItemCount() { return 4; }

        public Fragment getFragment(int position) {
            return registeredFragments.get(position);
        }
    }
}
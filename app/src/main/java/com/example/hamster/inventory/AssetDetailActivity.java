package com.example.hamster.inventory;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
    private ProgressBar loadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssetDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingIndicator = findViewById(R.id.loading_indicator);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        assetId = getIntent().getStringExtra("ASSET_ID");
        viewModel = new ViewModelProvider(this).get(AssetDetailViewModel.class);

        if (assetId != null && savedInstanceState == null) {
            viewModel.fetchAssetById(assetId);
            viewModel.fetchAllOptions();
        }

        pagerAdapter = new AssetDetailPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.setOffscreenPageLimit(5);


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
        buttonSave.setOnClickListener(v -> saveAllChanges());

        setupObservers();
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                loadingIndicator.setVisibility(View.VISIBLE);
            } else {
                loadingIndicator.setVisibility(View.GONE);
            }
        });

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
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + i);
            if (fragment instanceof FragmentDataCollector) {
                Log.d(TAG, "Mengumpulkan data dari fragment: " + fragment.getClass().getSimpleName());
                ((FragmentDataCollector) fragment).collectDataForSave();
            }
        }

        Log.d(TAG, "Semua data terkumpul. Memanggil saveChanges di ViewModel.");
        viewModel.saveChanges(assetId);
    }

    private static class AssetDetailPagerAdapter extends FragmentStateAdapter {

        public AssetDetailPagerAdapter(FragmentActivity fa) { super(fa); }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new AssetInfoFragment();
                case 1: return new AssetLocationFragment();
                case 2: return new AssetMaintenanceFragment();
                case 3: return new AssetDocumentsFragment();
                case 4: return new AssetReviewFragment();
                default: throw new IllegalStateException("Posisi fragment tidak valid: " + position);
            }
        }

        @Override
        public int getItemCount() { return 5; }
    }
}
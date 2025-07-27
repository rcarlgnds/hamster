package com.example.hamster.inventory;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.hamster.R;
import com.example.hamster.databinding.ActivityAssetDetailBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AssetDetailActivity extends AppCompatActivity {

    private AssetDetailViewModel viewModel;
    private ActivityAssetDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssetDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String assetId = getIntent().getStringExtra("ASSET_ID");

        viewModel = new ViewModelProvider(this).get(AssetDetailViewModel.class);
        if (assetId != null) {
            viewModel.fetchAssetById(assetId);
        }

        binding.viewPager.setAdapter(new AssetDetailPagerAdapter(this));

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Asset Info"); break;
                case 1: tab.setText("Location"); break;
                case 2: tab.setText("Maintenance"); break;
                case 3: tab.setText("Documents"); break;
                case 4: tab.setText("Review"); break;
            }
        }).attach();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private static class AssetDetailPagerAdapter extends FragmentStateAdapter {
        public AssetDetailPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new AssetInfoFragment();
                case 1: return new AssetLocationFragment();
                case 2: return new AssetMaintenanceFragment();
                case 3: return new AssetDocumentsFragment();
                default: return new Fragment();
            }
        }

        @Override
        public int getItemCount() {
            return 5;
        }
    }
}
package com.aktivo.hamster.inventory;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.aktivo.hamster.R;
import com.aktivo.hamster.data.constant.Permissions;
import com.aktivo.hamster.data.model.Permission;
import com.aktivo.hamster.data.model.User;
import com.aktivo.hamster.databinding.ActivityAssetDetailBinding;
import com.aktivo.hamster.utils.SessionManager;
import com.google.android.material.tabs.TabLayoutMediator;

public class AssetDetailActivity extends AppCompatActivity {

    private static final String TAG = "AssetDetailActivity";
    private SessionManager sessionManager;
    private AssetDetailViewModel viewModel;
    private ActivityAssetDetailBinding binding;
    private String assetId, assetCode;
    private LinearLayout llLeft, llRight;
    private Button buttonSaveDraft, buttonSave, buttonRegister;
    private AssetDetailPagerAdapter pagerAdapter;
    private ProgressBar loadingIndicator;
    private View loadingScrim;
    private Boolean viewFinancial = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        binding = ActivityAssetDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingIndicator = findViewById(R.id.loading_indicator);
        loadingScrim = findViewById(R.id.loading_scrim);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        assetId = getIntent().getStringExtra("ASSET_ID");
        assetCode = getIntent().getStringExtra("ASSET_CODE");
        viewModel = new ViewModelProvider(this).get(AssetDetailViewModel.class);

        if (assetId != null && savedInstanceState == null) {
            viewModel.fetchAssetById(assetId);
            viewModel.fetchAllOptions();
            viewModel.fetchAssetIsConfirmed(assetId);
            viewModel.setIsEditMode(false);
        } else if(assetCode != null && savedInstanceState == null) {
            viewModel.fetchAssetByCode(assetCode);
            viewModel.fetchAllOptions();
            viewModel.setIsEditMode(false);
        }

        pagerAdapter = new AssetDetailPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.setOffscreenPageLimit(5);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText(R.string.tab_info); break;
                case 1: tab.setText(R.string.tab_location); break;
                case 2: tab.setText(R.string.tab_finance); break;
                case 3: tab.setText(R.string.tab_documents); break;
                case 4: tab.setText(R.string.tab_review); break;
            }
        }).attach();

        setupViews();
        setupObservers();
    }

    private void setupViews() {
        llLeft = findViewById(R.id.btnLeft);
        llRight = findViewById(R.id.btnRight);

        buttonSave = findViewById(R.id.buttonSave);
        buttonSaveDraft = findViewById(R.id.buttonSaveDraft);
        buttonRegister = findViewById(R.id.buttonRegist);

        if(userHasPermission(Permissions.PERMISSION_VIEW_FINANCIAL_DETAIL)) {
            viewFinancial = true;
            viewModel.setIsViewFinancial(viewFinancial);
        } else {
            viewFinancial = false;
            viewModel.setIsViewFinancial(viewFinancial);
        }

        buttonRegister.setOnClickListener(v -> {
            Boolean currentEditable = viewModel.getIsEditable().getValue();
            Boolean newEditable = (currentEditable == null) ? true : !currentEditable;
            viewModel.setIsEditMode(newEditable);

            llLeft.setVisibility(View.GONE);
            llRight.setVisibility(View.VISIBLE);
        });
        buttonSave.setOnClickListener(v -> saveAllChanges(true));
        buttonSaveDraft.setOnClickListener(v -> saveAllChanges(false));
    }

    private void setupObservers() {
        viewModel.getAssetConfirmed().observe(this, confirmed -> {
            if (confirmed == null) {
                return;
            }

            if(confirmed == true) {
                llLeft.setVisibility(View.GONE);
            } else if(confirmed == false) {
                llLeft.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                loadingIndicator.setVisibility(View.VISIBLE);
                loadingScrim.setVisibility(View.VISIBLE);
            } else {
                loadingIndicator.setVisibility(View.GONE);
                loadingScrim.setVisibility(View.GONE);
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

    private boolean userHasPermission(String requiredPermissionKey) {
        User currentUser = sessionManager.getUser();

        if (currentUser == null) {
            Log.d("PermissionCheck", "Current user is null.");
            return false;
        }
        if (currentUser.getPermissions() == null) {
            Log.d("PermissionCheck", "Permissions list is null for user: " + currentUser.getEmail());
            return false;
        }

        Log.d("PermissionCheck", "--- Checking Permissions for user: " + currentUser.getEmail() + " ---");
        Log.d("PermissionCheck", "Looking for permission: '" + requiredPermissionKey + "'");

        for (Permission permission : currentUser.getPermissions()) {
            Log.d("PermissionCheck", "Found permission key: '" + permission.getKey() + "'");
            if (requiredPermissionKey.equals(permission.getKey())) {
                Log.d("PermissionCheck", "MATCH FOUND! Returning true.");
                return true;
            }
        }

        Log.d("PermissionCheck", "NO MATCH FOUND. Returning false.");
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void saveAllChanges(boolean isConfirmed) {
        if (assetId == null) return;

        String action = isConfirmed ? "Save" : "Save Draft";
        Log.d(TAG, "Tombol " + action + " ditekan. Memulai proses pengumpulan data.");

        for (int i = 0; i < pagerAdapter.getItemCount(); i++) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + i);
            if (fragment instanceof FragmentDataCollector) {
                Log.d(TAG, "Mengumpulkan data dari fragment: " + fragment.getClass().getSimpleName());
                ((FragmentDataCollector) fragment).collectDataForSave();
            }
        }

        Log.d(TAG, "Semua data terkumpul. Memanggil saveChanges di ViewModel dengan isConfirmed = " + isConfirmed);
        viewModel.saveChanges(assetId, isConfirmed);
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
                case 2: return new AssetFinanceFragment();
                case 3: return new AssetDocumentsFragment();
                case 4: return new AssetReviewFragment();
                default: throw new IllegalStateException("Posisi fragment tidak valid: " + position);
            }
        }

        @Override
        public int getItemCount() { return 5; }
    }
}
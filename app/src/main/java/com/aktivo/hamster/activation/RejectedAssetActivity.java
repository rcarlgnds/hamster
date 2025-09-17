package com.aktivo.hamster.activation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aktivo.hamster.R;
import com.aktivo.hamster.data.constant.AssetStatus;
import com.aktivo.hamster.data.model.AssetRejected;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RejectedAssetActivity extends AppCompatActivity {
    private RejectedAssetViewModel viewModel;
    private RecyclerView recyclerView;
    private RejectedAssetAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyMessage;
    private AssetRejected currentItem;
    
    private Uri photoUri = null;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Izin kamera dibutuhkan untuk melanjutkan proses", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && photoUri != null && currentItem != null) {
                    viewModel.continueRejection(currentItem.getId(), photoUri);
                } else {
                    Toast.makeText(this, "Gagal mengambil foto.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejected_asset);

        setupToolbar();
        setupViews();
        setupViewModel();
        setupRecyclerView();
        setupObservers();

        viewModel.refresh();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupViews() {
        progressBar = findViewById(R.id.progressBar);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        recyclerView = findViewById(R.id.recyclerViewRejectedAssets);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(RejectedAssetViewModel.class);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RejectedAssetAdapter(this, new ArrayList<>(), this::onActionClicked);
        recyclerView.setAdapter(adapter);

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
    }

    private void onActionClicked(AssetRejected item) {
        this.currentItem = item;
        String status = item.getStatus();

        if (AssetStatus.REJECTED_DOES_NOT_MEET_REQUEST.equalsIgnoreCase(status)) {
            checkCameraPermissionAndLaunch();
        } else if (AssetStatus.REJECTED_WRONG_LOCATION.equalsIgnoreCase(status)) {
            viewModel.confirmLocation(item.getId());
        }
    }

    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        File imageDir = new File(getFilesDir(), "images");
        if (!imageDir.exists()) imageDir.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File imageFile = new File(imageDir, "REJECTION_" + timeStamp + ".jpg");
        photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", imageFile);
        takePictureLauncher.launch(photoUri);
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getRejectedList().observe(this, items -> {
            boolean isEmpty = items == null || items.isEmpty();
            tvEmptyMessage.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            if (!isEmpty) {
                adapter.updateData(items);
            }
        });

        viewModel.getActionResult().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Aksi berhasil diproses.", Toast.LENGTH_SHORT).show();
                viewModel.refresh();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
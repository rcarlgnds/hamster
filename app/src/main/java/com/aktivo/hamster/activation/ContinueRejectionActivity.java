package com.aktivo.hamster.activation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.aktivo.hamster.R;
import com.aktivo.hamster.data.model.AssetRejected;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ContinueRejectionActivity extends AppCompatActivity {

    public static final String EXTRA_REJECTED_ASSET = "extra_rejected_asset";

    private ContinueRejectionViewModel viewModel;
    private AssetRejected rejectedAsset;
    private TextView tvAssetCode;
    private ImageView ivAssetPhoto;
    private Button btnAcknowledge;

    private String scannedQrCode = null;
    private Uri photoUri = null;

    private final ActivityResultLauncher<Intent> scanQrCodeLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    scannedQrCode = result.getData().getStringExtra("QR_CODE_RESULT");
                    if (scannedQrCode != null && rejectedAsset.getAssetCode().equals(scannedQrCode)) {
                        tvAssetCode.setText(scannedQrCode);
                        tvAssetCode.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "QR Code cocok!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "QR Code tidak cocok dengan aset yang dipilih.", Toast.LENGTH_LONG).show();
                        scannedQrCode = null;
                    }
                    checkAllDataAndEnableButton();
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Izin kamera dibutuhkan.", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && photoUri != null) {
                    Glide.with(this).load(photoUri).into(ivAssetPhoto);
                    checkAllDataAndEnableButton();
                } else {
                    Toast.makeText(this, "Gagal mengambil foto.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_rejection);

        rejectedAsset = (AssetRejected) getIntent().getSerializableExtra(EXTRA_REJECTED_ASSET);
        if (rejectedAsset == null) {
            Toast.makeText(this, "Data aset tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(ContinueRejectionViewModel.class);

        setupToolbar();
        setupViews();
        setupObservers();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Aset: " + rejectedAsset.getAssetName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupViews() {
        tvAssetCode = findViewById(R.id.tvAssetCode);
        ivAssetPhoto = findViewById(R.id.ivAssetPhoto);
        btnAcknowledge = findViewById(R.id.btnAcknowledge);
        Button btnScanQr = findViewById(R.id.btnScanQr);

        tvAssetCode.setVisibility(View.GONE);

        btnScanQr.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScannerActivity.class);
            scanQrCodeLauncher.launch(intent);
        });

        ivAssetPhoto.setOnClickListener(v -> checkCameraPermissionAndLaunch());

        btnAcknowledge.setOnClickListener(v -> {
            if (rejectedAsset.getId() != null && photoUri != null) {
                viewModel.continueRejection(rejectedAsset.getId(), photoUri);
            }
        });
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
        });

        viewModel.getActionResult().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Proses penolakan berhasil dilanjutkan.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
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

    private void checkAllDataAndEnableButton() {
        boolean isQrScannedAndValid = scannedQrCode != null && rejectedAsset.getAssetCode().equals(scannedQrCode);
        boolean isPhotoTaken = photoUri != null;
        btnAcknowledge.setEnabled(isQrScannedAndValid && isPhotoTaken);
    }
}
package com.example.hamster.activation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.hamster.R;
import com.example.hamster.data.model.AssetActivationStatus;
import com.example.hamster.databinding.ActivityActivationBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivationActivity extends AppCompatActivity {

    private ActivityActivationBinding binding;
    private ActivationViewModel viewModel;

    private String scannedAssetCode = null;
    private Uri photoUri = null;
    private boolean isAssetReadyForActivation = false;

    // --- ActivityResultLaunchers ---
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Izin kamera dibutuhkan untuk mengambil foto.", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && photoUri != null) {
                    Glide.with(this).load(photoUri).into(binding.ivAssetPhoto);
                    validateInputs();
                } else {
                    Toast.makeText(this, "Gagal mengambil foto.", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> qrCodeScannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String qrCode = result.getData().getStringExtra(ScannerActivity.EXTRA_QR_CODE_RESULT);
                    if (qrCode != null && !qrCode.isEmpty()) {
                        scannedAssetCode = qrCode;
                        binding.tvQrResult.setText("Kode Aset: " + scannedAssetCode);
                        viewModel.checkAssetStatus(scannedAssetCode);
                    }
                } else {
                    Toast.makeText(this, "Scan QR dibatalkan atau gagal.", Toast.LENGTH_SHORT).show();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActivationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(ActivationViewModel.class);
        setupListeners();
        setupObservers();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setupListeners() {
        binding.btnScanQr.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScannerActivity.class);
            qrCodeScannerLauncher.launch(intent);
        });

        binding.btnTakePhoto.setOnClickListener(v -> checkCameraPermissionAndLaunch());

        binding.btnStartActivation.setOnClickListener(v -> {
            if (scannedAssetCode != null && photoUri != null && isAssetReadyForActivation) {
                viewModel.startActivationProcess(scannedAssetCode);
            } else {
                Toast.makeText(this, "Pindai aset, ambil foto, dan pastikan aset siap diaktivasi.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupObservers() {
        // Observer untuk hasil pengecekan status
        viewModel.getStatusCheckState().observe(this, state -> {
            binding.progressBar.setVisibility(View.GONE);

            switch (state) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.tvStatusBadge.setVisibility(View.GONE);
                    isAssetReadyForActivation = false;
                    break;
                case FOUND:
                    isAssetReadyForActivation = false;
                    AssetActivationStatus statusData = viewModel.getAssetStatusData().getValue();
                    if (statusData != null && statusData.getStatus() != null) {
                        binding.tvStatusBadge.setText(statusData.getStatus());
                        binding.tvStatusBadge.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(this, "Aset ini sudah memiliki proses aktivasi.", Toast.LENGTH_LONG).show();
                    break;
                case NOT_FOUND:
                    isAssetReadyForActivation = true;
                    binding.tvStatusBadge.setVisibility(View.GONE);
                    Toast.makeText(this, "Aset siap untuk diaktivasi.", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    isAssetReadyForActivation = false;
                    binding.tvStatusBadge.setVisibility(View.GONE);
                    Toast.makeText(this, "Gagal memeriksa status aset. Coba lagi.", Toast.LENGTH_SHORT).show();
                    break;
            }
            validateInputs();
        });

        viewModel.getActivationState().observe(this, state -> {
            switch (state) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnStartActivation.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Proses aktivasi berhasil dimulai!", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Gagal memulai proses aktivasi. Cek kembali data.", Toast.LENGTH_SHORT).show();
                    validateInputs();
                    break;
                case IDLE:
                    binding.progressBar.setVisibility(View.GONE);
                    validateInputs();
                    break;
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File imageFile = new File(imageDir, "ACTIVATION_" + timeStamp + ".jpg");
        photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", imageFile);
        takePictureLauncher.launch(photoUri);
    }

    /**
     * Mengaktifkan tombol "Start Activation" hanya jika SEMUA kondisi terpenuhi.
     */
    private void validateInputs() {
        boolean conditionsMet = scannedAssetCode != null &&
                photoUri != null &&
                isAssetReadyForActivation;

        binding.btnStartActivation.setEnabled(conditionsMet);
    }
}
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

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(this, getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && photoUri != null) {
                    Glide.with(this).load(photoUri).into(binding.ivAssetPhoto);
                    validateInputs();
                } else {
                    Toast.makeText(this, getString(R.string.failed_to_capture_photo), Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> qrCodeScannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String qrCode = result.getData().getStringExtra(ScannerActivity.EXTRA_QR_CODE_RESULT);
                    if (qrCode != null && !qrCode.isEmpty()) {
                        scannedAssetCode = qrCode;
                        binding.tvQrResult.setText("Asset Code: " + scannedAssetCode);
                        viewModel.checkAssetStatus(scannedAssetCode);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.scan_cancelled), Toast.LENGTH_SHORT).show();
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
                viewModel.startActivationProcess(scannedAssetCode, photoUri);
            } else {
                Toast.makeText(this, "Scan asset, take photo, and ensure asset is ready.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupObservers() {
        viewModel.getStatusCheckState().observe(this, state -> {
            binding.progressBar.setVisibility(View.GONE);

            switch (state) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.tvStatusBadge.setVisibility(View.GONE);
                    binding.groupActivation.setVisibility(View.GONE);
                    isAssetReadyForActivation = false;
                    break;

                case FOUND_READY_TO_ACTIVATE:
                    isAssetReadyForActivation = false;
                    binding.groupActivation.setVisibility(View.VISIBLE);
                    AssetActivationStatus statusDataReadyToActivate = viewModel.getAssetStatusData().getValue();

                    if (statusDataReadyToActivate != null && statusDataReadyToActivate.getStatus() != null) {
                        String statusFromServer = statusDataReadyToActivate.getStatus().toUpperCase();

                        binding.tvStatusBadge.setText(statusDataReadyToActivate.getStatus());
                        binding.tvStatusBadge.setVisibility(View.VISIBLE);

                        switch (statusFromServer) {
                            case "PENDING":
                                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_pending_background);
                                break;
                            case "APPROVED":
                                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_activate_background);
                                break;
                            case "REJECTED":
                                binding.tvStatusBadge.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                                break;
                            default:
                                binding.tvStatusBadge.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
                                break;
                        }
                    }
                    isAssetReadyForActivation = true;
                    Toast.makeText(this, "This asset is ready to be activated.", Toast.LENGTH_LONG).show();
                    break;

                case FOUND_ALREADY_ACTIVATED:
                    isAssetReadyForActivation = false;
                    binding.groupActivation.setVisibility(View.GONE);
                    AssetActivationStatus statusDataAlreadyActivated = viewModel.getAssetStatusData().getValue();

                    if (statusDataAlreadyActivated != null && statusDataAlreadyActivated.getStatus() != null) {
                        String statusFromServer = statusDataAlreadyActivated.getStatus().toUpperCase();

                        binding.tvStatusBadge.setText(statusDataAlreadyActivated.getStatus());
                        binding.tvStatusBadge.setVisibility(View.VISIBLE);

                        switch (statusFromServer) {
                            case "PENDING":
                                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_pending_background);
                                break;
                            case "APPROVED":
                                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_activate_background);
                                break;
                            case "REJECTED":
                                binding.tvStatusBadge.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                                break;
                            default:
                                binding.tvStatusBadge.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
                                break;
                        }
                    }
                    Toast.makeText(this, "This asset already has an activation process.", Toast.LENGTH_LONG).show();
                    break;

                case NOT_FOUND:
                    isAssetReadyForActivation = false;
                    binding.tvStatusBadge.setVisibility(View.GONE);
                    binding.groupActivation.setVisibility(View.VISIBLE);
                    Toast.makeText(this, getString(R.string.asset_ready_to_activate), Toast.LENGTH_SHORT).show();
                    break;

                case ERROR:
                    isAssetReadyForActivation = false;
                    binding.tvStatusBadge.setVisibility(View.GONE);
                    binding.groupActivation.setVisibility(View.GONE);
                    Toast.makeText(this, getString(R.string.failed_to_check_status), Toast.LENGTH_SHORT).show();
                    break;

                case IDLE:
                    binding.tvStatusBadge.setVisibility(View.GONE);
                    binding.groupActivation.setVisibility(View.GONE);
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
                    Toast.makeText(this, getString(R.string.activation_started_successfully), Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, getString(R.string.activation_failed), Toast.LENGTH_SHORT).show();
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File imageFile = new File(imageDir, "ACTIVATION_" + timeStamp + ".jpg");
        photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", imageFile);
        takePictureLauncher.launch(photoUri);
    }

    private void validateInputs() {
        boolean conditionsMet = scannedAssetCode != null &&
                photoUri != null &&
                isAssetReadyForActivation;
        binding.btnStartActivation.setEnabled(conditionsMet);
    }
}
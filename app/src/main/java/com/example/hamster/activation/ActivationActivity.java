package com.example.hamster.activation;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.example.hamster.R;
import com.example.hamster.databinding.ActivityActivationBinding;

import java.io.File;

/**
 * Activity untuk menangani proses memulai aktivasi aset.
 * Memungkinkan pengguna untuk memindai QR code aset dan mengambil foto sebagai bukti.
 */
public class ActivationActivity extends AppCompatActivity {

    private ActivityActivationBinding binding;
    private ActivationViewModel viewModel;

    private String scannedAssetCode = null;
    private Uri photoUri = null;

    // --- ActivityResultLaunchers ---

    /**
     * Launcher untuk meminta izin kamera.
     * Jika izin diberikan, maka akan langsung membuka kamera.
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Izin kamera dibutuhkan untuk mengambil foto.", Toast.LENGTH_SHORT).show();
                }
            });

    /**
     * Launcher untuk aplikasi kamera.
     * Ketika foto berhasil diambil, akan memperbarui ImageView dan memvalidasi input.
     */
    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success) {
                    binding.ivAssetPhoto.setImageURI(photoUri);
                    validateInputs();
                } else {
                    Toast.makeText(this, "Gagal mengambil foto.", Toast.LENGTH_SHORT).show();
                }
            });

    /**
     * Launcher untuk ScannerActivity kustom kita.
     * Ketika QR code berhasil dipindai, launcher akan menerima hasilnya,
     * memperbarui UI, dan memvalidasi input.
     */
    private final ActivityResultLauncher<Intent> qrCodeScannerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String qrCode = result.getData().getStringExtra(ScannerActivity.EXTRA_QR_CODE_RESULT);
                    if (qrCode != null && !qrCode.isEmpty()) {
                        scannedAssetCode = qrCode;
                        binding.tvQrResult.setText(scannedAssetCode);
                        validateInputs();
                    }
                } else {
                    Toast.makeText(this, "Scan QR dibatalkan atau gagal.", Toast.LENGTH_SHORT).show();
                }
            });


    // --- Metode Lifecycle ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActivationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Pengaturan Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inisialisasi ViewModel
        viewModel = new ViewModelProvider(this).get(ActivationViewModel.class);

        setupListeners();
        setupObservers();
    }

    /**
     * Menangani navigasi kembali saat tombol panah di toolbar ditekan.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Mengatur click listener untuk semua elemen UI yang interaktif.
     */
    private void setupListeners() {
        // 1. Tombol Scan QR
        binding.btnScanQr.setOnClickListener(v -> {
            Intent intent = new Intent(ActivationActivity.this, ScannerActivity.class);
            qrCodeScannerLauncher.launch(intent);
        });

        // 2. Tombol Ambil Foto
        binding.btnTakePhoto.setOnClickListener(v -> {
            // Meminta izin kamera sebelum membuka kamera
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        });

        // 3. Tombol Mulai Aktivasi
        binding.btnStartActivation.setOnClickListener(v -> {
            if (scannedAssetCode != null) {
                // API hanya membutuhkan kode aset untuk memulai proses.
                // Foto dan catatan biasanya akan diunggah secara terpisah atau sebagai bagian dari request multi-part.
                viewModel.startActivationProcess(scannedAssetCode);
            }
        });
    }

    /**
     * Mengamati LiveData dari ViewModel untuk bereaksi terhadap perubahan state (misalnya, loading, success, error).
     */
    private void setupObservers() {
        viewModel.getActivationState().observe(this, state -> {
            switch (state) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnStartActivation.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Proses aktivasi berhasil dimulai!", Toast.LENGTH_LONG).show();
                    // Selesaikan activity dan kembali ke layar sebelumnya
                    finish();
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnStartActivation.setEnabled(true);
                    Toast.makeText(this, "Gagal memulai proses aktivasi.", Toast.LENGTH_SHORT).show();
                    break;
                case IDLE:
                default:
                    binding.progressBar.setVisibility(View.GONE);
                    validateInputs(); // Validasi ulang input jika state di-reset
                    break;
            }
        });
    }

    /**
     * Membuat URI file sementara dan membuka aplikasi kamera sistem.
     */
    private void launchCamera() {
        File photoFile = new File(getExternalFilesDir(null), "asset_photo_" + System.currentTimeMillis() + ".jpg");
        photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);
        takePictureLauncher.launch(photoUri);
    }

    /**
     * Memeriksa apakah QR code sudah dipindai DAN foto sudah diambil.
     * Mengaktifkan atau menonaktifkan tombol "Mulai Aktivasi" sesuai kondisi.
     */
    private void validateInputs() {
        boolean isReady = scannedAssetCode != null && !scannedAssetCode.isEmpty() && photoUri != null;
        binding.btnStartActivation.setEnabled(isReady);
    }
}
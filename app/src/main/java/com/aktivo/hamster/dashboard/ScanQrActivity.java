package com.aktivo.hamster.dashboard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.aktivo.hamster.R;
import com.aktivo.hamster.activation.ScannerActivity;
import com.aktivo.hamster.inventory.AssetDetailActivity;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScanQrActivity extends AppCompatActivity {
    private Button btnScanQr;

    private String scannedAssetCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan_qr);

        setupToolbar();
        setupViews();
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
        btnScanQr = findViewById(R.id.btnScanQr);
        btnScanQr.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScannerActivity.class);
            qrCodeScannerLauncher.launch(intent);
        });
    }

    private final ActivityResultLauncher<Intent> qrCodeScannerLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                String qrCode = result.getData().getStringExtra(ScannerActivity.EXTRA_QR_CODE_RESULT);
                if (qrCode != null && !qrCode.isEmpty()) {
                    scannedAssetCode = qrCode;
                    Intent intent = new Intent(this, AssetDetailActivity.class);
                    intent.putExtra("ASSET_CODE", scannedAssetCode);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, getString(R.string.scan_cancelled), Toast.LENGTH_SHORT).show();
            }
        });
}
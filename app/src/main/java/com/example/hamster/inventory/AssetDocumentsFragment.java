package com.example.hamster.inventory;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hamster.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AssetDocumentsFragment extends Fragment {

    private AssetDetailViewModel viewModel;
    private ImageView imageViewSerialNumber;
    private LinearLayout assetPhotosContainer;

    private Uri currentImageUri;
    private boolean isSerialPhoto;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(getContext(), "Izin kamera ditolak.", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
                if (result && currentImageUri != null) {
                    if (isSerialPhoto) {
                        imageViewSerialNumber.setImageURI(currentImageUri);
                    } else {
                        addAssetPhotoToContainer(currentImageUri);
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asset_documents, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);

        imageViewSerialNumber = view.findViewById(R.id.imageViewSerialNumber);
        assetPhotosContainer = view.findViewById(R.id.assetPhotosContainer);

        Button buttonSerialPhoto = view.findViewById(R.id.buttonTakeSerialPhoto);
        Button buttonAssetPhoto = view.findViewById(R.id.buttonTakeAssetPhoto);

        buttonSerialPhoto.setOnClickListener(v -> {
            isSerialPhoto = true;
            checkCameraPermissionAndLaunch();
        });

        buttonAssetPhoto.setOnClickListener(v -> {
            isSerialPhoto = false;
            checkCameraPermissionAndLaunch();
        });

        return view;
    }

    private void checkCameraPermissionAndLaunch() {
        if (getContext() == null) return;

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Izin Dibutuhkan")
                    .setMessage("Aplikasi ini memerlukan izin kamera untuk mengambil foto aset dan nomor seri.")
                    .setPositiveButton("Berikan Izin", (dialog, which) -> {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                    })
                    .setNegativeButton("Batalkan", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        if (getContext() == null) return;

        File imageDir = new File(requireContext().getFilesDir(), "images");
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }

        String fileName = (isSerialPhoto ? "serial_" : "asset_") +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";

        File imageFile = new File(imageDir, fileName);
        currentImageUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".provider",
                imageFile
        );

        takePictureLauncher.launch(currentImageUri);
    }

    private void addAssetPhotoToContainer(Uri uri) {
        if (getContext() == null) return;

        ImageView imageView = new ImageView(requireContext());
        int size = getResources().getDimensionPixelSize(R.dimen.asset_image_size);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(0, 0, 16, 0);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageURI(uri);
        assetPhotosContainer.addView(imageView);
    }
}
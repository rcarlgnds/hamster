// File: app/src/main/java/com/example/hamster/inventory/AssetDocumentsFragment.java
package com.example.hamster.inventory;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.bumptech.glide.Glide;
import com.example.hamster.R;
import com.example.hamster.data.model.AssetMediaFile;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssetDocumentsFragment extends Fragment implements FragmentDataCollector {

    private AssetDetailViewModel viewModel;
    private ImageView imageViewSerialNumber;
    private LinearLayout assetPhotosContainer;
    private FrameLayout serialNumberFrame;

    private Uri currentImageUri;
    private boolean isSerialPhoto;

    private Uri newSerialNumberUri = null;
    private final List<Uri> newAssetPhotosUris = new ArrayList<>();

    private String existingSerialPhotoId = null;
    private final List<String> existingAssetPhotoIds = new ArrayList<>();


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
                        newSerialNumberUri = currentImageUri;
                        existingSerialPhotoId = null;
                        Glide.with(this).load(newSerialNumberUri).into(imageViewSerialNumber);
                    } else {
                        newAssetPhotosUris.add(currentImageUri);
                        addAssetPhotoToContainer(currentImageUri, null, -1);
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asset_documents, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);

        imageViewSerialNumber = view.findViewById(R.id.imageViewSerialNumber);
        assetPhotosContainer = view.findViewById(R.id.assetPhotosContainer);
        serialNumberFrame = view.findViewById(R.id.serialNumberFrame);

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

        setupObservers();
        return view;
    }

    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null || asset.getMediaFiles() == null) return;

            assetPhotosContainer.removeAllViews();
            existingAssetPhotoIds.clear();
            existingSerialPhotoId = null;

            imageViewSerialNumber.setImageResource(R.drawable.ic_launcher_background);


            for (AssetMediaFile media : asset.getMediaFiles()) {
                if ("SERIAL_NUMBER_PHOTO".equals(media.getType()) && media.getMediaFile() != null) {
                    existingSerialPhotoId = media.getMediaFile().getId();
                    Glide.with(this)
                            .load(media.getMediaFile().getUrl())
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(imageViewSerialNumber);
                } else if ("ASSET_PHOTO".equals(media.getType()) && media.getMediaFile() != null) {
                    String mediaId = media.getMediaFile().getId();
                    existingAssetPhotoIds.add(mediaId);
                    addAssetPhotoToContainer(null, media.getMediaFile().getUrl(), existingAssetPhotoIds.size() -1);
                }
            }
        });
    }

    @Override
    public void collectDataForSave() {
        List<String> keepSerialIds = new ArrayList<>();
        if (newSerialNumberUri == null && existingSerialPhotoId != null) {
            keepSerialIds.add(existingSerialPhotoId);
        }

        viewModel.updateDocumentUris(newSerialNumberUri, newAssetPhotosUris, keepSerialIds, existingAssetPhotoIds);
    }

    private void addAssetPhotoToContainer(Uri localUri, String remoteUrl, String mediaId) {
        if (getContext() == null) return;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View photoView = inflater.inflate(R.layout.item_asset_photo, assetPhotosContainer, false);

        ImageView imageView = photoView.findViewById(R.id.imageViewAsset);
        ImageView deleteButton = photoView.findViewById(R.id.buttonDeletePhoto);

        if (localUri != null) {
            Glide.with(this).load(localUri).into(imageView);
            deleteButton.setOnClickListener(v -> {
                assetPhotosContainer.removeView(photoView);
                newAssetPhotosUris.remove(localUri);
            });
        } else if (remoteUrl != null) {
            Glide.with(this).load(remoteUrl).into(imageView);
            deleteButton.setOnClickListener(v -> {
                assetPhotosContainer.removeView(photoView);
                existingAssetPhotoIds.remove(mediaId);
            });
        }
        assetPhotosContainer.addView(photoView);
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

    private void addAssetPhotoToContainer(Uri localUri, String remoteUrl, int index) {
        if (getContext() == null) return;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View photoView = inflater.inflate(R.layout.item_asset_photo, assetPhotosContainer, false);

        ImageView imageView = photoView.findViewById(R.id.imageViewAsset);
        ImageView deleteButton = photoView.findViewById(R.id.buttonDeletePhoto);

        if (localUri != null) {
            Glide.with(this).load(localUri).into(imageView);
            deleteButton.setOnClickListener(v -> {
                assetPhotosContainer.removeView(photoView);
                newAssetPhotosUris.remove(localUri);
            });
        } else if (remoteUrl != null) {
            Glide.with(this).load(remoteUrl).into(imageView);
            deleteButton.setOnClickListener(v -> {
                assetPhotosContainer.removeView(photoView);
                if (index >= 0 && index < existingAssetPhotoIds.size()) {
                    existingAssetPhotoIds.remove(index);
                }
            });
        }

        assetPhotosContainer.addView(photoView);
    }
}
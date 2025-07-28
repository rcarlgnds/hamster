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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.hamster.R;
import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.AssetMediaFile;
import com.example.hamster.data.network.ApiClient;

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

    private Uri currentImageUri;
    private boolean isCapturingForSerial;

    // State LOKAL untuk fragment ini
    private final List<Uri> newSerialNumberUri = new ArrayList<>();
    private final List<Uri> newAssetPhotosUris = new ArrayList<>();
    private final List<String> existingSerialPhotoIds = new ArrayList<>();
    private final List<String> existingAssetPhotoIds = new ArrayList<>();

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) launchCamera();
                else Toast.makeText(getContext(), "Izin kamera ditolak.", Toast.LENGTH_SHORT).show();
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && currentImageUri != null) {
                    if (isCapturingForSerial) {
                        newSerialNumberUri.clear();
                        existingSerialPhotoIds.clear(); // Hapus ID lama jika ada foto baru
                        newSerialNumberUri.add(currentImageUri);
                        Glide.with(this).load(currentImageUri).into(imageViewSerialNumber);
                    } else {
                        newAssetPhotosUris.add(currentImageUri);
                        addAssetPhotoToContainer(currentImageUri, null, null);
                    }
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asset_documents, container, false);
        // Hapus inisialisasi dari sini
        initializeViews(view);
        setupListeners(view);
        setupObservers();
        return view;
    }

    private void initializeViews(View view) {
        imageViewSerialNumber = view.findViewById(R.id.imageViewSerialNumber);
        assetPhotosContainer = view.findViewById(R.id.assetPhotosContainer);
    }

    private void setupListeners(View view) {
        Button buttonSerialPhoto = view.findViewById(R.id.buttonTakeSerialPhoto);
        Button buttonAssetPhoto = view.findViewById(R.id.buttonTakeAssetPhoto);
        buttonSerialPhoto.setOnClickListener(v -> {
            isCapturingForSerial = true;
            checkCameraPermissionAndLaunch();
        });
        buttonAssetPhoto.setOnClickListener(v -> {
            isCapturingForSerial = false;
            checkCameraPermissionAndLaunch();
        });
    }

    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), this::displayAssetMedia);
    }

    private void displayAssetMedia(Asset asset) {
        if (asset == null) return;
        clearAllPhotos();
        if (asset.getMediaFiles() == null) return;

        for (AssetMediaFile media : asset.getMediaFiles()) {
            if (media.getMediaFile() != null) {
                String mediaId = media.getId();
                String fullUrl = ApiClient.BASE_MEDIA_URL + media.getMediaFile().getUrl();
                if ("SERIAL_NUMBER_PHOTO".equals(media.getType())) {
                    existingSerialPhotoIds.add(mediaId);
                    Glide.with(this).load(fullUrl).error(R.drawable.ic_broken_image).into(imageViewSerialNumber);
                } else if ("ASSET_PHOTO".equals(media.getType())) {
                    existingAssetPhotoIds.add(mediaId);
                    addAssetPhotoToContainer(null, fullUrl, mediaId);
                }
            }
        }
    }

    private void clearAllPhotos() {
        assetPhotosContainer.removeAllViews();
        imageViewSerialNumber.setImageResource(R.drawable.ic_launcher_background);
        newSerialNumberUri.clear();
        newAssetPhotosUris.clear();
        existingSerialPhotoIds.clear();
        existingAssetPhotoIds.clear();
    }

    @Override
    public void collectDataForSave() {
        // Cukup kirim state lokal fragment ini ke ViewModel
        viewModel.updateDocumentsData(
                newSerialNumberUri,
                existingSerialPhotoIds,
                newAssetPhotosUris,
                existingAssetPhotoIds
        );
    }

    private void addAssetPhotoToContainer(final Uri localUri, final String remoteUrl, final String mediaId) {
        if (getContext() == null) return;
        View photoView = LayoutInflater.from(getContext()).inflate(R.layout.item_asset_photo, assetPhotosContainer, false);
        ImageView imageView = photoView.findViewById(R.id.imageViewAsset);
        ImageView deleteButton = photoView.findViewById(R.id.buttonDeletePhoto);

        Object model = localUri != null ? localUri : remoteUrl;
        Glide.with(this).load(model).error(R.drawable.ic_broken_image).into(imageView);

        deleteButton.setOnClickListener(v -> {
            assetPhotosContainer.removeView(photoView);
            if (localUri != null) {
                newAssetPhotosUris.remove(localUri);
            } else {
                existingAssetPhotoIds.remove(mediaId);
            }
            Toast.makeText(getContext(), "Foto dihapus", Toast.LENGTH_SHORT).show();
        });
        assetPhotosContainer.addView(photoView);
    }

    private void checkCameraPermissionAndLaunch() {
        if (getContext() == null) return;
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        if (getContext() == null) return;
        File imageDir = new File(requireContext().getFilesDir(), "images");
        if (!imageDir.exists()) imageDir.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = (isCapturingForSerial ? "serial_" : "asset_") + timeStamp + ".jpg";
        File imageFile = new File(imageDir, fileName);
        currentImageUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", imageFile);
        takePictureLauncher.launch(currentImageUri);
    }
}
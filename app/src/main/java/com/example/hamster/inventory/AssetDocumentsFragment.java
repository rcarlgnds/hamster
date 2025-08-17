// File: AssetDocumentsFragment.java
package com.example.hamster.inventory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.stream.Collectors;

public class AssetDocumentsFragment extends Fragment implements FragmentDataCollector {

    private AssetDetailViewModel viewModel;

    // View Components
    private ImageView imageViewSerialNumber;
    private Button buttonTakeSerialPhoto, buttonViewSerialPhoto, buttonAddAssetPhoto;
    private RecyclerView recyclerViewAssetPhotos;

    // Adapter & Data
    private AssetPhotosAdapter photosAdapter;
    private final List<AssetPhotosAdapter.PhotoItem> assetPhotoItems = new ArrayList<>();

    // State
    private Uri currentImageUri;
    private boolean isCapturingForSerial;
    private AssetPhotosAdapter.PhotoItem serialNumberPhotoItem = null;


    // --- ActivityResultLaunchers ---
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) launchCamera();
                else Toast.makeText(getContext(), "Izin kamera ditolak.", Toast.LENGTH_SHORT).show();
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && currentImageUri != null) {
                    if (isCapturingForSerial) {
                        serialNumberPhotoItem = new AssetPhotosAdapter.PhotoItem(currentImageUri);
                        updateSerialPhotoUI();
                    } else {
                        AssetPhotosAdapter.PhotoItem newItem = new AssetPhotosAdapter.PhotoItem(currentImageUri);
                        assetPhotoItems.add(newItem);
                        photosAdapter.notifyItemInserted(assetPhotoItems.size() - 1);
                    }
                }
            });

    // --- Lifecycle Methods ---
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset_documents, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);

        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        setupObservers();
    }

    // --- Initialization ---
    private void initializeViews(View view) {
        imageViewSerialNumber = view.findViewById(R.id.imageViewSerialNumber);
        buttonTakeSerialPhoto = view.findViewById(R.id.buttonTakeSerialPhoto);
        buttonViewSerialPhoto = view.findViewById(R.id.buttonViewSerialPhoto);
        buttonAddAssetPhoto = view.findViewById(R.id.buttonAddAssetPhoto);
        recyclerViewAssetPhotos = view.findViewById(R.id.recyclerViewAssetPhotos);
    }

    private void setupRecyclerView() {
        photosAdapter = new AssetPhotosAdapter(requireContext(), assetPhotoItems, item -> {
            // Logika saat tombol delete di klik
            int position = assetPhotoItems.indexOf(item);
            if (position != -1) {
                assetPhotoItems.remove(position);
                photosAdapter.notifyItemRemoved(position);
                Toast.makeText(getContext(), "Foto dihapus", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewAssetPhotos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewAssetPhotos.setAdapter(photosAdapter);
    }

    private void setupListeners() {
        buttonTakeSerialPhoto.setOnClickListener(v -> {
            isCapturingForSerial = true;
            checkCameraPermissionAndLaunch();
        });
        buttonAddAssetPhoto.setOnClickListener(v -> {
            isCapturingForSerial = false;
            checkCameraPermissionAndLaunch();
        });
        buttonViewSerialPhoto.setOnClickListener(v -> {
            if (serialNumberPhotoItem != null && serialNumberPhotoItem.getModel() != null) {
                Intent intent = new Intent(getContext(), ImagePreviewActivity.class);
                intent.putExtra("IMAGE_URL", serialNumberPhotoItem.getModel().toString());
                requireContext().startActivity(intent);
            }
        });
    }

    // --- ViewModel Observers ---
    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), this::displayAssetMedia);
    }

    private void displayAssetMedia(Asset asset) {
        if (asset == null) return;
        clearAllPhotos();

        if (asset.getMediaFiles() != null) {
            for (AssetMediaFile media : asset.getMediaFiles()) {
                if (media.getMediaFile() != null) {
                    String fullUrl = ApiClient.BASE_MEDIA_URL + media.getMediaFile().getUrl();
                    if ("SERIAL_NUMBER_PHOTO".equals(media.getType())) {
                        serialNumberPhotoItem = new AssetPhotosAdapter.PhotoItem(fullUrl, media.getId());
                    } else if ("ASSET_PHOTO".equals(media.getType())) {
                        assetPhotoItems.add(new AssetPhotosAdapter.PhotoItem(fullUrl, media.getId()));
                    }
                }
            }
        }
        updateSerialPhotoUI();
        photosAdapter.notifyDataSetChanged();
    }

    private void updateSerialPhotoUI() {
        if (serialNumberPhotoItem != null) {
            Glide.with(this).load(serialNumberPhotoItem.getModel()).error(R.drawable.ic_broken_image).into(imageViewSerialNumber);
            buttonViewSerialPhoto.setVisibility(View.VISIBLE);
        } else {
            imageViewSerialNumber.setImageResource(0); // Kosongkan gambar
            buttonViewSerialPhoto.setVisibility(View.GONE);
        }
    }

    private void clearAllPhotos() {
        serialNumberPhotoItem = null;
        assetPhotoItems.clear();
        updateSerialPhotoUI();
        if (photosAdapter != null) {
            photosAdapter.notifyDataSetChanged();
        }
    }

    // --- Data Collection for Saving ---
    @Override
    public void collectDataForSave() {
        List<Uri> newSerialUri = new ArrayList<>();
        List<String> existingSerialIds = new ArrayList<>();
        if (serialNumberPhotoItem != null) {
            if (serialNumberPhotoItem.localUri != null) {
                newSerialUri.add(serialNumberPhotoItem.localUri);
            } else {
                existingSerialIds.add(serialNumberPhotoItem.mediaId);
            }
        }

        List<Uri> newAssetUris = assetPhotoItems.stream()
                .filter(item -> item.localUri != null)
                .map(item -> item.localUri)
                .collect(Collectors.toList());

        List<String> existingAssetIds = assetPhotoItems.stream()
                .filter(item -> item.remoteUrl != null)
                .map(item -> item.mediaId)
                .collect(Collectors.toList());

        viewModel.updateDocumentsData(newSerialUri, existingSerialIds, newAssetUris, existingAssetIds);
    }

    // --- Camera Logic ---
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
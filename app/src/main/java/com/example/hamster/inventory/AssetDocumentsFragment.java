// Buat di dalam package inventory
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
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.hamster.R;
import java.io.File;

public class AssetDocumentsFragment extends Fragment {

    private AssetDetailViewModel viewModel;
    private ImageView imageViewAssetPhoto;
    private Uri imageUri;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
                if (result) {
                    imageViewAssetPhoto.setImageURI(imageUri);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asset_documents, container, false);

        imageViewAssetPhoto = view.findViewById(R.id.imageViewAssetPhoto);
        Button buttonTakePhoto = view.findViewById(R.id.buttonTakePhoto);

        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);

        buttonTakePhoto.setOnClickListener(v -> {
            checkCameraPermissionAndLaunch();
        });

        return view;
    }

    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        File imagePath = new File(getContext().getFilesDir(), "images");
        if (!imagePath.exists()) imagePath.mkdirs();
        File newFile = new File(imagePath, "asset_photo.jpg");
        imageUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", newFile);
        takePictureLauncher.launch(imageUri);
    }
}
package com.example.hamster.inventory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hamster.R;
import com.example.hamster.data.model.AssetMediaFile;
import com.example.hamster.data.network.ApiClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.List;

public class AssetReviewFragment extends Fragment implements FragmentDataCollector {

    private AssetDetailViewModel viewModel;

    // Document Lists
    private final List<DocumentItem> licenseDocs = new ArrayList<>();
    private final List<DocumentItem> userManualDocs = new ArrayList<>();
    private final List<DocumentItem> customDocs = new ArrayList<>();

    // Adapters
    private DocumentAdapter licenseAdapter, userManualAdapter, customAdapter;

    // Views
    private TextInputEditText etCustomDocName;
    private TextInputLayout tilCustomDocName;

    // File Picker
    private Uri tempCustomFileUri;
    private ActivityResultLauncher<String[]> filePickerLauncher;
    private String currentPickingType = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AssetDetailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRecyclerViews(view);
        setupFilePicker();
        setupListeners(view);
        observeViewModel();
    }

    private void initializeViews(View view) {
        etCustomDocName = view.findViewById(R.id.etCustomDocName);
        tilCustomDocName = view.findViewById(R.id.tilCustomDocName);
    }

    private void setupRecyclerViews(View view) {
        // License Documents
        RecyclerView recyclerLicense = view.findViewById(R.id.recyclerLicense);
        recyclerLicense.setLayoutManager(new LinearLayoutManager(getContext()));
        licenseAdapter = new DocumentAdapter(getContext(), licenseDocs, createDocClickListener(licenseDocs));
        recyclerLicense.setAdapter(licenseAdapter);

        // User Manual Documents
        RecyclerView recyclerUserManual = view.findViewById(R.id.recyclerUserManual);
        recyclerUserManual.setLayoutManager(new LinearLayoutManager(getContext()));
        userManualAdapter = new DocumentAdapter(getContext(), userManualDocs, createDocClickListener(userManualDocs));
        recyclerUserManual.setAdapter(userManualAdapter);

        // Custom Documents
        RecyclerView recyclerCustom = view.findViewById(R.id.recyclerCustomDocs);
        recyclerCustom.setLayoutManager(new LinearLayoutManager(getContext()));
        customAdapter = new DocumentAdapter(getContext(), customDocs, createDocClickListener(customDocs));
        recyclerCustom.setAdapter(customAdapter);
    }

    private void setupFilePicker() {
        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri != null) {
                handlePickedFile(uri);
            }
        });
    }

    private void handlePickedFile(Uri uri) {
        switch (currentPickingType) {
            case "LICENSE_DOCUMENT":
                licenseDocs.add(new DocumentItem(uri, null, "LICENSE_DOCUMENT"));
                licenseAdapter.notifyItemInserted(licenseDocs.size() - 1);
                break;
            case "USER_MANUAL_DOCUMENT":
                userManualDocs.add(new DocumentItem(uri, null, "USER_MANUAL_DOCUMENT"));
                userManualAdapter.notifyItemInserted(userManualDocs.size() - 1);
                break;
            case "CUSTOM_DOCUMENT":
                tempCustomFileUri = uri;
                Toast.makeText(getContext(), "File selected. Add a name and press ADD.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setupListeners(View view) {
        view.findViewById(R.id.btnChooseLicense).setOnClickListener(v -> {
            currentPickingType = "LICENSE_DOCUMENT";
            filePickerLauncher.launch(new String[]{"image/*", "application/pdf"});
        });

        view.findViewById(R.id.btnChooseUserManual).setOnClickListener(v -> {
            currentPickingType = "USER_MANUAL_DOCUMENT";
            filePickerLauncher.launch(new String[]{"image/*", "application/pdf"});
        });

        view.findViewById(R.id.btnChooseCustomFile).setOnClickListener(v -> {
            currentPickingType = "CUSTOM_DOCUMENT";
            filePickerLauncher.launch(new String[]{"image/*", "application/pdf"});
        });

        view.findViewById(R.id.btnAddCustomDoc).setOnClickListener(v -> addCustomDocument());
    }

    private void addCustomDocument() {
        String name = etCustomDocName.getText().toString().trim();
        if (name.isEmpty()) {
            tilCustomDocName.setError("Document name is required");
            return;
        }
        if (tempCustomFileUri == null) {
            Toast.makeText(getContext(), "Please select a file first", Toast.LENGTH_SHORT).show();
            return;
        }
        tilCustomDocName.setError(null);

        customDocs.add(new DocumentItem(tempCustomFileUri, name, "CUSTOM_DOCUMENT"));
        customAdapter.notifyItemInserted(customDocs.size() - 1);

        etCustomDocName.setText("");
        tempCustomFileUri = null;
    }

    private void observeViewModel() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null || asset.getMediaFiles() == null) return;

            if (licenseDocs.isEmpty() && userManualDocs.isEmpty() && customDocs.isEmpty()) {
                for (AssetMediaFile media : asset.getMediaFiles()) {
                    String url = ApiClient.BASE_MEDIA_URL + media.getMediaFile().getUrl();
                    String originalName = (media.getName() != null) ? media.getName() : media.getMediaFile().getOriginalName();

                    switch (media.getType()) {
                        case "LICENSE_DOCUMENT":
                            licenseDocs.add(new DocumentItem(url, media.getId(), originalName, media.getType()));
                            break;
                        case "USER_MANUAL_DOCUMENT":
                            userManualDocs.add(new DocumentItem(url, media.getId(), originalName, media.getType()));
                            break;
                        case "CUSTOM_DOCUMENT":
                            customDocs.add(new DocumentItem(url, media.getId(), media.getName(), media.getType()));
                            break;
                    }
                }
                licenseAdapter.notifyDataSetChanged();
                userManualAdapter.notifyDataSetChanged();
                customAdapter.notifyDataSetChanged();
            }
        });
    }

    private DocumentAdapter.DocumentClickListener createDocClickListener(List<DocumentItem> list) {
        return new DocumentAdapter.DocumentClickListener() {
            @Override
            public void onDelete(DocumentItem item) {
                int position = list.indexOf(item);
                if (position != -1) {
                    list.remove(position);
                    if (list == licenseDocs) licenseAdapter.notifyItemRemoved(position);
                    else if (list == userManualDocs) userManualAdapter.notifyItemRemoved(position);
                    else if (list == customDocs) customAdapter.notifyItemRemoved(position);
                }
            }

            @Override
            public void onDownload(DocumentItem item) {
                if (item.isExisting() && getContext() != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.remoteUrl));
                    getContext().startActivity(intent);
                }
            }
        };
    }

    @Override
    public void collectDataForSave() {
        viewModel.updateReviewDocuments(licenseDocs, userManualDocs, customDocs);
    }
}
package com.example.hamster.inventory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.AssetMediaFile;
import com.example.hamster.data.network.ApiClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    private Uri tempCustomFileUri;

    private ActivityResultLauncher<String[]> filePickerLauncher;

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
        setupObservers();
    }

    private void initializeViews(View view) {
        etCustomDocName = view.findViewById(R.id.etCustomDocName);
        tilCustomDocName = view.findViewById(R.id.tilCustomDocName);
    }

    private void setupRecyclerViews(View view) {
        // License
        RecyclerView recyclerLicense = view.findViewById(R.id.recyclerLicense);
        recyclerLicense.setLayoutManager(new LinearLayoutManager(getContext()));
        licenseAdapter = new DocumentAdapter(getContext(), licenseDocs, createDocClickListener(licenseDocs));
        recyclerLicense.setAdapter(licenseAdapter);

        // User Manual
        RecyclerView recyclerUserManual = view.findViewById(R.id.recyclerUserManual);
        recyclerUserManual.setLayoutManager(new LinearLayoutManager(getContext()));
        userManualAdapter = new DocumentAdapter(getContext(), userManualDocs, createDocClickListener(userManualDocs));
        recyclerUserManual.setAdapter(userManualAdapter);

        // Custom
        RecyclerView recyclerCustom = view.findViewById(R.id.recyclerCustomDocs);
        recyclerCustom.setLayoutManager(new LinearLayoutManager(getContext()));
        customAdapter = new DocumentAdapter(getContext(), customDocs, createDocClickListener(customDocs));
        recyclerCustom.setAdapter(customAdapter);
    }

    private void setupFilePicker() {
        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri != null) {
                tempCustomFileUri = uri;
                Toast.makeText(getContext(), "File selected. Add name and press ADD.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners(View view) {
        view.findViewById(R.id.btnChooseLicense).setOnClickListener(v -> pickFile(licenseDocs, "LICENSE_DOCUMENT"));
        view.findViewById(R.id.btnChooseUserManual).setOnClickListener(v -> pickFile(userManualDocs, "USER_MANUAL_DOCUMENT"));

        view.findViewById(R.id.btnChooseCustomFile).setOnClickListener(v -> {
            filePickerLauncher.launch(new String[]{"image/jpeg", "image/png", "application/pdf"});
        });

        view.findViewById(R.id.btnAddCustomDoc).setOnClickListener(v -> addCustomDocument());
    }

    private void pickFile(List<DocumentItem> docList, String docType) {
        ActivityResultLauncher<String[]> singleFileLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(), uri -> {
                    if (uri != null) {
                        docList.clear();
                        docList.add(new DocumentItem(uri, null, docType));

                        if (docType.equals("LICENSE_DOCUMENT")) {
                            licenseAdapter.notifyDataSetChanged();
                        } else if (docType.equals("USER_MANUAL_DOCUMENT")) {
                            userManualAdapter.notifyDataSetChanged();
                        }
                    }
                });
        singleFileLauncher.launch(new String[]{"image/jpeg", "image/png", "application/pdf"});
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


    private void setupObservers() {
        viewModel.getAssetData().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null) return;

            licenseDocs.clear();
            userManualDocs.clear();
            customDocs.clear();

            if (asset.getMediaFiles() != null) {
                for (AssetMediaFile media : asset.getMediaFiles()) {
                    String url = ApiClient.BASE_MEDIA_URL + media.getMediaFile().getUrl();
                    switch (media.getType()) {
                        case "LICENSE_DOCUMENT":
                            licenseDocs.add(new DocumentItem(url, media.getId(), media.getMediaFile().getOriginalName(), media.getType()));
                            break;
                        case "USER_MANUAL_DOCUMENT":
                            userManualDocs.add(new DocumentItem(url, media.getId(), media.getMediaFile().getOriginalName(), media.getType()));
                            break;
                        case "CUSTOM_DOCUMENT":
                            customDocs.add(new DocumentItem(url, media.getId(), media.getName(), media.getType()));
                            break;
                    }
                }
            }
            licenseAdapter.notifyDataSetChanged();
            userManualAdapter.notifyDataSetChanged();
            customAdapter.notifyDataSetChanged();
        });
    }

    private DocumentAdapter.DocumentClickListener createDocClickListener(List<DocumentItem> list) {
        return new DocumentAdapter.DocumentClickListener() {
            @Override
            public void onDelete(DocumentItem item) {
                int position = list.indexOf(item);
                if (position != -1) {
                    list.remove(position);
                    // Determine which adapter to notify
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
    }
}
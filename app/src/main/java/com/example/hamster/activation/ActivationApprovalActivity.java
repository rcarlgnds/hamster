package com.example.hamster.activation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hamster.R;
import com.example.hamster.data.model.ApprovalItem;
import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.AssetMediaFile;
import com.example.hamster.data.network.ApiClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivationApprovalActivity extends AppCompatActivity {

    private ActivationApprovalViewModel viewModel;
    private RecyclerView recyclerView;
    private ApprovalAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyMessage;
    private ApprovalItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation_approval);

        setupToolbar();
        setupViews();
        setupViewModel();
        setupRecyclerView();
        setupObservers();

        viewModel.fetchPendingApprovals();
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
        progressBar = findViewById(R.id.progressBar);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        recyclerView = findViewById(R.id.recyclerViewApprovals);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ActivationApprovalViewModel.class);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApprovalAdapter(this, new ArrayList<>(), this::onConfirmationClicked);
        recyclerView.setAdapter(adapter);
    }

    private void onConfirmationClicked(ApprovalItem item) {
        this.currentItem = item;
        viewModel.fetchAssetDetailsById(item.getAssetId());
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getApprovalList().observe(this, items -> {
            boolean isEmpty = items == null || items.isEmpty();
            tvEmptyMessage.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            if (!isEmpty) {
                adapter.updateData(items);
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getApprovalResult().observe(this, success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(this, "Approval processed successfully", Toast.LENGTH_SHORT).show();
                    viewModel.fetchPendingApprovals();
                } else {
                }
            }
        });

        viewModel.getAssetDetails().observe(this, asset -> {
            if (asset != null) {
                showConfirmationDialog();
            }
        });
    }

    private void showConfirmationDialog() {
        if (currentItem == null) {
            Toast.makeText(this, "Error: No item data found.", Toast.LENGTH_SHORT).show();
            return;
        }
        Asset asset = viewModel.getAssetDetails().getValue();
        if (asset == null) {
            Toast.makeText(this, "Error: Asset details not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_approval_confirmation, null);
        builder.setView(dialogView);

        ImageView ivAssetPhoto = dialogView.findViewById(R.id.ivAssetPhoto);
        TextView tvScannedBy = dialogView.findViewById(R.id.tvScannedBy);
        TextInputEditText etRemarks = dialogView.findViewById(R.id.etRemarks);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnReject = dialogView.findViewById(R.id.btnReject);
        Button btnApprove = dialogView.findViewById(R.id.btnApprove);

        String photoUrl = findActivationPhotoUrl(asset);
        Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.ic_broken_image)
                .error(R.drawable.ic_broken_image)
                .into(ivAssetPhoto);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        long timestampInMillis = currentItem.getCreatedAt() * 1000L;
        String formattedDate = sdf.format(new Date(timestampInMillis));
        String scannedByText = "Request at " + formattedDate;
        tvScannedBy.setText(scannedByText);

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnReject.setOnClickListener(v -> {
            String remarks = etRemarks.getText() != null ? etRemarks.getText().toString() : "";
            viewModel.submitApproval(currentItem.getTransactionId(), false, remarks);
            dialog.dismiss();
        });

        btnApprove.setOnClickListener(v -> {
            String remarks = etRemarks.getText() != null ? etRemarks.getText().toString() : "";
            viewModel.submitApproval(currentItem.getTransactionId(), true, remarks);
            dialog.dismiss();
        });
    }

    private String findActivationPhotoUrl(Asset asset) {
        if (asset.getMediaFiles() != null) {
            for (AssetMediaFile mediaFile : asset.getMediaFiles()) {
                if ("ASSET_PHOTO".equals(mediaFile.getType()) && mediaFile.getMediaFile() != null) {
                    return ApiClient.BASE_MEDIA_URL + mediaFile.getMediaFile().getUrl();
                }
            }
        }
        return null;
    }
}
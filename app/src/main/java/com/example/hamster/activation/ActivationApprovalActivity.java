package com.example.hamster.activation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.hamster.data.model.Asset;
import com.example.hamster.data.model.AssetMediaFile;
import com.example.hamster.data.network.ApiClient;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ActivationApprovalActivity extends AppCompatActivity {

    private ActivationApprovalViewModel viewModel;
    private RecyclerView recyclerView;
    private ApprovalAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation_approval);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        progressBar = findViewById(R.id.progressBar);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        recyclerView = findViewById(R.id.recyclerViewApprovals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ApprovalAdapter(new ArrayList<>(), this::showConfirmationDialog);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ActivationApprovalViewModel.class);
        setupObservers();

        viewModel.fetchPendingApprovals();
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if(isLoading) {
                recyclerView.setVisibility(View.GONE);
                tvEmptyMessage.setVisibility(View.GONE);
            }
        });

        viewModel.getApprovalList().observe(this, assets -> {
            if (assets != null && !assets.isEmpty()) {
                adapter.updateData(assets);
                recyclerView.setVisibility(View.VISIBLE);
                tvEmptyMessage.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                tvEmptyMessage.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getApprovalResult().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Activation Approval got approved successfully", Toast.LENGTH_SHORT).show();
                viewModel.fetchPendingApprovals();
            }
        });
    }

    private void showConfirmationDialog(Asset asset) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_approval_confirmation, null);
        builder.setView(dialogView);

        ImageView ivAssetPhoto = dialogView.findViewById(R.id.ivAssetPhoto);
        TextView tvScannedBy = dialogView.findViewById(R.id.tvScannedBy);
        EditText etRemarks = dialogView.findViewById(R.id.etRemarks);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnReject = dialogView.findViewById(R.id.btnReject);
        Button btnApprove = dialogView.findViewById(R.id.btnApprove);

        String photoUrl = findActivationPhotoUrl(asset);
        Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.ic_broken_image)
                .into(ivAssetPhoto);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(new Date(asset.getCreatedAt()));
        String scannedByText = "Scanned by Admin at " + formattedDate;
        tvScannedBy.setText(scannedByText);

        final AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnReject.setOnClickListener(v -> {
            String remarks = etRemarks.getText().toString();
            viewModel.submitApproval(asset.getId(), false, remarks);
            dialog.dismiss();
        });

        btnApprove.setOnClickListener(v -> {
            String remarks = etRemarks.getText().toString();
            viewModel.submitApproval(asset.getId(), true, remarks);
            dialog.dismiss();
        });

        dialog.show();
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
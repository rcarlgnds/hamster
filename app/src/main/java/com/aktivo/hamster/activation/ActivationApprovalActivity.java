package com.aktivo.hamster.activation;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.aktivo.hamster.R;
import com.aktivo.hamster.data.model.ActivationDetailData;
import com.aktivo.hamster.data.model.ApprovalItem;
import com.aktivo.hamster.data.network.ApiClient;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

        viewModel.refresh();
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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        viewModel.loadMoreItems();
                    }
                }
            }
        });
    }
    private void onConfirmationClicked(ApprovalItem item) {
        this.currentItem = item;
        viewModel.fetchActivationDetails(item.getTransactionId());
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
                }
            }
        });

        viewModel.getActivationDetails().observe(this, details -> {
            if (details != null) {
                showConfirmationDialog(details);
            }
        });
    }

    private void showConfirmationDialog(ActivationDetailData details) {
        if (currentItem == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_approval_confirmation, null);
        builder.setView(dialogView);

        ImageView ivAssetPhoto = dialogView.findViewById(R.id.ivAssetPhoto);
        ProgressBar imageProgressBar = dialogView.findViewById(R.id.imageProgressBar);
        TextView tvScannedBy = dialogView.findViewById(R.id.tvScannedBy);
        Button btnReject = dialogView.findViewById(R.id.btnReject);
        Button btnApprove = dialogView.findViewById(R.id.btnApprove);

        imageProgressBar.setVisibility(View.VISIBLE);

        String photoUrl = null;
        if (details.getImage() != null && details.getImage().getUrl() != null) {
            photoUrl = ApiClient.BASE_MEDIA_URL + details.getImage().getUrl();
        }

        Glide.with(this)
                .load(photoUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imageProgressBar.setVisibility(View.GONE);
                        ivAssetPhoto.setImageResource(R.drawable.ic_broken_image);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(ivAssetPhoto);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        long timestampInMillis = details.getStartedAt() * 1000L;
        String scannedUser = details.getStartedBy().getFirstName();
        String formattedDate = sdf.format(new Date(timestampInMillis));
        String scannedByText = "Scanned by " + scannedUser + " at " + formattedDate;
        tvScannedBy.setText(scannedByText);

        final AlertDialog dialog = builder.create();
        dialog.show();

        LinearLayout rejectReasonLayout = dialog.findViewById(R.id.rejectReasonLayout);
        Spinner spinnerReasonCategory = dialog.findViewById(R.id.spinnerReasonCategory);

        List<String> reasons = Arrays.asList("Asset does not meet request", "Asset delivered to the wrong location");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                dialog.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                reasons
        );
        spinnerReasonCategory.setAdapter(adapter);

        btnReject.setOnClickListener(v -> {
            if (rejectReasonLayout.getVisibility() == View.GONE) {
                rejectReasonLayout.setVisibility(View.VISIBLE);
                btnReject.setText("Submit Reject");
                return;
            }

            String remark = "";
            String remarkSpinner = spinnerReasonCategory.getSelectedItem().toString();
            if(("Asset does not meet request").equalsIgnoreCase(remarkSpinner)) {
                remark = "DOES_NOT_MEET_REQUEST";
            } else if(("Asset delivered to the wrong location").equalsIgnoreCase(remarkSpinner)) {
                remark = "WRONG_LOCATION";
            }

            viewModel.submitApproval(currentItem.getTransactionId(), false, remark);

            dialog.dismiss();
        });

        btnApprove.setOnClickListener(v -> {
            viewModel.submitApproval(currentItem.getTransactionId(), true, null);
            dialog.dismiss();
        });
    }
}
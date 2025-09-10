package com.example.hamster.activation;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hamster.R;
import com.example.hamster.data.model.AssetRejected;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class RejectedAssetActivity extends AppCompatActivity {
    private RejectedAssetViewModel viewModel;
    private RecyclerView recyclerView;
    private RejectedAssetAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyMessage;
    private AssetRejected currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejected_asset);

        setupToolbar();
        setupViews();
        setupViewModel();
        setupRecyclerView();
        setupObservers();

        viewModel.fetchRejectedAssets();
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
        recyclerView = findViewById(R.id.recyclerViewRejectedAssets);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(RejectedAssetViewModel.class);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RejectedAssetAdapter(this, new ArrayList<>(), this::onActionClicked);
        recyclerView.setAdapter(adapter);
    }

    private void onActionClicked(AssetRejected item) {
        this.currentItem = item;
//      fetch detail
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getRejectedList().observe(this, items -> {
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
    }
}
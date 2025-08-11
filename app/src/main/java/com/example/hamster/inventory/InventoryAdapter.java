package com.example.hamster.inventory;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hamster.R;
import com.example.hamster.data.model.Asset;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<Asset> assetList;

    public InventoryAdapter(List<Asset> assetList) {
        // Initialize with a new list to prevent modification issues
        this.assetList = new ArrayList<>(assetList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Asset asset = assetList.get(position);
        holder.bind(asset);
    }

    @Override
    public int getItemCount() {
        return assetList.size();
    }

    public void updateData(List<Asset> newAssets) {
        this.assetList.clear();
        if (newAssets != null) {
            this.assetList.addAll(newAssets);
        }
        notifyDataSetChanged();
    }

    public List<Asset> getAssetList() {
        return this.assetList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardRoot;
        TextView textViewItemName;
        TextView textViewItemCode;
        TextView textViewStatusBadge;
        ImageView iconCopy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.card_root);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            textViewItemCode = itemView.findViewById(R.id.textViewItemCode);
            textViewStatusBadge = itemView.findViewById(R.id.textViewStatusBadge);
            iconCopy = itemView.findViewById(R.id.iconCopy);
        }

        public void bind(final Asset asset) {
            textViewItemName.setText(asset.getName());
            textViewItemCode.setText(asset.getCode());

            if ("Active".equalsIgnoreCase(asset.getStatus())) {
                textViewStatusBadge.setText(R.string.status_active);
                textViewStatusBadge.setBackgroundResource(R.drawable.bg_badge_activate_background);
            } else {
                textViewStatusBadge.setText(R.string.status_inactive);
                textViewStatusBadge.setBackgroundResource(R.drawable.bg_badge_inactivate_background);
            }

            cardRoot.setOnClickListener(v -> {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, AssetDetailActivity.class);
                intent.putExtra("ASSET_ID", asset.getId());
                context.startActivity(intent);
            });

            iconCopy.setOnClickListener(v -> {
                Context context = itemView.getContext();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Asset Code", asset.getCode());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
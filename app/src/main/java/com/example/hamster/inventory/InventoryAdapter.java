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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hamster.R;
import com.example.hamster.data.model.Asset;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private final List<Asset> assetList;
    private int copiedPosition = -1;

    public InventoryAdapter(List<Asset> assetList) {
        this.assetList = assetList;
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
        Context context = holder.itemView.getContext();

        holder.itemName.setText(asset.getName());
        holder.itemCode.setText(asset.getCode());

        if (asset.getStatus() != null) {
            holder.itemStatus.setText(asset.getStatus());
            if (asset.getStatus().equalsIgnoreCase("Active")) {
                holder.itemStatus.setBackgroundResource(R.drawable.badge_activate_background);
                holder.itemStatus.setTextColor(ContextCompat.getColor(context, R.color.md_theme_on_primary_container));
            } else {
                holder.itemStatus.setBackgroundResource(R.drawable.badge_inactivate_background);
                holder.itemStatus.setTextColor(ContextCompat.getColor(context, R.color.md_theme_on_inactive_badge));
            }

        }

        final boolean isCopied = position == copiedPosition;
        int colorToApply = isCopied ?
                ContextCompat.getColor(context, R.color.yellow_20) :
                ContextCompat.getColor(context, R.color.yellow_10);

        holder.itemCode.setTextColor(colorToApply);
        holder.iconCopy.setColorFilter(colorToApply);

        holder.codeLayout.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Item Code", asset.getCode());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Code copied: " + asset.getCode(), Toast.LENGTH_SHORT).show();

            if (copiedPosition != -1) {
                notifyItemChanged(copiedPosition);
            }
            copiedPosition = holder.getAdapterPosition();
            notifyItemChanged(copiedPosition);
        });

        holder.detailsLayout.setOnClickListener(v -> {
            Toast.makeText(context, "Buka detail untuk: " + asset.getName(), Toast.LENGTH_SHORT).show();
        });

        holder.detailsLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, AssetDetailActivity.class);
            intent.putExtra("ASSET_ID", asset.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return assetList != null ? assetList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemCode, itemStatus;
        LinearLayout codeLayout, detailsLayout;
        ImageView iconCopy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.textViewItemName);
            itemCode = itemView.findViewById(R.id.textViewItemCode);
            itemStatus = itemView.findViewById(R.id.textViewStatusBadge);
            codeLayout = itemView.findViewById(R.id.layoutCode);
            detailsLayout = itemView.findViewById(R.id.layoutClickForDetails);
            iconCopy = itemView.findViewById(R.id.iconCopy);
        }
    }
}
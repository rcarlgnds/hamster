package com.example.hamster.inventory;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import com.google.android.material.chip.Chip;

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
        holder.itemStatus.setText(asset.getStatus());

        final boolean isCopied = position == copiedPosition;

        int colorToApply = isCopied ?
                ContextCompat.getColor(context, R.color.yellow_80) :
                ContextCompat.getColor(context, R.color.yellow_10);

        holder.itemCode.setTextColor(colorToApply);
        holder.iconCopy.setColorFilter(colorToApply);
        //

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
    }

    @Override
    public int getItemCount() {
        return assetList != null ? assetList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemCode;
        Chip itemStatus;
        LinearLayout codeLayout, detailsLayout;
        ImageView iconCopy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.textViewItemName);
            itemCode = itemView.findViewById(R.id.textViewItemCode);
            itemStatus = itemView.findViewById(R.id.chipStatus);
            codeLayout = itemView.findViewById(R.id.layoutCode);
            detailsLayout = itemView.findViewById(R.id.layoutClickForDetails);
            iconCopy = itemView.findViewById(R.id.iconCopy);
        }
    }
}
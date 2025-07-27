package com.example.hamster.inventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hamster.R;
import com.example.hamster.data.model.Asset;
import com.google.android.material.chip.Chip;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<Asset> assetList;

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
        holder.itemName.setText(asset.getName());
        holder.itemCode.setText(asset.getCode());
        holder.itemStatus.setText(asset.getStatus());
    }

    @Override
    public int getItemCount() {
        return assetList != null ? assetList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemCode;
        Chip itemStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.textViewItemName);
            itemCode = itemView.findViewById(R.id.textViewItemCode);
            itemStatus = itemView.findViewById(R.id.chipStatus);
        }
    }
}
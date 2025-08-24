package com.example.hamster.activation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hamster.R;
import com.example.hamster.data.model.Asset;

import java.util.List;

public class ApprovalAdapter extends RecyclerView.Adapter<ApprovalAdapter.ApprovalViewHolder> {

    private List<Asset> assetList;
    private final OnConfirmationClickListener listener;

    public interface OnConfirmationClickListener {
        void onConfirmClick(Asset asset);
    }

    public ApprovalAdapter(List<Asset> assetList, OnConfirmationClickListener listener) {
        this.assetList = assetList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ApprovalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_approval, parent, false);
        return new ApprovalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovalViewHolder holder, int position) {
        Asset asset = assetList.get(position);
        holder.bind(asset, listener);
    }

    @Override
    public int getItemCount() {
        return assetList.size();
    }

    public void updateData(List<Asset> newAssetList) {
        this.assetList = newAssetList;
        notifyDataSetChanged();
    }

    static class ApprovalViewHolder extends RecyclerView.ViewHolder {
        TextView tvAssetCode, tvAssetName, tvRole, tvRoom;
        Button btnConfirmation;

        public ApprovalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAssetCode = itemView.findViewById(R.id.tvAssetCode);
            tvAssetName = itemView.findViewById(R.id.tvAssetName);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvRoom = itemView.findViewById(R.id.tvRoom);
            btnConfirmation = itemView.findViewById(R.id.btnConfirmation);
        }

        public void bind(final Asset asset, final OnConfirmationClickListener listener) {
            tvAssetCode.setText(asset.getCode());
            tvAssetName.setText(asset.getName());

            if (asset.getRoom() != null) {
                tvRoom.setText(asset.getRoom().getName());
            } else {
                tvRoom.setText("-");
            }

            tvRole.setText("Inventory Confirmation");

            btnConfirmation.setOnClickListener(v -> listener.onConfirmClick(asset));
        }
    }
}
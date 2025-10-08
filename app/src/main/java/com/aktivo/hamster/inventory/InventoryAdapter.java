package com.aktivo.hamster.inventory;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aktivo.hamster.R;
import com.aktivo.hamster.data.constant.AssetStatus;
import com.aktivo.hamster.data.model.Asset;
import com.aktivo.hamster.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<Asset> assetList;
    private final OnItemClickListener listener;
    private Map<String, Boolean> commissioningStatusMap;

    public interface OnItemClickListener {
        void onWorkOrderClick(Asset asset);
        void onNeedInstallationClick(Asset asset);
    }

    public InventoryAdapter(List<Asset> assetList, OnItemClickListener listener, Map<String, Boolean> commissioningStatusMap) {
        this.assetList = new ArrayList<>(assetList);
        this.listener = listener;
        this.commissioningStatusMap = commissioningStatusMap;
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
        Boolean isCommissioned = commissioningStatusMap.get(asset.getId());
        holder.bind(asset, listener, isCommissioned);
    }

    @Override
    public int getItemCount() {
        return assetList.size();
    }

    public void updateData(List<Asset> newAssets, Map<String, Boolean> newStatusMap) {
        this.assetList.clear();
        if (newAssets != null) {
            this.assetList.addAll(newAssets);
        }
        this.commissioningStatusMap = newStatusMap;
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
        MaterialButton btnWorkOrder, btnCommissioning;
        SessionManager sessionManager;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sessionManager = new SessionManager(itemView.getContext());
            cardRoot = itemView.findViewById(R.id.card_root);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            textViewItemCode = itemView.findViewById(R.id.textViewItemCode);
            textViewStatusBadge = itemView.findViewById(R.id.textViewStatusBadge);
            iconCopy = itemView.findViewById(R.id.iconCopy);
            btnCommissioning = itemView.findViewById(R.id.btnCommissioning);
            btnWorkOrder = itemView.findViewById(R.id.btnWorkOrder);
        }

        private String formatDate(Long timestamp) {
            if (timestamp == null) return "";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.format(new Date(timestamp * 1000));
        }

        public void bind(final Asset asset, final OnItemClickListener listener, final Boolean isCommissioned) {
            textViewItemName.setText(asset.getName());
            textViewItemCode.setText(asset.getCode());

            String activeStatus =
                    itemView.getContext().getString(R.string.status_active) +
                            " - " +
                            formatDate(asset.getEffectiveUsageDate());

            if (AssetStatus.ACTIVE.equalsIgnoreCase(asset.getStatus())) {
                textViewStatusBadge.setText(activeStatus);
                textViewStatusBadge.setBackgroundResource(R.drawable.bg_badge_activate_background);
                textViewStatusBadge.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
            } else if(AssetStatus.INACTIVE.equalsIgnoreCase(asset.getStatus())) {
                textViewStatusBadge.setText(R.string.status_inactive);
                textViewStatusBadge.setBackgroundResource(R.drawable.bg_badge_inactivate_background);
                textViewStatusBadge.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
            } else if(AssetStatus.REJECTED.equalsIgnoreCase(asset.getStatus())) {
                textViewStatusBadge.setText(R.string.status_rejected);
                textViewStatusBadge.setBackgroundResource(R.drawable.bg_badge_rejected_background);
                textViewStatusBadge.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            } else if(AssetStatus.PENDING_CONFIRMATION_BY_HEAD_OF_ROOM.equalsIgnoreCase(asset.getStatus())) {
                textViewStatusBadge.setText(R.string.status_pending_confirmation_by_head_of_room);
                textViewStatusBadge.setBackgroundResource(R.drawable.bg_badge_inactivate_background);
                textViewStatusBadge.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
            } else if(AssetStatus.PENDING_ACTIVATION_BY_HEAD_OF_FMS.equalsIgnoreCase(asset.getStatus())) {
                textViewStatusBadge.setText(R.string.status_pending_confirmation_by_head_of_fms);
                textViewStatusBadge.setBackgroundResource(R.drawable.bg_badge_inactivate_background);
                textViewStatusBadge.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
            } else if(AssetStatus.REJECTED_DOES_NOT_MEET_REQUEST.equalsIgnoreCase(asset.getStatus())) {
                textViewStatusBadge.setText(R.string.status_rejected_does_not_meet_request);
                textViewStatusBadge.setBackgroundResource(R.drawable.bg_badge_rejected_background);
                textViewStatusBadge.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            } else if(AssetStatus.REJECTED_WRONG_LOCATION.equalsIgnoreCase(asset.getStatus())) {
                textViewStatusBadge.setText(R.string.status_rejected_wrong_location);
                textViewStatusBadge.setBackgroundResource(R.drawable.bg_badge_rejected_background);
                textViewStatusBadge.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            } else if(AssetStatus.REJECTED_IN_LOGISTIC.equalsIgnoreCase(asset.getStatus())) {
                textViewStatusBadge.setText(R.string.status_rejected_in_logistic);
                textViewStatusBadge.setBackgroundResource(R.drawable.bg_badge_rejected_background);
                textViewStatusBadge.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            }

            boolean isConfirmed = asset.getIsConfirmed() != null && asset.getIsConfirmed();
            boolean isRegistered = asset.getRegisterUserId() != null;

            if (isConfirmed || isRegistered) {
                btnWorkOrder.setVisibility(View.GONE);
                btnCommissioning.setVisibility(View.VISIBLE);

                if (isCommissioned != null && isCommissioned) {
                    btnCommissioning.setText("Commisioned");
                    btnCommissioning.setEnabled(false);
                    btnCommissioning.setBackgroundColor(Color.LTGRAY);
                } else {
                    btnCommissioning.setText("Need Installation");
                    btnCommissioning.setEnabled(true);
                    btnCommissioning.setOnClickListener(v -> listener.onNeedInstallationClick(asset));
                }
            } else {
                btnWorkOrder.setVisibility(View.VISIBLE);
                btnCommissioning.setVisibility(View.GONE);
                btnWorkOrder.setOnClickListener(v -> listener.onWorkOrderClick(asset));
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
package com.aktivo.hamster.activation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aktivo.hamster.R;
import com.aktivo.hamster.data.model.AssetRejected;

import java.util.List;

public class RejectedAssetAdapter extends RecyclerView.Adapter<RejectedAssetAdapter.RejectedViewHolder> {

    private final List<AssetRejected> rejectedList;
    private final OnActionClickListener listener;
    private final Context context;

    private static final String STATUS_DOES_NOT_MEET_REQUEST = "REJECTED_DOES_NOT_MEET_REQUEST";
    private static final String STATUS_WRONG_LOCATION = "REJECTED_WRONG_LOCATION";

    public interface OnActionClickListener {
        void onActionClicked(AssetRejected item);
    }

    public RejectedAssetAdapter(Context context, List<AssetRejected> rejectedList, OnActionClickListener listener) {
        this.context = context;
        this.rejectedList = rejectedList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RejectedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_rejected, parent, false);
        return new RejectedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RejectedViewHolder holder, int position) {
        AssetRejected item = rejectedList.get(position);

        holder.tvAssetName.setText(item.getAssetName());
        holder.tvAssetCode.setText(item.getAssetCode());
        holder.tvStatus.setText(item.getStatus());

        holder.tvRejectedBy.setText(String.format("Rejected by: %s", item.getRejectedByPosition()));

        if (holder.tvRejectedAt != null) {
            holder.tvRejectedAt.setVisibility(View.GONE);
        }


        String status = item.getStatus();

        // Logika tombol ini tetap sama
        if (STATUS_DOES_NOT_MEET_REQUEST.equalsIgnoreCase(status)) {
            holder.btnAction.setText("Continue");
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setOnClickListener(v -> listener.onActionClicked(item));
        } else if (STATUS_WRONG_LOCATION.equalsIgnoreCase(status)) {
            holder.btnAction.setText("Confirm Location");
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setOnClickListener(v -> listener.onActionClicked(item));
        } else {
            holder.btnAction.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return rejectedList != null ? rejectedList.size() : 0;
    }

    public void updateData(List<AssetRejected> newRejectedList) {
        this.rejectedList.clear();
        if (newRejectedList != null) {
            this.rejectedList.addAll(newRejectedList);
        }
        notifyDataSetChanged();
    }

    static class RejectedViewHolder extends RecyclerView.ViewHolder {
        TextView tvAssetCode, tvAssetName, tvStatus, tvRejectedBy, tvRejectedAt;
        Button btnAction;

        public RejectedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAssetCode = itemView.findViewById(R.id.tvAssetCode);
            tvAssetName = itemView.findViewById(R.id.tvAssetName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRejectedBy = itemView.findViewById(R.id.tvRejectedBy);
            tvRejectedAt = itemView.findViewById(R.id.tvRejectedAt);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}
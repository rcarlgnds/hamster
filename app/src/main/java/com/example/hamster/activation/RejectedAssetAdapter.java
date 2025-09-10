package com.example.hamster.activation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hamster.R;
import com.example.hamster.data.constant.AssetStatus;
import com.example.hamster.data.model.AssetRejected;

import java.util.List;

public class RejectedAssetAdapter extends RecyclerView.Adapter<RejectedAssetAdapter.RejectedViewHolder> {

    private List<AssetRejected> rejectedList;
    private final OnActionClickListener listener;
    private final Context context;

    public interface OnActionClickListener {
        void onActionClick(AssetRejected item);
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
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return rejectedList.size();
    }

    public void updateData(List<AssetRejected> newRejectedList) {
        this.rejectedList.clear();
        if (newRejectedList != null) {
            this.rejectedList.addAll(newRejectedList);
        }
        notifyDataSetChanged();
    }

    static class RejectedViewHolder extends RecyclerView.ViewHolder {
        TextView tvRejectionStatus, tvAssetCode, tvAssetName, tvRole;
        Button btnAction;

        public RejectedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRejectionStatus = itemView.findViewById(R.id.tvRejectionStatusBadge);
            tvAssetCode = itemView.findViewById(R.id.tvAssetCode);
            tvAssetName = itemView.findViewById(R.id.tvAssetName);
            tvRole = itemView.findViewById(R.id.tvRole);
            btnAction = itemView.findViewById(R.id.btnAction);
        }

        public void bind(final AssetRejected item, final OnActionClickListener listener) {
            if(AssetStatus.REJECTED_DOES_NOT_MEET_REQUEST.equalsIgnoreCase(item.getStatus())) {
                tvRejectionStatus.setText(R.string.status_rejected_does_not_meet_request);
            } else if(AssetStatus.REJECTED_WRONG_LOCATION.equalsIgnoreCase(item.getStatus())) {
                tvRejectionStatus.setText(R.string.status_rejected_wrong_location);
            } else if(AssetStatus.REJECTED_IN_LOGISTIC.equalsIgnoreCase(item.getStatus())) {
                tvRejectionStatus.setText(R.string.status_rejected_in_logistic);
            }

            tvAssetCode.setText(item.getAssetCode());
            tvAssetName.setText(item.getAssetName());
            tvRole.setVisibility(View.GONE);

//            btnAction.setOnClickListener(v -> listener.onActionClick(item));
        }
    }
}

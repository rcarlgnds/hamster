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
import com.example.hamster.data.model.AssetRejected;
import java.util.List;
import java.util.List;

public class RejectedAssetAdapter extends RecyclerView.Adapter<RejectedAssetAdapter.RejectedViewHolder> {

    private List<AssetRejected> rejectedList;
    private final OnActionClickListener listener;
    private final Context context;
    private static final String STATUS_DOES_NOT_MEET_REQUEST = "Does Not Meet Request";
    private static final String STATUS_WRONG_LOCATION = "Wrong Location";


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

        // Set data ke TextViews
        holder.tvAssetName.setText(item.getName());
        holder.tvAssetCode.setText(item.getCode());
        holder.tvStatus.setText(item.getStatus());
        holder.tvRejectedBy.setText(String.format("Rejected by: %s", item.getRejectedBy()));
        holder.tvRejectedAt.setText(String.format("Rejected at: %s", item.getRejectedAt()));

        String status = item.getStatus();

        if (STATUS_DOES_NOT_MEET_REQUEST.equalsIgnoreCase(status)) {
            holder.btnAction.setText("Continue");
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setOnClickListener(v -> listener.onActionClick(item));
        } else if (STATUS_WRONG_LOCATION.equalsIgnoreCase(status)) {
            holder.btnAction.setText("Confirm Location");
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setOnClickListener(v -> listener.onActionClick(item));
        } else {
            holder.btnAction.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return rejectedList != null ? rejectedList.size() : 0;
    }

    // Method untuk mengupdate data di adapter
    public void updateData(List<AssetRejected> newRejectedList) {
        this.rejectedList.clear();
        if (newRejectedList != null) {
            this.rejectedList.addAll(newRejectedList);
        }
        notifyDataSetChanged();
    }

    // ViewHolder class yang sesuai dengan layout list_item_rejected.xml
    static class RejectedViewHolder extends RecyclerView.ViewHolder {
        TextView tvAssetCode, tvAssetName, tvStatus, tvRejectedBy, tvRejectedAt;
        Button btnAction;

        public RejectedViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inisialisasi view dengan ID yang benar dari XML
            tvAssetCode = itemView.findViewById(R.id.tvAssetCode);
            tvAssetName = itemView.findViewById(R.id.tvAssetName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRejectedBy = itemView.findViewById(R.id.tvRejectedBy);
            tvRejectedAt = itemView.findViewById(R.id.tvRejectedAt);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}

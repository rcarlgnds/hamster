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
import com.aktivo.hamster.data.constant.AssetStatus;
import com.aktivo.hamster.data.model.AssetRejected;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RejectedAssetAdapter extends RecyclerView.Adapter<RejectedAssetAdapter.RejectedViewHolder> {

    private final List<AssetRejected> rejectedList;
    private final OnActionClickListener listener;
    private final Context context;
    private static final ZoneId ZONE_JKT = ZoneId.of("Asia/Jakarta");
    private static final DateTimeFormatter OUT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).withZone(ZONE_JKT);

    public String formatToGmt7(String ts) {
        if(ts == null || ts.isEmpty()) return "-";

        try {
            if (ts.matches("\\d+")) {
                long v = Long.parseLong(ts);
                Instant instant = (ts.length() > 12) ? Instant.ofEpochMilli(v) : Instant.ofEpochSecond(v);
                return OUT_FMT.format(instant);
            }
            Instant instant = Instant.parse(ts);
            return OUT_FMT.format(instant);
        } catch (Exception e) {
            return ts;
        }
    }

    public interface OnActionClickListener {
        void onActionClicked(AssetRejected item);
    }

    public RejectedAssetAdapter(Context context, List<AssetRejected> rejectedList, OnActionClickListener listener) {
        this.context = context;
        this.rejectedList = new ArrayList<>(rejectedList);
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

        if(item.getStatus().equalsIgnoreCase(AssetStatus.REJECTED_DOES_NOT_MEET_REQUEST)) {
            holder.tvStatus.setText(R.string.status_rejected_does_not_meet_request);
        } else if(item.getStatus().equalsIgnoreCase(AssetStatus.REJECTED_WRONG_LOCATION)) {
            holder.tvStatus.setText(R.string.status_rejected_wrong_location);
        } else if(item.getStatus().equalsIgnoreCase(AssetStatus.REJECTED_IN_LOGISTIC)) {
            holder.tvStatus.setText(R.string.status_rejected_in_logistic);
        } else {
            holder.tvStatus.setText(item.getStatus());
        }

        if("1".equals(item.getRejectedAtStep())) {
            holder.tvRejectedBy.setText(String.format("Rejected by Head of room at: %s", formatToGmt7(item.getRejectedAt())));
        } else if("2".equals(item.getRejectedAtStep())) {
            holder.tvRejectedBy.setText(String.format("Rejected by Head of FMS at: %s", formatToGmt7(item.getRejectedAt())));
        } else {
            holder.tvRejectedBy.setText(String.format("Rejected by: %s", item.getRejectedByPosition()));
        }

        String status = item.getStatus();
        if (AssetStatus.REJECTED_DOES_NOT_MEET_REQUEST.equalsIgnoreCase(status)) {
            holder.btnAction.setText("Continue");
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setOnClickListener(v -> listener.onActionClicked(item));
        } else if (AssetStatus.REJECTED_WRONG_LOCATION.equalsIgnoreCase(status)) {
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
//            tvRejectedAt = itemView.findViewById(R.id.tvRejectedAt);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}
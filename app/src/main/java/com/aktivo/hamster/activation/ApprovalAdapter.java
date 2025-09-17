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
import com.aktivo.hamster.data.model.ApprovalItem;

import java.util.List;

public class ApprovalAdapter extends RecyclerView.Adapter<ApprovalAdapter.ApprovalViewHolder> {

    private List<ApprovalItem> approvalList;
    private final OnConfirmationClickListener listener;
    private final Context context;

    public interface OnConfirmationClickListener {
        void onConfirmClick(ApprovalItem item);
    }

    public ApprovalAdapter(Context context, List<ApprovalItem> approvalList, OnConfirmationClickListener listener) {
        this.context = context;
        this.approvalList = approvalList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ApprovalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_approval, parent, false);
        return new ApprovalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovalViewHolder holder, int position) {
        ApprovalItem item = approvalList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return approvalList.size();
    }

    public void updateData(List<ApprovalItem> newApprovalList) {
        this.approvalList.clear();
        if (newApprovalList != null) {
            this.approvalList.addAll(newApprovalList);
        }
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

        public void bind(final ApprovalItem item, final OnConfirmationClickListener listener) {
            tvAssetCode.setText(item.getAssetCode());
            tvAssetName.setText(item.getAssetName());
            tvRoom.setText(item.getRoom());
            tvRole.setVisibility(View.GONE);

            btnConfirmation.setOnClickListener(v -> listener.onConfirmClick(item));
        }
    }
}
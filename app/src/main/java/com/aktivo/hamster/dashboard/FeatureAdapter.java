package com.aktivo.hamster.dashboard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aktivo.hamster.R;
import com.aktivo.hamster.activation.ActivationActivity;
import com.aktivo.hamster.activation.ActivationApprovalActivity;
import com.aktivo.hamster.activation.ConfirmationApprovalActivity;
import com.aktivo.hamster.activation.RejectedAssetActivity;
import com.aktivo.hamster.data.constant.Routes;
import com.aktivo.hamster.inventory.InventoryActivity;
import java.util.ArrayList;
import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureAdapter.FeatureViewHolder> {
    public static class Feature {
        public final String name;
        public final int iconResId;
        public final String routeKey;

        public Feature(String name, int iconResId, String routeKey) {
            this.name = name;
            this.iconResId = iconResId;
            this.routeKey = routeKey;
        }
    }

    private final Context context;
    private final List<Feature> featureList;
    private final List<Feature> featureListFull;

    public FeatureAdapter(Context context, List<Feature> featureList) {
        this.context = context;
        this.featureList = new ArrayList<>(featureList);
        this.featureListFull = new ArrayList<>(featureList);
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_feature, parent, false);
        return new FeatureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {
        Feature feature = featureList.get(position);
        holder.bind(feature);
    }

    @Override
    public int getItemCount() {
        return featureList.size();
    }

    public void filter(String text) {
        featureList.clear();
        if (text.isEmpty()) {
            featureList.addAll(featureListFull);
        } else {
            text = text.toLowerCase();
            for (Feature item : featureListFull) {
                if (item.name.toLowerCase().contains(text)) {
                    featureList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    class FeatureViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;

        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iv_feature_icon);
            name = itemView.findViewById(R.id.tv_feature_name);
        }

        public void bind(final Feature feature) {
            name.setText(feature.name);
            icon.setImageResource(feature.iconResId);

            itemView.setOnClickListener(v -> {
                if (Routes.SCAN_QR.equalsIgnoreCase(feature.routeKey)) {
                    context.startActivity(new Intent(context, ScanQrActivity.class));
                }
                else if (Routes.INVENTORY.equalsIgnoreCase(feature.routeKey)) {
                    context.startActivity(new Intent(context, InventoryActivity.class));
                }
                else if (Routes.ACTIVATION.equalsIgnoreCase(feature.routeKey)) {
                    context.startActivity(new Intent(context, ActivationActivity.class));
                }
                else if (Routes.CONFIRMATION.equalsIgnoreCase(feature.routeKey)) {
                    context.startActivity(new Intent(context, ConfirmationApprovalActivity.class));
                }
                else if (Routes.APPROVAL.equalsIgnoreCase(feature.routeKey)) {
                    context.startActivity(new Intent(context, ActivationApprovalActivity.class));
                }
                else if (Routes.REJECTED.equalsIgnoreCase(feature.routeKey)) {
                    context.startActivity(new Intent(context, RejectedAssetActivity.class));
                }
            });
        }
    }
}
package com.aktivo.hamster.inventory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.aktivo.hamster.R;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class AssetPhotosAdapter extends RecyclerView.Adapter<AssetPhotosAdapter.PhotoViewHolder> {

    public interface OnPhotoActionListener {
        void onDeleteClick(int position);
        void onViewClick(PhotoItem item);
    }
    public static class PhotoItem {
        public final Uri localUri;
        public final String remoteUrl;
        public final String mediaId;

        public PhotoItem(Uri localUri) { this.localUri = localUri; this.remoteUrl = null; this.mediaId = null; }
        public PhotoItem(String remoteUrl, String mediaId) { this.localUri = null; this.remoteUrl = remoteUrl; this.mediaId = mediaId; }

        public Object getModel() {
            return localUri != null ? localUri : remoteUrl;
        }

        public boolean isLocal() {
            return localUri != null;
        }
    }

    private final Context context;
    private final List<PhotoItem> photoItems;
    private final OnPhotoActionListener actionListener;

    public AssetPhotosAdapter(Context context, List<PhotoItem> photoItems, OnPhotoActionListener listener) {
        this.context = context;
        this.photoItems = photoItems;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_asset_photo, parent, false);
        return new PhotoViewHolder(view, actionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        PhotoItem item = photoItems.get(position);
        holder.bind(item, context);
    }

    @Override
    public int getItemCount() {
        return photoItems.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAsset;
        MaterialButton buttonDelete, buttonView;

        public PhotoViewHolder(@NonNull View itemView, final OnPhotoActionListener listener) {
            super(itemView);
            imageViewAsset = itemView.findViewById(R.id.imageViewAsset);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonView = itemView.findViewById(R.id.buttonView);

            buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });

            buttonView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                    }
                }
            });
        }

        public void bind(final PhotoItem item, final Context context) {
            Glide.with(context)
                    .load(item.getModel())
                    .error(R.drawable.ic_broken_image)
                    .centerCrop()
                    .into(imageViewAsset);

            buttonView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ImagePreviewActivity.class);
                String url = item.isLocal() ? item.localUri.toString() : item.remoteUrl;
                intent.putExtra("IMAGE_URL", url);
                context.startActivity(intent);
            });
        }
    }
}
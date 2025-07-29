// File: AssetPhotosAdapter.java
package com.example.hamster.inventory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.hamster.R;
import java.util.List;

public class AssetPhotosAdapter extends RecyclerView.Adapter<AssetPhotosAdapter.PhotoViewHolder> {
    public static class PhotoItem {
        public final Uri localUri;
        public final String remoteUrl;
        public final String mediaId;

        public PhotoItem(Uri localUri) { this.localUri = localUri; this.remoteUrl = null; this.mediaId = null; }
        public PhotoItem(String remoteUrl, String mediaId) { this.localUri = null; this.remoteUrl = remoteUrl; this.mediaId = mediaId; }

        public Object getModel() {
            return localUri != null ? localUri : remoteUrl;
        }
    }

    private final Context context;
    private final List<PhotoItem> photoItems;
    private final OnPhotoDeleteListener deleteListener;

    public interface OnPhotoDeleteListener {
        void onDelete(PhotoItem item);
    }

    public AssetPhotosAdapter(Context context, List<PhotoItem> photoItems, OnPhotoDeleteListener listener) {
        this.context = context;
        this.photoItems = photoItems;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_asset_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        PhotoItem item = photoItems.get(position);
        holder.bind(item, context, deleteListener);
    }

    @Override
    public int getItemCount() {
        return photoItems.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAsset;
        Button buttonDelete, buttonView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAsset = itemView.findViewById(R.id.imageViewAsset);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonView = itemView.findViewById(R.id.buttonView);
        }

        public void bind(final PhotoItem item, final Context context, final OnPhotoDeleteListener deleteListener) {
            Glide.with(context)
                    .load(item.getModel())
                    .error(R.drawable.ic_broken_image)
                    .into(imageViewAsset);

            buttonDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDelete(item);
                }
            });

            buttonView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ImagePreviewActivity.class);
                intent.putExtra("IMAGE_URL", item.getModel().toString());
                context.startActivity(intent);
            });
        }
    }
}
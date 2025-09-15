package com.aktivo.hamster.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aktivo.hamster.R;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    public interface DocumentClickListener {
        void onDelete(DocumentItem item);
        void onDownload(DocumentItem item);
    }

    private final Context context;
    private final List<DocumentItem> documentList;
    private final DocumentClickListener listener;

    public DocumentAdapter(Context context, List<DocumentItem> documentList, DocumentClickListener listener) {
        this.context = context;
        this.documentList = documentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        DocumentItem item = documentList.get(position);
        holder.bind(item, context, listener);
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {
        ImageView iconFileType;
        TextView tvDocumentName;
        View buttonDelete, buttonDownload;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            iconFileType = itemView.findViewById(R.id.iconFileType);
            tvDocumentName = itemView.findViewById(R.id.tvDocumentName);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonDownload = itemView.findViewById(R.id.buttonDownload);
        }

        public void bind(final DocumentItem item, Context context, final DocumentClickListener listener) {
            tvDocumentName.setText(item.getFileName(context));

            if (item.isExisting()) {
                buttonDownload.setVisibility(View.VISIBLE);
                buttonDownload.setOnClickListener(v -> listener.onDownload(item));
            } else {
                buttonDownload.setVisibility(View.GONE);
            }

            String fileName = item.getFileName(context).toLowerCase();
            if (fileName.endsWith(".pdf")) {
                iconFileType.setImageResource(R.drawable.ic_pdf_file);
            } else {
                iconFileType.setImageResource(R.drawable.ic_image_file);
            }

            buttonDelete.setOnClickListener(v -> listener.onDelete(item));
        }
    }
}
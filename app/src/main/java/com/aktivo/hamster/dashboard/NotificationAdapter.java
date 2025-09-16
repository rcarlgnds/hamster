package com.aktivo.hamster.dashboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.aktivo.hamster.R;
import com.aktivo.hamster.data.database.NotificationEntity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotificationEntity> notificationList;
    private final OnNotificationClickListener listener;
    private final Context context;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationEntity notification, int position);
    }

    public NotificationAdapter(Context context, List<NotificationEntity> notificationList, OnNotificationClickListener listener) {
        this.context = context;
        this.notificationList = notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationEntity notification = notificationList.get(position);
        holder.bind(notification, position, listener);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void updateData(List<NotificationEntity> newNotifications) {
        this.notificationList = newNotifications;
        notifyDataSetChanged();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView title, message, timestamp;
        private final ImageView icon;
        private final View unreadIndicator;
        MaterialButton buttonCopyCode;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);
            timestamp = itemView.findViewById(R.id.notification_timestamp);
            icon = itemView.findViewById(R.id.icon_notification_type);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
            buttonCopyCode = itemView.findViewById(R.id.buttonCopyCode);
        }

        public void bind(final NotificationEntity notification, final int position, final OnNotificationClickListener listener) {
            title.setText(notification.title);
            message.setText(notification.body);
            timestamp.setText(formatTimestamp(new Date(notification.receivedAt)));

            itemView.setOnClickListener(v -> listener.onNotificationClick(notification, position));

            icon.setImageResource(R.drawable.ic_info);

            if (notification.isRead) {
                unreadIndicator.setVisibility(View.GONE);
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.read_notification_bg));
                cardView.setStrokeWidth(0);

                icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_stroke)));
                title.setTextColor(ContextCompat.getColor(context, R.color.gray_stroke));
                title.setTypeface(null, Typeface.NORMAL);
                message.setTextColor(ContextCompat.getColor(context, R.color.secondary_40));

            } else {
                unreadIndicator.setVisibility(View.VISIBLE);
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.unread_notification_bg));
                cardView.setStrokeColor(ContextCompat.getColor(context, R.color.colorPrimary));
                cardView.setStrokeWidth(2);

                icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
                title.setTextColor(ContextCompat.getColor(context, R.color.black));
                title.setTypeface(null, Typeface.BOLD);
                message.setTextColor(ContextCompat.getColor(context, R.color.secondary_40));
            }

            final String copyText = notification.copyString;
            if (!TextUtils.isEmpty(copyText)) {
                buttonCopyCode.setVisibility(View.VISIBLE);
                buttonCopyCode.setOnClickListener(v -> {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied Code", copyText);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(context, context.getString(R.string.code_copied), Toast.LENGTH_SHORT).show();

                    final CharSequence originalText = buttonCopyCode.getText();
                    final ColorStateList originalTextColor = buttonCopyCode.getTextColors();
                    final android.graphics.drawable.Drawable originalIcon = buttonCopyCode.getIcon();

                    buttonCopyCode.setText(context.getString(R.string.copied));
                    buttonCopyCode.setIconResource(R.drawable.ic_check_circle);
                    buttonCopyCode.setTextColor(ContextCompat.getColor(context, R.color.secondary_40));
                    buttonCopyCode.setEnabled(false);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        buttonCopyCode.setText(originalText);
                        buttonCopyCode.setIcon(originalIcon);
                        buttonCopyCode.setTextColor(originalTextColor);
                        buttonCopyCode.setEnabled(true);
                    }, 1500);
                });
            } else {
                buttonCopyCode.setVisibility(View.GONE);
            }
        }

        private String formatTimestamp(Date date) {
            if (date == null) return "";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
                sdf.setTimeZone(TimeZone.getDefault());
                return sdf.format(date);
            } catch (Exception e) {
                return "Invalid date";
            }
        }
    }
}
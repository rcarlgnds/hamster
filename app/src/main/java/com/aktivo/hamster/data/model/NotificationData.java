package com.aktivo.hamster.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NotificationData {
    @SerializedName("data")
    private List<Notification> notifications;

    private Pagination pagination;

    public List<Notification> getNotifications() {
        return notifications;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
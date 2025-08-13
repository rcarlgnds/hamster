package com.example.hamster.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NotificationData {

    @SerializedName("data")
    private List<Notification> notificationList;

    @SerializedName("pagination")
    private Pagination pagination;

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
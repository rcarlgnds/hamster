package com.aktivo.hamster.data.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "notifications", primaryKeys = {"id", "userId"})
public class NotificationEntity {

    @NonNull
    public String id;

    @NonNull
    public String userId;

    public String title;
    public String body;
    public long receivedAt;
    public boolean isRead;
    public String link;
    public String copyString;

    public NotificationEntity(@NonNull String id, @NonNull String userId, String title, String body, long receivedAt, boolean isRead, String link, String copyString) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.receivedAt = receivedAt;
        this.isRead = isRead;
        this.link = link;
        this.copyString = copyString;
    }
}
package com.aktivo.hamster.data.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class NotificationEntity {

    @PrimaryKey
    @NonNull
    public String id;
    public String title;
    public String body;
    public long receivedAt;
    public boolean isRead;
    public String link;
    public String copyString;


    public NotificationEntity(@NonNull String id, String title, String body, long receivedAt, boolean isRead, String link, String copyString) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.receivedAt = receivedAt;
        this.isRead = isRead;
        this.link = link;
        this.copyString = copyString;
    }
}
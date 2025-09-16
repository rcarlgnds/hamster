package com.aktivo.hamster.data.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class NotificationEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String body;
    public long receivedAt;
    public boolean isRead;

    public NotificationEntity(String title, String body, long receivedAt, boolean isRead) {
        this.title = title;
        this.body = body;
        this.receivedAt = receivedAt;
        this.isRead = isRead;
    }
}
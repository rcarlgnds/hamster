package com.aktivo.hamster.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationDao {

    @Insert
    void insert(NotificationEntity notification);

    @Query("SELECT * FROM notifications ORDER BY receivedAt DESC")
    LiveData<List<NotificationEntity>> getAllNotifications();
}
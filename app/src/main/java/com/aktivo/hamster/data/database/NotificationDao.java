package com.aktivo.hamster.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NotificationEntity notification);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NotificationEntity> notifications);

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY receivedAt DESC")
    LiveData<List<NotificationEntity>> getNotificationsForUser(String userId);

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId AND userId = :userId")
    void markAsRead(String notificationId, String userId);

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    void markAllAsReadForUser(String userId);

    @Query("DELETE FROM notifications")
    void deleteAll();

}
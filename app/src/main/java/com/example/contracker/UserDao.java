package com.example.contracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM User WHERE id = :id")
    User getUser(long id);
    @Query("SELECT * FROM User WHERE user = :user")
    User getUser(String user);

    @Query("SELECT * FROM User ORDER BY id DESC")
    List<User> getUsers();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long addUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
}

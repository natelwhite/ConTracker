package com.example.contracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ConventionDao {
    @Query("SELECT * FROM Convention WHERE id = :id")
    Convention getConvention(long id);
    @Query("SELECT * FROM Convention ORDER BY start DESC")
    List<Convention> getConventions();
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long addConvention(Convention convention);
    @Update
    void updateConvention(Convention convention);
    @Delete
    void deleteConvention(Convention convention);
}

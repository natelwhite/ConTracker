package com.example.contracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface StateDao {
    @Query("SELECT * FROM State WHERE id = :id")
    State getState(long id);

    @Query("SELECT * FROM State ORDER BY name DESC")
    List<State> getStates();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long addState(State state);

    @Update
    void updateState(State state);

    @Delete
    void deleteState(State state);
}
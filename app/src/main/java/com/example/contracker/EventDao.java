package com.example.contracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM Event WHERE id = :id")
    Event getEvent(long id);
    @Query("SELECT * FROM Event ORDER BY date DESC")
    List<Event> getAllEvents();
    @Query("SELECT * FROM Event WHERE convention_id = :conventionId ORDER BY date DESC")
    List<Event> getEvents(long conventionId);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long addEvent(Event event);
    @Update
    void updateEvent(Event event);
    @Delete
    void deleteEvent(Event event);
}

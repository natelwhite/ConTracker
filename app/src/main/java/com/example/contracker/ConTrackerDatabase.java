package com.example.contracker;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Convention.class, State.class, Event.class, User.class}, version = 6)
public abstract class ConTrackerDatabase extends RoomDatabase {
    public abstract ConventionDao conventionDao();
    public abstract StateDao stateDao();
    public abstract EventDao eventDao();
    public abstract UserDao userDao();
}

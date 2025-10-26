package com.example.contracker;

import android.content.Context;

import androidx.room.Room;

import java.util.List;

public class ConTrackerRepo {
    private static ConTrackerRepo mConTrackerRepo;
    private final ConTrackerDatabase database;
    private final ConventionDao mConventionDao;
    private final StateDao mStateDao;
    private final UserDao mUserDao;
    private final EventDao mEventDao;
    public static ConTrackerRepo getInstance(Context ctx) {
        if (mConTrackerRepo == null) {
            mConTrackerRepo = new ConTrackerRepo(ctx);
        }
        return mConTrackerRepo;
    }
    private ConTrackerRepo(Context ctx) {
        database = Room.databaseBuilder(ctx, ConTrackerDatabase.class, "con_tracker.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
        mConventionDao = database.conventionDao();
        mStateDao = database.stateDao();
        mUserDao = database.userDao();
        mEventDao = database.eventDao();
        if (mStateDao.getStates().isEmpty()) {
            populateStateTable();
        }
    }
    // populate all states in states table
    private void populateStateTable() {
        State[] states = {
            new State("Alabama", "AL"),
            new State("Alaska", "AK"),
            new State("Arizona", "AZ"),
            new State("Arkansas", "AR"),
            new State("California", "CA"),
            new State("Colorado", "CO"),
            new State("Connecticut", "CT"),
            new State("Delaware", "DE"),
            new State("Florida", "FL"),
            new State("Georgia", "GA"),
            new State("Hawaii", "HI"),
            new State("Idaho", "IA"),
            new State("Illinois", "IL"),
            new State("Indiana", "IN"),
            new State("Iowa", "IA"),
            new State("Kansas", "KS"),
            new State("Kentucky", "KY"),
            new State("Louisiana", "LA"),
            new State("Maine", "MA"),
            new State("Maryland", "MD"),
            new State("Massachusetts", "MA"),
            new State("Michigan", "MI"),
            new State("Minnesota", "MN"),
            new State("Mississippi", "MS"),
            new State("Missouri", "MO"),
            new State("Montana", "MT"),
            new State("Nebraska", "NE"),
            new State("Nevada", "NV"),
            new State("New Hampshire", "NH"),
            new State("New Jersey", "NJ"),
            new State("New Mexico", "NM"),
            new State("New York", "NY"),
            new State("North Carolina", "NC"),
            new State("North Dakota", "ND"),
            new State("Ohio", "OH"),
            new State("Oklahoma", "OK"),
            new State("Oregon", "OR"),
            new State("Pennsylvania", "PA"),
            new State("Rhode Island", "RI"),
            new State("South Carolina", "SC"),
            new State("South Dakota", "SD"),
            new State("Tennessee", "TN"),
            new State("Texas", "TX"),
            new State("Utah", "UT"),
            new State("Vermont", "VT"),
            new State("Virginia", "VA"),
            new State("Washington", "WA"),
            new State("West Virginia", "WV"),
            new State("Wisconsin", "WI"),
            new State("Wyoming", "WY"),
        };
        for (State state : states) {
            mStateDao.addState(state);
        }
    }
    // user CRUD
    public long addUser(User user) {
        return mUserDao.addUser(user);
    }
    public User getUser(String user) {
        return mUserDao.getUser(user);
    }
    public List<User> getUsers() {
        return mUserDao.getUsers();
    }
    public void deleteUser(User user) {
        mUserDao.deleteUser(user);
    }
    // state CRUD
    public long addState(State state) {
        return mStateDao.addState(state);
    }
    public State getState(long stateId) {
        return mStateDao.getState(stateId);
    }
    public List<State> getStates() {
        return mStateDao.getStates();
    }
    public void deleteState(State state) {
        mStateDao.deleteState(state);
    }
    // convention CRUD
    public long addConvention(Convention convention) {
        return mConventionDao.addConvention(convention);
    }
    public Convention getConvention(long conventionId) {
        return mConventionDao.getConvention(conventionId);
    }
    public List<Convention> getConventions() {
        return mConventionDao.getConventions();
    }
    public void updateConvention(Convention convention) {
        mConventionDao.updateConvention(convention);
    }
    public void deleteConvention(Convention convention) {
        mConventionDao.deleteConvention(convention);
    }
    // event CRUD
    public long addEvent(Event event) {
        return mEventDao.addEvent(event);
    }
    public Event getEvent(long eventId) {
        return mEventDao.getEvent(eventId);
    }
    public List<Event> getEvents(long conventionId) {
        return mEventDao.getEvents(conventionId);
    }
    public void updateEvent(Event event) {
        mEventDao.updateEvent(event);
    }
    public void deleteEvent(Event event) {
        mEventDao.deleteEvent(event);
    }
}

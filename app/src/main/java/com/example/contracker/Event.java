package com.example.contracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(
        foreignKeys = @ForeignKey(entity = Convention.class, parentColumns = "id", childColumns = "convention_id"),
        indices = {@Index("convention_id")}
)
public class Event {
    // columns
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;
    @ColumnInfo(name = "convention_id")
    private long mConventionId;
    @NonNull
    @ColumnInfo(name = "title")
    private String mTitle;
    @ColumnInfo(name = "date")
    private long mDate;
    @ColumnInfo(name = "startTime")
    private String mStart;
    @ColumnInfo(name = "endTime")
    private String mEnd;
    @NonNull
    @ColumnInfo(name = "location")
    private String mLocation;
    @NonNull
    @ColumnInfo(name = "description")
    private String mDescription;

    // constructor
    public Event() {
        this.mTitle = "";
        this.mDate = 0;
        this.mStart = "";
        this.mEnd = "";
        this.mLocation = "";
        this.mDescription = "";
    }
    // used to verify all fields have been set
    // true if valid, false if not
    public Boolean isValid() {
        return !mTitle.isEmpty() && !mLocation.isEmpty() && !mDescription.isEmpty() && mDate != 0 && !mStart.isEmpty();
    }

    // getters
    public long getId() { return this.mId; }
    public long getConventionId() { return this.mConventionId; }
    public String getTitle() { return this.mTitle; }
    public long getDate() { return this.mDate; }
    public String getStart() { return this.mStart; }
    public String getEnd() { return this.mEnd; }
    public String getLocation() { return this.mLocation; }
    public String getDescription() { return this.mDescription; }

    // setters
    public void setId(long id) { this.mId = id; }
    public void setConventionId(long conventionId) { this.mConventionId = conventionId; }
    public void setTitle(String title) { this.mTitle = title; }
    public void setDate(long date) { this.mDate = date; }
    public void setStart(String start) { this.mStart = start; }
    public void setEnd(String end) { this.mEnd = end; }
    public void setLocation(String location) { this.mLocation = location; }
    public void setDescription(String description) { this.mDescription = description; }
}

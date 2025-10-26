package com.example.contracker;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        foreignKeys = @ForeignKey(entity = State.class, parentColumns = "id", childColumns = "state_id"),
        indices = {@Index("state_id")}
)
public class Convention {
    // columns
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;
    @NonNull
    @ColumnInfo(name = "title")
    private String mTitle;
    @ColumnInfo(name = "start")
    private long mStart;
    @ColumnInfo(name = "end")
    private long mEnd;
    @NonNull
    @ColumnInfo(name = "address")
    private String mAddress;
    @NonNull
    @ColumnInfo(name = "city")
    private String mCity;
    @ColumnInfo(name = "state_id")
    private long mStateId;

    // constructor
    public Convention() {
        mTitle = "";
        mStart = 0;
        mEnd = 0;
        mCity = "";
        mAddress = "";
    }
    // used to verify all fields have been set
    // true if valid, false if not
    public Boolean isValid() {
        return !mTitle.isEmpty() && mStart != 0 && mEnd != 0 && !mCity.isEmpty() && !mAddress.isEmpty();
    }

    // getters
    public long getId() { return this.mId; }
    public long getStateId() { return this.mStateId; }
    public String getTitle() { return this.mTitle; }
    public long getStart() { return this.mStart; }
    public long getEnd() { return this.mEnd; }
    public String getCity() { return this.mCity; }
    public String getAddress() { return this.mAddress; }

    // setters
    public void setId(long id) { this.mId = id; }
    public void setStateId(long id) { this.mStateId = id; }
    public void setTitle(String title) { this.mTitle = title; }
    public void setStart(long start) { this.mStart = start; }
    public void setEnd(long end) { this.mEnd = end; }
    public void setCity(String city) { this.mCity = city; }
    public void setAddress(String address) { this.mAddress = address; }
}

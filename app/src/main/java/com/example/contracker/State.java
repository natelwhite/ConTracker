package com.example.contracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class State {
    // columns
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;
    @NonNull
    @ColumnInfo(name = "name")
    private String mName;
    @NonNull
    @ColumnInfo(name = "abbreviation")
    private String mAbbreviation;

    // constructor
    public State(@NonNull String name, @NonNull String abbreviation) {
        mName = name;
        mAbbreviation = abbreviation;
    }

    // getters
    public long getId() { return this.mId; }
    public String getName() { return this.mName; }
    public String getAbbreviation() { return this.mAbbreviation; }

    // setters
    public void setId(long id) { this.mId = id; }
    public void setName(@NonNull String name) { this.mName = name; }
    public void setAbbreviation(@NonNull String abbreviation) { this.mAbbreviation = abbreviation; }
}

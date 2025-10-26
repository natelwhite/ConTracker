package com.example.contracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jspecify.annotations.Nullable;

@Entity
public class User {
    // columns
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;
    @NonNull
    @ColumnInfo(name = "user")
    private String mUser;
    @NonNull
    @ColumnInfo(name = "pass")
    private String mPass;
    @NonNull
    @ColumnInfo(name = "recievesSMS")
    private Boolean mSMSPermission;

    // constructor
    User() {
        this.mUser = "";
        this.mPass = "";
        mSMSPermission = Boolean.FALSE;
    }
    // used to validate data before saving to database
    // true if valid, false if not
    public Boolean isValid() {
        return !mUser.isEmpty() && !mPass.isEmpty();
    }

    // getters
    public long getId() { return this.mId; }
    public String getUser() { return this.mUser; }
    public String getPass() { return this.mPass; }
    public Boolean getSMSPermission() { return this.mSMSPermission; }

    // setters
    public void setId(long id) { this.mId = id; }
    public void setUser(@NonNull String user) { this.mUser = user; }
    public void setPass(@NonNull String pass) { this.mPass = pass; }
    public void setSMSPermission(@NonNull Boolean permission) { this.mSMSPermission = permission; }

}

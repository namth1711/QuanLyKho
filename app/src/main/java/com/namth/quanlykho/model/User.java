package com.namth.quanlykho.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class User implements Parcelable {
    private String matv;
    private String username;
    private String password;
    private String full_name;
    private String email;
    private String lever;

    public User(String matv, String username, String password, String full_name, String email, String lever) {
        this.username = username;
        this.password = password;
        this.full_name = full_name;
        this.email = email;
        this.lever = lever;
        this.matv = matv;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLever() {
        return lever;
    }

    public void setLever(String lever) {
        this.lever = lever;
    }

    public String getMatv() {
        return matv;
    }

    public void setMatv(String matv) {
        this.matv = matv;
    }

    @Override
    public String toString() {
        return "User{" +
                "matv='" + matv + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", full_name='" + full_name + '\'' +
                ", email='" + email + '\'' +
                ", lever='" + lever + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.matv);
        dest.writeString(this.username);
        dest.writeString(this.password);
        dest.writeString(this.full_name);
        dest.writeString(this.email);
        dest.writeString(this.lever);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    protected User(Parcel in) {
        this.matv = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.full_name = in.readString();
        this.email = in.readString();
        this.lever = in.readString();
    }
}
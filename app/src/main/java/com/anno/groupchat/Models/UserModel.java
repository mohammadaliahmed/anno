package com.anno.groupchat.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class UserModel implements Parcelable {
    String name, phone, avatar, fcmKey;
    long time;
    HashMap<String, String> blockedMe;
    HashMap<String, String> blockedList;

    public UserModel() {
    }

    public UserModel(String name, String phone, String avatar, long time) {
        this.name = name;
        this.phone = phone;
        this.avatar = avatar;
        this.time = time;
    }

    protected UserModel(Parcel in) {
        name = in.readString();
        phone = in.readString();
        avatar = in.readString();
        fcmKey = in.readString();
        time = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(avatar);
        dest.writeString(fcmKey);
        dest.writeLong(time);
    }

    public HashMap<String, String> getBlockedMe() {
        return blockedMe;
    }

    public void setBlockedMe(HashMap<String, String> blockedMe) {
        this.blockedMe = blockedMe;
    }

    public HashMap<String, String> getBlockedList() {
        return blockedList;
    }

    public void setBlockedList(HashMap<String, String> blockedList) {
        this.blockedList = blockedList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFcmKey() {
        return fcmKey;
    }

    public void setFcmKey(String fcmKey) {
        this.fcmKey = fcmKey;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

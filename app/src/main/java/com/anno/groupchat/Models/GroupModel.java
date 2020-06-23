package com.anno.groupchat.Models;

import java.util.HashMap;

public class GroupModel {
    String id,name,groupDescription,picUrl,adminName,adminId,text;
    long time;
    HashMap<String,String> members;

    public GroupModel() {
    }

    public GroupModel(String id, String name,String groupDescription, String picUrl, String adminName,
                      String adminId, long time, HashMap<String, String> members,String text) {
        this.id = id;
        this.name = name;
        this.picUrl = picUrl;
        this.adminName = adminName;
        this.adminId = adminId;
        this.time = time;
        this.members = members;
        this.text = text;
        this.groupDescription = groupDescription;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public HashMap<String, String> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, String> members) {
        this.members = members;
    }
}

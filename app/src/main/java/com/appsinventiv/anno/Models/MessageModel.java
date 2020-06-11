package com.appsinventiv.anno.Models;

public class MessageModel {
    String id, text,messageType, messageById, groupId,groupName,picUrl;
    long time;

    public MessageModel() {
    }

    public MessageModel(String id, String text, String messageType, String messageById,
                        String groupId, String groupName, String picUrl, long time) {
        this.id = id;
        this.text = text;
        this.messageType = messageType;
        this.messageById = messageById;
        this.groupId = groupId;
        this.groupName = groupName;
        this.picUrl = picUrl;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageById() {
        return messageById;
    }

    public void setMessageById(String messageById) {
        this.messageById = messageById;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

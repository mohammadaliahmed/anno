package com.anno.groupchat.Models;

public class MessageModel {
    String id, text, messageType, messageById, groupId, groupName, picUrl;
    long time;
    String oldMessageId;
    boolean imageUploading;
    boolean selected;

    public MessageModel() {
    }

    public MessageModel(String id, String text, String messageType, String messageById,
                        String groupId, String groupName, String picUrl, long time, String oldMessageId, boolean imageUploading) {
        this.id = id;
        this.text = text;
        this.messageType = messageType;
        this.messageById = messageById;
        this.groupId = groupId;
        this.groupName = groupName;
        this.picUrl = picUrl;
        this.time = time;
        this.oldMessageId = oldMessageId;
        this.imageUploading = imageUploading;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isImageUploading() {
        return imageUploading;
    }

    public void setImageUploading(boolean imageUploading) {
        this.imageUploading = imageUploading;
    }

    public String getOldMessageId() {
        return oldMessageId;
    }

    public void setOldMessageId(String oldMessageId) {
        this.oldMessageId = oldMessageId;
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

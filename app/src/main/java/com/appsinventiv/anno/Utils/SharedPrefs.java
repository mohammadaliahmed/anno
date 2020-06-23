package com.appsinventiv.anno.Utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.appsinventiv.anno.Models.GroupModel;
import com.appsinventiv.anno.Models.MessageModel;
import com.appsinventiv.anno.Models.UserModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by AliAh on 20/02/2018.
 */

public class SharedPrefs {


    private SharedPrefs() {

    }

//
//    public static void setRequestsSentList(ArrayList<FriendsRequestModel> itemList) {
//
//        Gson gson = new Gson();
//        String json = gson.toJson(itemList);
//        preferenceSetter("FriendsRequestModel", json);
//    }
//
//    public static List<FriendsRequestModel> getRequestsSentList() {
//        Gson gson = new Gson();
//        List<FriendsRequestModel> playersList = (List<FriendsRequestModel>) gson.fromJson(preferenceGetter("FriendsRequestModel"),
//                new TypeToken<List<FriendsRequestModel>>() {
//                }.getType());
//        return playersList;
//    }

    //
    public static void setMuted(String value) {
        preferenceSetter("muted", value);
    }

    public static String getMuted() {
        return preferenceGetter("muted");
    }

    //    public static void setPickedList(ArrayList<StoriesPickedModel> itemList) {
//
//        Gson gson = new Gson();
//        String json = gson.toJson(itemList);
//        preferenceSetter("storiesPicked", json);
//    }
//
//    public static ArrayList getPickedList() {
//        Gson gson = new Gson();
//        ArrayList<StoriesPickedModel> playersList = (ArrayList<StoriesPickedModel>) gson.fromJson(preferenceGetter("storiesPicked"),
//                new TypeToken<ArrayList<StoriesPickedModel>>() {
//                }.getType());
//        return playersList;
//    }
//
    public static void setHomeList(List<GroupModel> itemList) {

        Gson gson = new Gson();
        String json = gson.toJson(itemList);
        preferenceSetter("setHomeList", json);
    }

    public static ArrayList<GroupModel> getHomeList() {
        Gson gson = new Gson();
        ArrayList<GroupModel> playersList = (ArrayList<GroupModel>) gson.fromJson(preferenceGetter("setHomeList"),
                new TypeToken<ArrayList<GroupModel>>() {
                }.getType());
        return playersList;
    }


    public static void setInsideMessages(ArrayList<MessageModel> itemList, String groupId) {

        Gson gson = new Gson();
        String json = gson.toJson(itemList);
        preferenceSetter("setInsideMessages" + groupId, json);
    }

    public static ArrayList<MessageModel> getInsideMessages(String groupId) {
        Gson gson = new Gson();
        ArrayList<MessageModel> playersList = (ArrayList<MessageModel>) gson.fromJson(preferenceGetter("setInsideMessages" + groupId),
                new TypeToken<ArrayList<MessageModel>>() {
                }.getType());
        return playersList;
    }

    public static void setUnreadMessages(HashMap<String, Boolean> itemList) {

        Gson gson = new Gson();
        String json = gson.toJson(itemList);
        preferenceSetter("setUnreadMessages", json);
    }

    public static HashMap<String , Boolean> getUnreadMessages() {
        Gson gson = new Gson();

        HashMap<String, Boolean> retMap = new Gson().fromJson(
                preferenceGetter("setUnreadMessages"), new TypeToken<HashMap<String, Boolean>>() {
                }.getType()
        );

        return retMap;
    }

    public static void clearCartMenuIds() {
        Gson gson = new Gson();
        String json = "";
        preferenceSetter("cartMenu", json);


    }


    public static void setPhoneContactsName(HashMap<String, String> itemList) {

        Gson gson = new Gson();
        String json = gson.toJson(itemList);
        preferenceSetter("setPhoneContacts", json);
    }

    public static HashMap<String, String> getPhoneContactsName() {
        Gson gson = new Gson();

        HashMap<String, String> retMap = new Gson().fromJson(
                preferenceGetter("setPhoneContacts"), new TypeToken<HashMap<String, String>>() {
                }.getType()
        );

        return retMap;
    }

//    public static void setHomeMessages(HashMap<Integer, UserMessages> itemList) {
//
//        Gson gson = new Gson();
//        String json = gson.toJson(itemList);
//        preferenceSetter("setHomeMessages", json);
//    }
//
//    public static HashMap<Integer, UserMessages> getHomeMessages() {
//        Gson gson = new Gson();
//
//        HashMap<Integer, UserMessages> retMap = new Gson().fromJson(
//                preferenceGetter("setHomeMessages"), new TypeToken<HashMap<Integer, UserMessages>>() {
//                }.getType()
//        );
//
//        return retMap;
//    }

    public static void clearTableIds() {
        Gson gson = new Gson();
        String json = "";
        preferenceSetter("tableId", json);


    }

    public static void setFcmKey(String token) {
        preferenceSetter("setFcmKey", token);
    }

    public static String getFcmKey() {
        return preferenceGetter("setFcmKey");
    }

    public static void setRoomId(String token) {
        preferenceSetter("setRoomId", token);
    }

    public static String getRoomId() {
        return preferenceGetter("setRoomId");
    }

    public static void setPhone(String token) {
        preferenceSetter("setPhone", token);
    }

    public static String getPhone() {
        return preferenceGetter("setPhone");
    }


    public static UserModel getParticipantModel(String userId) {
        Gson gson = new Gson();
        UserModel model = gson.fromJson(preferenceGetter(userId), UserModel.class);
        return model;
    }


    public static void setUserModel(UserModel model) {

        Gson gson = new Gson();
        String json = gson.toJson(model);
        preferenceSetter("UserModel", json);
    }

    public static UserModel getUserModel() {
        Gson gson = new Gson();
        UserModel model = gson.fromJson(preferenceGetter("UserModel"), UserModel.class);
        return model;
    }


//    public static void setHomeStories(ArrayList<ArrayList<StoryModel>> itemList) {
//
//        Gson gson = new Gson();
//        String json = gson.toJson(itemList);
//        preferenceSetter("getHomeStories", json);
//    }
//
//    public static ArrayList<ArrayList<StoryModel>> getHomeStories() {
//        Gson gson = new Gson();
//        ArrayList<ArrayList<StoryModel>> playersList = (ArrayList<ArrayList<StoryModel>>) gson.fromJson(preferenceGetter("getHomeStories"),
//                new TypeToken<ArrayList<ArrayList<StoryModel>>>() {
//                }.getType());
//
//        return playersList;
//    }
//
//    public static void setPosts(List<PostModel> itemList) {
//
//        Gson gson = new Gson();
//        String json = gson.toJson(itemList);
//        preferenceSetter("PostsModel", json);
//    }
//
//    public static List<PostModel> getPosts() {
//        Gson gson = new Gson();
//        List<PostModel> playersList = (List<PostModel>) gson.fromJson(preferenceGetter("PostsModel"),
//                new TypeToken<List<PostModel>>() {
//                }.getType());
//        return playersList;
//    }

    public static void preferenceSetter(String key, String value) {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String preferenceGetter(String key) {
        SharedPreferences pref;
        String value = "";
        pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        value = pref.getString(key, "");
        return value;
    }


    public static void logout() {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

}

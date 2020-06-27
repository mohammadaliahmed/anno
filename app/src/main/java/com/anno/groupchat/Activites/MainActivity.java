package com.anno.groupchat.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.anno.groupchat.Activites.NativeAds.NativeTemplateStyle;
import com.anno.groupchat.Activites.NativeAds.TemplateView;
import com.anno.groupchat.Activites.UserManagement.EditProfile;
import com.anno.groupchat.Activites.UserManagement.ListOfUsers;
import com.anno.groupchat.Adapter.ChatListAdapter;
import com.anno.groupchat.Models.GroupModel;
import com.anno.groupchat.Models.MessageModel;
import com.anno.groupchat.Models.UserModel;
import com.anno.groupchat.R;
import com.anno.groupchat.Utils.Constants;
import com.anno.groupchat.Utils.SharedPrefs;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ImageView newChat;

    DatabaseReference mDatabase;
    ChatListAdapter adapter;
    RecyclerView recyclerview;
    private ArrayList<GroupModel> itemList = new ArrayList<>();
    HashMap<String, GroupModel> groupModelHashMap = new HashMap<>();
    TemplateView template;
    int messageCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);

        }
        getPermissions();

        template = findViewById(R.id.my_template);

        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        itemList = SharedPrefs.getHomeList();
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        adapter = new ChatListAdapter(this, itemList, new ChatListAdapter.ChatListAdapterCallback() {
            @Override
            public void onSelected(String name) {

            }
        });
        recyclerview.setAdapter(adapter);
        newChat = findViewById(R.id.newChat);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        newChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ListOfUsers.class));
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isComplete()) {
                    String token = task.getResult().getToken();
                    if (SharedPrefs.getUserModel() != null && !SharedPrefs.getPhone().equalsIgnoreCase("")) {
                        mDatabase.child("Users").child(SharedPrefs.getPhone()).child("fcmKey").setValue(token);
                    }

                }
            }
        });

        getLastMessagesFromDb();
        getUserDataFromDB();


    }

    private void getUserDataFromDB() {
        mDatabase.child("Users").child(SharedPrefs.getUserModel().getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    UserModel model = dataSnapshot.getValue(UserModel.class);
                    if (model != null) {
                        SharedPrefs.setUserModel(model);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showNativeAd() {
        template.setVisibility(View.VISIBLE);
        String adId = "ca-app-pub-5349923547931941/3486006875";
        String testAdId = "ca-app-pub-3940256099942544/2247696110";
        String adToShow = adId;
        MobileAds.initialize(MainActivity.this, adToShow);
        AdLoader adLoader = new AdLoader.Builder(MainActivity.this, adToShow)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));


                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();

                        template.setStyles(styles);
                        template.setNativeAd(unifiedNativeAd);

                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void getLastMessagesFromDb() {

        mDatabase.child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (SharedPrefs.getUserModel() != null) {
                        groupModelHashMap.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            GroupModel model = snapshot.getValue(GroupModel.class);
                            if (model != null && model.getMembers() != null) {
                                if (model.getMembers().containsKey(SharedPrefs.getUserModel().getPhone())) {
                                    groupModelHashMap.put(snapshot.getKey(), model);
                                    updateUnreadMap(snapshot.getKey());
                                }
                            }
                        }
                        itemList = new ArrayList<>(groupModelHashMap.values());
                        Collections.sort(itemList, new Comparator<GroupModel>() {
                            @Override
                            public int compare(GroupModel listData, GroupModel t1) {
                                Long ob1 = listData.getTime();
                                Long ob2 = t1.getTime();
                                return ob2.compareTo(ob1);

                            }
                        });
                        SharedPrefs.setHomeList(itemList);

                        adapter.setItemList(itemList);
                        if (itemList.size() > 0 && itemList.size() < 5) {
                            showNativeAd();
                        }

                        getInsideMessagesFromServer();
                    } else {
                        itemList = new ArrayList<>();
                        adapter.setItemList(itemList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//
    }

    private void getInsideMessagesFromServer() {
        messageCount = 0;
        if (itemList != null && itemList.size() > 0) {
            getMessages(itemList.get(messageCount).getId());

        }
    }

    private void getMessages(final String groupId) {
        mDatabase.child("Chats").child(SharedPrefs.getUserModel().getPhone()).child(groupId).limitToLast(50).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, MessageModel> messagesMap = new HashMap<>();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MessageModel messageModel = snapshot.getValue(MessageModel.class);
                        if (messageModel != null) {
                            messagesMap.put(snapshot.getKey(), messageModel);
                        }
                    }

                    ArrayList<MessageModel> messagesList = new ArrayList<>(messagesMap.values());
                    Collections.sort(messagesList, new Comparator<MessageModel>() {
                        @Override
                        public int compare(MessageModel listData, MessageModel t1) {
                            String ob1 = listData.getId();
                            String ob2 = t1.getId();
                            return ob1.compareTo(ob2);

                        }
                    });

                    SharedPrefs.setInsideMessages(messagesList, groupId);
                    messageCount++;
                    if (itemList.size() > 20) {
                        if (messageCount <= 20) {
                            getMessages(itemList.get(messageCount).getId());
                        }
                    } else {
                        if (messageCount < itemList.size()) {
                            getMessages(itemList.get(messageCount).getId());
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUnreadMap(String key) {
//        HashMap<String, Boolean> map = SharedPrefs.getUnreadMessages();
//        if (map != null) {
//            map.put(key, true);
//            SharedPrefs.setUnreadMessages(map);
//        } else {
//            map = new HashMap<>();
//            map.put(key, true);
//            SharedPrefs.setUnreadMessages(map);
//        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.profile) {
            startActivity(new Intent(MainActivity.this, EditProfile.class));
        }

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_CONTACTS,


        };

        if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            getDataFromDB();
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {

                }
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("newMsg"));
        adapter.updateUnread();

    }

    private void getDataFromDB() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        ArrayList<String> phoneList = new ArrayList<>();
        HashMap<String, String> phoneMap = new HashMap<>();

        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replace("-", "");
            if (phoneNumber.length() > 8) {
                String numb = phoneNumber.substring(phoneNumber.length() - 8);
                phoneList.add(numb);
                phoneMap.put(numb, name);

            }

        }
        SharedPrefs.setPhoneContactsName(phoneMap);
        phones.close();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getDataFromDB();
            // Permission is granted. Continue the action or workflow
            // in your app.
        } else {
            // Explain to the user that the feature is unavailable because
            // the features requires a permission that the user has denied.
            // At the same time, respect the user's decision. Don't link to
            // system settings in an effort to convince the user to change
            // their decision.
        }
        return;

        // Other 'case' lines to check for other
        // permissions this app might request.

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            adapter.updateUnread();
        }
    };

    @Override
    protected void onPause() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        } catch (Exception e) {

        }
        super.onPause();


    }


}

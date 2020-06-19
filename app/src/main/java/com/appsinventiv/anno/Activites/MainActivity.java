package com.appsinventiv.anno.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.appsinventiv.anno.Activites.NativeAds.NativeTemplateStyle;
import com.appsinventiv.anno.Activites.NativeAds.TemplateView;
import com.appsinventiv.anno.Activites.UserManagement.EditProfile;
import com.appsinventiv.anno.Activites.UserManagement.ListOfUsers;
import com.appsinventiv.anno.Adapter.ChatListAdapter;
import com.appsinventiv.anno.Models.GroupModel;
import com.appsinventiv.anno.Models.MessageModel;
import com.appsinventiv.anno.Models.UserModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.SharedPrefs;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView newChat;

    DatabaseReference mDatabase;
    ChatListAdapter adapter;
    RecyclerView recyclerview;
    private ArrayList<GroupModel> itemList = new ArrayList<>();
    HashMap<String, GroupModel> groupModelHashMap = new HashMap<>();
    TemplateView template;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);

        }

        template = findViewById(R.id.my_template);
        String abc = "content://com.appsinventiv.anno.provider/external_files/Pictures/test/JPEG_20200618_175816.jpg";

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
        String adId = "ca-app-pub-5349923547931941/3486006875";
        String testAdId = "ca-app-pub-3940256099942544/2247696110";
        String adToShow = testAdId;
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
                    groupModelHashMap.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        GroupModel model = snapshot.getValue(GroupModel.class);
                        if (model != null) {
                            if (model.getMembers().containsKey(SharedPrefs.getUserModel().getPhone())) {
                                groupModelHashMap.put(snapshot.getKey(), model);
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
                } else {
                    itemList = new ArrayList<>();
                    adapter.setItemList(itemList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//
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
        if (id == R.id.logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert");
            builder.setMessage("Sure to logout? ");

            // add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPrefs.logout();
                    Intent ii = new Intent(MainActivity.this, Splash.class);
                    ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(ii);
                    finish();

                }
            });
            builder.setNegativeButton("Cancel", null);

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

package com.appsinventiv.anno.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.appsinventiv.anno.Activites.UserManagement.ListOfUsers;
import com.appsinventiv.anno.Adapter.ChatListAdapter;
import com.appsinventiv.anno.Models.GroupModel;
import com.appsinventiv.anno.Models.MessageModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.SharedPrefs;
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

public class MainActivity extends AppCompatActivity {

    ImageView newChat;

    DatabaseReference mDatabase;
    ChatListAdapter adapter;
    RecyclerView recyclerview;
    private ArrayList<GroupModel> itemList = new ArrayList<>();
    HashMap<String, GroupModel> groupModelHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);

        }
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
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


    }

    private void getLastMessagesFromDb() {

        mDatabase.child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
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

                    adapter.setItemList(itemList);
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

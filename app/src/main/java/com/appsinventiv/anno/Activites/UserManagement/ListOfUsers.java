package com.appsinventiv.anno.Activites.UserManagement;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.appsinventiv.anno.Activites.GroupManagement.CreateGroup;
import com.appsinventiv.anno.Activites.MainActivity;
import com.appsinventiv.anno.Activites.Phone.RequestCode;
import com.appsinventiv.anno.Adapter.UserListAdapter;
import com.appsinventiv.anno.Models.UserModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListOfUsers extends AppCompatActivity {

    RecyclerView recyclerview;
    UserListAdapter adapter;
    private ArrayList<UserModel> userList = new ArrayList<>();
    DatabaseReference mDatabase;
    HashMap<String, UserModel> userMap = new HashMap<>();
    public static HashMap<String, String> selectedMap = new HashMap<>();
    ImageView next;
    private ArrayList<String> phoneList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_users);
        recyclerview = findViewById(R.id.recyclerview);
        next = findViewById(R.id.next);
        getPermissions();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Select contact");


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListOfUsers.this, CreateGroup.class));

            }
        });

        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new UserListAdapter(this, userList, new UserListAdapter.UserListAdapterCallback() {
            @Override
            public void onSelected(String id) {
                selectedMap.put(id, id);
                ListOfUsers.this.setTitle(selectedMap.size() + " contacts selected");
                next.setVisibility(View.VISIBLE);

            }

            @Override
            public void unSelected(String id) {
                selectedMap.remove(id);
                if (selectedMap.size() > 0) {
                    next.setVisibility(View.VISIBLE);
                    ListOfUsers.this.setTitle(selectedMap.size() + " contacts selected");
                } else {
                    next.setVisibility(View.GONE);
                    ListOfUsers.this.setTitle("Select contact");

                }

            }
        });
        recyclerview.setAdapter(adapter);
        if (phoneList.size() > 0) {
            getDataFromServer();
        }

    }

    private void getDataFromServer() {
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel model = snapshot.getValue(UserModel.class);
                        if (model != null) {
                            if (!model.getPhone().equalsIgnoreCase(SharedPrefs.getUserModel().getPhone())) {
                                String abc = model.getPhone().substring(model.getPhone().length() - 8);
                                if (phoneList.contains(abc)) {
                                    userMap.put(snapshot.getKey(), model);
                                }
                            }
                        }
                    }
                    userList = new ArrayList<>(userMap.values());
                    adapter.setItemList(userList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDataFromDB() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(phoneNumber.length()>8) {
                String abc = phoneNumber.substring(phoneNumber.length() - 8);

                phoneList.add(abc);
            }

        }
        phones.close();
    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_CONTACTS,


        };

        if (!hasPermissions(ListOfUsers.this, PERMISSIONS)) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}

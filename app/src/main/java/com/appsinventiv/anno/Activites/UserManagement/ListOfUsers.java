package com.appsinventiv.anno.Activites.UserManagement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_users);
        recyclerview = findViewById(R.id.recyclerview);
        next = findViewById(R.id.next);
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
        getDataFromServer();

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
                                userMap.put(snapshot.getKey(), model);
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

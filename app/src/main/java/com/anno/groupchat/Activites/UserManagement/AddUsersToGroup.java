package com.anno.groupchat.Activites.UserManagement;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.anno.groupchat.Activites.GroupManagement.GroupInfo;
import com.anno.groupchat.Adapter.UserListAdapter;
import com.anno.groupchat.Models.UserModel;
import com.anno.groupchat.R;
import com.anno.groupchat.Utils.CommonUtils;
import com.anno.groupchat.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddUsersToGroup extends AppCompatActivity {

    RecyclerView recyclerview;
    UserListAdapter adapter;
    private ArrayList<UserModel> userList = new ArrayList<>();
    DatabaseReference mDatabase;
    HashMap<String, UserModel> userMap = new HashMap<>();
    HashMap<String, String> phoneMap = new HashMap<>();
    public HashMap<String, Object> selectedMap = new HashMap<>();
    ImageView next;
    private ArrayList<String> phoneList = new ArrayList<>();
    private String groupId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_users);
        recyclerview = findViewById(R.id.recyclerview);
        next = findViewById(R.id.next);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        getPermissions();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
getSupportActionBar().setElevation(0);
        }
        this.setTitle("Select contact");

        groupId = getIntent().getStringExtra("groupId");
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedMap.size() > 0) {

                    mDatabase.child("Groups").child(groupId).child("members").updateChildren(selectedMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonUtils.showToast("Group members added");
                            finish();
                        }
                    });

                } else {
                    CommonUtils.showToast("Please select some members");
                }
            }
        });

        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new UserListAdapter(this, userList, new UserListAdapter.UserListAdapterCallback() {
            @Override
            public void onSelected(String id) {
                selectedMap.put(id, id);
                AddUsersToGroup.this.setTitle(selectedMap.size() + " contacts selected");
                next.setVisibility(View.VISIBLE);

            }

            @Override
            public void unSelected(String id) {
                selectedMap.remove(id);
                if (selectedMap.size() > 0) {
                    next.setVisibility(View.VISIBLE);
                    AddUsersToGroup.this.setTitle(selectedMap.size() + " contacts selected");
                } else {
                    next.setVisibility(View.GONE);
                    AddUsersToGroup.this.setTitle("Select contact");

                }

            }

            @Override
            public void onLongClick(String id) {

            }
        });
        recyclerview.setAdapter(adapter);


    }

    private void getDataFromServer() {
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userList.clear();
                    userMap.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            UserModel model = snapshot.getValue(UserModel.class);
                            if (model != null && model.getPhone() != null) {
                                if (!model.getPhone().equalsIgnoreCase(SharedPrefs.getUserModel().getPhone())) {
                                    String abc = model.getPhone().substring(model.getPhone().length() - 8);
                                    if (phoneList.contains(abc)) {
                                        String name = phoneMap.get(abc);
                                        model.setName(name);
                                        if (!GroupInfo.phoneListt.contains(snapshot.getKey())) {
                                            userMap.put(snapshot.getKey(), model);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                    userList = new ArrayList<>(userMap.values());
                    adapter.updateList(userList);
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
            if (phoneNumber.length() > 8) {
                phoneNumber = phoneNumber.replace(" ", "");
                phoneNumber = phoneNumber.replace("-", "");
                String abc = phoneNumber.substring(phoneNumber.length() - 8);

                phoneList.add(abc);
                phoneMap.put(abc, name);
            }

        }
        phones.close();
        if (phoneList.size() > 0) {
            getDataFromServer();
        }
    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_CONTACTS,


        };

        if (!hasPermissions(AddUsersToGroup.this, PERMISSIONS)) {
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


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_menu, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

//                loadHistory(query);
                adapter.filter(query);
                return true;

            }

        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        //noinspection SimplifiableIfStatement
//        if (id == R.id.invite) {
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//
//            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_TEXT, "Anno Chat\n Download Now\n" + "http://play.google.com/store/apps/details?id=" + ListOfUsers.this.getPackageName());
//            startActivity(Intent.createChooser(shareIntent, "Share App via.."));
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

}

package com.anno.groupchat.Activites.UserManagement;

import android.Manifest;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anno.groupchat.Activites.GroupManagement.CreateGroup;
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

public class ListOfUsers extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 24;
    RecyclerView recyclerview;
    UserListAdapter adapter;
    private ArrayList<UserModel> userList = new ArrayList<>();
    DatabaseReference mDatabase;
    HashMap<String, UserModel> userMap = new HashMap<>();
    HashMap<String, String> phoneMap = new HashMap<>();
    public static HashMap<String, String> selectedMap = new HashMap<>();
    ImageView next;
    private ArrayList<String> phoneList = new ArrayList<>();
    Button invite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_users);
        recyclerview = findViewById(R.id.recyclerview);
        next = findViewById(R.id.next);
        invite = findViewById(R.id.invite);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        getPermissions();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("Select contact");


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedMap.size() >= 2) {
                    startActivity(new Intent(ListOfUsers.this, CreateGroup.class));
                    finish();
                } else {
                    CommonUtils.showToast("Please select one or more members");
                }

            }
        });
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);

                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, SharedPrefs.getUserModel().getPhone() + " has invited you to an anonymous group chat.\n Join now . " + "http://play.google.com/store/apps/details?id=" + ListOfUsers.this.getPackageName());
                startActivity(Intent.createChooser(shareIntent, "Share App via.."));

            }
        });
        selectedMap.put(SharedPrefs.getUserModel().getPhone(), SharedPrefs.getUserModel().getPhone());

        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new UserListAdapter(this, userList, new UserListAdapter.UserListAdapterCallback() {
            @Override
            public void onSelected(String id) {
                selectedMap.put(id, id);
                ListOfUsers.this.setTitle((selectedMap.size() - 1) + " contacts selected");
                next.setVisibility(View.VISIBLE);

            }

            @Override
            public void unSelected(String id) {
                selectedMap.remove(id);
                if (selectedMap.size() > 0) {
                    next.setVisibility(View.VISIBLE);
                    ListOfUsers.this.setTitle((selectedMap.size() - 1) + " contacts selected");
                } else {
                    next.setVisibility(View.GONE);
                    ListOfUsers.this.setTitle("Select contact");

                }

            }

            @Override
            public void onLongClick(String id) {
                if (SharedPrefs.getUserModel().getBlockedMe() != null && SharedPrefs.getUserModel().getBlockedMe().containsKey(id)) {

                } else if (SharedPrefs.getUserModel().getBlockedList() != null && SharedPrefs.getUserModel().getBlockedList().containsKey(id)) {
                    showUnBlockAlert(id);
                } else {
                    showBlockAlert(id);
                }
            }
        });
        recyclerview.setAdapter(adapter);


    }

    private void showBlockAlert(final String phone) {

        final Dialog dialog = new Dialog(ListOfUsers.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_custom_dialog, null);

        dialog.setContentView(layout);

        TextView message = layout.findViewById(R.id.message);
        TextView cancel = layout.findViewById(R.id.cancel);
        TextView yes = layout.findViewById(R.id.yes);


        message.setText("Do you want to block this user?");
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mDatabase.child("Users").child(SharedPrefs.getUserModel().getPhone()).child("blockedList").child(phone).setValue(phone);
                mDatabase.child("Users").child(phone).child("blockedMe").child(SharedPrefs.getUserModel().getPhone()).setValue(SharedPrefs.getUserModel().getPhone()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Blocked");
                        getDataFromServer();
                    }
                });
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });


        dialog.show();


    }

    private void showUnBlockAlert(final String phone) {

        final Dialog dialog = new Dialog(ListOfUsers.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_custom_dialog, null);

        dialog.setContentView(layout);

        TextView message = layout.findViewById(R.id.message);
        TextView cancel = layout.findViewById(R.id.cancel);
        TextView yes = layout.findViewById(R.id.yes);


        message.setText("Do you want to unblock this user?");
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mDatabase.child("Users").child(SharedPrefs.getUserModel().getPhone()).child("blockedList").child(phone).removeValue();
                mDatabase.child("Users").child(phone).child("blockedMe").child(SharedPrefs.getUserModel().getPhone()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("UnBlocked");
                        getDataFromServer();
                    }
                });
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });


        dialog.show();


    }

    private void getDataFromServer() {
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    userMap.clear();
                    userList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            UserModel model = snapshot.getValue(UserModel.class);
                            if (model != null && model.getPhone() != null) {
                                if (!model.getPhone().equalsIgnoreCase(SharedPrefs.getUserModel().getPhone())) {
                                    String abc = model.getPhone().substring(model.getPhone().length() - 8);
                                    if (phoneList.contains(abc)) {
                                        String name = phoneMap.get(abc);
                                        model.setName(name);
                                        userMap.put(snapshot.getKey(), model);
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
            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replace("-", "");
            if (phoneNumber.length() > 8) {
                String numb = phoneNumber.substring(phoneNumber.length() - 8);
                phoneList.add(numb);
                phoneMap.put(numb, name);
                SharedPrefs.setPhoneContactsName(phoneMap);
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

        if (!hasPermissions(ListOfUsers.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            getDataFromDB();
        }
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

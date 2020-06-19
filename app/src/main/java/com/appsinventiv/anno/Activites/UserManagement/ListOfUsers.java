package com.appsinventiv.anno.Activites.UserManagement;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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
import com.appsinventiv.anno.Activites.Splash;
import com.appsinventiv.anno.Adapter.UserListAdapter;
import com.appsinventiv.anno.Models.UserModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.CommonUtils;
import com.appsinventiv.anno.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
        }
        this.setTitle("Select contact");


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListOfUsers.this, CreateGroup.class));
                finish();

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to block this user? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUnBlockAlert(final String phone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to unblock this user? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
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
            if (phoneNumber.length() > 8) {
                phoneNumber = phoneNumber.replace(" ", "");
                phoneNumber = phoneNumber.replace("-", "");
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.invite) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Anno Chat\n Download Now\n" + "http://play.google.com/store/apps/details?id=" + ListOfUsers.this.getPackageName());
            startActivity(Intent.createChooser(shareIntent, "Share App via.."));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

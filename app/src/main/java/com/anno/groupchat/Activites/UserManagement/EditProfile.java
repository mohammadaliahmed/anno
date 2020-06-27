package com.anno.groupchat.Activites.UserManagement;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anno.groupchat.Activites.Splash;
import com.anno.groupchat.Adapter.AvatarAdapter;
import com.anno.groupchat.Adapter.BlockedUsersAdapter;
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
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    EditText name;
    Button update, edit;
    RecyclerView recyclerview;
    AvatarAdapter adapter;
    String selectedAvatar;
    ArrayList<String> avatarList = new ArrayList<>();
    DatabaseReference mDatabase;
    RelativeLayout wholeLayout;
    CircleImageView image;
    TextView phone;
    TextView chooseAvatar;
    RecyclerView blockedUsersRecycler;
    BlockedUsersAdapter blockedUsersAdapter;
    private ArrayList<UserModel> blockedUserList = new ArrayList<>();
    TextView noBlockedUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        this.setTitle("My profile");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        noBlockedUsers = findViewById(R.id.noBlockedUsers);
        phone = findViewById(R.id.phone);
        blockedUsersRecycler = findViewById(R.id.blockedUsers);
        name = findViewById(R.id.name);
        image = findViewById(R.id.image);
        edit = findViewById(R.id.edit);
        chooseAvatar = findViewById(R.id.chooseAvatar);
        wholeLayout = findViewById(R.id.wholeLayout);
        update = findViewById(R.id.update);
        recyclerview = findViewById(R.id.recyclerview);
        avatarList.add("avatar1");
        avatarList.add("avatar2");
        avatarList.add("avatar3");
        avatarList.add("avatar4");
        avatarList.add("avatar5");

        recyclerview.setLayoutManager(new GridLayoutManager(this, 5));
        adapter = new AvatarAdapter(this, avatarList, new AvatarAdapter.AvatarAdapterCallback() {
            @Override
            public void onSelected(String name) {
                selectedAvatar = name;
            }
        });

        blockedUsersAdapter = new BlockedUsersAdapter(this, blockedUserList, new BlockedUsersAdapter.BLockUserListAdapterCallback() {
            @Override
            public void onClick(String id) {
                showUnBlockAlert(id);
            }
        });
        blockedUsersRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        blockedUsersRecycler.setAdapter(blockedUsersAdapter);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedAvatar == null) {
                    CommonUtils.showToast("Please Select Avatar");
                } else if (name.getText().length() == 0) {
                    name.setError("Enter name");
                } else {
                    updateProfile();

                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chooseAvatar.setVisibility(View.VISIBLE);
                edit.setVisibility(View.GONE);
                update.setVisibility(View.VISIBLE);
                recyclerview.setAdapter(adapter);
                name.setEnabled(true);
                blockedUsersAdapter.setCanEdit(true);
            }
        });


        getUserData();

    }

    private void getUserData() {
        mDatabase.child("Users").child(SharedPrefs.getUserModel().getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    UserModel model = dataSnapshot.getValue(UserModel.class);
                    blockedUserList.clear();
                    if (model != null) {
                        name.setText(model.getName());
                        phone.setText(model.getPhone());
                        selectedAvatar = model.getAvatar();
                        if (model.getAvatar().equalsIgnoreCase("avatar1")) {
                            adapter.setSelected(0);
                            image.setImageDrawable(getResources().getDrawable(R.drawable.avatar1));

                        } else if (model.getAvatar().equalsIgnoreCase("avatar2")) {
                            adapter.setSelected(1);
                            image.setImageDrawable(getResources().getDrawable(R.drawable.avatar2));

                        } else if (model.getAvatar().equalsIgnoreCase("avatar3")) {
                            adapter.setSelected(2);
                            image.setImageDrawable(getResources().getDrawable(R.drawable.avatar3));

                        } else if (model.getAvatar().equalsIgnoreCase("avatar4")) {
                            adapter.setSelected(3);
                            image.setImageDrawable(getResources().getDrawable(R.drawable.avatar4));

                        } else if (model.getAvatar().equalsIgnoreCase("avatar5")) {
                            adapter.setSelected(4);
                            image.setImageDrawable(getResources().getDrawable(R.drawable.avatar5));

                        }
                        if (model.getBlockedList() != null && model.getBlockedList().size() > 0) {
                            noBlockedUsers.setVisibility(View.GONE);
                            for (Map.Entry<String, String> me : model.getBlockedList().entrySet()) {
                                getBlockedUser(me.getValue());

                            }

                        } else {
                            noBlockedUsers.setVisibility(View.VISIBLE);
                            blockedUserList.clear();
                            blockedUsersAdapter.setItemList(blockedUserList);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void previousState() {
        recyclerview.setAdapter(null);
        chooseAvatar.setVisibility(View.GONE);
        edit.setVisibility(View.VISIBLE);
        update.setVisibility(View.GONE);
        name.setEnabled(false);
        blockedUsersAdapter.setCanEdit(false);
    }

    private void getBlockedUser(String values) {
        mDatabase.child("Users").child(values).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    UserModel model = dataSnapshot.getValue(UserModel.class);
                    if (model != null && model.getName() != null) {
                        blockedUserList.add(model);
                        blockedUsersAdapter.setItemList(blockedUserList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateProfile() {
        previousState();
        wholeLayout.setVisibility(View.VISIBLE);

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name.getText().toString());
        map.put("avatar", selectedAvatar);


        mDatabase.child("Users").child(SharedPrefs.getPhone()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                wholeLayout.setVisibility(View.GONE);
                CommonUtils.showToast("Profile Updated");


            }
        });
    }

    private void showUnBlockAlert(final String phone) {

        final Dialog dialog = new Dialog(this);
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


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_main, menu);
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

        if (id == R.id.logout) {


            final Dialog dialog = new Dialog(EditProfile.this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = layoutInflater.inflate(R.layout.alert_custom_dialog, null);

            dialog.setContentView(layout);

            TextView message = layout.findViewById(R.id.message);
            TextView cancel = layout.findViewById(R.id.cancel);
            TextView yes = layout.findViewById(R.id.yes);


            message.setText("Sure to Logout?");
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    mDatabase.child("Users").child(SharedPrefs.getUserModel().getPhone()).child("fcmKey").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            try {
                                FirebaseInstanceId.getInstance().deleteInstanceId();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            SharedPrefs.logout();
                            Intent ii = new Intent(EditProfile.this, Splash.class);
                            ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(ii);
                            finish();
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

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

}

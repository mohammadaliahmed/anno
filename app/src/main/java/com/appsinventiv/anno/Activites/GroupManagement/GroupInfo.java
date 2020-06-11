package com.appsinventiv.anno.Activites.GroupManagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.anno.Activites.MainActivity;
import com.appsinventiv.anno.Activites.Phone.RequestCode;
import com.appsinventiv.anno.Activites.SingleChatScreen;
import com.appsinventiv.anno.Activites.UserManagement.CreateProfile;
import com.appsinventiv.anno.Adapter.GroupInfoUserList;
import com.appsinventiv.anno.Models.GroupModel;
import com.appsinventiv.anno.Models.UserModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.CommonUtils;
import com.appsinventiv.anno.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GroupInfo extends AppCompatActivity {
    String groupId;
    DatabaseReference mDatabase;
    private GroupModel groupModel;
    private ArrayList<UserModel> membersList = new ArrayList<>();
    GroupInfoUserList adapter;

    EditText name;
    TextView groupName;
    ImageView update;

    RecyclerView recyclerview;
    TextView participantsCount;
    Button leaveGroup;
    TextInputLayout edName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Group info");
        groupId = getIntent().getStringExtra("groupId");

        participantsCount = findViewById(R.id.participantsCount);
        edName = findViewById(R.id.edName);
        leaveGroup = findViewById(R.id.leaveGroup);
        recyclerview = findViewById(R.id.recyclerview);
        name = findViewById(R.id.name);
        groupName = findViewById(R.id.groupName);
        update = findViewById(R.id.update);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new GroupInfoUserList(this, membersList, new GroupInfoUserList.GroupUserListAdapterCallback() {
            @Override
            public void onSelected(String id) {
                showRemoveAlert(id);

            }
        });
        recyclerview.setAdapter(adapter);

        leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLeaveAlert();
            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().length() == 0) {
                    name.setError("Enter name");
                } else {
                    mDatabase.child("Groups").child(groupId).child("name").setValue(name.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonUtils.showToast("Name updated");
                        }
                    });
                }
            }
        });

        getGroupDetails();
    }

    private void showRemoveAlert(final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Remove this member? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Groups").child(groupId).child("members").child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Successfully removed");

                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showLeaveAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to leave this group? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Groups").child(groupId).child("members").child(SharedPrefs.getUserModel().getPhone()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("You left the group");
                        Intent i = new Intent(GroupInfo.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getGroupDetails() {
        mDatabase.child("Groups").child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    membersList.clear();
                    groupModel = dataSnapshot.getValue(GroupModel.class);
                    if (groupModel != null) {
                        groupName.setText(groupModel.getName());
                        name.setText(groupModel.getName());


                        if (groupModel.getAdminId().equalsIgnoreCase(SharedPrefs.getUserModel().getPhone())) {
                            //isadmin
                            update.setVisibility(View.VISIBLE);
                            edName.setVisibility(View.VISIBLE);
                            groupName.setVisibility(View.GONE);
                            leaveGroup.setVisibility(View.GONE);
                            adapter.setAdmin(true);
                        } else {
                            update.setVisibility(View.GONE);
                            edName.setVisibility(View.GONE);
                            groupName.setVisibility(View.VISIBLE);
                        }


                        for (String phone : groupModel.getMembers().values()) {
                            getMembersFromServer(phone);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMembersFromServer(String phone) {
        mDatabase.child("Users").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    UserModel model = dataSnapshot.getValue(UserModel.class);
                    if (model != null) {
                        membersList.add(model);
                    }
                }
                participantsCount.setText(membersList.size() + " members");
                adapter.setItemList(membersList);
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

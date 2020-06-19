package com.appsinventiv.anno.Activites.UserManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.anno.Activites.MainActivity;
import com.appsinventiv.anno.Adapter.AvatarAdapter;
import com.appsinventiv.anno.Models.UserModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.CommonUtils;
import com.appsinventiv.anno.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    EditText name;
    Button update;
    RecyclerView recyclerview;
    AvatarAdapter adapter;
    String selectedAvatar;
    ArrayList<String> avatarList = new ArrayList<>();
    DatabaseReference mDatabase;
    RelativeLayout wholeLayout;
    CircleImageView image;
    TextView phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("My profile");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        phone = findViewById(R.id.phone);
        name = findViewById(R.id.name);
        image = findViewById(R.id.image);
        wholeLayout = findViewById(R.id.wholeLayout);
        update = findViewById(R.id.update);
        recyclerview = findViewById(R.id.recyclerview);
        avatarList.add("avatar1");
        avatarList.add("avatar2");
        avatarList.add("avatar3");
        avatarList.add("avatar4");
        avatarList.add("avatar5");

        recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new AvatarAdapter(this, avatarList, new AvatarAdapter.AvatarAdapterCallback() {
            @Override
            public void onSelected(String name) {
                selectedAvatar = name;
            }
        });
        recyclerview.setAdapter(adapter);


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


        getUserData();

    }

    private void getUserData() {
        mDatabase.child("Users").child(SharedPrefs.getUserModel().getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    UserModel model = dataSnapshot.getValue(UserModel.class);
                    if (model != null) {
                        name.setText(model.getName());
                        phone.setText(model.getPhone());
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
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateProfile() {
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

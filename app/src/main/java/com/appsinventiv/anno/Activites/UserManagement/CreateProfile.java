package com.appsinventiv.anno.Activites.UserManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.appsinventiv.anno.Activites.MainActivity;
import com.appsinventiv.anno.Adapter.AvatarAdapter;
import com.appsinventiv.anno.Models.UserModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.CommonUtils;
import com.appsinventiv.anno.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CreateProfile extends AppCompatActivity {

    EditText name;
    Button create;
    RecyclerView recyclerview;
    AvatarAdapter adapter;
    String selectedAvatar;
    ArrayList<String> avatarList = new ArrayList<>();
    DatabaseReference mDatabase;
    RelativeLayout wholeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        name = findViewById(R.id.name);
        wholeLayout = findViewById(R.id.wholeLayout);
        create = findViewById(R.id.create);
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


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedAvatar == null) {
                    CommonUtils.showToast("Please Select Avatar");
                } else if (name.getText().length() == 0) {
                    name.setError("Enter name");
                } else {
                    signUp();
                }
            }
        });


    }

    private void signUp() {
        wholeLayout.setVisibility(View.VISIBLE);
        final UserModel userModel = new UserModel(
                name.getText().toString(),
                SharedPrefs.getPhone(),
                selectedAvatar,
                System.currentTimeMillis()
        );

        mDatabase.child("Users").child(SharedPrefs.getPhone()).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                wholeLayout.setVisibility(View.GONE);
                SharedPrefs.setUserModel(userModel);
                CommonUtils.showToast("Account created successfully");
                startActivity(new Intent(CreateProfile.this, MainActivity.class));
                finish();

            }
        });
    }
}

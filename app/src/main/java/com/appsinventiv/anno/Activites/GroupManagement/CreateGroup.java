package com.appsinventiv.anno.Activites.GroupManagement;

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
import android.widget.EditText;
import android.widget.TextView;

import com.appsinventiv.anno.Activites.MainActivity;
import com.appsinventiv.anno.Activites.Phone.RequestCode;
import com.appsinventiv.anno.Activites.SingleChatScreen;
import com.appsinventiv.anno.Activites.UserManagement.CreateProfile;
import com.appsinventiv.anno.Activites.UserManagement.ListOfUsers;
import com.appsinventiv.anno.Models.GroupModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.CommonUtils;
import com.appsinventiv.anno.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import androidx.appcompat.app.AppCompatActivity;

public class CreateGroup extends AppCompatActivity {

    EditText groupName;
    Button create;
    DatabaseReference mDatabase;
    TextView participantsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Create group");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        create = findViewById(R.id.create);
        groupName = findViewById(R.id.groupName);
        participantsCount = findViewById(R.id.participantsCount);
        participantsCount.setText(ListOfUsers.selectedMap.size() + " participants");
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupName.getText().length() == 0) {
                    groupName.setError("Enter name");
                } else {
                    createGroup();
                }
            }
        });

    }

    private void createGroup() {
        ListOfUsers.selectedMap.put(SharedPrefs.getUserModel().getPhone(), SharedPrefs.getUserModel().getPhone());
        final String key = mDatabase.push().getKey();
        GroupModel model = new GroupModel(key,
                groupName.getText().toString(),
                "",
                SharedPrefs.getUserModel().getName(),
                SharedPrefs.getUserModel().getPhone(),
                System.currentTimeMillis(),
                ListOfUsers.selectedMap, "Group created"
        );
        mDatabase.child("Groups").child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                CommonUtils.showToast("Group Created");
                Intent i = new Intent(CreateGroup.this, SingleChatScreen.class);
                i.putExtra("groupId", key);
                i.putExtra("groupName", groupName.getText().toString());
                startActivity(i);
                finish();

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

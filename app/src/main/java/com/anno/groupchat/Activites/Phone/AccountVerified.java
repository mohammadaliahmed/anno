package com.anno.groupchat.Activites.Phone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.anno.groupchat.Activites.UserManagement.CreateProfile;
import com.anno.groupchat.R;

import androidx.appcompat.app.AppCompatActivity;

public class AccountVerified extends AppCompatActivity {

    Button continueTo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_verified);
        continueTo = findViewById(R.id.continueTo);
        continueTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AccountVerified.this, CreateProfile.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

    }


}

package com.appsinventiv.anno.Activites.Phone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.CommonUtils;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;

public class RequestCode extends AppCompatActivity {

    EditText phone;
    Button verify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_code);
        verify = findViewById(R.id.verify);
        phone = findViewById(R.id.phone);


        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone.getText().length() == 0) {
                    phone.setError("Cant be empty");
                } else if (phone.getText().length() < 10 || phone.getText().length() > 12) {
                    phone.setError("Enter valid phone number");
                } else {
                    requestCode();
                }
            }
        });
    }

    private void requestCode() {
        String ph = phone.getText().toString();
        if (ph.startsWith("03")) {
            ph.substring(1);
        }
        Intent i = new Intent(RequestCode.this, VerifyCode.class);
        i.putExtra("number", "+92" + ph);
        startActivity(i);



    }


}

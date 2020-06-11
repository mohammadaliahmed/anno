package com.appsinventiv.anno.Activites.Phone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appsinventiv.anno.Activites.MainActivity;
import com.appsinventiv.anno.Models.UserModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.CommonUtils;
import com.appsinventiv.anno.Utils.SharedPrefs;
import com.goodiebag.pinview.Pinview;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class VerifyCode extends AppCompatActivity {

    Button verify;
    PhoneAuthProvider phoneAuth;
    Pinview pin;

    String phoneNumber;
    TextView number;
    TextView change, changen;
    Button validate;
    private String smsCode;
    DatabaseReference mDatabase;
    HashMap<String, UserModel> userMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);
        verify = findViewById(R.id.verify);
        pin = findViewById(R.id.pinview);
        number = findViewById(R.id.number);
        change = findViewById(R.id.change);
        changen = findViewById(R.id.changen);
        validate = findViewById(R.id.validate);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getUsersFromDB();

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
//                if (!pin.getValue().equalsIgnoreCase("")) {
//                    if (smsCode != null) {
//                        if (smsCode.equalsIgnoreCase(pin.getValue())) {
                checkUser();
//                            CommonUtils.showToast("Verified");
//                            SharedPrefs.setPhone(phoneNumber);
//
//                        } else {
//                            CommonUtils.showToast("Wrong Pin");
//                        }
//                    } else {
//                        CommonUtils.showToast("Wrong Pin");
//                    }
//                } else {
//                    CommonUtils.showToast("Enter Pin");
//                }
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        changen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        pin.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                //Make api calls here or what not
//                CommonUtils.showToast(pinview.getValue());
            }
        });

        phoneNumber = getIntent().getStringExtra("number");
        number.setText(phoneNumber);
//        requestCode();


    }

    private void checkUser() {
        SharedPrefs.setPhone(phoneNumber);
        if (userMap.containsKey(phoneNumber)) {
            SharedPrefs.setUserModel(userMap.get(phoneNumber));
            Intent i = new Intent(VerifyCode.this, MainActivity.class);
            startActivity(i);
            CommonUtils.showToast("Logged in successfully");
        } else {
            Intent i = new Intent(VerifyCode.this, AccountVerified.class);
            startActivity(i);
        }
        finish();
    }

    private void getUsersFromDB() {
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null) {
                            userMap.put(snapshot.getKey(), userModel);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void requestCode() {

        phoneAuth = PhoneAuthProvider.getInstance();


        phoneAuth.verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        smsCode = phoneAuthCredential.getSmsCode();
                        pin.setValue(phoneAuthCredential.getSmsCode());


                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        CommonUtils.showToast(e.getMessage());

                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        CommonUtils.showToast("Code sent");

                        // Save verification ID and resending token so we can use them later


                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                        CommonUtils.showToast("Time out");
//                            sendCode.setText("Resend");
//                            progress.setVisibility(View.GONE);
                        finish();

                    }
                }
        );
    }


}

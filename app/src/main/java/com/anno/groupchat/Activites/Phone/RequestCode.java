package com.anno.groupchat.Activites.Phone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anno.groupchat.R;
import com.rilixtech.Country;
import com.rilixtech.CountryCodePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

public class RequestCode extends AppCompatActivity {

    AppCompatEditText phone;
    Button verify;

    CountryCodePicker ccp;
    private String foneCode;
    TextView countryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_code);
        verify = findViewById(R.id.verify);
        phone = findViewById(R.id.phone);
        countryName = findViewById(R.id.countryName);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        foneCode = "+92";

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
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                foneCode = selectedCountry.getPhoneCode();
                countryName.setText("(" + selectedCountry.getName() + ")");
            }
        });
        ccp.registerPhoneNumberTextView(phone);

    }

    private void requestCode() {
        String ph = phone.getText().toString();
//        if (ph.startsWith("03")) {
//            ph=ph.substring(1);
//        }
        Intent i = new Intent(RequestCode.this, VerifyCode.class);
        i.putExtra("number", foneCode + ph);
        startActivity(i);


    }


}

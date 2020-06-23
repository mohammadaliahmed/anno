package com.anno.groupchat.Activites;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


import com.anno.groupchat.Activites.Phone.RequestCode;
import com.anno.groupchat.Activites.UserManagement.CreateProfile;
import com.anno.groupchat.R;
import com.anno.groupchat.Utils.SharedPrefs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1000;
    Button login;

    boolean allowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        login = findViewById(R.id.login);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allowed) {
                    Intent i = new Intent(Splash.this, RequestCode.class);
                    startActivity(i);
                    finish();
                } else {
                    showAgreeAlert();
                }
            }
        });


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (SharedPrefs.getUserModel() != null) {
                    Intent i = new Intent(Splash.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    if (!SharedPrefs.getPhone().equalsIgnoreCase("")) {
                        Intent i = new Intent(Splash.this, CreateProfile.class);
                        startActivity(i);
                        finish();
                    } else {
                        login.setVisibility(View.VISIBLE);
                    }
                }

                // close this activity
            }
        }, SPLASH_TIME_OUT);
        /*
         * alias: annochat
         * key: annochat
         * pass: annochat
         * */

    }

    private void showAgreeAlert() {
        final Dialog dialog = new Dialog(this, R.style.AppTheme_NoActionBar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_agree_curved, null);

        dialog.setContentView(layout);

        Button continueTo = layout.findViewById(R.id.continueTo);


        continueTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                getPermissions();
            }
        });


        dialog.show();
    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_CONTACTS,


        };

        if (!hasPermissions(Splash.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            Intent i = new Intent(Splash.this, RequestCode.class);
            startActivity(i);
            finish();
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                } else {
                    allowed = true;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            allowed = true;
            Intent i = new Intent(Splash.this, RequestCode.class);
            startActivity(i);
            finish();
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

}

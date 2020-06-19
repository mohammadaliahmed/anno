package com.appsinventiv.anno.Activites.GroupManagement;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appsinventiv.anno.Activites.SingleChatScreen;
import com.appsinventiv.anno.Activites.UserManagement.ListOfUsers;
import com.appsinventiv.anno.Models.GroupModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.CommonUtils;
import com.appsinventiv.anno.Utils.CompressImageToThumnail;
import com.appsinventiv.anno.Utils.GifSizeFilter;
import com.appsinventiv.anno.Utils.Glide4Engine;
import com.appsinventiv.anno.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class CreateGroup extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE = 23;
    EditText groupName, groupDescription;
    Button create;
    DatabaseReference mDatabase;
    TextView participantsCount;
    CircleImageView pickImage;
    List<Uri> mSelected = new ArrayList<>();
    String imageUrl;
    StorageReference mStorageRef;
    private String downloadUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getPermissions();
        this.setTitle("Create group");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        pickImage = findViewById(R.id.pickImage);
        create = findViewById(R.id.create);
        groupName = findViewById(R.id.groupName);
        groupDescription = findViewById(R.id.groupDescription);
        participantsCount = findViewById(R.id.participantsCount);
        participantsCount.setText(ListOfUsers.selectedMap.size() + " participants");
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupName.getText().length() == 0) {
                    groupName.setError("Enter name");
                } else {
                    if (mSelected.size() > 0) {
                        putPictures("" + imageUrl);
                    } else {
                        createGroup();
                    }
                }
            }
        });

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelected.clear();
                initMatisse();

            }
        });


    }


    private void initMatisse() {
        Matisse.from(CreateGroup.this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(1)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new Glide4Engine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && data != null) {
            mSelected = Matisse.obtainResult(data);
            String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

//            CompressImage image = new CompressImage(CreateGroup.this);

            imageUrl = CompressImageToThumnail.compressImage("" + mSelected.get(0), CreateGroup.this);


            Glide.with(CreateGroup.this).load(mSelected.get(0)).into(pickImage);

        }


    }

    public void putPictures(String path) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        CommonUtils.showToast("Uploading Image");
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

        ;
        Uri file = Uri.fromFile(new File(path));


        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUrl = uri.toString();
                                createGroup();
                            }
                        });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        CommonUtils.showToast(exception.getMessage());
                    }
                });


    }

    private void createGroup() {
        ListOfUsers.selectedMap.put(SharedPrefs.getUserModel().getPhone(), SharedPrefs.getUserModel().getPhone());
        final String key = mDatabase.push().getKey();
        GroupModel model = new GroupModel(key,
                groupName.getText().toString(),
                groupDescription.getText().toString(),
                "" + downloadUrl,
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

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,

        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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

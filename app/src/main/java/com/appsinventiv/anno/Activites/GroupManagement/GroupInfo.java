package com.appsinventiv.anno.Activites.GroupManagement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
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
import com.appsinventiv.anno.Utils.CompressImageToThumnail;
import com.appsinventiv.anno.Utils.GifSizeFilter;
import com.appsinventiv.anno.Utils.Glide4Engine;
import com.appsinventiv.anno.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroupInfo extends AppCompatActivity {
    private static final int REQUEST_CODE_CHOOSE = 23;
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
    CircleImageView pickImage;
    private List<Uri> mSelected = new ArrayList<>();
    private String imageUrl;
    private String downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        getPermissions();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Group info");
        groupId = getIntent().getStringExtra("groupId");

        pickImage = findViewById(R.id.pickImage);
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
                    if (mSelected.size() > 0) {
                        putPictures(imageUrl);
                    } else {
                        mDatabase.child("Groups").child(groupId).child("name").setValue(name.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                CommonUtils.showToast("Name updated");
                            }
                        });
                    }
                }
            }
        });

        getGroupDetails();
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupModel.getAdminId().equalsIgnoreCase(SharedPrefs.getUserModel().getPhone())) {
                    mSelected.clear();
                    initMatisse();
                }

            }
        });
    }

    private void initMatisse() {
        Matisse.from(this)
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

            imageUrl = CompressImageToThumnail.compressImage("" + mSelected.get(0), GroupInfo.this);


            Glide.with(GroupInfo.this).load(mSelected.get(0)).into(pickImage);

        }


    }

    public void putPictures(String path) {
        StorageReference mStorageRef;
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
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("picUrl", downloadUrl);
                                map.put("name", name.getText().toString());
                                mDatabase.child("Groups").child(groupId).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        CommonUtils.showToast("Name updated");
                                    }
                                });

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

                        try {
                            Glide.with(GroupInfo.this).load(groupModel.getPicUrl()).placeholder(R.drawable.profile).into(pickImage);
                        } catch (Exception e) {

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

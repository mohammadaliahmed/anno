package com.appsinventiv.anno.Activites;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.anno.Activites.GroupManagement.CreateGroup;
import com.appsinventiv.anno.Activites.GroupManagement.GroupInfo;
import com.appsinventiv.anno.Activites.Phone.RequestCode;
import com.appsinventiv.anno.Adapter.MessagesAdapter;
import com.appsinventiv.anno.Adapter.SimpleFragmentPagerAdapter;
import com.appsinventiv.anno.Models.GroupModel;
import com.appsinventiv.anno.Models.MessageModel;
import com.appsinventiv.anno.Models.UserModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.CommonUtils;
import com.appsinventiv.anno.Utils.ConnectivityManager;
import com.appsinventiv.anno.Utils.Constants;
import com.appsinventiv.anno.Utils.GifSizeFilter;
import com.appsinventiv.anno.Utils.Glide4Engine;
import com.appsinventiv.anno.Utils.KeyboardUtils;
import com.appsinventiv.anno.Utils.NotificationAsync;
import com.appsinventiv.anno.Utils.NotificationObserver;
import com.appsinventiv.anno.Utils.SharedPrefs;
import com.appsinventiv.anno.Utils.SwipeToDeleteCallback;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shain.messenger.MessageSwipeController;
import com.shain.messenger.SwipeControllerActions;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

public class SingleChatScreen extends AppCompatActivity implements NotificationObserver {
    private static final int REQUEST_CODE_CHOOSE = 23;
    ImageView send;
    EmojiEditText message;

    DatabaseReference mDatabase;
    String messageText;
    ArrayList<UserModel> membersList = new ArrayList<>();
    String groupId, groupName;
    RecyclerView recyclerview;
    MessagesAdapter adapter;
    private ArrayList<MessageModel> messagesList = new ArrayList<>();
    private ArrayList<String> keysList = new ArrayList<>();
    private List<Uri> mSelected = new ArrayList<>();
    private ArrayList<String> imageUrl = new ArrayList<>();
    public static ArrayList<String> photosArray = new ArrayList<>();
    private boolean memeLayoutShowing;

    public HashMap<String, MessageModel> getMessagesMap() {
        return messagesMap;
    }


    private HashMap<String, MessageModel> messagesMap = new HashMap<>();
    private GroupModel groupModel;

    TextView groupNameTv;
    ImageView back;
    CircleImageView groupImage;
    AdView adView;

    ImageView emoji;
    ViewGroup rootView;
    private EmojiPopup emojiPopup;
    private MessageSwipeController swipeController;
    CardView replyLayout, memesLayout;
    TextView replyOldText;
    private boolean replyShowing;
    private String oldMessageId;
    ImageView close;
    ImageView camera;
    StorageReference mStorageRef;
    int uploadedCount = 0;
    ImageView replyImage, memes;


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("memeShare"));
    }

    @Override
    protected void onPause() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        } catch (Exception e) {

        }
        super.onPause();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat_screen);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getPermissions();
        groupNameTv = findViewById(R.id.groupNameTv);
        memes = findViewById(R.id.memes);
        camera = findViewById(R.id.camera);
        close = findViewById(R.id.close);
        adView = findViewById(R.id.adView);
        memesLayout = findViewById(R.id.memesLayout);
        emoji = findViewById(R.id.emoji);
        replyOldText = findViewById(R.id.replyOldText);
        replyLayout = findViewById(R.id.replyLayout);
        replyImage = findViewById(R.id.replyImage);

        rootView = findViewById(R.id.main_activity_root_view);


        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");
        Constants.GROUP_ID = groupId;
        groupNameTv.setText(groupName);


        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        groupImage = findViewById(R.id.groupImage);
        back = findViewById(R.id.back);
        message = findViewById(R.id.message);
        recyclerview = findViewById(R.id.recyclerview);
        send = findViewById(R.id.send);

        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SingleChatScreen.this, GroupInfo.class);
                i.putExtra("groupId", groupId);
                startActivity(i);
            }
        });
        groupNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SingleChatScreen.this, GroupInfo.class);
                i.putExtra("groupId", groupId);
                startActivity(i);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.getText().length() == 0) {
                    message.setError("Cant be empty");
                } else {
                    messageText = message.getText().toString();
                    message.setText("");
                    sendMessage(Constants.MESSAGE_TYPE_TEXT, "");
                }
            }
        });

        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(message);


        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggles visibility of the Popup.
                if (emojiPopup.isShowing()) {
                    emojiPopup.dismiss();
                } else {
                    emojiPopup.toggle();
                }

            }
        });
        memes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (memeLayoutShowing) {
                    memeLayoutShowing = false;
                    memesLayout.setVisibility(View.GONE);
                } else {
                    memeLayoutShowing = true;
                    memesLayout.setVisibility(View.VISIBLE);
                    setupMemesLayout();

                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyShowing = false;
                replyLayout.setVisibility(View.GONE);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initMatisse();
            }
        });

        getGroupDetails();
        setupRecyclerView();

    }

    private void setupMemesLayout() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        SimpleFragmentPagerAdapter simpleFragmentPagerAdapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(simpleFragmentPagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initMatisse() {
        uploadedCount = 0;
        mSelected.clear();
        imageUrl.clear();
        memeLayoutShowing = false;
        memesLayout.setVisibility(View.GONE);
        Matisse.from(this)
                .choose(MimeType.ofImage(), false)
                .showSingleMediaType(true)
                .countable(true)
                .capture(true)
                .captureStrategy(
                        new CaptureStrategy(true, "com.appsinventiv.anno.provider", "test"))
                .maxSelectable(10)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new Glide4Engine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    private void setupRecyclerView() {
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));


        messagesList = SharedPrefs.getInsideMessages(groupId);
        if (messagesList == null) {
            messagesList = new ArrayList<>();
        }

        adapter = new MessagesAdapter(this, messagesList, new MessagesAdapter.MessagesAdapterCallback() {
            @Override
            public void onSelected(MessageModel model) {
                showDeleteAlert(model);
            }

            @Override
            public void onGoToMessage(String id) {

//                List<String> indexes = new ArrayList<String>(messagesMap.keySet()); // <== Set to List
                int pos = keysList.indexOf(id);
                recyclerview.scrollToPosition(pos);
            }

        });
        recyclerview.setAdapter(adapter);
        recyclerview.scrollToPosition(messagesList.size() - 1);


        swipeController = new MessageSwipeController(this, new SwipeControllerActions() {
            @Override
            public void showReplyUI(int position) {
                showReplyLayout(position);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerview);


        getMessagesFromServer();
    }

    private void showReplyLayout(int position) {
        memeLayoutShowing = false;
        memesLayout.setVisibility(View.GONE);
        message.requestFocus();
        KeyboardUtils.toggleKeyboardVisibility(this);
        oldMessageId = messagesList.get(position).getId();
        replyShowing = true;
        replyLayout.setVisibility(View.VISIBLE);
        if (messagesList.get(position).getMessageType().equalsIgnoreCase(Constants.MESSAGE_TYPE_IMAGE)) {
            replyImage.setVisibility(View.VISIBLE);
            replyOldText.setText("Photo");
            Glide.with(this).load(messagesList.get(position).getPicUrl()).into(replyImage);
        } else {
            replyImage.setVisibility(View.GONE);
            replyOldText.setText(messagesList.get(position).getText());
        }

    }

    private void showDeleteAlert(final MessageModel model) {
        memeLayoutShowing = false;
        memesLayout.setVisibility(View.GONE);
        final Dialog dialog = new Dialog(SingleChatScreen.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_dialog_delete, null);

        dialog.setContentView(layout);

        TextView deleteEvery = layout.findViewById(R.id.deleteEvery);
        TextView cancel = layout.findViewById(R.id.cancel);
        TextView delete = layout.findViewById(R.id.delete);

        if (model.getMessageById().equalsIgnoreCase(SharedPrefs.getUserModel().getPhone())) {


            if ((System.currentTimeMillis() - model.getTime() < 600000)) {

                deleteEvery.setVisibility(View.VISIBLE);
            } else {
                deleteEvery.setVisibility(View.GONE);
            }

        } else {
            deleteEvery.setVisibility(View.GONE);

        }


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });


        deleteEvery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                for (final UserModel member : membersList) {

                    mDatabase.child("Chats").child(member.getPhone()).child(groupId).child(model.getId()).child("messageType").setValue(Constants.MESSAGE_TYPE_DELETED);
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("Chats").child(SharedPrefs.getUserModel().getPhone()).child(groupId).child(model.getId()).removeValue();

                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void getMessagesFromServer() {
        mDatabase.child("Chats").child(SharedPrefs.getUserModel().getPhone()).child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesMap.clear();
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MessageModel messageModel = snapshot.getValue(MessageModel.class);
                        if (messageModel != null) {
                            messagesMap.put(snapshot.getKey(), messageModel);
                        }
                    }

                    messagesList = new ArrayList<>(messagesMap.values());
                    Collections.sort(messagesList, new Comparator<MessageModel>() {
                        @Override
                        public int compare(MessageModel listData, MessageModel t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();
                            return ob1.compareTo(ob2);

                        }
                    });
                    SharedPrefs.setInsideMessages(messagesList, groupId);
                    keysList.clear();
                    for (MessageModel keys : messagesList) {
                        keysList.add(keys.getId());
                        if (keys.getMessageType().equalsIgnoreCase(Constants.MESSAGE_TYPE_IMAGE)) {
                            photosArray.add(keys.getPicUrl());
                        }
                    }
                    adapter.setItemList(messagesList);
                    recyclerview.scrollToPosition(messagesList.size() - 1);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getGroupDetails() {
        mDatabase.child("Groups").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    groupModel = dataSnapshot.getValue(GroupModel.class);
                    if (groupModel != null) {
                        groupNameTv.setText(groupModel.getName());
                    }

                    try {
                        Glide.with(SingleChatScreen.this).load(groupModel.getPicUrl()).placeholder(R.drawable.profile).into(groupImage);
                    } catch (Exception e) {

                    }

                    for (String phone : groupModel.getMembers().values()) {
                        getMembersFromServer(phone);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String messageType, String picUrl) {
        if (!ConnectivityManager.isNetworkConnected(this)) {
            CommonUtils.showToast("Unable to send message\nPlease check your internet connection");
        } else {
            memeLayoutShowing = false;
            memesLayout.setVisibility(View.GONE);
            String key = mDatabase.push().getKey();
            MessageModel messageModel;
            if (replyShowing) {
                messageModel = new MessageModel(
                        key,
                        messageText,
                        Constants.MESSAGE_TYPE_REPLY,
                        SharedPrefs.getUserModel().getPhone(),
                        groupId,
                        groupName,
                        "",
                        System.currentTimeMillis(), oldMessageId, false);
                replyShowing = false;
            } else {
                messageModel = new MessageModel(
                        key,
                        messageText,
                        messageType,
                        SharedPrefs.getUserModel().getPhone(),
                        groupId,
                        groupName,
                        picUrl,
                        System.currentTimeMillis(), "", false);
            }

            for (final UserModel member : membersList) {
                mDatabase.child("Chats").child(member.getPhone()).child(groupId).child(key).setValue(messageModel);
            }
            replyLayout.setVisibility(View.GONE);
            HashMap<String, Object> abc = new HashMap<>();
            if (messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_IMAGE)) {
                abc.put("text", "\uD83D\uDCF7 Image");
            } else {
                abc.put("text", messageText);
            }
            abc.put("time", System.currentTimeMillis());
            mDatabase.child("Groups").child(groupId).updateChildren(abc);
            if (messageType.equalsIgnoreCase(Constants.MESSAGE_TYPE_IMAGE)) {
                sendNotifications("\uD83D\uDCF7 Image");
            } else {
                sendNotifications(messageText);

            }
        }

    }

    private void sendNotifications(String msg) {
        for (final UserModel member : membersList) {
            if (!member.getPhone().equalsIgnoreCase(SharedPrefs.getUserModel().getPhone())) {
                NotificationAsync notificationAsync = new NotificationAsync(SingleChatScreen.this);
                String NotificationTitle = "New message in " + groupName;
                String NotificationMessage = msg;
                notificationAsync.execute(
                        "ali",
                        member.getFcmKey(),
                        NotificationTitle,
                        NotificationMessage,
                        "chat", "" + groupId,
                        groupName
                );
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && data != null) {
            mSelected = Matisse.obtainResult(data);
            imageUrl.clear();
            if (mSelected.get(0).toString().contains("com.appsinventiv.anno.provider")) {
                String uri = "" + mSelected.get(0);
                String lastSegment = uri.substring(uri.lastIndexOf("/") + 1);
                String aasdas = "/storage/emulated/0/Pictures/test/" + lastSegment;
//                imageUrl.add(aasdas);
                File videoFile = new File(aasdas);


                MediaScannerConnection.scanFile(this, new String[]{videoFile.getAbsolutePath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("dfsfsd", uri.toString());
                                imageUrl.add(com.appsinventiv.buyandsell.Utils.CompressImage.compressImage("" + uri.toString(), SingleChatScreen.this));
                                putPictures(imageUrl.get(0));


                            }
                        });

            } else {
                for (Uri uri : mSelected) {
                    imageUrl.add(com.appsinventiv.buyandsell.Utils.CompressImage.compressImage("" + uri, this));
                }
                if (imageUrl.size() > 0) {
                    putPictures(imageUrl.get(uploadedCount));

                }
            }


        }


    }

    public void putPictures(String path) {
        MessageModel messageModel = new MessageModel(
                mDatabase.push().getKey(),
                messageText,
                Constants.MESSAGE_TYPE_IMAGE,
                SharedPrefs.getUserModel().getPhone(),
                groupId,
                groupName,
                mSelected.get(uploadedCount) + "",
                System.currentTimeMillis(), "", true);
        messagesList.add(messageModel);
        adapter.setItemList(messagesList);
        recyclerview.scrollToPosition(messagesList.size() - 1);
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
                                String downloadUrl = uri.toString();
                                uploadedCount++;
                                sendMessage(Constants.MESSAGE_TYPE_IMAGE, downloadUrl);
                                if (uploadedCount < imageUrl.size()) {
                                    putPictures(imageUrl.get(uploadedCount));
                                }
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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String picUrl = intent.getStringExtra("picUrl");
            showPreviewAlert(picUrl);
        }
    };

    private void showPreviewAlert(final String picUrl) {
        final Dialog dialog = new Dialog(this, R.style.AppTheme_NoActionBar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_preview_curved, null);

        dialog.setContentView(layout);

        ImageView imagePreview = layout.findViewById(R.id.imagePreview);
        ImageView send = layout.findViewById(R.id.send);
        TextView sendTo = layout.findViewById(R.id.sendTo);
        Glide.with(SingleChatScreen.this).load(picUrl).into(imagePreview);
        sendTo.setText("Send to ] " + groupName);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                sendMessage(Constants.MESSAGE_TYPE_IMAGE, picUrl);
            }
        });


        dialog.show();
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


    @Override
    public void onSuccess(String chatId) {

    }

    @Override
    public void onFailure() {

    }
}

package com.appsinventiv.anno.Activites;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.anno.Activites.GroupManagement.GroupInfo;
import com.appsinventiv.anno.Activites.Phone.RequestCode;
import com.appsinventiv.anno.Activites.UserManagement.CreateProfile;
import com.appsinventiv.anno.Adapter.MessagesAdapter;
import com.appsinventiv.anno.Models.GroupModel;
import com.appsinventiv.anno.Models.MessageModel;
import com.appsinventiv.anno.Models.UserModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.Constants;
import com.appsinventiv.anno.Utils.NotificationAsync;
import com.appsinventiv.anno.Utils.NotificationObserver;
import com.appsinventiv.anno.Utils.SharedPrefs;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SingleChatScreen extends AppCompatActivity implements NotificationObserver {
    ImageView send;
    EditText message;

    DatabaseReference mDatabase;
    String messageText;
    ArrayList<UserModel> membersList = new ArrayList<>();
    String groupId, groupName;
    RecyclerView recyclerview;
    MessagesAdapter adapter;
    private ArrayList<MessageModel> messagesList = new ArrayList<>();
    private HashMap<String, MessageModel> messagesMap = new HashMap<>();
    private GroupModel groupModel;

    TextView groupNameTv;
    ImageView back;
    CircleImageView groupImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat_screen);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        groupNameTv = findViewById(R.id.groupNameTv);

        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");
        groupNameTv.setText(groupName);


        mDatabase = FirebaseDatabase.getInstance().getReference();
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
                    sendMessage();
                }
            }
        });

        getGroupDetails();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        adapter = new MessagesAdapter(this, messagesList, new MessagesAdapter.MessagesAdapterCallback() {
            @Override
            public void onSelected(MessageModel model) {
                showDeleteAlert(model);
            }

        });
        recyclerview.setAdapter(adapter);

        getMessagesFromServer();
    }

    private void showDeleteAlert(final MessageModel model) {
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

    private void sendMessage() {
        String key = mDatabase.push().getKey();
        MessageModel messageModel = new MessageModel(
                key,
                messageText,
                Constants.MESSAGE_TYPE_TEXT,
                SharedPrefs.getUserModel().getPhone(),
                groupId,
                groupName,
                "",
                System.currentTimeMillis());

        for (final UserModel member : membersList) {
            mDatabase.child("Chats").child(member.getPhone()).child(groupId).child(key).setValue(messageModel);
        }
        HashMap<String, Object> abc = new HashMap<>();
        abc.put("text", messageText);
        abc.put("time", System.currentTimeMillis());
        mDatabase.child("Groups").child(groupId).updateChildren(abc);
        sendNotifications();

    }

    private void sendNotifications() {
        for (final UserModel member : membersList) {
            if (!member.getPhone().equalsIgnoreCase(SharedPrefs.getUserModel().getPhone())) {
                NotificationAsync notificationAsync = new NotificationAsync(SingleChatScreen.this);
                String NotificationTitle = "New message in " + groupName;
                String NotificationMessage = messageText;
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

package com.appsinventiv.anno.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.anno.Activites.SingleChatScreen;
import com.appsinventiv.anno.Models.GroupModel;
import com.appsinventiv.anno.Models.MessageModel;
import com.appsinventiv.anno.Models.UserModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    Context context;
    ArrayList<GroupModel> itemList;
    ChatListAdapterCallback callback;

    public ChatListAdapter(Context context, ArrayList<GroupModel> itemList, ChatListAdapterCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;
    }

    public void setItemList(ArrayList<GroupModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_layout, parent, false);
        ChatListAdapter.ViewHolder viewHolder = new ChatListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final GroupModel model = itemList.get(position);


        holder.name.setText(model.getName());
        holder.message.setText(model.getText());
        holder.time.setText(CommonUtils.getFormattedTime(model.getTime()));
        try {
            Glide.with(context).load(model.getPicUrl()).placeholder(R.drawable.profile).into(holder.image);
        } catch (Exception e) {

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleChatScreen.class);
                intent.putExtra("groupId", model.getId());
                intent.putExtra("groupName", model.getName());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, message, time;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }
    }

    public interface ChatListAdapterCallback {
        public void onSelected(String name);
    }
}

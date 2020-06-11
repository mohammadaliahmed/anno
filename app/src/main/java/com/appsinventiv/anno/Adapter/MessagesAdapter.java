package com.appsinventiv.anno.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsinventiv.anno.Activites.SingleChatScreen;
import com.appsinventiv.anno.Models.MessageModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.CommonUtils;
import com.appsinventiv.anno.Utils.Constants;
import com.appsinventiv.anno.Utils.SharedPrefs;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    Context context;
    ArrayList<MessageModel> itemList;
    MessagesAdapterCallback callback;
    public int RIGHT_CHAT = 1;
    public int LEFT_CHAT = 0;

    public MessagesAdapter(Context context, ArrayList<MessageModel> itemList, MessagesAdapterCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;
    }

    public void setItemList(ArrayList<MessageModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        if (viewType == RIGHT_CHAT) {
            View view = LayoutInflater.from(context).inflate(R.layout.right_chat_layout, parent, false);
            viewHolder = new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.left_chat_layout, parent, false);
            viewHolder = new ViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel model = itemList.get(position);
        if (model.getMessageById() != null) {
            if (model.getMessageById().equals(SharedPrefs.getUserModel().getPhone())) {
                return RIGHT_CHAT;
            } else {
                return LEFT_CHAT;
            }
        }
        return -1;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final MessageModel model = itemList.get(position);

        if (model.getMessageType().equalsIgnoreCase(Constants.MESSAGE_TYPE_DELETED)) {
            holder.deletedLayout.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.GONE);
            holder.messageText.setVisibility(View.GONE);
        } else if (model.getMessageType().equalsIgnoreCase(Constants.MESSAGE_TYPE_TEXT)) {
            holder.messageText.setText(model.getText());
            holder.time.setText(CommonUtils.getFormattedTime(model.getTime()));
            holder.deletedLayout.setVisibility(View.GONE);
            holder.time.setVisibility(View.VISIBLE);
            holder.messageText.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                callback.onSelected(model);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, messageText, time;
        LinearLayout deletedLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            messageText = itemView.findViewById(R.id.messageText);
            time = itemView.findViewById(R.id.time);
            deletedLayout = itemView.findViewById(R.id.deletedLayout);
        }
    }

    public interface MessagesAdapterCallback {
        public void onSelected(MessageModel model);
    }
}

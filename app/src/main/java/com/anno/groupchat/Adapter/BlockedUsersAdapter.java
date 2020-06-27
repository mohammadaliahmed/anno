package com.anno.groupchat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anno.groupchat.Models.UserModel;
import com.anno.groupchat.R;
import com.anno.groupchat.Utils.SharedPrefs;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BlockedUsersAdapter extends RecyclerView.Adapter<BlockedUsersAdapter.ViewHolder> {
    Context context;
    ArrayList<UserModel> itemList;
    BLockUserListAdapterCallback callback;

    boolean canEdit;

    public BlockedUsersAdapter(Context context, ArrayList<UserModel> itemList, BLockUserListAdapterCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;

    }

    public void setItemList(ArrayList<UserModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blocked_user_item_layout, parent, false);
        BlockedUsersAdapter.ViewHolder viewHolder = new BlockedUsersAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final UserModel model = itemList.get(position);
        if (model.getAvatar().equalsIgnoreCase("avatar1")) {
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar1));
        } else if (model.getAvatar().equalsIgnoreCase("avatar2")) {
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar2));
        } else if (model.getAvatar().equalsIgnoreCase("avatar3")) {
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar3));
        } else if (model.getAvatar().equalsIgnoreCase("avatar4")) {
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar4));
        } else if (model.getAvatar().equalsIgnoreCase("avatar5")) {
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar5));
        }

//        holder.name.setText(model.getName());
        try {
            if (SharedPrefs.getPhoneContactsName().get(model.getPhone().substring(model.getPhone().length() - 8)) == null) {
                holder.name.setText(model.getName());
            } else {
                holder.name.setText(SharedPrefs.getPhoneContactsName().get(model.getPhone().substring(model.getPhone().length() - 8)));
            }
        } catch (Exception e) {

        }


        if (canEdit) {
            holder.unblock.setVisibility(View.VISIBLE);
        } else {
            holder.unblock.setVisibility(View.GONE);

        }
        holder.unblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onClick(model.getPhone());


            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        Button unblock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            unblock = itemView.findViewById(R.id.unblock);

        }
    }

    public interface BLockUserListAdapterCallback {

        public void onClick(String id);
    }
}

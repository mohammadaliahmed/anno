package com.appsinventiv.anno.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.anno.Activites.SingleChatScreen;
import com.appsinventiv.anno.Models.UserModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.SharedPrefs;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.security.Signature;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    Context context;
    ArrayList<UserModel> itemList;
    UserListAdapterCallback callback;


    public UserListAdapter(Context context, ArrayList<UserModel> itemList, UserListAdapterCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;
    }

    public void setItemList(ArrayList<UserModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item_layout, parent, false);
        UserListAdapter.ViewHolder viewHolder = new UserListAdapter.ViewHolder(view);


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

        holder.name.setText(model.getName());

        if (SharedPrefs.getUserModel().getBlockedList() != null && SharedPrefs.getUserModel().getBlockedList().containsKey(model.getPhone())) {
            holder.blockLayout.setVisibility(View.VISIBLE);
            holder.checkbox.setVisibility(View.GONE);
            holder.blockText.setText("You blocked this user. Select to unblock");
        } else if (SharedPrefs.getUserModel().getBlockedMe() != null && SharedPrefs.getUserModel().getBlockedMe().containsKey(model.getPhone())) {
            holder.blockLayout.setVisibility(View.VISIBLE);
            holder.blockText.setText("This user has block you");
            holder.checkbox.setVisibility(View.GONE);


        } else {
            holder.checkbox.setVisibility(View.VISIBLE);

            holder.blockLayout.setVisibility(View.GONE);

        }


//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, SingleChatScreen.class);
//                intent.putExtra("name", model.getName());
//                intent.putExtra("phone", model.getPhone());
//                context.startActivity(intent);
//
//            }
//        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                callback.onLongClick(model.getPhone());
                return false;
            }
        });
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    if (compoundButton.isChecked()) {
                        callback.onSelected(model.getPhone());
                    } else {
                        callback.unSelected(model.getPhone());
                    }
                }
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
        CheckBox checkbox;
        RelativeLayout blockLayout;
        TextView blockText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            checkbox = itemView.findViewById(R.id.checkbox);
            blockText = itemView.findViewById(R.id.blockText);
            blockLayout = itemView.findViewById(R.id.blockLayout);
        }
    }

    public interface UserListAdapterCallback {
        public void onSelected(String id);

        public void unSelected(String id);

        public void onLongClick(String id);
    }
}

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
import android.widget.TextView;

import com.appsinventiv.anno.Activites.SingleChatScreen;
import com.appsinventiv.anno.Models.UserModel;
import com.appsinventiv.anno.R;

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
        if (model.getAvatar().equalsIgnoreCase("man1")) {
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.man1));
        } else if (model.getAvatar().equalsIgnoreCase("man2")) {
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.man2));
        } else if (model.getAvatar().equalsIgnoreCase("girl1")) {
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.girl1));
        } else if (model.getAvatar().equalsIgnoreCase("girl2")) {
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.girl2));
        }

        holder.name.setText(model.getName());

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }

    public interface UserListAdapterCallback {
        public void onSelected(String id);

        public void unSelected(String id);
    }
}

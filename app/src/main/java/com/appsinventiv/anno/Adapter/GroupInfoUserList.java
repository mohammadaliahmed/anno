package com.appsinventiv.anno.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.anno.Models.UserModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.SharedPrefs;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupInfoUserList extends RecyclerView.Adapter<GroupInfoUserList.ViewHolder> {
    Context context;
    ArrayList<UserModel> itemList;
    GroupUserListAdapterCallback callback;
    boolean isAdmin;


    public GroupInfoUserList(Context context, ArrayList<UserModel> itemList, GroupUserListAdapterCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;
    }

    public void setItemList(ArrayList<UserModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.g_user_item_layout, parent, false);
        GroupInfoUserList.ViewHolder viewHolder = new GroupInfoUserList.ViewHolder(view);

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


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (isAdmin) {
                    if (!model.getPhone().equalsIgnoreCase(SharedPrefs.getUserModel().getPhone())) {
                        callback.onSelected(model.getPhone());

                    }
                }
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
        TextView name;
        CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }

    public interface GroupUserListAdapterCallback {
        public void onSelected(String id);

    }
}

package com.appsinventiv.anno.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import de.hdodenhof.circleimageview.CircleImageView;

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
        if (isAdmin) {
            if (model.getPhone().equalsIgnoreCase(SharedPrefs.getUserModel().getPhone())) {
                holder.kick.setVisibility(View.GONE);
                holder.admin.setText("*admin");
            } else {
                holder.kick.setVisibility(View.VISIBLE);
                holder.admin.setText("");


            }
        } else {
            holder.kick.setVisibility(View.GONE);
            holder.admin.setText("");

        }

        if (SharedPrefs.getPhoneContactsName().get(model.getPhone().substring(model.getPhone().length() - 8)) == null) {
            holder.name.setText(model.getName());
        } else {
            holder.name.setText(SharedPrefs.getPhoneContactsName().get(model.getPhone().substring(model.getPhone().length() - 8)));
        }

//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                if (isAdmin) {
//                    if (!model.getPhone().equalsIgnoreCase(SharedPrefs.getUserModel().getPhone())) {
//                        callback.onSelected(model.getPhone());
//
//                    }
//                }
//                return false;
//            }
//        });

        holder.kick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onSelected(model.getPhone());

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
        ImageView kick;
        TextView admin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            checkbox = itemView.findViewById(R.id.checkbox);
            kick = itemView.findViewById(R.id.kick);
            admin = itemView.findViewById(R.id.admin);
        }
    }

    public interface GroupUserListAdapterCallback {
        public void onSelected(String id);

    }
}

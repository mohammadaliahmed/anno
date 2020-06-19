package com.appsinventiv.anno.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appsinventiv.anno.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.ViewHolder> {
    Context context;
    ArrayList<String> avatarList;
    int selected = -1;
    AvatarAdapterCallback callback;

    public AvatarAdapter(Context context, ArrayList<String> avatarList, AvatarAdapterCallback callback) {
        this.context = context;
        this.avatarList = avatarList;
        this.callback = callback;
    }

    public void setSelected(int selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.avatar_item, parent, false);
        AvatarAdapter.ViewHolder viewHolder = new AvatarAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final String name = avatarList.get(position);
        if (name.equalsIgnoreCase("avatar1")) {
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar1));
        } else if (name.equalsIgnoreCase("avatar2")) {
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar2));
        } else if (name.equalsIgnoreCase("avatar3")) {
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar3));
        } else if (name.equalsIgnoreCase("avatar4")) {
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar4));
        }else if (name.equalsIgnoreCase("avatar5")) {
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar5));
        }
        if (selected == position) {
            holder.tick.setVisibility(View.VISIBLE);
        } else {
            holder.tick.setVisibility(View.GONE);
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onSelected(name);
                selected = position;
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return avatarList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, tick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tick = itemView.findViewById(R.id.tick);
            image = itemView.findViewById(R.id.image);
        }
    }

    public interface AvatarAdapterCallback {
        public void onSelected(String name);
    }
}

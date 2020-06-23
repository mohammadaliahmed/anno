package com.anno.groupchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.anno.groupchat.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class MemesAdapter extends RecyclerView.Adapter<MemesAdapter.ViewHolder> {
    Context context;
    ArrayList<String> itemList;
    DatabaseReference mDatabase;
    boolean isGif;

    public void setGif(boolean gif) {
        isGif = gif;
        notifyDataSetChanged();
    }

    public MemesAdapter(Context context, ArrayList<String> itemList) {
        this.context = context;
        this.itemList = itemList;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void setItemList(ArrayList<String> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meme_item, parent, false);
        MemesAdapter.ViewHolder viewHolder = new MemesAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final String url = itemList.get(position);
        Glide.with(context).load(url).into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("sender", "Broadcasting message");
                Intent intent = new Intent("memeShare");
                // You can also include some extra data.
                intent.putExtra("picUrl", url);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, tick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }


}

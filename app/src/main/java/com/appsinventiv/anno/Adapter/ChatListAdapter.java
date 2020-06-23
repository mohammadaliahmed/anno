package com.appsinventiv.anno.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.anno.Activites.NativeAds.NativeTemplateStyle;
import com.appsinventiv.anno.Activites.NativeAds.TemplateView;
import com.appsinventiv.anno.Activites.SingleChatScreen;
import com.appsinventiv.anno.Models.GroupModel;
import com.appsinventiv.anno.Models.MessageModel;
import com.appsinventiv.anno.Models.UserModel;
import com.appsinventiv.anno.R;
import com.appsinventiv.anno.Utils.CommonUtils;
import com.appsinventiv.anno.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private static final int AD_TYPE = 1;
    private static final int CONTENT_TYPE = 0;
    private static final int LIST_AD_DELTA = 5;

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

    public void updateUnread() {
        if (itemList != null && itemList.size() > 0) {

            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ChatListAdapter.ViewHolder viewHolder;
        if (viewType == CONTENT_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_layout, parent, false);
            viewHolder = new ChatListAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.google_ad_layout, parent, false);
            viewHolder = new ChatListAdapter.ViewHolder(view);
        }

        return viewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);

        switch (viewType) {
            case CONTENT_TYPE:
//                final GroupModel model = itemList.get(position);

                HashMap<String, Boolean> map = SharedPrefs.getUnreadMessages();

                final GroupModel model = itemList.get(getRealPosition(position));
                if (map != null && map.get(model.getId()) != null && map.get(model.getId())) {
                    holder.name.setTypeface(holder.name.getTypeface(), Typeface.BOLD);
                    holder.message.setTypeface(holder.message.getTypeface(), Typeface.BOLD);
                    holder.unread_dot.setVisibility(View.VISIBLE);

                } else {
                    holder.name.setTypeface(holder.name.getTypeface(), Typeface.NORMAL);
                    holder.message.setTypeface(holder.message.getTypeface(), Typeface.NORMAL);
                    holder.unread_dot.setVisibility(View.GONE);
                }

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

                break;
            case AD_TYPE:
//                AdRequest adRequest = new AdRequest.Builder().build();
//                holder.adView.loadAd(adRequest);
                holder.template.setVisibility(View.VISIBLE);
                String adId = "ca-app-pub-5349923547931941/3486006875";
                String testAdId = "ca-app-pub-3940256099942544/2247696110";
                String adToShow = adId;
                MobileAds.initialize(context, adToShow);
                AdLoader adLoader = new AdLoader.Builder(context, adToShow)
                        .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                            @Override
                            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                                ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary));


                                NativeTemplateStyle styles = new
                                        NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();

                                holder.template.setStyles(styles);
                                holder.template.setNativeAd(unifiedNativeAd);

                            }
                        })
                        .build();

                adLoader.loadAd(new AdRequest.Builder().build());

                break;
        }


    }

    private int getRealPosition(int position) {
        if (LIST_AD_DELTA == 0) {
            return position;
        } else {
            return position - position / LIST_AD_DELTA;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position > 0 && position == 5)
            return AD_TYPE;
        return CONTENT_TYPE;
    }

    @Override
    public int getItemCount() {
//        return itemList.size();
        int additionalContent = 0;
        if (itemList.size() > 0 && LIST_AD_DELTA > 0 && itemList.size() > LIST_AD_DELTA) {
            additionalContent = itemList.size() / LIST_AD_DELTA;
        }
        return itemList.size() + additionalContent;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, message, time;

        AdView adView;
        TemplateView template;
        View unread_dot;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            adView = itemView.findViewById(R.id.adView);
            template = itemView.findViewById(R.id.my_template);
            unread_dot = itemView.findViewById(R.id.unread_dot);
        }
    }

    public interface ChatListAdapterCallback {
        public void onSelected(String name);
    }
}

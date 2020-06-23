package com.anno.groupchat.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.anno.groupchat.R;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by AliAh on 15/01/2018.
 */

public class PhotosAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<String> pictures;

    public PhotosAdapter(Context context, List<String> pictures) {
        this.context = context;
        this.pictures = pictures;
    }

    @Override
    public int getCount() {
        return pictures.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (LinearLayout) object);

    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.pictures_slider_layout, container, false);
        ImageView imageView = view.findViewById(R.id.images);

        Glide.with(context)
                .load(pictures.get(position))

                .into(imageView);


        imageView.setOnTouchListener(new ImageMatrixTouchHandler(context));


        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}

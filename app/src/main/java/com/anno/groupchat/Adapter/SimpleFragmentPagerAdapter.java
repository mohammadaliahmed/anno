package com.anno.groupchat.Adapter;

import android.content.Context;

import com.anno.groupchat.Fragments.EighteenFragment;
import com.anno.groupchat.Fragments.GifsFragment;
import com.anno.groupchat.Fragments.MemesFragment;
import com.anno.groupchat.Fragments.ReatFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


/**
 * Created by AliAh on 02/03/2018.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new MemesFragment();
        } else if (position == 1) {
            return new GifsFragment();
        } else if (position == 2) {
            return new ReatFragment();
        } else if (position == 3) {
            return new EighteenFragment();
        } else {
            return null;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 4;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "Memes";
            case 1:
                return "GIFs";
            case 2:
                return "Reacts";
            case 3:
                return "18+";


            default:
                return null;
        }
    }

}

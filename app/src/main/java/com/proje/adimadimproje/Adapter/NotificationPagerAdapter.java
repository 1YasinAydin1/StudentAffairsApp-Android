package com.proje.adimadimproje.Adapter;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.proje.adimadimproje.fragment.MessageFragment;
import com.proje.adimadimproje.fragment.LikeFragment;


public class NotificationPagerAdapter extends FragmentStatePagerAdapter {
    final int pageCount = 2;

    private String tabTitles[] = new String[]{"Mesajlar","Bildirim"};

    public NotificationPagerAdapter(@NonNull FragmentManager fm) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return  new MessageFragment();
            case 1:
                return  new LikeFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}

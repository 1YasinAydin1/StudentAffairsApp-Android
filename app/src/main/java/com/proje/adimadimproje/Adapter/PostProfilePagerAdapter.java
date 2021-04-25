package com.proje.adimadimproje.Adapter;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.proje.adimadimproje.WrapContentHeightViewPager;
import com.proje.adimadimproje.fragment.PostFragment;

import java.util.ArrayList;
import java.util.List;


public class PostProfilePagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    private int mCurrentPosition = -1;

    public PostProfilePagerAdapter(FragmentManager fm,String controlUser) {
        super(fm);
        this.fragments = new ArrayList<Fragment>();
        fragments.add(new PostFragment(1));
        if (controlUser.equals("currentID"))
            fragments.add(new PostFragment(2));
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (position != mCurrentPosition) {
            Fragment fragment = (Fragment) object;
            WrapContentHeightViewPager pager = (WrapContentHeightViewPager) container;
            if (fragment != null && fragment.getView() != null) {
                mCurrentPosition = position;
                pager.measureCurrentView(fragment.getView());
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
package com.proje.adimadimproje.Adapter;

import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.proje.adimadimproje.WrapContentHeightViewPager;
import com.proje.adimadimproje.fragment.PostFragment;

import java.util.ArrayList;
import java.util.List;

public class PostSalesPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;
    private int mCurrentPosition = -1;

    private String tabTitles[] = new String[]{"Satılıyor","Satıldı","Favori"};
    public PostSalesPagerAdapter(FragmentManager fm,String controlUser) {
        super(fm);
        this.fragments = new ArrayList<Fragment>();
        fragments.add(new PostFragment(3));
        fragments.add(new PostFragment(4));
        if (controlUser.equals("currentID"))
            fragments.add(new PostFragment(5));

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

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
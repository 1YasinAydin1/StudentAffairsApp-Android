package com.proje.adimadimproje.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.proje.adimadimproje.R;
import com.proje.adimadimproje.Adapter.NotificationPagerAdapter;

public class NotificationFragment extends Fragment {


    // Burası MessageFragment ve LikeFragment fragmanlarının ana tutucu fragmanıdır
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        ViewPager vp = view.findViewById(R.id.pager);
        PagerAdapter adapter = new NotificationPagerAdapter(getActivity().getSupportFragmentManager());

        vp.setAdapter(adapter);
        TabLayout tL = view.findViewById(R.id.sfsfsf);

        tL.setupWithViewPager(vp);
        tL.getTabAt(0).setIcon(R.drawable.ic_message);
        tL.getTabAt(1).setIcon(R.drawable.ic_bell);
        return view;
    }
}
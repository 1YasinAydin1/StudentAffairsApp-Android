package com.proje.adimadimproje.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proje.adimadimproje.Activity.PostAddProfileActivity;
import com.proje.adimadimproje.Activity.PostSalesActivity;
import com.proje.adimadimproje.R;

import pl.droidsonroids.gif.GifImageView;

public class PostSwitchFragment extends Fragment {

    GifImageView SalesPost, ProfilePost;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_switch, container, false);

       SalesPost = view.findViewById(R.id.SalesPost);
        SalesPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), PostSalesActivity.class));
            }
        });
       ProfilePost = view.findViewById(R.id.ProfilPost);
        ProfilePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), PostAddProfileActivity.class));
            }
        });
        return view;
    }
}
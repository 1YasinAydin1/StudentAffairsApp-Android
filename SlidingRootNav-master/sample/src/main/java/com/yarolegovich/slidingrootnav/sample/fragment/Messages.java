package com.yarolegovich.slidingrootnav.sample.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yarolegovich.slidingrootnav.sample.R;

public class Messages extends Fragment {


    View view;
    TextView textView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_messages, container, false);
       /* if (getArguments() !=null){
            String info = getArguments().getString("info");

        }*/
        textView = view.findViewById(R.id.textView3);
        textView.setText(INFO.sss);
        return view;
    }

}
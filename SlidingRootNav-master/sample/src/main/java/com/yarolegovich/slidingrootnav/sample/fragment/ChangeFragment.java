package com.yarolegovich.slidingrootnav.sample.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yarolegovich.slidingrootnav.sample.R;

public class ChangeFragment extends Fragment {

    private Context context;

    public ChangeFragment(Context context){
    this.context = context;
    }
    public void change(Fragment fragment){
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,fragment,"fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    /*public void change2(Fragment fragment,String info){
        Bundle bundle = new Bundle();
        bundle.putString("info",info);
        fragment.setArguments(bundle);
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,fragment,"fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }*/

}
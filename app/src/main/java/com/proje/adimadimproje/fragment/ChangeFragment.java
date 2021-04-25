package com.proje.adimadimproje.fragment;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.proje.adimadimproje.R;


public class ChangeFragment extends Fragment {

    private Context context;

    // Fragman değiştirme işleminde kodlar tekrar tekrar yazmak yerine bir defa yazılmış ve ilgili yerlerde çağırılmıştır
    public ChangeFragment(Context context){
        this.context = context;
    }
    public void change(Fragment fragment){
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left,R.anim.enter_left_to_right,R.anim.exit_left_to_right)
                .replace(R.id.container,fragment)
                .addToBackStack(null)
                .commit();
    }


}

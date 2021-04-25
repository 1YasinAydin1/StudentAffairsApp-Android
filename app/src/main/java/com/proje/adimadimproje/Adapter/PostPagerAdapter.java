package com.proje.adimadimproje.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.proje.adimadimproje.Activity.MainActivity;
import com.proje.adimadimproje.Activity.PostDetailActivity;
import com.proje.adimadimproje.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostPagerAdapter extends PagerAdapter {

    private Context context;
    private String[] imageUris;
    private String postID;
    private String PostControl;
    boolean isTrue;

    public PostPagerAdapter(Context context, String[] imageUris, String postID, String PostControl) {
        this.context = context;
        this.imageUris = imageUris;
        this.postID = postID;
        this.PostControl = PostControl;
    }

    @Override
    public int getCount() {
        return imageUris.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final ImageView imageView = new ImageView(context);
        Picasso.get().load(imageUris[position]).fit().centerInside().into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() { // PostDetail aktivitesinde defalarca baştan açılmayı önlemek içindir
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                isTrue = sharedPreferences.getBoolean("forPostDetailOnClick", false);
                if (!isTrue){
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("forPostDetailOnClick",true);
                    editor.apply();
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postID",postID);
                if (PostControl.equals("Profile"))
                    intent.putExtra("post","Profile");
                else
                    intent.putExtra("post","Sales");

                context.startActivity(intent);
            }
            }
        });
        container.addView(imageView);
        return imageView;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}

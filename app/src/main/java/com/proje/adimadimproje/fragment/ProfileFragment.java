package com.proje.adimadimproje.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.proje.adimadimproje.Activity.ChatActivity;
import com.proje.adimadimproje.Activity.ListActivity;
import com.proje.adimadimproje.Activity.MainActivity;
import com.proje.adimadimproje.Activity.ProfileEditActivity;
import com.proje.adimadimproje.Adapter.PostProfilePagerAdapter;
import com.proje.adimadimproje.Adapter.PostSalesPagerAdapter;
import com.proje.adimadimproje.Model.PostProfile;
import com.proje.adimadimproje.Model.PostSales;
import com.proje.adimadimproje.Model.User;
import com.proje.adimadimproje.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    CircleImageView ProfileCircleIV;

    ImageButton ProfileMessageButton,ProfileFollowButton,ProfileSettings,ProfileLocationImageButton;
    TextView ProfileUserName,ProfileUserName2,ProfileUserNameLastName,ProfilePostCount,ProfileFollowedCount,ProfileFollowerCount,ProfileSwapTextView;
    ImageView ProfileBackGroundImageView,ProfilePostSwap;
    Boolean isTrue = false;
    int postCount = 0;
    String controlUser;
    FirebaseUser firebaseUser;
    String profileID;
    List<String> FavoriteList;
    LinearLayout centerInfo,rightInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileID = sharedPreferences.getString("profileID", "none");

        ProfileCircleIV = view.findViewById(R.id.ProfileCircleIV);
        ProfilePostSwap = view.findViewById(R.id.ProfilePostSwap);
        ProfileSwapTextView = view.findViewById(R.id.ProfileSwapTextView);

        // region FollowList
        centerInfo = view.findViewById(R.id.centerInfo);
        centerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("ID",profileID);
                intent.putExtra("title","Follower");
                startActivity(intent);
            }
        });
        rightInfo = view.findViewById(R.id.rightInfo);
        rightInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("ID",profileID);
                intent.putExtra("title","Followed");
                startActivity(intent);
            }
        });
        // endregion
        // region Tab

        if (profileID.equals(firebaseUser.getUid()))
            controlUser = "currentID";
        else
            controlUser = "profileID";




        profilePostSocialTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#373636"),PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#5E5656"),PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        ////////////////////////////////////////////////////////////////////////////////////////////////
        // Kişisel Kullanım içindir
        PostProfilePagerAdapter pageAdapter = new PostProfilePagerAdapter(getActivity().getSupportFragmentManager(),controlUser);
        ViewPager pager = (ViewPager)view.findViewById(R.id.ProfilePostpager);
        pager.setAdapter(pageAdapter);
        TabLayout ProfilePostTabLayout = view.findViewById(R.id.ProfilePostTabLayout);

        ProfilePostTabLayout.setupWithViewPager(pager);
        ProfilePostTabLayout.getTabAt(0).setIcon(R.drawable.ic_post_focus);
        ProfilePostTabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#343434"), PorterDuff.Mode.SRC_IN);
        if (controlUser.equals("currentID")){
            ProfilePostTabLayout.getTabAt(1).setIcon(R.drawable.ic_profile_post_favorite);
            ProfilePostTabLayout.getTabAt(1).getIcon().setColorFilter(Color.parseColor("#8f8f8f"), PorterDuff.Mode.SRC_IN);
        }
        ProfilePostTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#343434"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#8f8f8f"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        ////////////////////////////////////////////////////////////////////////////////////////////////
        // İlanlar içindir
        PostSalesPagerAdapter pageAdapter1 = new PostSalesPagerAdapter(getActivity().getSupportFragmentManager(),controlUser);
        ViewPager pager1 = (ViewPager)view.findViewById(R.id.SalesPostpager);
        pager1.setAdapter(pageAdapter1);
        TabLayout SalesPostTabLayout = view.findViewById(R.id.SalesPostTabLayout);

        SalesPostTabLayout.setupWithViewPager(pager1);
        SalesPostTabLayout.getTabAt(0).setIcon(R.drawable.ic_sell);
        SalesPostTabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#343434"), PorterDuff.Mode.SRC_IN);
        SalesPostTabLayout.getTabAt(1).setIcon(R.drawable.ic_hand_shake);
        SalesPostTabLayout.getTabAt(1).getIcon().setColorFilter(Color.parseColor("#8f8f8f"), PorterDuff.Mode.SRC_IN);

        if (controlUser.equals("currentID")){
            SalesPostTabLayout.getTabAt(2).setIcon(R.drawable.ic_profile_post_favorite);
            SalesPostTabLayout.getTabAt(2).getIcon().setColorFilter(Color.parseColor("#8f8f8f"), PorterDuff.Mode.SRC_IN);
        }
        SalesPostTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#343434"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#8f8f8f"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ProfilePostSwap.setOnClickListener(new View.OnClickListener() { // Hesaplar arası geçiş işlemi
            @Override
            public void onClick(View v) {
                if (isTrue){
                    ProfileSwapTextView.setText("Kişisel Hesap");
                    ProfilePostTabLayout.setVisibility(View.VISIBLE);
                    SalesPostTabLayout.setVisibility(View.GONE);
                    pager.setVisibility(View.VISIBLE);
                    pager1.setVisibility(View.GONE);
                    isTrue = false;
                }else{
                    ProfileSwapTextView.setText("Ticari Hesap");
                    ProfilePostTabLayout.setVisibility(View.GONE);
                    SalesPostTabLayout.setVisibility(View.VISIBLE);
                    pager.setVisibility(View.GONE);
                    pager1.setVisibility(View.VISIBLE);
                    isTrue = true;
                }
            }
        });
        // endregion

        ProfileBackGroundImageView = view.findViewById(R.id.ProfileBackGroundImageView);
        ProfileFollowButton = view.findViewById(R.id.ProfileFollowButton);
        ProfileMessageButton = view.findViewById(R.id.ProfileMessageButton);
        ProfileLocationImageButton = view.findViewById(R.id.ProfileLocationImageButton);
        ProfileLocationImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),MainActivity.class);
                intent.putExtra("location","location");
                startActivity(intent);
            }
        });

        ProfileSettings = view.findViewById(R.id.ProfileSettings);
        ProfileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ProfileEditActivity.class));
            }
        });

        ProfileUserName = view.findViewById(R.id.ProfileUserName);
        ProfileUserName2 = view.findViewById(R.id.ProfileUserName2);
        ProfileUserNameLastName = view.findViewById(R.id.ProfileUserNameLastName);
        ProfilePostCount = view.findViewById(R.id.ProfilePostCount);
        ProfileFollowedCount = view.findViewById(R.id.ProfileFollowedCount);
        ProfileFollowerCount = view.findViewById(R.id.ProfileFollowerCount);

        Control(firebaseUser.getUid(),profileID);

        return view;
    }

    private void Control(String getCurrentUserID, String profileID){
        // Kullanıcılara göre değişen yapı
        if (getCurrentUserID.equals(profileID)){// profilden gelen
            userInfo(getCurrentUserID);
            getFollowed(getCurrentUserID);
            getFollower(getCurrentUserID);
            getPostProfile(getCurrentUserID);
            ProfileFollowButton.setVisibility(View.GONE);
            ProfileMessageButton.setVisibility(View.GONE);
        }else{// searchtan gelen
            ProfileLocationImageButton.setVisibility(View.GONE);
            ProfileSettings.setVisibility(View.GONE);
            userInfo(profileID);
            getFollowed(profileID);
            getFollower(profileID);
            getPostProfile(profileID);
            isTrueFollowingOrFalseFollowing(profileID,ProfileFollowButton);
            ProfileFollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // Follower kişiyi takip edenler Followed kişinin takip ettikleri
                    if (ProfileFollowButton.getTag().equals("Follow")){
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(firebaseUser.getUid()).child("Followed").child(profileID).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(profileID).child("Follower").child(firebaseUser.getUid()).setValue(true);

                    }else{
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(firebaseUser.getUid()).child("Followed").child(profileID).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(profileID).child("Follower").child(firebaseUser.getUid()).removeValue();

                    }
                }
            });
            ProfileMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    String saf = profileID;
                    intent.putExtra("ChatProfileID",profileID);
                    startActivity(intent);
                }
            });
        }
    }


    private void userInfo(String UserID){
        DatabaseReference userPath= FirebaseDatabase.getInstance().getReference("UserInfo").child(UserID);
        userPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Picasso.get().load(user.getUserImage()).fit().centerCrop().into(ProfileCircleIV);
                Picasso.get().load(user.getUserBackgroundImage()).fit().centerCrop().into(ProfileBackGroundImageView);
                ProfileUserName.setText(user.getUserName());
                ProfileUserName2.setText(user.getUserName());
                ProfileUserNameLastName.setText(user.getUserNameLastName());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isTrueFollowingOrFalseFollowing (final String userID, final ImageButton button){
        FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("Followed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(userID).exists()){
                    button.setTag("Followed");
                    button.setImageResource(R.drawable.ic_followed);
                }else{
                    button.setTag("Follow");
                    button.setImageResource(R.drawable.ic_follow);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowed(String UserID){
        //takip edilen sayısını al
        FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(UserID).child("Followed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProfileFollowedCount.setText(""+snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getFollower(String UserID){
        //takipçi sayısını al
        FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(UserID).child("Follower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProfileFollowerCount.setText(""+snapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getPostProfile(String profileID){
        final Query Posts = FirebaseDatabase.getInstance().getReference("PostProfile").orderByChild("time");
        Posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //postP.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    HashMap<String,Object> data = (HashMap<String, Object>) ds.getValue();
                    if (data.get("userID").equals(profileID)){
                        postCount++;
                    }
                }
                ProfilePostCount.setText(String.valueOf(postCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
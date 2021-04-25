package com.proje.adimadimproje.fragment;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.proje.adimadimproje.Adapter.PostProfileAdapter;
import com.proje.adimadimproje.Adapter.SearchUserAdapter;
import com.proje.adimadimproje.Model.PostProfile;
import com.proje.adimadimproje.Model.User;
import com.proje.adimadimproje.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView HomeRecyclerView,HomeSearchRecyclerView;
    PostProfileAdapter postProfileAdapter;
    List<PostProfile> postPs;
    List<String> FollowList;
    List<User> userList;
    SearchUserAdapter userAdapter;
    LinearLayout FollowZeroLinearLayout;
    ScrollView FollowMoreScrollView;
    SearchView HomeUserSearchView;
    ImageView HomeSearchViewDisable,HomeSearchViewEnable;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FollowZeroLinearLayout = view.findViewById(R.id.FollowZeroLinearLayout);
        FollowMoreScrollView = view.findViewById(R.id.FollowMoreScrollView);
        HomeSearchViewDisable = view.findViewById(R.id.HomeSearchViewDisable);
        HomeSearchViewEnable = view.findViewById(R.id.HomeSearchViewEnable);
        FollowZeroLinearLayout.setVisibility(View.GONE);
        FollowMoreScrollView.setVisibility(View.GONE);
        HomeUserSearchView = view.findViewById(R.id.HomeUserSearchView);
        HomeRecyclerView = view.findViewById(R.id.HomeRecyclerView);
        HomeSearchRecyclerView = view.findViewById(R.id.HomeSearchRecyclerView);
        HomeRecyclerView.setHasFixedSize(true);
        HomeSearchRecyclerView.setHasFixedSize(true);
        HomeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        HomeSearchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        postPs = new ArrayList<>();
        userList = new ArrayList<>();
        userAdapter = new SearchUserAdapter(getContext(),userList);
        HomeSearchRecyclerView .setAdapter(userAdapter);
        UserGet();
        HomeUserSearchView.setOnSearchClickListener(new View.OnClickListener() { // SearchView işlemleri
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(container);
                HomeSearchViewEnable.setVisibility(View.GONE);
                HomeSearchViewDisable.setVisibility(View.VISIBLE);
                HomeSearchRecyclerView.setVisibility(View.VISIBLE);
                HomeRecyclerView.setVisibility(View.GONE);
                FollowZeroLinearLayout.setVisibility(View.GONE);
                HomeSearchRecyclerView.setVisibility(View.VISIBLE);
                FollowMoreScrollView.setVisibility(View.GONE);

            }
        });
        HomeUserSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // SearchView arama işlemi
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                UserSearch(newText);
                return false;
            }
        });
        HomeUserSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                TransitionManager.beginDelayedTransition(container);
                HomeSearchViewEnable.setVisibility(View.VISIBLE);
                HomeSearchViewDisable.setVisibility(View.GONE);
                HomeSearchRecyclerView.setVisibility(View.GONE);
                HomeRecyclerView.setVisibility(View.VISIBLE);
                if(postPs.size() == 0){
                    FollowZeroLinearLayout.setVisibility(View.VISIBLE);
                    FollowMoreScrollView.setVisibility(View.GONE);}
                else{
                    FollowZeroLinearLayout.setVisibility(View.GONE);
                    FollowMoreScrollView.setVisibility(View.VISIBLE);}
                return false;
            }
        });
        FollowControl();

        postProfileAdapter = new PostProfileAdapter(getContext(),postPs,true);
        HomeRecyclerView.setAdapter(postProfileAdapter);
        return view;
    }


    private void UserSearch(String s){ // Kullanıcıların ID numaraları çekiliyor
        Query query = FirebaseDatabase.getInstance().getReference("UserInfo").orderByChild("userName").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    userList.add(user);
                }

                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void UserGet(){// Kullanıcıların bilgileri çekiliyor
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("UserInfo");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if (!user.getID().equals(FirebaseAuth.getInstance().getUid()))
                        userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void FollowControl(){ // Takip Durumu
        FollowList = new ArrayList<>();
        DatabaseReference follow = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Followed");
        follow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FollowList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    FollowList.add(ds.getKey());
                }
                FollowList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                getPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPosts(){ // Ana Sayfa gönderileri çekiliyor
        final Query Posts = FirebaseDatabase.getInstance().getReference("PostProfile").orderByChild("time");
        Posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postPs.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    HashMap<String,Object> data = (HashMap<String, Object>) ds.getValue();

                        for (String ID : FollowList){
                            if (data.get("userID").equals(ID)){

                                int imageSize =Integer.parseInt((String)data.get("imageSize"));

                                String[] image = new String[imageSize];
                                for (int i = 0 ; i < imageSize ; i++){
                                    String imageName = "image"+(i+1);
                                    String imageS = (String) data.get(imageName);
                                    image[i] = imageS;
                                }


                                PostProfile post = new PostProfile(data.get("PostPID")+"",data.get("userID")+"",
                                        data.get("PostPTitle")+"",  data.get("time")+"",
                                        data.get("PostPDate")+"",""+data.get("PostPTime"),
                                        data.get("imageSize")+"",image);
                                postPs.add(post);
                            }
                        }
                }
                if(postPs.size() == 0){
                    FollowZeroLinearLayout.setVisibility(View.VISIBLE);
                    FollowMoreScrollView.setVisibility(View.GONE);}
                else{
                    FollowZeroLinearLayout.setVisibility(View.GONE);
                    FollowMoreScrollView.setVisibility(View.VISIBLE);}
                Collections.reverse(postPs);
                postProfileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
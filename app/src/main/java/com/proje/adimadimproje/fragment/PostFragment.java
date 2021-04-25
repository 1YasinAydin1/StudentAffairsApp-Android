package com.proje.adimadimproje.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.proje.adimadimproje.Adapter.RowProfilePostAdapter;
import com.proje.adimadimproje.Model.PostProfile;
import com.proje.adimadimproje.Model.PostSales;
import com.proje.adimadimproje.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PostFragment extends Fragment {
    int controlNumber;
    public PostFragment(int controlNumber) {
        this.controlNumber = controlNumber;
    }

    FirebaseUser firebaseUser;

    List<PostProfile> postP,postPFavorites;
    List<PostSales> postS,postSFavorites;
    List<String> FavoriteList;
    String profileID;
    RowProfilePostAdapter postPAdapter,postPFavoritesAdapter,postSAdapter,postSFavoritesAdapter;
    RecyclerView ProfilePostRecyclerView,ProfilePostRecyclerView1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_post, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileID = sharedPreferences.getString("profileID", "none");


        ProfilePostRecyclerView = view.findViewById(R.id.ProfilePostRecyclerView);
        ProfilePostRecyclerView.setHasFixedSize(true);
        ProfilePostRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        // Durumlara göre içleri doldurulan RecyclerView
        if (controlNumber==1){
            postP = new ArrayList<>();
            postS = new ArrayList<>();
            postPAdapter = new RowProfilePostAdapter(getContext(), postP,postS);
            ProfilePostRecyclerView.setAdapter(postPAdapter);
            getPostProfile(profileID);
        }else if (controlNumber==2){
            postPFavorites = new ArrayList<>();
            postS = new ArrayList<>();
            postPFavoritesAdapter = new RowProfilePostAdapter(getContext(), postPFavorites,postS);
            ProfilePostRecyclerView.setAdapter(postPFavoritesAdapter);
            ProfileFavoritesControl();
        }else if (controlNumber==3){
            postP = new ArrayList<>();
            postS = new ArrayList<>();
            postSAdapter = new RowProfilePostAdapter(getContext(), postP,postS);
            ProfilePostRecyclerView.setAdapter(postSAdapter);
            getPostSales();
        }else if (controlNumber==4){
            postP = new ArrayList<>();
            postS = new ArrayList<>();
            postSAdapter = new RowProfilePostAdapter(getContext(), postP,postS);
            ProfilePostRecyclerView.setAdapter(postSAdapter);
            getPostSales();
        }else if (controlNumber==5){
            postP = new ArrayList<>();
            postSFavorites = new ArrayList<>();
            postSFavoritesAdapter = new RowProfilePostAdapter(getContext(), postP,postSFavorites);
            ProfilePostRecyclerView.setAdapter(postSFavoritesAdapter);
            SalesFavoritesControl();
        }

        return view;
    }
    private void getPostProfile(String profileID){
        final Query Posts = FirebaseDatabase.getInstance().getReference("PostProfile").orderByChild("time");
        Posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postP.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    HashMap<String,Object> data = (HashMap<String, Object>) ds.getValue();

                    if (data.get("userID").equals(profileID)){

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
                        postP.add(post);

                    }
                }
                Collections.reverse(postP);
                postPAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ProfileFavoritesControl(){
        FavoriteList = new ArrayList<>();
        DatabaseReference follow = FirebaseDatabase.getInstance().getReference("ProfileFavorites").child(firebaseUser.getUid());
        follow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FavoriteList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    FavoriteList.add(ds.getKey());
                }
                getProfileFavorites();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getProfileFavorites(){
        final Query favorites = FirebaseDatabase.getInstance().getReference("PostProfile");
        favorites.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postPFavorites.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    HashMap<String,Object> data = (HashMap<String, Object>) ds.getValue();

                    for (String ID : FavoriteList){
                        if (data.get("PostPID").equals(ID)){
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
                            postPFavorites.add(post);
                        }
                    }
                }
                Collections.reverse(postPFavorites);
                postPFavoritesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void SalesFavoritesControl(){
        FavoriteList = new ArrayList<>();
        DatabaseReference follow = FirebaseDatabase.getInstance().getReference("SalesFavorites").child(firebaseUser.getUid());
        follow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FavoriteList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    FavoriteList.add(ds.getKey());
                }
                getSalesFavorites();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getSalesFavorites() {
        final Query favorites = FirebaseDatabase.getInstance().getReference("PostSales");
        favorites.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postSFavorites.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    HashMap<String,Object> data = (HashMap<String, Object>) ds.getValue();
                    String saf = (String) data.get("PostSID");
                    for (String ID : FavoriteList){
                        if (data.get("PostSID").equals(ID)){
                            int imageSize =Integer.parseInt((String)data.get("imageSize"));
                            String[] image = new String[imageSize];
                            for (int i = 0 ; i < imageSize ; i++){
                                String imageName = "image"+(i+1);
                                String imageS = (String) data.get(imageName);
                                image[i] = imageS;
                            }
                            PostSales post = new PostSales(data.get("PostSID")+"",data.get("userID")+"",
                                    data.get("PostSStatus")+"",data.get("PostSCategory")+"",  data.get("PostSTitle")+"",
                                    data.get("PostSComment")+"",""+data.get("PostSPrice"),
                                    data.get("PostSTag1")+"",data.get("PostSTag2")+"",data.get("PostSTag3")+"",
                                    data.get("PostSCCName1")+"",data.get("PostSCCName2")+"",data.get("PostSCCName3")+"",
                                    data.get("PostSTime")+"",data.get("PostSDate")+"",data.get("PostSTime")+"",
                                    data.get("imageSize")+"",image);
                            postSFavorites.add(post);
                        }
                    }
                }
                Collections.reverse(postSFavorites);
                postSFavoritesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getPostSales(){
        final Query Posts = FirebaseDatabase.getInstance().getReference("PostSales").orderByChild("time");
        Posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postS.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    HashMap<String,Object> data = (HashMap<String, Object>) ds.getValue();
                    Boolean postStatus = (Boolean) data.get("PostSStatus");
                    if (data.get("userID").equals(profileID)){
                        int imageSize =Integer.parseInt((String)data.get("imageSize"));
                        String[] image = new String[imageSize];
                        for (int i = 0 ; i < imageSize ; i++){
                            String imageName = "image"+(i+1);
                            String imageS = (String) data.get(imageName);
                            image[i] = imageS;
                        }
                        PostSales post = new PostSales(data.get("PostSID")+"",data.get("userID")+"",
                                data.get("PostSStatus")+"",data.get("PostSCategory")+"",  data.get("PostSTitle")+"",
                                data.get("PostSComment")+"",""+data.get("PostSPrice"),
                                data.get("PostSTag1")+"",data.get("PostSTag2")+"",data.get("PostSTag3")+"",
                                data.get("PostSCCName1")+"",data.get("PostSCCName2")+"",data.get("PostSCCName3")+"",
                                data.get("PostSTime")+"",data.get("PostSDate")+"",data.get("PostSTime")+"",
                                data.get("imageSize")+"",image);
                        if (controlNumber==3 && !postStatus)
                            postS.add(post);
                       if (controlNumber==4 && postStatus)
                            postS.add(post);
                        }
                }
                postSAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
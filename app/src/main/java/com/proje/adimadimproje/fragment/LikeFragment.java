package com.proje.adimadimproje.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proje.adimadimproje.Adapter.LikeAdapter;
import com.proje.adimadimproje.Model.Like;
import com.proje.adimadimproje.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LikeFragment extends Fragment {

    RecyclerView LikeRecyclerView;
    LikeAdapter likeAdapter;
    List<Like> likes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_like, container, false);
        LikeRecyclerView = view.findViewById(R.id.LikeRecyclerView);
        LikeRecyclerView.setHasFixedSize(true);
        LikeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        likes = new ArrayList<>();
        likeAdapter = new LikeAdapter(getContext(),likes);
        LikeRecyclerView.setAdapter(likeAdapter);
        getLike();
        return view;
    }

    private void getLike() { // Bildirimler çekiliyor
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notification").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Like like = ds.getValue(Like.class);
                    likes.add(like);
                }
                Collections.reverse(likes);
                likeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
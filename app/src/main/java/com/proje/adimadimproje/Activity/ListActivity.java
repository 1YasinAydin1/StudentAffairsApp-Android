package com.proje.adimadimproje.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proje.adimadimproje.Adapter.SearchUserAdapter;
import com.proje.adimadimproje.Model.User;
import com.proje.adimadimproje.R;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    RecyclerView ProfileFollowRecyclerView;
    List<User> users;
    List<String> userIDList;
    SearchUserAdapter searchUserAdapter;
    TextView ProfileFollowUserName,ProfileFollowTextView;
    String ID,title;
    ImageView ProfileEditClosed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent(); // Listelenecek veriler ile ilgili bilgiler alınıyor
        ID =  intent.getStringExtra("ID");
        title =  intent.getStringExtra("title");

        ProfileEditClosed = findViewById(R.id.ProfileEditClosed);
        ProfileEditClosed.setOnClickListener(new View.OnClickListener() { // Aktivite sonlandırılıyor
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ProfileFollowUserName = findViewById(R.id.ProfileFollowUserName);
        ProfileFollowTextView = findViewById(R.id.ProfileFollowTextView);

        users = new ArrayList<>();
        userIDList = new ArrayList<>();
        ProfileFollowRecyclerView = findViewById(R.id.ProfileFollowRecyclerView);
        ProfileFollowRecyclerView.setHasFixedSize(true);
        ProfileFollowRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchUserAdapter = new SearchUserAdapter(this,users);
        ProfileFollowRecyclerView.setAdapter(searchUserAdapter);
        switch (title){// Listelenecek veri seçiliyor ve ilgili fonksiyon çalıştırılıyor
            case "Followed":
                getFollowedUserInfo();
                ProfileFollowTextView.setText("Takip Edilenler");
                break;
            case "Follower":
                getFollowerUserInfo();
                ProfileFollowTextView.setText("Takipçi");
                break;
            case "Likes":
                getLikesUserInfo();
                ProfileFollowUserName.setVisibility(View.GONE);
                ProfileFollowTextView.setText("Beğeniler");
                break;
        }
        PostUserInfo();
    }

    private void getLikesUserInfo() { // İlgili gönderiyi beğenen kullanıcıların ID numaraları alınıyor
        FirebaseDatabase.getInstance().getReference("Likes").child(ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIDList.clear();
                for (DataSnapshot ds:snapshot.getChildren())
                    userIDList.add(ds.getKey());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getFollowedUserInfo() { // İlgili kullanıcının takip ettikleri kullanıcıların ID numaraları alınıyor
        FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(ID).child("Followed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIDList.clear();
                for (DataSnapshot ds:snapshot.getChildren())
                    userIDList.add(ds.getKey());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getFollowerUserInfo() {  // İlgili kullanıcıyı takip eden kullanıcıların ID numaraları alınıyor
        FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(ID).child("Follower").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIDList.clear();
                for (DataSnapshot ds:snapshot.getChildren())
                    userIDList.add(ds.getKey());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void PostUserInfo(){ // İlgili gönderiyi beğenen kullanıcıların bilgileri alınıyor
        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference("UserInfo");
        userInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    for (String ID:userIDList){
                        if (user.getID().equals(ID))
                            users.add(user);
                        ProfileFollowUserName.setText(user.getUserName());
                    }
                }
                searchUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
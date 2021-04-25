package com.proje.adimadimproje.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proje.adimadimproje.Adapter.CommentAdapter;

import com.proje.adimadimproje.Model.Comment;
import com.proje.adimadimproje.Model.User;
import com.proje.adimadimproje.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    CommentAdapter commentAdapter;
    List<Comment> commentList;
    EditText CommentEditText;
    TextView CommentEnterTextView;
    CircleImageView AddCommentProfileCircleIV;

    String PostID, PostUserID,PostControl;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        CommentEditText = findViewById(R.id.CommentEditText);
        CommentEnterTextView = findViewById(R.id.CommentEnterTextView);
        AddCommentProfileCircleIV = findViewById(R.id.AddCommentProfileCircleIV);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.CommentRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this,commentList);
        recyclerView.setAdapter(commentAdapter);

        Toolbar toolbar = findViewById(R.id.CommentToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Yorumlar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        PostID = intent.getStringExtra("PostID");
        PostControl = intent.getStringExtra("Post");
        PostUserID = intent.getStringExtra("PostuserID");

        CommentEnterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });
        GetProfileImage();
        GetComment();
    }

    private void addComment() { // Yeni yorum ekleniyor
        if (!CommentEditText.getText().equals("")){
        DatabaseReference comment = FirebaseDatabase.getInstance().getReference("Comment").child(PostID);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("Comment",CommentEditText.getText().toString());
        hashMap.put("PostControl",PostControl);
        hashMap.put("userID",firebaseUser.getUid());
        comment.push().setValue(hashMap);
        if (!PostUserID.equals(firebaseUser.getUid()))
            setPostNotification();
        CommentEditText.setText("");
        }
    }
    private void GetComment() { // Daha önce yapılmış yorumlar getiriliyor
        DatabaseReference image = FirebaseDatabase.getInstance().getReference("Comment").child(PostID);
        image.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Comment comment = ds.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetProfileImage() { // Yorum yapan kullanıcı bilgileri çekiliyor
        DatabaseReference image = FirebaseDatabase.getInstance().getReference("UserInfo").child(firebaseUser.getUid());
        image.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getUserImage()).fit().centerInside().into(AddCommentProfileCircleIV);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setPostNotification(){  // Veritabanına bildirim yazılıyor

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notification").child(PostUserID);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("userID",firebaseUser.getUid());
        hashMap.put("text","Yorum Yaptı : "+CommentEditText.getText().toString());
        hashMap.put("PostName",PostControl);
        hashMap.put("postID",PostID);
        hashMap.put("isPost",true);

        databaseReference.push().setValue(hashMap);
    }

}
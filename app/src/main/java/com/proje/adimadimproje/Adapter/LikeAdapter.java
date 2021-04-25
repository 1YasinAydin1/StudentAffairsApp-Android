package com.proje.adimadimproje.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proje.adimadimproje.Activity.MainActivity;
import com.proje.adimadimproje.Activity.PostDetailActivity;
import com.proje.adimadimproje.Model.Like;
import com.proje.adimadimproje.Model.User;
import com.proje.adimadimproje.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {

    Context context;
    List<Like> likes;

    public LikeAdapter(Context context, List<Like> likes) {
        this.context = context;
        this.likes = likes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_like,parent,false);
        return new LikeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Like like = likes.get(position);
        holder.LikeRowText.setText(like.getText());
        userInfo(holder.LikeUserRowCircleIV,holder.LikeRowUserNameTextView,like.getUserID());
        if (like.getIsPost()){
            holder.LikePostImage.setVisibility(View.VISIBLE);
            if (like.getPostName().equals("Profile"))
                postInfo(holder.LikePostImage,like.getPostID(),"Profile");
            else
                postInfo(holder.LikePostImage,like.getPostID(),"Sales");

        }else{
            holder.LikePostImage.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like.getIsPost()){
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postID",like.getPostID());
                    if (like.getPostName().equals("Profile"))
                        intent.putExtra("post","Profile");
                    else
                        intent.putExtra("post","Sales");
                    context.startActivity(intent);

                }else{
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileID",like.getUserID());
                    editor.apply();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("profileID",like.getUserID());
                    intent.putExtra("PostSales","PostSales1");
                    intent.putExtra("location","location1");
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return likes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView LikeUserRowCircleIV;
        TextView LikeRowUserNameTextView,LikeRowText;
        ImageView LikePostImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            LikeUserRowCircleIV = itemView.findViewById(R.id.LikeUserRowCircleIV);
            LikeRowUserNameTextView = itemView.findViewById(R.id.LikeRowUserNameTextView);
            LikeRowText = itemView.findViewById(R.id.LikeRowText);
            LikePostImage = itemView.findViewById(R.id.LikePostImage);
        }
    }

    private void userInfo(CircleImageView LikeUserRowCircleIV,TextView LikeRowUserNameTextView, String userID){
        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference("UserInfo").child(userID);
        userInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                LikeRowUserNameTextView.setText(user.getUserName());
                Picasso.get().load(user.getUserImage()).fit().centerCrop().into(LikeUserRowCircleIV);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void postInfo(ImageView LikePostImage, String postID, String controlPost){

        if (controlPost.equals("Profile")){
            DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference("PostProfile").child(postID);
            userInfo.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    HashMap<String,Object> data = (HashMap<String, Object>) snapshot.getValue();
                    if (data!=null){
                    Picasso.get().load(data.get("image1").toString()).fit().centerCrop().into(LikePostImage);}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });}else{
            DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference("PostSales").child(postID);
            userInfo.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String,Object> data = (HashMap<String, Object>) snapshot.getValue();
                    if (data!=null){
                    Picasso.get().load(data.get("image1").toString()).fit().centerCrop().into(LikePostImage);}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });}
    }
}

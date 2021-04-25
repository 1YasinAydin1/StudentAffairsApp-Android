package com.proje.adimadimproje.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proje.adimadimproje.Activity.MainActivity;
import com.proje.adimadimproje.Model.Comment;
import com.proje.adimadimproje.Model.User;
import com.proje.adimadimproje.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    Context context;
    List<Comment> commentList;
    FirebaseUser firebaseUser;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_comment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = commentList.get(position);
        holder.CommentTextView.setText(comment.getComment());
        getUserInfo(holder.CommentProfileCircleIV,holder.CommentUserNameTextView,comment.getUserID());
        holder.CommentUserNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("PostUserID",comment.getUserID());
                context.startActivity(intent);
            }
        });
        holder.CommentProfileCircleIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("PostUserID",comment.getUserID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView CommentUserNameTextView,CommentTextView;
        CircleImageView CommentProfileCircleIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CommentUserNameTextView = itemView.findViewById(R.id.CommentUserNameTextView);
            CommentTextView = itemView.findViewById(R.id.CommentTextView);
            CommentProfileCircleIV = itemView.findViewById(R.id.CommentProfileCircleIV);
        }
    }
    private void getUserInfo(final CircleImageView imageView,final TextView CommentUserNameTextView, final String userID){

        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(userID);
        userInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getUserImage()).fit().centerCrop().into(imageView);
                CommentUserNameTextView.setText(user.getUserName());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

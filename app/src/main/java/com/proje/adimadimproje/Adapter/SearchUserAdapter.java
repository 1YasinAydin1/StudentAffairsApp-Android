package com.proje.adimadimproje.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
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
import com.proje.adimadimproje.Model.User;
import com.proje.adimadimproje.R;
import com.proje.adimadimproje.fragment.ChangeFragment;
import com.proje.adimadimproje.fragment.ProfileFragment;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SearchUserAdapter extends  RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;
    private EditText SearchEditText;

    private FirebaseUser firebaseUser;
    Boolean isTrue = false;
    Resources res;
    public SearchUserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        res = context.getResources();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(context).inflate(R.layout.row_user,parent,false);
            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        if (isTrue){
            holder.RowUserRelativeLayout.setBackground(res.getDrawable(R.color.color12));isTrue = false;}
        else{
            holder.RowUserRelativeLayout.setBackground(res.getDrawable(R.color.white));isTrue = true;}

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = userList.get(position);
        holder.FollowButton.setVisibility(View.VISIBLE);

        holder.UserRowUserNameLastNameTextView.setText(user.getUserNameLastName());
        holder.UserRowUserNameTextView.setText(user.getUserName());
        Picasso.get().load(user.getUserImage()).fit().centerCrop().into(holder.UserRowCircleIV);
        isTrueFollowingOrFalseFollowing(user.getID(),holder.FollowButton);
        if (user.getID().equals(firebaseUser.getUid()))
            holder.FollowButton.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() { // Profile Yönlendiriliyor
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MainActivity.class);
                intent.putExtra("profileID",user.getID());
                intent.putExtra("location","location1");
                intent.putExtra("PostSales","PostSales1");
                context.startActivity(intent);
            }
        });
        holder.FollowButton.setOnClickListener(new View.OnClickListener() { // Takip Durumu
            @Override
            public void onClick(View v) {
                if (holder.FollowButton.getText().toString().equals("Takip Et")){
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("Followed").child(user.getID()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getID()).child("Follower").child(firebaseUser.getUid()).setValue(true);
                    setReceiverPostNotification(user.getID());
                    setSenderPostNotification(user.getID());
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("Followed").child(user.getID()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getID()).child("Follower").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }

    private void setReceiverPostNotification(String userID){  // Takip edilen kişiye bildirim yazılıyor
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notification").child(userID);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("userID",firebaseUser.getUid());
        hashMap.put("text","artık seni takip ediyor");
        hashMap.put("postID","");
        hashMap.put("isPost",false);
        databaseReference.push().setValue(hashMap);
    }
    private void setSenderPostNotification(String userID){  // Takip eden kişiye bildirim yazılıyor
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notification").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("userID",userID);
        hashMap.put("text"," takip etmeye başladın");
        hashMap.put("postID","");
        hashMap.put("isPost",false);
        databaseReference.push().setValue(hashMap);
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView UserRowUserNameLastNameTextView,UserRowUserNameTextView;
        ImageView UserRowCircleIV;
        Button FollowButton;
        RelativeLayout RowUserRelativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            UserRowUserNameLastNameTextView = itemView.findViewById(R.id.UserRowUserNameLastNameTextView);
            UserRowUserNameTextView = itemView.findViewById(R.id.UserRowUserNameTextView);
            UserRowCircleIV = itemView.findViewById(R.id.UserRowCircleIV);
            FollowButton = itemView.findViewById(R.id.FollowButton);
            RowUserRelativeLayout = itemView.findViewById(R.id.RowUserRelativeLayout);
        }
    }

    private void isTrueFollowingOrFalseFollowing (final String userID, final Button button){
        FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("Followed").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(userID).exists())
                            button.setText("Takip Ediliyor");
                        else
                            button.setText("Takip Et");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}

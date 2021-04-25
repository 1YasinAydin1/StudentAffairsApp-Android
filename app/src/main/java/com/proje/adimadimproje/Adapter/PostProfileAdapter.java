package com.proje.adimadimproje.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.proje.adimadimproje.Activity.CommentActivity;
import com.proje.adimadimproje.Activity.ListActivity;
import com.proje.adimadimproje.Activity.MainActivity;
import com.proje.adimadimproje.Activity.PostDetailActivity;
import com.proje.adimadimproje.Activity.SignInActivity;
import com.proje.adimadimproje.Model.PostProfile;
import com.proje.adimadimproje.Model.User;
import com.proje.adimadimproje.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

import de.hdodenhof.circleimageview.CircleImageView;
import in.goodiebag.carouselpicker.CarouselPicker;

public class PostProfileAdapter extends RecyclerView.Adapter<PostProfileAdapter.ViewHolder> {

    Context context;
    List<PostProfile> postPs;
    FirebaseUser firebaseUser;
    int current_position = 0;
    List<ImageView> imageViews;
    List<Integer> imagePosition;
    boolean isTrue;

    public PostProfileAdapter(Context context, List<PostProfile> postPs,boolean isTrue) {
        this.context = context;
        this.postPs = postPs;
        this.isTrue = isTrue;

        imageViews = new ArrayList<>();
        imagePosition = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_post_profile,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final PostProfile post = postPs.get(position);
        holder.PostDateTextView.setText(post.getPostPDate());
        holder.PostTimeTextView.setText(post.getPostPTime());
        holder.PostTitleTextView.setText(post.getPostPTitle());
        String[] imageUrls = post.getImage();
        PostPagerAdapter adaptor = new PostPagerAdapter(context,imageUrls,post.getPostPID(),"Profile");
        holder.PostViewPager.setAdapter(adaptor);

     /*   Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (current_position == imageUrls.length)
                    current_position=0;
                holder.PostViewPager.setCurrentItem(current_position++,true);
            }
        };
        if (current_position == imageUrls.length)
            current_position=0;
        holder.PostViewPager.setCurrentItem(current_position++,true);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                holder.PostViewPager.post(runnable);
            }
        },250,2500);

            imageViews.clear();
            imagePosition.clear();
            if (holder.PostViewPagerPosition.getChildCount()>0)
                holder.PostViewPagerPosition.removeAllViews();
            for (int i = 0;i<imageUrls.length;i++){
                imagePosition.add(i);
                imageViews.add(i,new ImageView(context));
                holder.PostViewPagerPosition.addView(imageViews.get(i));}
            for (int i = 0;i<imageUrls.length;i++){
                if (imagePosition.get(i)==current_position)
                    imageViews.get(i).setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_addpost));
                else
                    imageViews.get(i).setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_back));
            }
*/


        PostUserInfo(holder.PostProfileCircleIV,holder.PostUserNameTextView,holder.PostUserNameTextView2,post.getUserID());
        GetLikes(post.getPostPID(),holder.PostLike,holder.PostLikeTextView);
        GetFavorites(post.getPostPID(),holder.PostFavorite);
        GetComment(post.getPostPID(),holder.PostCommentaryTextView);
        holder.PostProfileCircleIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("profileID",post.getUserID());
                intent.putExtra("PostSales","PostSales1");
                intent.putExtra("location","location1");
                context.startActivity(intent);
            }
        });
        holder.PostUserNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("profileID",post.getUserID());
                intent.putExtra("PostSales","PostSales1");
                intent.putExtra("location","location1");
                context.startActivity(intent);
            }
        });
        holder.PostUserNameTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("profileID",post.getUserID());
                intent.putExtra("PostSales","PostSales1");
                intent.putExtra("location","location1");
                context.startActivity(intent);
            }
        });


        holder.PostLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.PostLike.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostPID()).child(firebaseUser.getUid()).setValue(true);
                    if (!post.getUserID().equals(firebaseUser.getUid()))
                        setPostNotification(post.getUserID(),post.getPostPID());}
                else
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostPID()).child(firebaseUser.getUid()).removeValue();
            }
        });
        holder.PostFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.PostFavorite.getTag().equals("favorite"))
                    FirebaseDatabase.getInstance().getReference().child("ProfileFavorites").child(firebaseUser.getUid()).child(post.getPostPID()).setValue(true);
                else
                    FirebaseDatabase.getInstance().getReference().child("ProfileFavorites").child(firebaseUser.getUid()).child(post.getPostPID()).removeValue();
            }
        });
        holder.PostCommentary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("PostID",post.getPostPID());
                intent.putExtra("Post","Profile");
                intent.putExtra("PostuserID",post.getUserID());
                context.startActivity(intent);
            }
        });
        holder.PostCommentaryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("PostID",post.getPostPID());
                intent.putExtra("Post","Profile");
                intent.putExtra("PostuserID",post.getUserID());
                context.startActivity(intent);
            }
        });
        holder.PostLikeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra("ID",post.getPostPID());
                intent.putExtra("title","Likes");
                context.startActivity(intent);
            }
        });

        if (!firebaseUser.getUid().equals(post.getUserID()))
            holder.PostProfileMenuImageView.setVisibility(View.GONE);
        holder.PostProfileMenuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context,holder.PostProfileMenuImageView);
                popupMenu.inflate(R.menu.profile_post_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.profile_post_edit:
                                Toast.makeText(context, "sil", Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                LinearLayout linearLayout = new LinearLayout(context);
                                linearLayout.setOrientation(LinearLayout.VERTICAL);
                                linearLayout.setPadding(50,50,50,50);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(0,20,0,20);
                                EditText editTextTitle = new EditText(context);
                                editTextTitle.setBackground(ContextCompat.getDrawable(context,R.drawable.custom_input2));
                                editTextTitle.setPadding(50,10,0,10);
                                editTextTitle.setHintTextColor(Color.parseColor("#4a4b46"));
                                editTextTitle.setLayoutParams(params);
                                editTextTitle.setHint("Gönderi Başlığı");
                                linearLayout.addView(editTextTitle);
                                builder.setView(linearLayout);
                                builder.setMessage("Gönderi Başlık Düzenleme") .setTitle("Gönderi Düzenleme")
                                        .setCancelable(false)
                                        .setPositiveButton("Onayla", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                DatabaseReference PostUpdatePath = FirebaseDatabase.getInstance().getReference("PostProfile").child(post.getPostPID());

                                                String Title = editTextTitle.getText().toString().trim();

                                                if (Title.equals("")){
                                                    Toast.makeText(context, "Boş bırakılamaz", Toast.LENGTH_SHORT).show();
                                                    dialog.cancel();
                                                }
                                                else{
                                                    HashMap<String,Object> PostUpdateValue = new HashMap<>();
                                                    PostUpdateValue.put("PostPTitle",Title);
                                                    PostUpdatePath.updateChildren(PostUpdateValue);
                                                }
                                            }
                                        })
                                        .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                                break;
                            case R.id.profile_post_delete:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                builder1.setMessage("Gönderiyi silmek istediğinize emin misiniz?") .setTitle("Gönderi Silme")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference("PostProfile").child(post.getPostPID());
                                                databaseReference.removeValue();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert1 = builder1.create();
                                alert1.show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postPs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        CircleImageView PostProfileCircleIV;
        TextView PostUserNameTextView,PostPriceTextView,PostLikeTextView,PostUserNameTextView2,
                PostTitleTextView,PostCommentTextView,PostCommentaryTextView,PostDateTextView,PostTimeTextView;
        ViewPager PostViewPager;
        ImageView PostLike,PostCommentary,PostFavorite,PostProfileMenuImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            PostProfileCircleIV = itemView.findViewById(R.id.PostProfileCircleIV);


            PostUserNameTextView = itemView.findViewById(R.id.PostUserNameTextView);
            PostPriceTextView = itemView.findViewById(R.id.PostPriceTextView);
            PostLikeTextView = itemView.findViewById(R.id.PostLikeTextView);
            PostUserNameTextView2 = itemView.findViewById(R.id.PostUserNameTextView2);
            PostTitleTextView = itemView.findViewById(R.id.PostTitleTextView);
            PostCommentaryTextView = itemView.findViewById(R.id.PostCommentaryTextView);
            PostDateTextView = itemView.findViewById(R.id.PostDateTextView);
            PostTimeTextView = itemView.findViewById(R.id.PostTimeTextView);


            PostViewPager = itemView.findViewById(R.id.PostViewPager);

            PostLike = itemView.findViewById(R.id.PostLike);
            PostCommentary = itemView.findViewById(R.id.PostCommentary);
            PostFavorite = itemView.findViewById(R.id.PostFavorite);
            PostProfileMenuImageView = itemView.findViewById(R.id.PostProfileMenuImageView);

        }

    }
    private void SlidePosition(){


    }
    private void GetComment(String PostID,final TextView PostCommentaryTextView){

        DatabaseReference comment = FirebaseDatabase.getInstance().getReference()
                .child("Comment").child(PostID);
        comment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostCommentaryTextView.setText(snapshot.getChildrenCount()+" yorumun tümünü gör");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetLikes(String PostID, final ImageView imageView, final TextView PostLikeTextView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference like = FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(PostID);
        like.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_profile_post_liked);
                    imageView.setTag("liked");

                }else{
                    imageView.setImageResource(R.drawable.ic_profile_post_like);
                    imageView.setTag("like");
                }
                PostLikeTextView.setText(snapshot.getChildrenCount()+" kişi beğendi");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetFavorites(String PostID, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference favorite = FirebaseDatabase.getInstance().getReference()
                .child("ProfileFavorites").child(firebaseUser.getUid());
        favorite.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(PostID).exists()){
                    imageView.setImageResource(R.drawable.ic_profile_post_favorited);
                    imageView.setTag("favorited");

                }else{
                    imageView.setImageResource(R.drawable.ic_profile_post_favorite);
                    imageView.setTag("favorite");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void PostUserInfo(final CircleImageView circleImageView, final TextView UserNameTextView, final TextView UserName2TextView, String userID){
        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference("UserInfo").child(userID);
        userInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getUserImage()).fit().centerCrop().into(circleImageView);
                UserNameTextView.setText(user.getUserName());
                UserName2TextView.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setPostNotification(String userID,String postID){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notification").child(userID);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("userID",firebaseUser.getUid());
        hashMap.put("text"," gönderini beğendi");
        hashMap.put("PostName","Profile");
        hashMap.put("postID",postID);
        hashMap.put("isPost",true);

        databaseReference.push().setValue(hashMap);
    }
}


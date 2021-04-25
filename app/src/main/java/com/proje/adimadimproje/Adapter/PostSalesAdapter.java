package com.proje.adimadimproje.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proje.adimadimproje.Activity.ChatActivity;
import com.proje.adimadimproje.Activity.CommentActivity;
import com.proje.adimadimproje.Activity.MainActivity;
import com.proje.adimadimproje.Model.PostSales;
import com.proje.adimadimproje.Model.User;
import com.proje.adimadimproje.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostSalesAdapter extends RecyclerView.Adapter<PostSalesAdapter.ViewHolder> {

    Context context;
    List<PostSales> sales;
    FirebaseUser firebaseUser;
    boolean isTrue;
    public PostSalesAdapter(Context context, List<PostSales> sales,boolean isTrue) {
        this.context = context;
        this.sales = sales;
        this.isTrue = isTrue;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_post_sales,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        PostSales postSales = sales.get(position);

        String[] imageUrls = postSales.getImage();
        PostPagerAdapter adaptor = new PostPagerAdapter(context,imageUrls,postSales.getPostSID(),"Sales");
        holder.PostSalesViewPager.setAdapter(adaptor);

        holder.PostSalesTitleTextView.setText(postSales.getPostSTitle());
        holder.PostSalesCommentTextView.setText(postSales.getPostSComment());
        holder.PostSalesPriceTextView.setText(postSales.getPostSPrice()+" TL");
        holder.PostCCName1.setText(postSales.getPostSCCName1());
        holder.PostCCName2.setText(postSales.getPostSCCName2());
        holder.PostCCName3.setText(postSales.getPostSCCName3());
        holder.PostCCReturnName1.setText(postSales.getPostSTag1());
        holder.PostCCReturnName2.setText(postSales.getPostSTag2());
        holder.PostCCReturnName3.setText(postSales.getPostSTag3());
        holder.PostSaleCategoryTextView.setText("Kategori : "+postSales.getPostSCategory());
        holder.PostSalesDateTextView.setText(postSales.getPostSDate());
        holder.PostSalesTimeTextView.setText(postSales.getTime());

        PostUserInfo(holder.PostSalesUserCircleIV,holder.PostSalesUserNameTextView,postSales.getUserID());
        GetLikes(postSales.getPostSID(),holder.PostSalesLikeImageView);
        GetFavorites(postSales.getPostSID(),holder.PostSalesFavoriteImageView);


        holder.PostSalesUserCircleIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("profileID",postSales.getUserID());
                intent.putExtra("postID","postID");
                intent.putExtra("location","location1");
                context.startActivity(intent);
            }
        });
        holder.PostSalesLikeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.PostSalesLikeImageView.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postSales.getPostSID()).child(firebaseUser.getUid()).setValue(true);
                    if (!postSales.getUserID().equals(firebaseUser.getUid()))
                        setPostNotification(postSales.getUserID(),postSales.getPostSID());}
                else
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postSales.getPostSID()).child(firebaseUser.getUid()).removeValue();
            }
        });
        holder.PostSalesFavoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.PostSalesFavoriteImageView.getTag().equals("favorite"))
                    FirebaseDatabase.getInstance().getReference().child("SalesFavorites").child(firebaseUser.getUid()).child(postSales.getPostSID()).setValue(true);
                else
                    FirebaseDatabase.getInstance().getReference().child("SalesFavorites").child(firebaseUser.getUid()).child(postSales.getPostSID()).removeValue();
            }
        });
        holder.PostSalesCommentaryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("PostID",postSales.getPostSID());
                intent.putExtra("Post","Sales");
                intent.putExtra("PostuserID",postSales.getUserID());
                context.startActivity(intent);
            }
        });
        if (firebaseUser.getUid().equals(postSales.getUserID()))
            holder.PostSalesGoToMessage.setVisibility(View.GONE);
        holder.PostSalesGoToMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("ChatProfileID",postSales.getUserID());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView PostSalesTitleTextView,PostSalesCommentTextView,PostSalesPriceTextView,
                PostCCName1,PostCCReturnName1,PostCCName2,PostCCReturnName2,
                PostCCName3,PostCCReturnName3,PostSaleCategoryTextView,
                PostSalesDateTextView,PostSalesTimeTextView,PostSalesUserNameTextView,PostSalesRandomTextView;
        CircleImageView PostSalesUserCircleIV;
        ViewPager PostSalesViewPager;
        RelativeLayout PostSalesGoToMessage;
        ImageView PostSalesLikeImageView,PostSalesCommentaryImageView,PostSalesFavoriteImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            PostSalesTitleTextView = itemView.findViewById(R.id.PostSalesTitleTextView);
            PostSalesCommentTextView = itemView.findViewById(R.id.PostSalesCommentTextView);
            PostSalesPriceTextView = itemView.findViewById(R.id.PostSalesPriceTextView);
            PostCCName1 = itemView.findViewById(R.id.PostCCName1);
            PostCCReturnName1 = itemView.findViewById(R.id.PostCCReturnName1);
            PostCCName2 = itemView.findViewById(R.id.PostCCName2);
            PostCCReturnName2 = itemView.findViewById(R.id.PostCCReturnName2);
            PostCCName3 = itemView.findViewById(R.id.PostCCName3);
            PostCCReturnName3 = itemView.findViewById(R.id.PostCCReturnName3);
            PostSaleCategoryTextView = itemView.findViewById(R.id.PostSaleCategoryTextView);
            PostSalesDateTextView = itemView.findViewById(R.id.PostSalesDateTextView);
            PostSalesTimeTextView = itemView.findViewById(R.id.PostSalesTimeTextView);
            PostSalesUserNameTextView = itemView.findViewById(R.id.PostSalesUserNameTextView);
            PostSalesRandomTextView = itemView.findViewById(R.id.PostSalesRandomTextView);

            PostSalesUserCircleIV = itemView.findViewById(R.id.PostSalesUserCircleIV);

            PostSalesViewPager = itemView.findViewById(R.id.PostSalesViewPager);

            PostSalesLikeImageView = itemView.findViewById(R.id.PostSalesLikeImageView);
            PostSalesCommentaryImageView = itemView.findViewById(R.id.PostSalesCommentaryImageView);
            PostSalesFavoriteImageView = itemView.findViewById(R.id.PostSalesFavoriteImageView);

            PostSalesGoToMessage = itemView.findViewById(R.id.PostSalesGoToMessage);
        }
    }

    private void PostUserInfo(final CircleImageView circleImageView, final TextView UserNameTextView,String userID){
        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference("UserInfo").child(userID);
        userInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getUserImage()).fit().centerCrop().into(circleImageView);
                UserNameTextView.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void GetLikes(String PostID, final ImageView imageView){
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
                    imageView.setImageResource(R.drawable.ic_sales_post_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void GetFavorites(String PostID, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference favorite = FirebaseDatabase.getInstance().getReference()
                .child("SalesFavorites").child(firebaseUser.getUid());
        favorite.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(PostID).exists()){
                    imageView.setImageResource(R.drawable.ic_profile_post_favorited);
                    imageView.setTag("favorited");

                }else{
                    imageView.setImageResource(R.drawable.ic_sales_post_favorite);
                    imageView.setTag("favorite");

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setPostNotification(String userID,String postID){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notification").child(userID);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("userID",FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("text"," ilanını beğendi");
        hashMap.put("PostName","Sales");
        hashMap.put("postID",postID);
        hashMap.put("isPost",true);

        databaseReference.push().setValue(hashMap);
    }
}

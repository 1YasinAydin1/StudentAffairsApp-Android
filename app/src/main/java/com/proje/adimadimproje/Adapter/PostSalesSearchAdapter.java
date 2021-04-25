package com.proje.adimadimproje.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.proje.adimadimproje.Activity.PostDetailActivity;
import com.proje.adimadimproje.Model.PostSales;
import com.proje.adimadimproje.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class PostSalesSearchAdapter extends RecyclerView.Adapter<PostSalesSearchAdapter.ViewHolder> {

    Context context;
    List<PostSales> sales;

    public PostSalesSearchAdapter(Context context, List<PostSales> sales) {
        this.context = context;
        this.sales = sales;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_search_post_sales,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostSales postSales = sales.get(position);
        Picasso.get().load(postSales.getImage()[0]).fit().centerInside().into(holder.PostSalesImageView);
        GetLikes(postSales.getPostSID(),holder.PostSalesLikeImageButton);
        holder.PostSalesLikeImageButton.setOnClickListener(new View.OnClickListener() { // Kullanıcının İlanların beğenilme durumları değiştiriliyor
            @Override
            public void onClick(View v) {
                if (holder.PostSalesLikeImageButton.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postSales.getPostSID()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                    setPostNotification(postSales.getUserID(),postSales.getPostSID());}
                else
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(postSales.getPostSID()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() { // İlan detay
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postID",postSales.getPostSID());
                intent.putExtra("post","Sales");
                context.startActivity(intent);
            }
        });
        holder.PostSalesTitleTextView.setText(postSales.getPostSTitle());
        holder.PostSalesPriceTextView.setText(postSales.getPostSPrice()+" ₺");
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView PostSalesImageView,PostSalesLikeImageButton;
        TextView PostSalesTitleTextView,PostSalesPriceTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            PostSalesImageView = itemView.findViewById(R.id.PostSalesImageView);
            PostSalesLikeImageButton = itemView.findViewById(R.id.PostSalesLikeImageButton);
            PostSalesTitleTextView = itemView.findViewById(R.id.PostSalesTitleTextView);
            PostSalesPriceTextView = itemView.findViewById(R.id.PostSalesPriceTextView);
        }
    }
    private void GetLikes(String PostID, final ImageView imageView){ // Kullanıcının İlanların beğenilme durumları
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setPostNotification(String userID,String postID){ // Veritabanına bildirim yazılıyor
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

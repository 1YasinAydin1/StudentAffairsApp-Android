package com.proje.adimadimproje.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.proje.adimadimproje.Activity.MainActivity;
import com.proje.adimadimproje.Activity.PostDetailActivity;
import com.proje.adimadimproje.Model.PostProfile;
import com.proje.adimadimproje.Model.PostSales;
import com.proje.adimadimproje.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RowProfilePostAdapter extends RecyclerView.Adapter<RowProfilePostAdapter.ViewHolder>{

    Context context;
    List<PostProfile> postP;
    List<PostSales> postS;

    public RowProfilePostAdapter(Context context, List<PostProfile> postP, List<PostSales> postS) {
        this.context = context;
        this.postP = postP;
        this.postS = postS;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_profile_post,parent,false);
        return new RowProfilePostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // try catch kullanım amacı gelen listelerden biri boş diğeri doludur
        // Aslında ayrı ayrı adaptörler yazılmalıdır fakat böyle yaparak kod sayısından tasarruf ediyoruz
        try {
            PostProfile post = postP.get(position);
            Picasso.get().load(post.getImage()[0])
                    .fit().centerCrop().into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postID",post.getPostPID());
                    intent.putExtra("post","Profile");
                    context.startActivity(intent);
                }
            });
        }catch (Exception e){}
        try {
           PostSales post = postS.get(position);
            Picasso.get().load(post.getImage()[0])
                    .fit().centerCrop().into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postID",post.getPostSID());
                    intent.putExtra("post","Sales");
                    context.startActivity(intent);
                }
            });
        }catch (Exception e){}

    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (postP.size()==0)
            size = postS.size();
        else
            size = postP.size();
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
       public ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.RowProfilePostImageView);
        }
    }
}

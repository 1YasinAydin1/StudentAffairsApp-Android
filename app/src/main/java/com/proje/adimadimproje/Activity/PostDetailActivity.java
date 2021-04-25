package com.proje.adimadimproje.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.proje.adimadimproje.Adapter.PostProfileAdapter;
import com.proje.adimadimproje.Adapter.PostSalesAdapter;
import com.proje.adimadimproje.Model.PostProfile;
import com.proje.adimadimproje.Model.PostSales;
import com.proje.adimadimproje.Model.User;
import com.proje.adimadimproje.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    RecyclerView PostDetailRecyclerView;
    PostProfileAdapter postProfileAdapter;
    PostSalesAdapter salesAdapter;
    List<PostProfile> postPs;
    List<PostSales> postSs;
    TextView PostDetailUserName,PostTitle;
    String postID,postUserID, postControl;
    boolean isTrue;
    Toolbar toolbar;
    Boolean postStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Intent intent = getIntent(); // İlgili bilgiler alınıyor
        postID = intent.getStringExtra("postID");
        postControl = intent.getStringExtra("post");


        toolbar = findViewById(R.id.PostDetailToolBar);
        setSupportActionBar(toolbar); // toolbar değişkeni aktivitenin toolbar'ı olarak ayarlanıyor
        ImageView PostDetailEditClosed = findViewById(R.id.PostDetailEditClosed);
        PostDetailEditClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = PostDetailActivity.this.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putBoolean("forPostDetailOnClick",false);
                editor.apply();finish();
            }
        });

        PostDetailUserName = findViewById(R.id.PostDetailUserName);
        PostTitle = findViewById(R.id.PostTitle);

        PostDetailRecyclerView = findViewById(R.id.PostDetailRecyclerView);
        PostDetailRecyclerView.setHasFixedSize(true);
        PostDetailRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        if (postControl.equals("Profile")){
            postPs = new ArrayList<>();
            postProfileAdapter = new PostProfileAdapter(this,postPs,isTrue);
            PostDetailRecyclerView.setAdapter(postProfileAdapter);
            PostTitle.setText("Gönderiler");
            getPostProfile();
        }else{
            postSs = new ArrayList<>();
            salesAdapter = new PostSalesAdapter(this, postSs,isTrue);
            PostDetailRecyclerView.setAdapter(salesAdapter);
            PostTitle.setText("İlan");
            getPostSales();
        }

    }

    @Override
    public void onBackPressed() { // Gönderide yer alan ViewPager ' a basıldıkça yeniden açılmaması içindir
        SharedPreferences.Editor editor = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
        editor.putBoolean("forPostDetailOnClick",false);
        editor.apply();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // İlanlar için ayar menüsüdür

        if (postUserID.equals(FirebaseAuth.getInstance().getUid())&& postControl.equals("Sales")){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sales_post_menu,menu);}
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.sales_post_delete:
                PostDelete(postID);
                break;
            case R.id.sales_post_edit:
                PostEdit(postID);
                break;
            case R.id.sales_post_status:
                PostStatus(postID);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void PostStatus(String postID) { // İlgili ilanın durumunu gösterir/değiştirir

        String message;
        if (!postStatus)
            message = "İlanınız şu an \"Satılık\" durumundadır\n\"Satıldı\" durumuna güncelemek istiyor musunuz? ";
        else
            message = "İlanınız şu an \"Satıldı\" durumundadır\nTekrar \"Satılık\" durumuna güncelemek istiyor musunuz? ";
        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);
        builder.setMessage(message) .setTitle("İlan Durumu")
                .setCancelable(false)
                .setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DatabaseReference PostUpdatePath = FirebaseDatabase.getInstance().getReference("PostSales").child(postID);
                        if (!postStatus){
                            HashMap<String,Object> PostUpdateValue = new HashMap<>();
                            PostUpdateValue.put("PostSStatus",true);
                            PostUpdatePath.updateChildren(PostUpdateValue);
                        }else{
                            HashMap<String,Object> PostUpdateValue = new HashMap<>();
                            PostUpdateValue.put("PostSStatus",false);
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
    }

    private void PostDelete(String postID) { // İlan için silme seçeneğidir
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("İlanı silmek istediğinize emin misiniz?") .setTitle("İlan Silme")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference("PostSales").child(postID);
                        databaseReference.removeValue();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void PostEdit(String postID) { // İlan için düzenleme seçeneğidir
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(50,50,50,50);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,20,0,20);
        EditText editTextTitle = new EditText(this);
        EditText editTextComment = new EditText(this);
        EditText editTextPrice = new EditText(this);
        editTextTitle.setBackground(ContextCompat.getDrawable(this,R.drawable.custom_input1));
        editTextComment.setBackground(ContextCompat.getDrawable(this,R.drawable.custom_input1));
        editTextPrice.setBackground(ContextCompat.getDrawable(this,R.drawable.custom_input1));
        editTextTitle.setPadding(50,10,0,10);
        editTextComment.setPadding(50,10,0,10);
        editTextPrice.setPadding(50,10,0,10);
        editTextTitle.setHintTextColor(Color.parseColor("#9C3D3D"));
        editTextComment.setHintTextColor(Color.parseColor("#9C3D3D"));
        editTextPrice.setHintTextColor(Color.parseColor("#9C3D3D"));
        editTextTitle.setLayoutParams(params);
        editTextComment.setLayoutParams(params);
        editTextPrice.setLayoutParams(params);
        editTextTitle.setHint("İlan Başlığı");
        editTextComment.setHint("İlan Açıklaması");
        editTextPrice.setHint("İlan Fiyatı");
        editTextPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
        linearLayout.addView(editTextTitle);
        linearLayout.addView(editTextComment);
        linearLayout.addView(editTextPrice);
        builder.setView(linearLayout);
        builder.setMessage("İlan Bilgileri Güncelleme") .setTitle("İlan Güncelleme")
                .setCancelable(false)
                .setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        DatabaseReference PostUpdatePath = FirebaseDatabase.getInstance().getReference("PostSales").child(postID);

                        String Title = editTextTitle.getText().toString().trim();
                        String Comment = editTextComment.getText().toString().trim();
                        String Price = editTextPrice.getText().toString().trim();
                        if (Title.equals("")&&Comment.equals("")&&Price.equals("")){
                            Toast.makeText(PostDetailActivity.this, "Eksik bilgiler ile güncellenemez", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                        else{
                        HashMap<String,Object> PostUpdateValue = new HashMap<>();
                        if (!Title.equals(""))
                            PostUpdateValue.put("PostSTitle",Title);
                        if (!Comment.equals(""))
                            PostUpdateValue.put("PostSComment",Comment);
                        if (!Price.equals(""))
                            PostUpdateValue.put("PostSPrice",Price);
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
    }
    private void getPostProfile(){ // Gönderi ilan değil ise yani kullanıcının kişisel paylaşımı ise zamana göre tüm gönderilerini getirir
        final Query Posts = FirebaseDatabase.getInstance().getReference("PostProfile").orderByChild("time");
        Posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postPs.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    HashMap<String, Object> data = (HashMap<String, Object>) ds.getValue();
                    if (data.get("PostPID").equals(postID)){
                        postUserID = String.valueOf(data.get("userID"));
                        break;
                    }
                }
                for (DataSnapshot ds:snapshot.getChildren()){
                    HashMap<String,Object> data = (HashMap<String, Object>) ds.getValue();
                    if (data.get("userID").equals(postUserID)){
                        int imageSize =Integer.parseInt((String)data.get("imageSize"));
                        String[] image = new String[imageSize];
                        for (int i = 0 ; i < imageSize ; i++){
                            String imageName = "image"+(i+1);
                            String imageS = (String) data.get(imageName);
                            image[i] = imageS;
                        }
                        PostProfile post = new PostProfile(data.get("PostPID")+"",data.get("userID")+"",
                                data.get("PostPTitle")+"",  data.get("time")+"",
                                data.get("PostPDate")+"",""+data.get("PostPTime"),
                                data.get("imageSize")+"",image);
                        postPs.add(post);
                    }
                }
                Collections.reverse(postPs);

                for (int i = 0;i <postPs.size();i++)
                    if (postID.matches(postPs.get(i).getPostPID()))
                        PostDetailRecyclerView.scrollToPosition(i);
                postProfileAdapter.notifyDataSetChanged();
                PostUserInfo(postUserID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getPostSales(){ // Gönderi ilan ise sadece o ilan getirilir
        final Query Posts = FirebaseDatabase.getInstance().getReference("PostSales").orderByChild("PostSID").equalTo(postID);
        Posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postSs.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                    HashMap<String,Object> data = (HashMap<String, Object>) ds.getValue();

                    if (postID.equals(data.get("PostSID"))){
                        int imageSize =Integer.parseInt((String)data.get("imageSize"));
                        String[] image = new String[imageSize];
                        for (int i = 0 ; i < imageSize ; i++){
                            String imageName = "image"+(i+1);
                            String imageS = (String) data.get(imageName);
                            image[i] = imageS;
                        }
                        postUserID = String.valueOf(data.get("userID"));
                        postStatus = (Boolean) data.get("PostSStatus");
                        PostSales post = new PostSales(data.get("PostSID")+"",data.get("userID")+"",
                                data.get("PostSStatus")+"",data.get("PostSCategory")+"",  data.get("PostSTitle")+"",
                                data.get("PostSComment")+"",""+data.get("PostSPrice"),
                                data.get("PostSTag1")+"",data.get("PostSTag2")+"",data.get("PostSTag3")+"",
                                data.get("PostSCCName1")+"",data.get("PostSCCName2")+"",data.get("PostSCCName3")+"",
                                data.get("PostSTime")+"",data.get("PostSDate")+"",data.get("PostSTime")+"",
                                data.get("imageSize")+"",image);
                        postSs.add(post);
                    } }
                PostUserInfo(postUserID);
                salesAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void PostUserInfo(String userID){ // Gönderiyi atan kullanıcının bilgileri
        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference("UserInfo").child(userID);
        userInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                PostDetailUserName.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
package com.proje.adimadimproje.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.proje.adimadimproje.Adapter.PostPagerAdapter;
import com.proje.adimadimproje.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PostAddProfileActivity extends AppCompatActivity {

    StorageReference imageReference;
    List<Uri> imageUris;
    ArrayList<String> DownLoadImageUris;
    EditText PostTitleEditText;
    TextView PostAddTextView;
    ImageView ClosePostImageView;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_profile);

        PostTitleEditText = findViewById(R.id.PostTitleEditText);
        PostAddTextView = findViewById(R.id.PostAddTextView);
        ClosePostImageView = findViewById(R.id.ClosePostImageView);
        viewPager = findViewById(R.id.PostViewPager);

        selectedImage ();

        imageReference = FirebaseStorage.getInstance().getReference();
        ClosePostImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        PostAddTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostLoading();
            }
        });
    }

    private void selectedImage () // İzin alma işlemleri
    {
        // Burada izin daha önce alınmamışsa ya da reddedilmiş ise
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        // Burada izin daha önce verilmiş ise
        else {
            Intent intentToGallery = new Intent();
            intentToGallery.setType("image/*");
            intentToGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            intentToGallery.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intentToGallery,"Select Image"),0);
        }
    }

    // Burada kullanıcı izini verdikten sonra galerinin açılması için
    // Tekrardan basması gerekmemesi için
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==1) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intentToGallery = new Intent();
                intentToGallery.setType("image/*");
                intentToGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intentToGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentToGallery,"Select Image"),0);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Galeri ile işi bittikten sonra kontrol ediyoruz resim seçmiş mi yoksa seçmemiş mi?
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==0 && resultCode == RESULT_OK && data !=null)
            TransferFromDataToList(data);
        else
            finish();

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void TransferFromDataToList(Intent data) { // Gelen verileri işlenebilir hale getiriyoruz
        imageUris = new ArrayList<>();
        ClipData clipData = data.getClipData();
        if (clipData !=null){
            for (int i = 0 ; i < clipData.getItemCount() ; i++){
                Uri imageData = clipData.getItemAt(i).getUri();
                imageUris.add(imageData);
            }
        }else {
        Uri imageData = data.getData();
        imageUris.add(imageData);
        }
        String[] imageArray = new String[imageUris.size()];
        for (int i = 0 ; i < imageUris.size() ; i++){

            imageArray[i] = imageUris.get(i).toString();
        }
        PostPagerAdapter adaptor = new PostPagerAdapter(this,imageArray,"postID","Profile");
        viewPager.setAdapter(adaptor);
    }

    private void PostLoading() { // Gönderi veritabanında yükleniyor
        final ProgressDialog pg = new ProgressDialog(this);
        pg.setMessage("Gönderiliyor");
        pg.show();
        DownLoadImageUris = new ArrayList<>();
        final String PostTitle = PostTitleEditText.getText().toString().trim();
        if (imageUris !=null){
        for (Uri uri : imageUris){
            UUID uuid = UUID.randomUUID();
            final String imageName = "PostProfilImages/"+uuid+".jpg";
            imageReference.child(imageName).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    FirebaseStorage.getInstance().getReference(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onSuccess(Uri uri) {
                            DownLoadImageUris.add(uri.toString());
                            if (imageUris.size() == DownLoadImageUris.size()){
                                HashMap<String,Object> hashMap = new HashMap<>();
                                for (int i = 0 ; i < imageUris.size() ; i++){
                                    String imageURL = "image"+(i+1);
                                    hashMap.put(imageURL, DownLoadImageUris.get(i));
                                }
                                hashMap.put("imageSize",String.valueOf(imageUris.size()));
                                UUID uuid = UUID.randomUUID();
                                hashMap.put("PostPID",String.valueOf(uuid));
                                hashMap.put("PostPTitle",PostTitle);
                                hashMap.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                hashMap.put("time", ServerValue.TIMESTAMP);
                                String currentDay = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
                                String currentMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
                                switch (currentMonth){
                                    case "01": currentMonth = "Ocak";break;
                                    case "02": currentMonth = "Şubat";break;
                                    case "03": currentMonth = "Mart";break;
                                    case "04": currentMonth = "Nisan";break;
                                    case "05": currentMonth = "Mayıs";break;
                                    case "06": currentMonth = "Haziran";break;
                                    case "07": currentMonth = "Temmuz";break;
                                    case "08": currentMonth = "Ağustos";break;
                                    case "09": currentMonth = "Eylül";break;
                                    case "10": currentMonth = "Ekim";break;
                                    case "11": currentMonth = "Kasım";break;
                                    case "12": currentMonth = "Aralık";break;
                                }
                                String currentHour = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
                                currentHour = String.valueOf(Integer.parseInt(currentHour)+3);
                                if (Integer.parseInt(currentHour) >23){
                                    currentHour = "0"+String.valueOf(Integer.parseInt(currentHour)-24);
                                    currentDay = String.valueOf(Integer.parseInt(currentDay)+1);}
                                String currentMinute = new SimpleDateFormat("mm", Locale.getDefault()).format(new Date());
                                hashMap.put("PostPDate",currentDay+" "+currentMonth);
                                hashMap.put("PostPTime",currentHour+":"+currentMinute);

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("PostProfile").child(String.valueOf(uuid));
                                databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            pg.dismiss();
                                            Intent intent = new Intent(PostAddProfileActivity.this, MainActivity.class);
                                            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PostAddProfileActivity.this,"Gönderme Başarısız",Toast.LENGTH_LONG).show();
                                        pg.dismiss();
                                        Intent intent = new Intent(PostAddProfileActivity.this,MainActivity.class);
                                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });
                            }

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {pg.dismiss();
                    Toast.makeText(PostAddProfileActivity.this,"Hata", Toast.LENGTH_LONG).show();
                }
            });

        }
        }else{ pg.dismiss();Toast.makeText(PostAddProfileActivity.this,"Boş bırakılan Alanlar Mevcut",Toast.LENGTH_LONG).show();}

    }
}

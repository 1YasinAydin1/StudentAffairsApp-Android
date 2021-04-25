package com.proje.adimadimproje.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.proje.adimadimproje.Adapter.PostPagerAdapter;
import com.proje.adimadimproje.Adapter.SpinnerAdapter;
import com.proje.adimadimproje.Model.SpinnerModel;
import com.proje.adimadimproje.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PostSalesActivity extends AppCompatActivity {

    StorageReference imageReference;

    List<Uri> imageUris;
    ArrayList<String> DimageUris;
    String Category="",Tag1="",Tag2="",Tag3="";

    EditText PostTitleEditText,PostCommentEditText,PostPriceEditText;
    TextView PostAddTextView;
    ImageView ClosePostImageView;
    Spinner PostSpinner,PostSpinnerVol1,PostSpinnerVol2,PostSpinnerVol3;
    ViewPager viewPager;
    List<SpinnerModel> spinnerModels;
    SpinnerAdapter spinnerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_sales);

        PostTitleEditText = findViewById(R.id.PostTitleEditText);
        PostCommentEditText = findViewById(R.id.PostCommentEditText);
        PostPriceEditText = findViewById(R.id.PostPriceEditText);
        PostAddTextView = findViewById(R.id.PostAddTextView);

        ClosePostImageView = findViewById(R.id.ClosePostImageView);

        PostSpinner = findViewById(R.id.PostSpinner);
        spinnerModels = new ArrayList<>();
        // region SpinnerCategory
        spinnerModels.add(new SpinnerModel(R.drawable.ic_category,"Kategori"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_house,"Emlak"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_vehicle,"Araç"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_furniture,"Ev Eşyası ve Spot"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_electronic,"Elektronik"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_movie_music_book,"Film,Kitap ve Müzik"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_tools,"Ders araç, gereç ve ilgili"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_fashion,"Giyim ve Aksesuar"));
        spinnerModels.add(new SpinnerModel(R.drawable.ic_job,"İş İlanı"));
        spinnerAdapter = new SpinnerAdapter(this,spinnerModels);
        PostSpinner.setAdapter(spinnerAdapter);
        PostSpinnerVol1 = findViewById(R.id.PostSpinnerVol1);
        PostSpinnerVol2 = findViewById(R.id.PostSpinnerVol2);
        PostSpinnerVol3 = findViewById(R.id.PostSpinnerVol3);
        SpinnerText();
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

    private void SpinnerText() {
        PostSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerModel spinnerModel = (SpinnerModel) parent.getItemAtPosition(position);
                Category = spinnerModel.getTextView();
                PostSpinnerVol1.setVisibility(View.GONE);
                PostSpinnerVol2.setVisibility(View.GONE);
                PostSpinnerVol3.setVisibility(View.GONE);
                if (position!=0){
                    PostSpinnerVol1.setVisibility(View.VISIBLE);
                    PostSpinnerVol2.setVisibility(View.VISIBLE);
                    PostSpinnerVol3.setVisibility(View.VISIBLE);
                    ArrayAdapter<CharSequence> adapter1;
                    ArrayAdapter<CharSequence> adapter2;
                    ArrayAdapter<CharSequence> adapter3;
                    switch (position){
                        case 1:
                            adapter1 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.EmlakKimden, R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.EmlakDurum,R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.EmlakMetreKare, R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                        case 2:
                            adapter1 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.AracKimden, R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.AracDurum, R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.AracModel, R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                        case 3:
                            adapter1 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.EvEşyasıKimden, R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.EsyaDurum, R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.EsyaTur, R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                        case 4:
                            adapter1 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.ElektronikKimden,R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.ElektronikDurum,R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.ElektronikTur,R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                        case 5:
                            adapter1 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.EglenceKimden, R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.EglenceDurum, R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.EglenceTur,R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                        case 6:
                            adapter1 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.EgitimKimden, R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.EgitimDurum, R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.EgitimTur, R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                        case 7:
                            adapter1 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.GiyimKusamKimden, R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.GiyimKusamDurum, R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.GiyimKusamTur,R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                        case 8:
                            adapter1 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.IsIlanKimden, R.layout.row_spinner_dropdown);
                            adapter1.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol1.setAdapter(adapter1);

                            adapter2 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.IsIlanDurum,R.layout.row_spinner_dropdown);
                            adapter2.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol2.setAdapter(adapter2);

                            adapter3 = ArrayAdapter.createFromResource(PostSalesActivity.this, R.array.IsIlanKimdenTur, R.layout.row_spinner_dropdown);
                            adapter3.setDropDownViewResource(R.layout.row_spinner_textcolor);
                            PostSpinnerVol3.setAdapter(adapter3);
                            break;
                    }
                    PostSpinnerVol1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position!=0)
                                Tag1 = PostSpinnerVol1.getItemAtPosition(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    PostSpinnerVol2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position!=0)
                                Tag2 = PostSpinnerVol2.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    PostSpinnerVol3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position!=0)
                                Tag3 = PostSpinnerVol3.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }


    private void selectedImage ()
    {
        if (ContextCompat.checkSelfPermission(PostSalesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(PostSalesActivity.this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        else {
            Intent intentToGallery = new Intent();
            intentToGallery.setType("image/*");
            intentToGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            intentToGallery.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intentToGallery,"Select Image"),0);
        }
    }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==0 && resultCode == RESULT_OK && data !=null)
            TransferFromDataToList(data);
        else
            finish();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void TransferFromDataToList(Intent data) {
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
        PostPagerAdapter adaptor = new PostPagerAdapter(PostSalesActivity.this,imageArray,"postID","Sales");
        viewPager.setAdapter(adaptor);
    }

    private void PostLoading() {
        final ProgressDialog pg = new ProgressDialog(PostSalesActivity.this);
        pg.setMessage("Gönderiliyor");
        pg.show();
        DimageUris = new ArrayList<>();
        final String PostTitle = PostTitleEditText.getText().toString().trim();
        final String PostComment = PostCommentEditText.getText().toString().trim();
        final String PostPrice = PostPriceEditText.getText().toString().trim();
        if (!PostTitle.matches("") && !PostPrice.matches("")
                && !Category.equals("") && !Tag1.equals("") && !Tag2.equals("") && !Tag3.equals("")&& imageUris !=null){
            for (Uri uri : imageUris){
                UUID uuid = UUID.randomUUID();
                final String imageName = "PostSalesImages/"+uuid+".jpg";
                imageReference.child(imageName).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        FirebaseStorage.getInstance().getReference(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onSuccess(Uri uri) {

                                DimageUris.add(uri.toString());
                                if (imageUris.size() == DimageUris.size()){

                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    for (int i = 0 ; i < imageUris.size() ; i++){
                                        String imageURL = "image"+(i+1);
                                        hashMap.put(imageURL,DimageUris.get(i));
                                    }
                                    hashMap.put("imageSize",String.valueOf(imageUris.size()));
                                    UUID uuid = UUID.randomUUID();
                                    hashMap.put("PostSID",String.valueOf(uuid));
                                    hashMap.put("PostSStatus",false);
                                    hashMap.put("PostSTitle",PostTitle);
                                    hashMap.put("PostSComment",PostComment);
                                    hashMap.put("PostSPrice",Integer.parseInt(PostPrice));
                                    hashMap.put("PostSCCName1",PostSpinnerVol1.getItemAtPosition(0).toString());
                                    hashMap.put("PostSCCName2",PostSpinnerVol2.getItemAtPosition(0).toString());
                                    hashMap.put("PostSCCName3",PostSpinnerVol3.getItemAtPosition(0).toString());
                                    hashMap.put("PostSTag1",Tag1);
                                    hashMap.put("PostSTag2",Tag2);
                                    hashMap.put("PostSTag3",Tag3);
                                    hashMap.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap.put("PostSCategory",Category);
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
                                    hashMap.put("PostSDate",currentDay+" "+currentMonth);
                                    hashMap.put("PostSTime",currentHour+":"+currentMinute);

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("PostSales").child(String.valueOf(uuid));
                                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                pg.dismiss();
                                                Intent intent = new Intent(PostSalesActivity.this, MainActivity.class);
                                                intent.putExtra("PostSales","PostSales");
                                                intent.putExtra("location","PostSales");
                                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(PostSalesActivity.this,"Gönderme Başarısız",Toast.LENGTH_LONG).show();
                                            pg.dismiss();
                                            Intent intent = new Intent(PostSalesActivity.this,MainActivity.class);
                                            intent.putExtra("PostSales","PostSales");
                                            intent.putExtra("location","PostSales");
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
                        Toast.makeText(PostSalesActivity.this,"Hata",Toast.LENGTH_LONG).show();
                    }
                });

            }
        }else{ pg.dismiss();Toast.makeText(PostSalesActivity.this,"Boş bırakılan Alanlar Mevcut",Toast.LENGTH_LONG).show();}

    }
}
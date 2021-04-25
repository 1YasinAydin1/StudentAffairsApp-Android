package com.proje.adimadimproje.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.proje.adimadimproje.Model.User;
import com.proje.adimadimproje.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditActivity extends AppCompatActivity {

    CircleImageView ProfileEditCircleImageView;
    TextView ProfileImageEditTextView,ProfileEditEnter,ProfileBackGroundEditTextView;
    EditText ProfileEditNameLastName,ProfileEditUserName;
    ImageView ProfileEditClosed,ProfileEditBackGroundImageView;
    ImageButton ProfileEditClose;
    FirebaseUser firebaseUser;
    StorageTask storageTask;
    LinearLayout ProfileEditPasswordReset;
    StorageReference storageReference;
    String control;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        ProfileEditCircleImageView = findViewById(R.id.ProfileEditCircleImageView);
        ProfileEditClosed = findViewById(R.id.ProfileEditClosed);
        ProfileEditClose = findViewById(R.id.ProfileEditClose);
        ProfileEditPasswordReset = findViewById(R.id.ProfileEditPasswordReset);
        ProfileEditPasswordReset.setOnClickListener(new View.OnClickListener() { // Şifre değiştirme işlemi
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(firebaseUser.getEmail()).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(ProfileEditActivity.this, "Şifre aktivasyonu mail adresinize gönderilmiştir", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(ProfileEditActivity.this, "Hata", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        ProfileEditClose.setOnClickListener(new View.OnClickListener() { // Aktivite sonlandırılır
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(ProfileEditActivity.this,SignInActivity.class));
            }
        });

        ProfileImageEditTextView = findViewById(R.id.ProfileImageEditTextView);
        ProfileEditBackGroundImageView = findViewById(R.id.ProfileEditBackGroundImageView);
        ProfileBackGroundEditTextView = findViewById(R.id.ProfileBackGroundEditTextView);
        ProfileEditEnter = findViewById(R.id.ProfileEditEnter);

        ProfileEditNameLastName = findViewById(R.id.ProfileEditNameLastName);
        ProfileEditUserName = findViewById(R.id.ProfileEditUserName);

        storageReference = FirebaseStorage.getInstance().getReference("ProfileImage");

        // Kullanıcı bilgileri alınıyor
        DatabaseReference userPath = FirebaseDatabase.getInstance().getReference("UserInfo").child(firebaseUser.getUid());
        userPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                ProfileEditNameLastName.setText(user.getUserNameLastName());
                ProfileEditUserName.setText(user.getUserName());
                Picasso.get().load(user.getUserImage()).fit().centerCrop().into(ProfileEditCircleImageView);
                Picasso.get().load(user.getUserBackgroundImage()).fit().centerCrop().into(ProfileEditBackGroundImageView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ProfileEditClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ProfileImageEditTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Profil resmi değiştirmek için galeriye yönlendiriliyor
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(ProfileEditActivity.this);
                        control = "ProfileImage";
            }
        });
        ProfileBackGroundEditTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Profil arka plan resmini değiştirmek için galeriye yönlendiriliyor
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .start(ProfileEditActivity.this);
                        control = "ProfileBackGround";
            }
        });
        ProfileEditEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileEdit(ProfileEditNameLastName.getText().toString(),ProfileEditUserName.getText().toString());
                finish();
            }
        });
    }


    private void ProfileEdit(String ProfileEditNameLastName, String ProfileEditUserName) { // Kullanıcı bilgileri güncelleme işlemi
        if (ProfileEditNameLastName.equals("")||ProfileEditUserName.equals(""))
            Toast.makeText(this, "Kullanıcı bilgileri boş bırakılamaz", Toast.LENGTH_SHORT).show();
        else {
        DatabaseReference updatePath = FirebaseDatabase.getInstance().getReference("UserInfo").child(firebaseUser.getUid());

        HashMap<String,Object> updateHashMap = new HashMap<>();
        updateHashMap.put("userNameLastName",ProfileEditNameLastName);
        updateHashMap.put("userName",ProfileEditUserName);

        updatePath.updateChildren(updateHashMap);
    }}

    private void ImageLoading(){
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Yükleniyor");
        pd.show();

        if (uri !=null){
            UUID uuid = UUID.randomUUID();
            StorageReference imagePath = storageReference.child(uuid+".jpg");
            storageTask = imagePath.putFile(uri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    return imagePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        String uri = task.getResult().toString();


                        DatabaseReference userPath = FirebaseDatabase.getInstance().getReference("UserInfo").child(firebaseUser.getUid());

                        HashMap<String,Object> resimHashMap = new HashMap<>();
                        if (control.equals("ProfileImage"))
                            resimHashMap.put("userImage",uri);
                        else
                            resimHashMap.put("userBackgroundImage",uri);
                        userPath.updateChildren(resimHashMap);
                        pd.dismiss();
                    }
                    else
                        Toast.makeText(ProfileEditActivity.this, "Yükleme Başarısız", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileEditActivity.this, "Yükleme Başarısız", Toast.LENGTH_SHORT).show();
                }
            });
        }else
            Toast.makeText(ProfileEditActivity.this, "Yükleme Başarısız", Toast.LENGTH_SHORT).show();
    }

    // Galeriden resim seçildikten sonra oval kırpma işlemi uygulanıyor
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            uri = result.getUri();
            ImageLoading();
        }else
            Toast.makeText(ProfileEditActivity.this, "Başarısız", Toast.LENGTH_SHORT).show();
    }
}
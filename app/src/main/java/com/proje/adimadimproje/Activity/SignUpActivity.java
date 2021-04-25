package com.proje.adimadimproje.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proje.adimadimproje.R;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    EditText SignUpNameLastNameEditText,SignUpUserNameEditText,SignUpEmailEditText,SignUpUserPasswordEditText;
    Button SignUpButton;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        SignUpNameLastNameEditText = findViewById(R.id.SignUpNameLastNameEditText);
        SignUpUserNameEditText = findViewById(R.id.SignUpUserNameEditText);
        SignUpEmailEditText = findViewById(R.id.SignUpEmailEditText);
        SignUpUserPasswordEditText = findViewById(R.id.SignUpUserPasswordEditText);
        SignUpButton = findViewById(R.id.SignUpButton);

    }

    public void SignUptoMainClicked(View view){ // Kullanıcı oluşturuluyor ve ana sayfaya yönlendiriliyor
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Oluşturuluyor ...");

        final String nameLastName = SignUpNameLastNameEditText.getText().toString().trim();
        final String userName = SignUpUserNameEditText.getText().toString().trim();
        final String email =SignUpEmailEditText.getText().toString().trim();
        final String password = SignUpUserPasswordEditText.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            SignUpEmailEditText.setError("Geçersiz Email");
            SignUpEmailEditText.setFocusable(true);
        }if(password.length()<6 ){
            SignUpUserPasswordEditText.setError("En az 6 karakter olmalıdır");
            SignUpUserPasswordEditText.setFocusable(true);
        }else{  progressBar.show();



            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    String userID = firebaseAuth.getCurrentUser().getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(userID);

                    HashMap<String,Object> hashMap = new HashMap<>();


                    hashMap.put("ID",userID);
                    hashMap.put("userNameLastName",nameLastName);
                    hashMap.put("userName",userName);
                    hashMap.put("userEmail",email);
                    // Burada veritabanına bizim elle yüklediğimiz default resimler oluşturulan kullanıcıya atanır
                    // Dileğinde profil sayfasından değiştirebilir
                    hashMap.put("userImage","https://firebasestorage.googleapis.com/v0/b/adimadimproje-7b4ab.appspot.com/o/placeholder-person.jpg?alt=media&token=5a2438e5-5aad-4c98-ac17-302814c030b0");
                    hashMap.put("userBackgroundImage","https://firebasestorage.googleapis.com/v0/b/adimadimproje-7b4ab.appspot.com/o/1155011.jpg?alt=media&token=fecb003f-55ca-4d70-9467-b470185996d5");

                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.dismiss();
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this,"Hata",Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.dismiss();
                    Toast.makeText(SignUpActivity.this,"Hatalı bilgi girişi",Toast.LENGTH_LONG).show();
                }
            });

        }
    }
}
package com.proje.adimadimproje.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proje.adimadimproje.R;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    EditText SignInEmailEditText,SignInUserPasswordEditText;
    Button SignInButton,SignIntoSignUpButton;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    TextView SignInPasswordReset;
    ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();

        SignInEmailEditText = findViewById(R.id.SignInEmailEditText);
        SignInUserPasswordEditText = findViewById(R.id.SignInUserPasswordEditText);
        SignInButton = findViewById(R.id.SignInButton);
        SignIntoSignUpButton = findViewById(R.id.SignIntoSignUpButton);
        SignInPasswordReset = findViewById(R.id.SignInPasswordReset);
        SignInPasswordReset.setOnClickListener(new View.OnClickListener() { // Şifre güncelleme işlemi
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                LinearLayout linearLayout = new LinearLayout(SignInActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(50,50,50,50);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,20,0,20);
                EditText editTextEmail = new EditText(SignInActivity.this);
                editTextEmail.setBackground(ContextCompat.getDrawable(SignInActivity.this,R.drawable.custom_input2));
                editTextEmail.setPadding(50,10,0,10);
                editTextEmail.setHintTextColor(Color.parseColor("#9C3D3D"));
                editTextEmail.setLayoutParams(params);
                editTextEmail.setHint("Email");
                linearLayout.addView(editTextEmail);
                builder.setView(linearLayout);
                builder.setMessage("İlan Bilgileri Güncelleme") .setTitle("İlan Güncelleme")
                        .setCancelable(false)
                        .setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String Email = editTextEmail.getText().toString().trim();
                                if (Email.equals("")){
                                    Toast.makeText(SignInActivity.this, "Boş Bırakılamaz", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                                else{
                                    FirebaseAuth.getInstance().sendPasswordResetEmail(Email).
                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                        Toast.makeText(SignInActivity.this, "Şifre aktivasyonu mail adresinize gönderilmiştir", Toast.LENGTH_SHORT).show();
                                                    else
                                                        Toast.makeText(SignInActivity.this, "Hata", Toast.LENGTH_SHORT).show();
                                                }
                                            });
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
        })
        userControl();
    }
    private void userControl() { // Kullanıcı oturumu var mı yok mu kontrol ediyoruz
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser !=null){
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void SignInButtonClicked(View view){
        String email = SignInEmailEditText.getText().toString().trim();
        String password = SignInUserPasswordEditText.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            SignInEmailEditText.setError("Geçersiz Email");
            SignInEmailEditText.setFocusable(true);
        }if(password.length()<6){
            SignInUserPasswordEditText.setError("Şifre en az 6 karakter olmalıdır");
            SignInUserPasswordEditText.setFocusable(true);
        }else{ progressBar = new ProgressDialog(this);
            progressBar.setMessage("Giriş Yapılıyor ..."); progressBar.show();
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(firebaseAuth.getCurrentUser().getUid());
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                progressBar.dismiss();
                                Intent intent = new Intent(SignInActivity.this,MainActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressBar.dismiss();
                                Toast.makeText(SignInActivity.this,"Hatalı bilgi girişi",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.dismiss();
                    Toast.makeText(SignInActivity.this,"Hatalı bilgi girişi",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public void SignIntoSignUpButtonClicked(View view){
        startActivity(new Intent(this, SignUpActivity.class));
    }
}
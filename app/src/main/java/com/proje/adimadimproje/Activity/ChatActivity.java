package com.proje.adimadimproje.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.proje.adimadimproje.Adapter.ChatAdapter;
import com.proje.adimadimproje.Model.Chat;
import com.proje.adimadimproje.Model.User;
import com.proje.adimadimproje.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    EditText ChatMessageEditText;
    CircleImageView ChatProfileUserCircleImageView,ChatProfileUserCircleImageView2;
    ImageButton ChatSendImageButton;
    TextView ChatUserName,ChatUserName2;
    FirebaseUser firebaseUser;
    String chatProfileID;
    String date,time,forRowChatUserName,forRowChatCircleIV;
    List<Chat> chats;
    List<String> chatKeys;
    ChatAdapter chatAdapter;
    RecyclerView ChatRecyclerView;
    LinearLayout LinearLayoutReceiverInfo;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        toolbar = findViewById(R.id.ChatToolBar);
        setSupportActionBar(toolbar); // toolbar değişkeni aktivitenin toolbar'ı olarak ayarlanıyor

        Bundle intent = getIntent().getExtras(); // Alıcının ID numarısı alınıyor
        if (!intent.getString("ChatProfileID").equals("ChatProfileID")){
            chatProfileID = intent.getString("ChatProfileID");
        }

        ChatMessageEditText = findViewById(R.id.ChatMessageEditText);
        LinearLayoutReceiverInfo = findViewById(R.id.LinearLayoutReceiverInfo);
        ChatProfileUserCircleImageView = findViewById(R.id.ChatProfileUserCircleImageView);
        ChatProfileUserCircleImageView2 = findViewById(R.id.ChatProfileUserCircleImageView2);
        ChatSendImageButton = findViewById(R.id.ChatSendImageButton);

        chats = new ArrayList<>();
        chatKeys = new ArrayList<>();
        ChatUserName = findViewById(R.id.chatProfileUserName);
        ChatUserName2 = findViewById(R.id.chatProfileUserName2);
        ChatRecyclerView = findViewById(R.id.ChatRecyclerView);
        userInfo();
        chatAdapter = new ChatAdapter(this,chats,chatKeys,forRowChatUserName,forRowChatCircleIV);
        ChatRecyclerView.setHasFixedSize(true);
        ChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ChatRecyclerView.setAdapter(chatAdapter);

        ChatSendImageButton.setOnClickListener(new View.OnClickListener() { // Mesaj gönder butonuna basıldığında veritabanına yazılması
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                if (!ChatMessageEditText.getText().equals("")){ // Boş mesajı gönderemiyor

                    String message = ChatMessageEditText.getText().toString();
                    String senderPath = "Chats/"+firebaseUser.getUid()+"/"+chatProfileID;
                    String receiverPath = "Chats/"+chatProfileID+"/"+firebaseUser.getUid();
                    DatabaseReference userMessagePath = FirebaseDatabase.getInstance().getReference() // Yol Oluşturuluyor
                            .child("Chats").child(firebaseUser.getUid()).child(chatProfileID).push();

                    String messageKey = userMessagePath.getKey();


                    // Mesaj tarih ve saat bilgileri oluşturuluyor
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    date = dateFormat.format(calendar.getTime());
                    String currentHour = new android.icu.text.SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
                    currentHour = String.valueOf(Integer.parseInt(currentHour)+3);
                    String currentMinute = new android.icu.text.SimpleDateFormat("mm", Locale.getDefault()).format(new Date());
                    time = currentHour+":"+currentMinute;

                    HashMap messageMap = new HashMap<>();
                    messageMap.put("senderUserID",firebaseUser.getUid());
                    messageMap.put("receiverUserID",chatProfileID);
                    messageMap.put("message",message);
                    messageMap.put("date",date);
                    messageMap.put("time",time);

                    Map messageDetailMap = new HashMap<>();
                    messageDetailMap.put(senderPath+"/"+messageKey,messageMap);
                    messageDetailMap.put(receiverPath+"/"+messageKey,messageMap);

                    // Mesaj hem Alıcı hem de Gönderici yollarına ekleniyor
                    FirebaseDatabase.getInstance().getReference().updateChildren(messageDetailMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful())
                                Toast.makeText(ChatActivity.this, "Gönderildi", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(ChatActivity.this, "Gönderilemedi", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ChatMessageEditText.setText("");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // İlanlar için ayar menüsüdür
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.chat_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.chat_me_delete:
                    FirebaseDatabase.getInstance().getReference()
                            .child("Chats").child(firebaseUser.getUid())
                            .child(chatProfileID).removeValue();
                    finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Mesaj gönderildikçe, alındıkça veya silindikçe adaptöre yansıtılıyor
        FirebaseDatabase.getInstance().getReference().child("Chats").child(firebaseUser.getUid()).child(chatProfileID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Chat chat = snapshot.getValue(Chat.class);
                chatKeys.add(snapshot.getKey());
                chats.add(chat);
                chatAdapter.notifyDataSetChanged();
                ChatRecyclerView.smoothScrollToPosition(ChatRecyclerView.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Chat chat = snapshot.getValue(Chat.class);
                chatKeys.remove(snapshot.getKey());
                chats.remove(chat);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void userInfo(){

        // Mesajın alıcısının bilgileri alınıyor
        DatabaseReference userPath= FirebaseDatabase.getInstance().getReference("UserInfo").child(chatProfileID);
        userPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                forRowChatUserName = snapshot.child("userName").getValue().toString();
                forRowChatCircleIV = snapshot.child("userImage").getValue().toString();
                ChatUserName.setText(snapshot.child("userName").getValue().toString());
                Picasso.get().load(snapshot.child("userImage").getValue().toString()).fit().centerCrop().into(ChatProfileUserCircleImageView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
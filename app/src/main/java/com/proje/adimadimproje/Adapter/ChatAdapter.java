package com.proje.adimadimproje.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proje.adimadimproje.Activity.PostDetailActivity;
import com.proje.adimadimproje.Model.Chat;
import com.proje.adimadimproje.R;
import com.squareup.picasso.Picasso;

import java.sql.SQLOutput;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    Context context;
    List<Chat> chats;
    List<String> chatKeys;
    FirebaseUser firebaseUser;
    DatabaseReference userPath;
    String forRowChatUserName,forRowChatCircleIV;

    Animation blink_anim,blink_anim1;
    Boolean isTrue = true;
    public ChatAdapter(Context context, List<Chat> chats,List<String> chatKeys, String forRowChatUserName, String forRowChatCircleIV) {
        this.context = context;
        this.chats = chats;
        this.chatKeys = chatKeys;
        this.forRowChatUserName = forRowChatUserName;
        this.forRowChatCircleIV = forRowChatCircleIV;
        blink_anim = AnimationUtils.loadAnimation( this.context, R.anim.blink);
        blink_anim1 = AnimationUtils.loadAnimation( this.context, R.anim.blink1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chat,parent,false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return new ChatAdapter.ViewHolder(view);
    }
    int control = 0;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.LinearLayoutReceiverInfo.setVisibility(View.GONE);
        if (position==0){
            holder.LinearLayoutReceiverInfo.setVisibility(View.VISIBLE);}

        String getCurrentUserID = firebaseUser.getUid();
        Chat chat = chats.get(position);
        userPath = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(chat.getReceiverUserID());
        userPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.get().load(snapshot.child("userImage").getValue().toString()).fit().centerCrop().into(holder.ChatRowCircleIV);
                holder.ChatUserName2.setText(snapshot.child("userName").getValue().toString());
                Picasso.get().load(snapshot.child("userImage").getValue().toString()).fit().centerCrop().into(holder.ChatProfileUserCircleImageView2);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (getCurrentUserID.equals(chat.getSenderUserID())){
            holder.senderLinearLayout.setVisibility(View.VISIBLE);
            holder.senderMessageTextView.setVisibility(View.VISIBLE);
            holder.receiverLinearLayout.setVisibility(View.GONE);
            holder.ChatRowCircleIV.setVisibility(View.GONE);
            holder.senderMessageTextView.setText(chat.getMessage());
            control = 0;
        }else{ // visible 0 | gone 8
            holder.ChatRowCircleIV.setVisibility(View.GONE);
            /*holder.ChatRowCircleIV.setVisibility(View.VISIBLE);*/
            holder.senderLinearLayout.setVisibility(View.GONE);
            holder.receiverLinearLayout.setVisibility(View.VISIBLE);
            holder.senderMessageTextView.setVisibility(View.GONE);
            holder.receiverMessageTextView.setText(chat.getMessage());
        }
        holder.receiverLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTrue){
                    holder.ChatReceiverDelete.startAnimation(blink_anim1);
                    holder.ChatReceiverDelete.setVisibility(View.GONE);
                    isTrue = true;
                }else{
                    holder.ChatReceiverDelete.setVisibility(View.VISIBLE);
                    holder.ChatReceiverDelete.startAnimation(blink_anim); isTrue = false;}
                FirebaseDatabase.getInstance().getReference()
                        .child("Chats").child(firebaseUser.getUid()).child(chat.getSenderUserID())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()){
                                    try {
                                        if (chatKeys.get(position).equals(ds.getKey())){
                                            holder.ChatReceiverDelete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (chatKeys.get(position).equals(ds.getKey())) {
                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("Chats").child(firebaseUser.getUid())
                                                                .child(chat.getSenderUserID()).child(ds.getKey()).removeValue();
                                                        chats.remove(position);
                                                        holder.ChatReceiverDelete.startAnimation(blink_anim1);
                                                        holder.ChatReceiverDelete.setVisibility(View.GONE);
                                                        isTrue = true;
                                                    }
                                                }
                                            });

                                        }}catch (Exception e){}}
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });
        holder.senderLinearLayout.setOnClickListener(new View.OnClickListener() { // Gönderilen mesajların seçeneklere göre silinmesi
        @Override
        public void onClick(View v) {
            if (!isTrue){
                holder.ChatDeleteMenu.startAnimation(blink_anim1);
                holder.ChatDeleteMenu.setVisibility(View.GONE);
                isTrue = true;
            }else{
            holder.ChatDeleteMenu.setVisibility(View.VISIBLE);
            holder.ChatDeleteMenu.startAnimation(blink_anim); isTrue = false;}
               FirebaseDatabase.getInstance().getReference()
                    .child("Chats").child(firebaseUser.getUid()).child(chat.getReceiverUserID())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()){
                                try {
                                if (chatKeys.get(position).equals(ds.getKey())){
                                    holder.ChatSenderDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (chatKeys.get(position).equals(ds.getKey())) {
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("Chats").child(firebaseUser.getUid())
                                                        .child(chat.getReceiverUserID()).child(ds.getKey()).removeValue();
                                                chats.remove(position);
                                                holder.ChatDeleteMenu.startAnimation(blink_anim1);
                                                holder.ChatDeleteMenu.setVisibility(View.GONE);
                                                isTrue = true;
                                            }
                                        }
                                    });
                                    holder.ChatEveryoneDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (chatKeys.get(position).equals(ds.getKey())) {
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("Chats").child(firebaseUser.getUid())
                                                        .child(chat.getReceiverUserID()).child(ds.getKey()).removeValue();
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("Chats").child(chat.getReceiverUserID())
                                                        .child(firebaseUser.getUid()).child(ds.getKey()).removeValue();
                                                chats.remove(position);

                                                holder.ChatDeleteMenu.startAnimation(blink_anim1);
                                                holder.ChatDeleteMenu.setVisibility(View.GONE);
                                                isTrue = true;
                                            }
                                        }
                                    });
                            }}catch (Exception e){}}
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
        });

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout receiverLinearLayout,senderLinearLayout,LinearLayoutReceiverInfo,ChatDeleteMenu;
        CircleImageView ChatRowCircleIV,ChatProfileUserCircleImageView2;
        TextView receiverMessageTextView,senderMessageTextView,ChatUserName2,ChatSenderDelete,ChatEveryoneDelete,ChatReceiverDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMessageTextView = itemView.findViewById(R.id.receiverMessageTextView);
            ChatSenderDelete = itemView.findViewById(R.id.ChatSenderDelete);
            ChatEveryoneDelete = itemView.findViewById(R.id.ChatEveryoneDelete);
            ChatReceiverDelete = itemView.findViewById(R.id.ChatReceiverDelete);
            senderMessageTextView = itemView.findViewById(R.id.senderMessageTextView);
            ChatRowCircleIV = itemView.findViewById(R.id.ChatRowCircleIV);
            ChatProfileUserCircleImageView2 = itemView.findViewById(R.id.ChatProfileUserCircleImageView2);
            receiverLinearLayout = itemView.findViewById(R.id.receiverLinearLayout);
            senderLinearLayout = itemView.findViewById(R.id.senderLinearLayout);
            ChatDeleteMenu = itemView.findViewById(R.id.ChatDeleteMenu);
            LinearLayoutReceiverInfo = itemView.findViewById(R.id.LinearLayoutReceiverInfo);
            ChatUserName2 = itemView.findViewById(R.id.chatProfileUserName2);

        }
    }
}

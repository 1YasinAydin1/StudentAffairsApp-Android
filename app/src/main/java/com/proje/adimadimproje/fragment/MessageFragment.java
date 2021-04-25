package com.proje.adimadimproje.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proje.adimadimproje.Activity.ChatActivity;
import com.proje.adimadimproje.Model.Chat;
import com.proje.adimadimproje.Model.User;
import com.proje.adimadimproje.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageFragment extends Fragment {

    View view;
    RecyclerView MessageRecyclerView;
    DatabaseReference databaseReference,userPath;
    FirebaseUser firebaseUser;
    WebView WebViewWebView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);

        MessageRecyclerView = view.findViewById(R.id.MessageRecyclerView);
        MessageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(firebaseUser.getUid());
        userPath = FirebaseDatabase.getInstance().getReference().child("UserInfo");
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> build = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(databaseReference,User.class)
                .build();
        FirebaseRecyclerAdapter<User,ChatViewHolder> adapter = new FirebaseRecyclerAdapter<User, ChatViewHolder>(build) {
            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull User model) {

                final String userID = getRef(position).getKey();
                userPath.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String userNameLastName = snapshot.child("userNameLastName").getValue().toString();
                        final String image = snapshot.child("userImage").getValue().toString();
                        Picasso.get().load(image).fit().centerCrop().into(holder.UserRowCircleIV);
                         holder.UserRowUserNameTextView.setText(userNameLastName);

                        FirebaseDatabase.getInstance().getReference().child("Chats").child(firebaseUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()){
                                Chat chat = ds.getValue(Chat.class);
                                    holder.UserRowUserNameLastNameTextView.setText(chat.getMessage());}
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), ChatActivity.class);
                                intent.putExtra("ChatProfileID",userID);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user,parent,false);
                return new ChatViewHolder(view);
            }
        };
        MessageRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();
    }
    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        CircleImageView UserRowCircleIV;
        TextView UserRowUserNameTextView,UserRowUserNameLastNameTextView;
        Button FollowButton;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            UserRowCircleIV = itemView.findViewById(R.id.UserRowCircleIV);
            UserRowUserNameTextView = itemView.findViewById(R.id.UserRowUserNameTextView);
            UserRowUserNameLastNameTextView = itemView.findViewById(R.id.UserRowUserNameLastNameTextView);
            FollowButton = itemView.findViewById(R.id.FollowButton);
            FollowButton.setVisibility(View.GONE);
        }
    }
}
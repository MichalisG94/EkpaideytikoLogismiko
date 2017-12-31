package com.michalis.ekpaideytikologismiko;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder>{

    private List<WallPost> postsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, body;
        public CircleImageView profPic;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            body = (TextView) view.findViewById(R.id.body);
            profPic = (CircleImageView) view.findViewById(R.id.profPic);
        }
    }

    public PostsAdapter(List<WallPost> postsList) {
        this.postsList = postsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        String UserId = mFirebaseUser.getUid();
        ImageView deleteIcon = (ImageView) holder.itemView.findViewById(R.id.deleteIcon);

        final WallPost post = postsList.get(position);
        holder.title.setText(post.getPostTitle());
        holder.body.setText(post.getPostBody());
        final String downloadUrl = post.getPicUrl();
        String postUid = post.getUserID();

        if(downloadUrl != null) {
            Picasso.with(holder.body.getContext()).load(downloadUrl).into(holder.profPic);
        } else {
            Picasso.with(holder.body.getContext()).load(R.mipmap.ic_launcher).into(holder.profPic);
        }
        //O kathe xrhsths mporei na diagrapsei mono ta dika tou post
        if(UserId.equals(postUid)) {
            deleteIcon.setVisibility(View.VISIBLE);
        }
        else {
            deleteIcon.setVisibility(View.INVISIBLE);
        }

        holder.profPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Provoli profil toy xrhsth poy ekane to post
                Intent i = new Intent(v.getContext(), ProfileActivity.class);
                i.putExtra("post", post.getUserID());
                v.getContext().startActivity(i);
            }
        });

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Delete this post?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String postTitle = post.getPostTitle();
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.child("Posts List")
                                        .orderByChild("postTitle")
                                        .equalTo(postTitle)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChildren()) {
                                                    DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                                    firstChild.getRef().removeValue();
                                                }
                                                WallActivity.updateList();
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.setTitle("Confirm");
                dialog.show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return postsList.size();
    }
}
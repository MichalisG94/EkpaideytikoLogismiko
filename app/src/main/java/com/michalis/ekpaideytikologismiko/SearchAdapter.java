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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.text.Normalizer.Form;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.michalis.ekpaideytikologismiko.SearchResultsActivity.filterdNames;
import static java.lang.Thread.sleep;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder>{

    private List<UserProfile> userProfileList = new ArrayList<>();
    private String imageURL;
    private final String TAG = "SearchAdapter";

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name, surname, age;
        public CircleImageView profPic;

        public MyViewHolder(View view) {
            super(view);
            Log.i(TAG, "MyViewHolder");
            name = (TextView) view.findViewById(R.id.name);
            surname = (TextView) view.findViewById(R.id.surname);
            age = (TextView) view.findViewById(R.id.age);
            profPic = (CircleImageView) view.findViewById(R.id.SearchProfPic);
        }
    }

    public SearchAdapter(List<UserProfile> userProfileList) {
        this.userProfileList = userProfileList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_search_layout, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder");
        //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        UserProfile user = userProfileList.get(position);
        holder.name.setText(user.name);
        holder.surname.setText(user.surname);
        holder.age.setText(String.valueOf(user.age));
        final String UserID = user.userID;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), ProfileActivity.class);
                i.putExtra("SearchAdapter", UserID);
                view.getContext().startActivity(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return userProfileList.size();
    }

    public void filter(String text, List<UserProfile> searchlist) {
        searchlist.clear();
        if(text.isEmpty()){
            searchlist.clear();
            searchlist.addAll(filterdNames);
        } else{
            //text = Normalizer.normalize(text ,Normalizer.Form.NFD);
            //text = text.replace("ά", "α").replace("ή", "η").replace("ί", "ι").replace("ό", "ο").replace("έ", "ε").replace("ύ", "υ").replace("ώ", "ω");
            //text = text.normalizer('NFD').replace(/[\u0300-\u036f]/g, "");
            text = text.toLowerCase();
            for(UserProfile name: filterdNames){
                if(name.name.trim().toLowerCase().contains(text) || name.surname.trim().toLowerCase().contains(text)){
                    searchlist.add(name);
                }
            }
        }
        notifyDataSetChanged();
    }
}
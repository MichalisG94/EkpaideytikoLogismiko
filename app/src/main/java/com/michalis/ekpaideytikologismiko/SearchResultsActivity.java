package com.michalis.ekpaideytikologismiko;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.data.DataHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private List<UserProfile> userProfileList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    public static ArrayList<UserProfile> filterdNames = new ArrayList<>();
    private DatabaseReference databaseReference;
    private static final String TAG = "SearchResultsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        recyclerView = (RecyclerView) findViewById(R.id.search_recyclerView);
        searchAdapter = new SearchAdapter(userProfileList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(searchAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        updateProfileList();
    }

    public void updateProfileList() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filterdNames.clear();
                userProfileList.clear();
                //searchList.clear();

                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    UserProfile userProfile = data.getValue(UserProfile.class);
                    userProfileList.add(userProfile);
                }

                filterdNames.addAll(userProfileList);
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.searchItem);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAdapter.filter(query, userProfileList);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdapter.filter(newText, userProfileList);
                Log.i(TAG, "onQueryTextChange");
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
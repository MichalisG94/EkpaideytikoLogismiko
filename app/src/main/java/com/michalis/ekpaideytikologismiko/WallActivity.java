package com.michalis.ekpaideytikologismiko;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.michalis.ekpaideytikologismiko.MainActivity.help;

public class WallActivity extends AppCompatActivity {

    private static List<WallPost> postsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private static PostsAdapter mAdapter;
    private Button postTextBtn;
    private EditText titlePostText, bodyPostText;
    private de.hdodenhof.circleimageview.CircleImageView profPic;
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public final String TAG = "WallActivity";
    private String imageURL;
    boolean executed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);
        postTextBtn = (Button) findViewById(R.id.sendPostButton);
        titlePostText = (EditText) findViewById(R.id.titlePostText);
        bodyPostText = (EditText) findViewById(R.id.bodyPostText);
        profPic = (CircleImageView) findViewById(R.id.profPic);
        recyclerView = (RecyclerView) findViewById(R.id.wall_recyclerView);
        mAdapter = new PostsAdapter(postsList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        updateList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        executed = false;
        getPictureUrl();

        postTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Post button clicked!");
                mFirebaseAuth = FirebaseAuth.getInstance();
                mFirebaseUser = mFirebaseAuth.getCurrentUser();
                String userId = mFirebaseUser.getUid();
                String key = databaseReference.child("Posts List").push().getKey();
                if(executed) { //Epeidh h onDataChange ekteleitai asygxrona xrhsimopoioyme thn boolean metavliti gia na exakrivosoume oti exei ektelestei
                    Log.i(TAG, "getPictureUrl executed = " + imageURL);
                    WallPost post = new WallPost(titlePostText.getText().toString(), bodyPostText.getText().toString(), imageURL, userId);
                    databaseReference.child("Posts List").child(key).setValue(post);
                    //Reset the EditTexts after submission
                    titlePostText.setText(null);
                    bodyPostText.setText(null);
                    Toast.makeText(WallActivity.this, "Post submitted!", Toast.LENGTH_SHORT).show();
                    //Hide the keyboard after button click
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(bodyPostText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    updateList();
                }
            }
        });
    }

    private void getPictureUrl() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        String UserId = mFirebaseUser.getUid();
        databaseReference.child("ImagesUrl").child(UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    imageURL = dataSnapshot.getValue().toString();
                }
                else {
                    imageURL = null;
                }
                executed = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void updateList(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("Posts List").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postsList.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if(data.exists()) {
                        WallPost posts = data.getValue(WallPost.class);
                        postsList.add(posts);
                        //postsList.sort();
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.icon_help_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.helpIcon: {
                String msg = "Γράψε ένα μήνυμα για να επικοινωνήσεις με τους συμμαθητές σου αλλά και με τους καθηγητές σου! \n" +
                        "Με \"κλικ\" πάνω στην εικόνα κάθε χρήστη εμφανίζεται το προφίλ του.";
                String msgTitle = "Help!";
                help(msgTitle, msg, WallActivity.this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

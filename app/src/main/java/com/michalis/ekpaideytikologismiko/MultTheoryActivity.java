package com.michalis.ekpaideytikologismiko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.michalis.ekpaideytikologismiko.MainActivity.checkProfessor;
import static com.michalis.ekpaideytikologismiko.MainActivity.help;
import static com.michalis.ekpaideytikologismiko.PlusTheoryActivity.textGet;

public class MultTheoryActivity extends AppCompatActivity {
    private ViewFlipper flipper;
    private ImageButton nextBtn, prevBtn;
    private TextView text1, text2, text3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mult_theory);
        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        text3 = (TextView) findViewById(R.id.text3);
        nextBtn = (ImageButton) findViewById(R.id.nextBtn);
        prevBtn = (ImageButton) findViewById(R.id.prevBtn);
        flipper = (ViewFlipper) findViewById(R.id.flipper);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setStepSize(0.5f);
        Button submitBtn = (Button) findViewById(R.id.submitBtn);
        Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        if((mFirebaseUser != null)) {

            if(checkProfessor(mFirebaseUser)) {
                submitBtn.setEnabled(false);
            }

            final String UserId = mFirebaseUser.getUid();

            if(checkProfessor(mFirebaseUser)) {
                text1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Intent i = new Intent(MultTheoryActivity.this, EditTheoryActivity.class);
                        i.putExtra("chapter", "Chapter 3");
                        i.putExtra("view", "View 1");
                        MultTheoryActivity.this.startActivity(i);
                        return true;
                    }
                });

                text2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Intent i = new Intent(MultTheoryActivity.this, EditTheoryActivity.class);
                        i.putExtra("chapter", "Chapter 3");
                        i.putExtra("view", "View 2");
                        MultTheoryActivity.this.startActivity(i);
                        return true;
                    }
                });

                text3.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Intent i = new Intent(MultTheoryActivity.this, EditTheoryActivity.class);
                        i.putExtra("chapter", "Chapter 3");
                        i.putExtra("view", "View 3");
                        MultTheoryActivity.this.startActivity(i);
                        return true;
                    }
                });
            }

            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    float rating = ratingBar.getRating(); // get total number of stars of rating bar
                    ref.child("Ratings").child("Theory").child("Chapter 3").child(UserId).child("Rating").setValue(rating);
                    finish();
                }
            });
        }
        else {
            submitBtn.setEnabled(false);
        }

        //Emfanise to yparxon rating ean yparxei
        ref.child("Ratings").child("Theory").child("Chapter 3").addListenerForSingleValueEvent(new ValueEventListener() {
            float totalRating;
            int counter = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot data1 : data.getChildren()) {
                        totalRating = totalRating + Float.parseFloat(data1.getValue().toString());
                        counter++;
                    }
                }
                ratingBar.setRating(totalRating / counter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipper.setInAnimation(MultTheoryActivity.this, R.anim.in_from_right);
                flipper.setOutAnimation(MultTheoryActivity.this, R.anim.out_to_left);
                //Ean eimai sto teleytaio view
                if(flipper.getDisplayedChild() == (flipper.getChildCount()-2 )) {
                    if(mFirebaseUser == null) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Πρέπει να είσαι συνδεδεμένος για να βαθμολογήσεις!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    prevBtn.setVisibility(View.INVISIBLE);
                }
                if(flipper.getDisplayedChild() == (flipper.getChildCount()-1 )) {
                    finish();
                }
                flipper.showNext();
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipper.setInAnimation(MultTheoryActivity.this, R.anim.in_from_left);
                flipper.setOutAnimation(MultTheoryActivity.this, R.anim.out_to_right);
                flipper.showPrevious();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("TESTDEBUG", "onResume");
        textGet("Chapter 3", "View 1", text1);
        textGet("Chapter 3", "View 2", text2);
        textGet("Chapter 3", "View 3", text3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser != null) {
            if (checkProfessor(mFirebaseUser)) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.icon_help_menu, menu);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.helpIcon: {
                String msg = "Πάτησε παρατεταμένα το κείμενο της θεωρίας προκειμένου να το επεξεργαστείς";
                String msgTitle = "Help!";
                help(msgTitle, msg, MultTheoryActivity.this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
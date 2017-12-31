package com.michalis.ekpaideytikologismiko;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditTheoryActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_theory);
        final EditText theoryText = (EditText) findViewById(R.id.theoryText);
        ImageButton checkBtn = (ImageButton) findViewById(R.id.checkBtn);

        extras = this.getIntent().getExtras();
        final String chapter = (String) extras.get("chapter");
        final String viewNumber = (String) extras.get("view");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Theory Text").child(chapter).child(viewNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text;
                if(dataSnapshot.exists()) {
                    text = dataSnapshot.getValue().toString();
                    theoryText.setText(text);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("Theory Text").child(chapter).child(viewNumber).setValue(theoryText.getText().toString());
                finish();
            }
        });

    }
}
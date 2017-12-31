package com.michalis.ekpaideytikologismiko;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {
    public Button button;
    public TextView name, surname, age;
    public EditText editName, editSurname, editAge;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        name = (TextView) findViewById(R.id.textView1);
        surname = (TextView) findViewById(R.id.textView2);
        age = (TextView) findViewById(R.id.textView3);
        editName = (EditText) findViewById(R.id.editText1);
        editName.requestFocus();
        editSurname = (EditText) findViewById(R.id.editText2);
        editAge = (EditText) findViewById(R.id.editText3);
        button = (Button) findViewById(R.id.button1);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        final String UserId = mFirebaseUser.getUid();
        databaseReference.child("Profile").child(UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                //Ean o xrhsths kanei login gia proth fora den yparxoun stoixeia gia na provlithoun
                if(userProfile != null) {
                    editName.setText(userProfile.name);
                    editSurname.setText(userProfile.surname);
                    editAge.setText(String.valueOf(userProfile.age));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                UserProfile userProf = new UserProfile(editName.getText().toString(), editSurname.getText().toString(),
                        Integer.parseInt(editAge.getText().toString()), UserId);
                databaseReference.child("Profile").child(UserId).setValue(userProf);
                finish();
            }
        });
    }
}

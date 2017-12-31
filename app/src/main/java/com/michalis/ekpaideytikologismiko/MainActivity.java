package com.michalis.ekpaideytikologismiko;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    Button buttonTest, buttonTheory, buttonWall, buttonProfile, buttonAuthTool;
    // Pinakas me ta UserID ton kathigiton gia taytopoihsh
    public static String[] professors = {"2cAhAY3cYXQboLeeXfvxhmElIGf2", "Vnu7MsRlC5RfiEFkDwGd92qVnCc2"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonTest = (Button) findViewById(R.id.testbutton);
        buttonTheory = (Button) findViewById(R.id.theorybutton);
        buttonWall = (Button) findViewById(R.id.wallbutton);
        buttonProfile = (Button) findViewById(R.id.profilbutton);
        buttonAuthTool = (Button) findViewById(R.id.authToolBtn);
        boolean isGuest = false;

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            boolean isProfessor = checkProfessor(mFirebaseUser);
            if (!isProfessor) {
                buttonAuthTool.setVisibility(View.INVISIBLE);
            }
            else {
                buttonTest.setVisibility(View.INVISIBLE);
            }
        }
        else {
            isGuest = true;
            buttonAuthTool.setVisibility(View.INVISIBLE);
        }
        final boolean finalIsGuest = isGuest;

        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!finalIsGuest) {
                    Intent i = new Intent(MainActivity.this, ChapterChoiceActivity.class);
                    MainActivity.this.startActivity(i);
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Πρέπει να είσαι συνδεδεμένος για να προχωρήσεις!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        buttonTheory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, TheoryActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        buttonWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!finalIsGuest) {
                    Intent i = new Intent(MainActivity.this, WallActivity.class);
                    MainActivity.this.startActivity(i);
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Πρέπει να είσαι συνδεδεμένος για να προχωρήσεις!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!finalIsGuest) {
                    Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                    MainActivity.this.startActivity(i);
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Πρέπει να είσαι συνδεδεμένος για να προχωρήσεις!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        buttonAuthTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, QuestionsActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.main_menu, menu );
        inflater.inflate(R.menu.icon_help_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.helpIcon: {
                String msg = "Καλώς ήρθατε στην εφαρμογή \"Εκπαιδευτικό Λογισμικό\" \nΔιαβάστε την θεωρία και έπειτα εξεταστείτε πανω στην ύλη. \nΑλληλεπιδράστε με τους συμμαθητές σας" +
                        " αλλά και με τους καθηγητές σας μέσω μηνυμάτων στον \"τοίχο\"";
                String msgTitle = "Καλώς ήρθατε!";
                help(msgTitle, msg, MainActivity.this);
                return true;
            }
        }
        switch ( item.getItemId() ) {
            case R.id.logout: {
                logOut();
                return true;
            }
        }
        switch ( item.getItemId() ) {
            case R.id.exit: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean checkProfessor(FirebaseUser mFirebaseUser) {
        String UserId = mFirebaseUser.getUid();
        boolean isProfessor = false;
        for(int i=0; i<professors.length; i++) {
            if (professors[i].equals(UserId)) { isProfessor = true; }
        }
        return isProfessor;
    }

    public static void help(String msgTitle, String msg, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setTitle(msgTitle);
        dialog.show();
    }

    private void logOut() {
        database = FirebaseDatabase.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth.getInstance().signOut();
        Intent i = new Intent(this, LogInActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}

package com.michalis.ekpaideytikologismiko;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    protected EditText emailEditText;
    protected EditText passwordEditText;
    protected Button logInButton, guestButton;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private CheckBox rememberMeCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private boolean saveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_log_in_);

        mFirebaseAuth = FirebaseAuth.getInstance();
        //mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        emailEditText = (EditText) findViewById(R.id.emailField);
        passwordEditText = (EditText) findViewById(R.id.passwordField);
        logInButton = (Button) findViewById(R.id.loginButton);
        guestButton = (Button) findViewById(R.id.guestBtn);
        rememberMeCheckBox = (CheckBox)findViewById(R.id.rememberMeCheckBox);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            emailEditText.setText(loginPreferences.getString("email", ""));
            passwordEditText.setText(loginPreferences.getString("password", ""));
            rememberMeCheckBox.setChecked(true);
        }

        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                email = email.trim();
                password = password.trim();

                if (rememberMeCheckBox.isChecked()) {
                    loginPrefsEditor.putBoolean("saveLogin", true);
                    loginPrefsEditor.putString("email", email);
                    loginPrefsEditor.putString("password", password);
                    loginPrefsEditor.apply();
                } else {
                    loginPrefsEditor.clear();
                    loginPrefsEditor.commit();
                }
                //Elegxoume oti symplhrothikan kai to  Email kai o Kodikos
                //Dhmiourgia katallhlon mhnymaton se parathiro
                if (email.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                    builder.setMessage("Please enter E-mail and Password")
                            .setTitle("Error!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                //Syndesh me to Email kai ton Kodiko pou dothike
                else {
                    mFirebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //Ean egine syndesh epityxhmena metaferomaste sto activity GetGeoPoints
                                    //Epishs ginetai clear to activity stack kai apotrepei ton xrhsth na epistrepsei sto activity ayto
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    //Ean den egine epityxhs syndesh emfanizontai katallhla mhnymata
                                    else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                                        builder.setMessage(task.getException().getMessage())
                                                .setTitle("Error!")
                                                .setPositiveButton(android.R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });
                }
               // logInButton.setEnabled(false);

                final ProgressDialog progressDialog = new ProgressDialog(LogInActivity.this,R.style.Theme_AppCompat_DayNight_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
/*
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                logInButton.setEnabled(true);
                                //finish();
                            }
                        }, 3000); */
            }

        });

    }
}
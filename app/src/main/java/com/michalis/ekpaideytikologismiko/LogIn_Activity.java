package com.michalis.ekpaideytikologismiko;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
public class LogIn_Activity extends AppCompatActivity {

    protected EditText emailEditText;
    protected EditText passwordEditText;
    protected Button logInButton;
    protected TextView signUpTextView;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_log_in_);

        mFirebaseAuth = FirebaseAuth.getInstance();

        emailEditText = (EditText) findViewById(R.id.emailField);
        passwordEditText = (EditText) findViewById(R.id.passwordField);
        logInButton = (Button) findViewById(R.id.loginButton);


        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                email = email.trim();
                password = password.trim();

                //Elegxoume oti symplhrothikan kai to  Email kai o Kodikos
                //Dhmiourgia katallhlon mhnymaton se parathiro
                if (email.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LogIn_Activity.this);
                    builder.setMessage("Please enter E-mail and Password")
                            .setTitle("Error!")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                //Syndesh me to Email kai ton Kodiko pou dothike
                else {
                    mFirebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LogIn_Activity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //Ean egine syndesh epityxhmena metaferomaste sto activity GetGeoPoints
                                    //Epishs ginetai clear to activity stack kai apotrepei ton xrhsth na epistrepsei sto activity ayto
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(LogIn_Activity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    //Ean den egine epityxhs syndesh emfanizontai katallhla mhnymata
                                    else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LogIn_Activity.this);
                                        builder.setMessage(task.getException().getMessage())
                                                .setTitle("Error!")
                                                .setPositiveButton(android.R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
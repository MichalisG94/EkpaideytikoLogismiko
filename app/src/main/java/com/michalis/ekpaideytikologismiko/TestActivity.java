package com.michalis.ekpaideytikologismiko;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.michalis.ekpaideytikologismiko.MainActivity.help;

public class TestActivity extends AppCompatActivity {

    List<Questions> questionsList, chosenQuestionsList;
    Button nextQuestionBtn;
    TextView questionText;
    EditText answerText;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private int j = 0; // Metrhths tou next button
    private String[] answerArray;
    public final String TAG = "TestActivity" ;
    int correct = 0, wrong = 0, savedValue = 0, sillyMistakesCounter = 0;
    long startTime = 0, endTime = 0, elapsedTime = 0;
    boolean buttonClicked = false;
    final int NUM_OF_QUESTIONS = 6, SILLY_MISTAKE_ELAPSED_TIME = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_exam);
        nextQuestionBtn = (Button) findViewById(R.id.nextQuestionBtn);
        questionText = (TextView) findViewById(R.id.questionTextView);
        answerText = (EditText) findViewById(R.id.answerEditText);
        questionsList = new ArrayList<>();
        chosenQuestionsList = new ArrayList<>();
        startTime = System.currentTimeMillis();

    }

    @Override
    protected void onResume() {
        super.onResume();
        final String chapter = this.getIntent().getStringExtra("chapter"); //Elegxos gia to poio kefalaio exei epilexthei
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        final String UserId = mFirebaseUser.getUid();
        Log.i( TAG , "chapter is : " + chapter);

        database.getReference().child("User Statistics").child(UserId).child("Chapter " + chapter).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Ginetai aytos o elegxos giati ean o xrhsths mpainei gia proti fora h den exei xanaexetastei se ena kefalaio to datasnapshot den yparxei kai einai null kai ara exceptions
                //Ean den yparxei den xreiazetai na diavasoyme thn prohgoymenh timh profanos kai ara to savedValue menei 0 -- arxikopoieitai etsi sthn arxh --
                if (dataSnapshot.exists()) {
                    savedValue = Integer.parseInt(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        database.getReference("Questions List").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                //Prospelasi olon ton erotiseon kai prosthiki sthn lista
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Questions questions = data.getValue(Questions.class);
                    //Ean exei epilexthei to Final Test tote gemizoume thn lista me oles tis erotiseis apo ola ta kefalaia
                    if(chapter.equals("Revision")) {
                        questionsList.add(questions);
                    }
                    else {
                        //Gemizoume tin lista me tis erotiseis pou antistoixoun sto epilegmeno kefalaio
                        if (questions.getChapter().equals(chapter)) {
                            questionsList.add(questions);
                        }
                    }
                }
                Collections.shuffle(questionsList);   //Anakatema ths listas, gia random emfanish
                chosenQuestionsList = questionsList.subList(0, NUM_OF_QUESTIONS); //Epilogh apo thn anakatemenh lista n stoixeion - Gia na exoume panta sygkekrimeno arithmo erotiseon
                answerArray = new String[chosenQuestionsList.size()];
                for (int i = 0; i < chosenQuestionsList.size(); i++) {
                    answerArray[i] = chosenQuestionsList.get(i).getAnswer(); //Epeidh h lista exei anakateytei pername tis apanthseis na tairiazoyn me tis erotiseis
                }

                questionText.setText(chosenQuestionsList.get(j).getQuestion());

                nextQuestionBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (answerArray[j].equals(answerText.getText().toString())) {
                            correct++;
                        }
                        else {
                            Log.i(TAG, "Wrong answer! :(");
                            wrong++;
                            //Έλεγχος για την ταχύτητα των απαντήσεων
                            if(!buttonClicked) {
                                endTime = System.currentTimeMillis();
                                elapsedTime = ((endTime - startTime) / 1000);
                                if(elapsedTime < SILLY_MISTAKE_ELAPSED_TIME) //Ean apantaei se ligotero apo 6 deyterolepta einai lathos aprosexias
                                    sillyMistakesCounter++;
                                buttonClicked = true;
                            }
                        }
                        j++;
                        if(j == chosenQuestionsList.size()) {
                                if (savedValue != 0) {
                                    //Ean yparxoun prohgoymena apothikeymena stoixeia prepei na ta xrhsimopoihsoume stoys ypologismous mas
                                    database.getReference().child("User Statistics").child(UserId).child("Chapter " + chapter).setValue((savedValue + (correct * 100 / chosenQuestionsList.size())) / 2);
                                }
                                else {
                                    database.getReference().child("User Statistics").child(UserId).child("Chapter " + chapter).setValue((correct * 100 / chosenQuestionsList.size()));
                                }

                            RelativeLayout questionsLayout = (RelativeLayout) findViewById(R.id.questionsLayout);
                            RelativeLayout ratingLayout = (RelativeLayout) findViewById(R.id.ratingLayout);
                            Button submitBtn = (Button) findViewById(R.id.submitBtn);
                            Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
                            final RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
                            ratingBar.setStepSize(0.5f);
                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                            questionsLayout.setVisibility(View.INVISIBLE);
                            ratingLayout.setVisibility(View.VISIBLE);

                            submitBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    float rating = ratingBar.getRating(); // get total number of stars of rating bar
                                    ref.child("Ratings").child("Tests").child("Chapter " + chapter).child(UserId).child("Rating").setValue(rating);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
                                    if(sillyMistakesCounter > NUM_OF_QUESTIONS/2) {
                                        builder.setMessage("Ευχαριστούμε για την αξιολόγηση! \nΠροσπάθησε να κάνεις τα διαγωνίσματα με μεγαλύτερη προσοχή!").setCancelable(false)
                                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                });
                                    }
                                    else if(wrong >= chosenQuestionsList.size()/2){
                                        builder.setMessage("Ευχαριστούμε για την αξιολόγηση! \nΠροσπάθησε να ξαναδιαβάσεις το κεφάλαιο " + chapter + " και ξαναδοκίμασε το διαγώνισμα").setCancelable(false)
                                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                });
                                    }
                                    else {
                                        builder.setMessage("Ευχαριστούμε για την αξιολόγηση! \nΤέλος διαγωνίσματος!").setCancelable(false)
                                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                });
                                    }
                                    AlertDialog dialog = builder.create();
                                    dialog.setTitle("Chapter " + chapter);
                                    dialog.show();
                                }
                            });

                            //Emfanise to yparxon rating ean yparxei
                            ref.child("Ratings").child("Tests").child("Chapter " + chapter).addListenerForSingleValueEvent(new ValueEventListener() {
                                float totalRating;
                                int counter = 0;
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot data : dataSnapshot.getChildren()){
                                        for(DataSnapshot data1 : data.getChildren()) {
                                            totalRating = totalRating + Float.parseFloat(data1.getValue().toString());
                                            counter++;
                                        }
                                    }
                                    ratingBar.setRating(totalRating/counter);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            cancelBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
                                    Log.i(TAG, "sillyMistakesCounter : " + sillyMistakesCounter);
                                    if(sillyMistakesCounter > NUM_OF_QUESTIONS/2) {
                                        builder.setMessage("Προσπάθησε να κάνεις τα διαγωνίσματα με μεγαλύτερη προσοχή!").setCancelable(false)
                                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                });
                                    }
                                    else if(wrong >= chosenQuestionsList.size()/2){
                                        builder.setMessage("Προσπάθησε να ξαναδιαβάσεις το κεφάλαιο " + chapter + " και ξαναδοκίμασε το διαγώνισμα").setCancelable(false)
                                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                });
                                    }
                                    else {
                                        builder.setMessage("Τέλος διαγωνίσματος!").setCancelable(false)
                                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                });
                                    }
                                    AlertDialog dialog = builder.create();
                                    dialog.setTitle("Chapter " + chapter);
                                    dialog.show();
                                }
                            });
                        }
                        else {
                            questionText.setText(chosenQuestionsList.get(j).getQuestion());
                            answerText.setText(null);
                            startTime = System.currentTimeMillis();
                            buttonClicked = false;
                        }
                    }
                });
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
                String msg = "Απαντήστε στις ερωτήσεις με βάση την θεωρία που έχετε διδαχτεί. \nΣτο τέλος σας δίνεται η δυνατότητα να " +
                        "βαθμολογήσετε την εξέταση.";
                String msgTitle = "Help!";
                help(msgTitle, msg, TestActivity.this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

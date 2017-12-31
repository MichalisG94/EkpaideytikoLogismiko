package com.michalis.ekpaideytikologismiko;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class AddQuestionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean hasExtras = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);
        EditText questionEditText = (EditText)findViewById(R.id.questionEditText);
        EditText answerEditText = (EditText)findViewById(R.id.answerEditText);
        EditText chapterEditText = (EditText)findViewById(R.id.chapterEditText);
        if (this.getIntent().getExtras() != null) {
            hasExtras = true;
            Bundle extras = this.getIntent().getExtras();
            Questions questions = (Questions) extras.get("question");
            if (questions != null) {
                chapterEditText.setText(questions.getChapter());
                questionEditText.setText(questions.getQuestion());
                answerEditText.setText(questions.getAnswer());
            }
        }

        ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);
        if (hasExtras) {saveButton.setVisibility(View.INVISIBLE); }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveQuestion();
            }
        });

        ImageButton deleteButton = (ImageButton)findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteQuestion();
            }
        });

        ImageButton editButton = (ImageButton)findViewById(R.id.addButton);
        if (!hasExtras) {editButton.setVisibility(View.INVISIBLE); }
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editQuestion();
            }
        });
    }

    public void deleteQuestion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddQuestionsActivity.this);
        builder.setMessage("Delete this question?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText questionEditText = (EditText)findViewById(R.id.questionEditText);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        String questionTitle = questionEditText.getText().toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child("Questions List")
                                .orderByChild("question")
                                .equalTo(questionTitle)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChildren()) {
                                            DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                            firstChild.getRef().removeValue();
                                        }
                                        Toast.makeText(AddQuestionsActivity.this, "Question deleted successfully!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setTitle("Confirm");
        dialog.show();
    }

    public void saveQuestion() {
        EditText chapterEditText = (EditText)findViewById(R.id.chapterEditText);
        EditText questionEditText = (EditText)findViewById(R.id.questionEditText);
        EditText answerEditText = (EditText)findViewById(R.id.answerEditText);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String key = database.getReference("Questions List").push().getKey();

        Questions questions = new Questions();
        questions.setChapter(chapterEditText.getText().toString());
        questions.setQuestion(questionEditText.getText().toString());
        questions.setAnswer(answerEditText.getText().toString());
        questions.setQuestionID(key);
        //String myQuestion = questions.getQuestion();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put( key, questions.toFirebaseObject());
        database.getReference("Questions List").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Toast.makeText(AddQuestionsActivity.this, "Question added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    public void editQuestion() {
        // get the data to save in firebase
        final EditText chapterEditText = (EditText)findViewById(R.id.chapterEditText);
        final EditText questionEditText = (EditText)findViewById(R.id.questionEditText);
        final EditText answerEditText = (EditText)findViewById(R.id.answerEditText);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Bundle extras = this.getIntent().getExtras();
        final Questions clickedQuestion = (Questions) extras.get("question");

        //save it to the firebase
        databaseReference.child("Questions List")
                .orderByChild("question")
                .equalTo(clickedQuestion.getQuestion())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                    Questions oldquestion = firstChild.getValue(Questions.class);
                    String key = oldquestion.getQuestionID();
                    Questions newquestion = new Questions();
                    newquestion.setChapter(chapterEditText.getText().toString());
                    newquestion.setQuestion(questionEditText.getText().toString());
                    newquestion.setAnswer(answerEditText.getText().toString());
                    newquestion.setQuestionID(key);

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(newquestion.getQuestionID(), newquestion.toFirebaseObject());
                    databaseReference.child("Questions List").updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Toast.makeText(AddQuestionsActivity.this, "Question updated successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}



package com.michalis.ekpaideytikologismiko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.michalis.ekpaideytikologismiko.MainActivity.help;

public class QuestionsActivity extends AppCompatActivity {

    QuestionsAdapter adapter;
    ArrayList<Questions> questionsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        ImageButton fab = (ImageButton) findViewById(R.id.fab1);
        ImageButton chartsBtn = (ImageButton) findViewById(R.id.chartsBtn);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(QuestionsActivity.this, AddQuestionsActivity.class);
                QuestionsActivity.this.startActivity(i);
            }
        });

        chartsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(QuestionsActivity.this, ChartsActivity.class);
                QuestionsActivity.this.startActivity(i);
            }
        });

        questionsList = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.questions_recyclerView);
        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new QuestionsAdapter(QuestionsActivity.this, questionsList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference("Questions List").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        questionsList.clear();

                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            Questions questions = data.getValue(Questions.class);
                            questionsList.add(questions);
                        }
                        adapter.notifyDataSetChanged();
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
                String msg = "Επέλεξε μια ερώτηση για να την επεξεργαστείς ή να την διαγράψεις.\nΕπέλεξε από τα εικονίδια για να προσθέσεις μια καινούργια ερώτηση ή να δεις τα στατιστικά των μαθητών";
                String msgTitle = "Help!";
                help(msgTitle, msg, QuestionsActivity.this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}


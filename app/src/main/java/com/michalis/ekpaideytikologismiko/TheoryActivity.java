package com.michalis.ekpaideytikologismiko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import static com.michalis.ekpaideytikologismiko.MainActivity.help;

public class TheoryActivity extends AppCompatActivity {

    Button plusButton, minButton, multButton, divbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theory);
        plusButton = (Button) findViewById(R.id.plusbutton);
        minButton = (Button) findViewById(R.id.minbutton);
        multButton = (Button) findViewById(R.id.multbutton);
        divbutton = (Button) findViewById(R.id.divbutton);


        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TheoryActivity.this, PlusTheoryActivity.class);
                TheoryActivity.this.startActivity(i);
            }
        });

        minButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TheoryActivity.this, MinusTheoryActivity.class);
                TheoryActivity.this.startActivity(i);
            }
        });

        multButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TheoryActivity.this, MultTheoryActivity.class);
                TheoryActivity.this.startActivity(i);
            }
        });

        divbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TheoryActivity.this, DivTheoryActivity.class);
                TheoryActivity.this.startActivity(i);
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
                String msg = "Επέλεξε ένα κεφάλαιο για να διαβάσεις την αντίστοιχη θεωρία του";
                String msgTitle = "Help!";
                help(msgTitle, msg, TheoryActivity.this);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.michalis.ekpaideytikologismiko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChapterChoiceActivity extends AppCompatActivity {

    Button ch1Btn, ch2Btn, ch3Btn, ch4Btn, finalTestBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_choice);
        ch1Btn = (Button) findViewById(R.id.chapter1Btn);
        ch2Btn = (Button) findViewById(R.id.chapter2Btn);
        ch3Btn = (Button) findViewById(R.id.chapter3Btn);
        ch4Btn = (Button) findViewById(R.id.chapter4Btn);
        finalTestBtn = (Button) findViewById(R.id.finalTestBtn);

        ch1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChapterChoiceActivity.this, TestActivity.class);
                i.putExtra("chapter", "1");
                ChapterChoiceActivity.this.startActivity(i);
            }
        });

        ch2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChapterChoiceActivity.this, TestActivity.class);
                i.putExtra("chapter", "2");
                ChapterChoiceActivity.this.startActivity(i);
            }
        });

        ch3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChapterChoiceActivity.this, TestActivity.class);
                i.putExtra("chapter", "3");
                ChapterChoiceActivity.this.startActivity(i);
            }
        });

        ch4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChapterChoiceActivity.this, TestActivity.class);
                i.putExtra("chapter", "4");
                ChapterChoiceActivity.this.startActivity(i);
            }
        });

        finalTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChapterChoiceActivity.this, TestActivity.class);
                i.putExtra("chapter", "Revision");
                ChapterChoiceActivity.this.startActivity(i);
            }
        });
    }
}

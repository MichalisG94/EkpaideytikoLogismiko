package com.michalis.ekpaideytikologismiko;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChartsActivity extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ArrayList<PieEntry> chapterStats = new ArrayList<>();
    public final String TAG = "ChartsActivity";
    private int ch1TotVal, ch2TotVal, ch3TotVal, ch4TotVal, chRTotVal, i=0, j=0, k=0, m=0, n=0;
    private boolean isDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        final PieChart pieChart1 = (PieChart) findViewById(R.id.pieChart1);
        final ArrayList<String> labels = new ArrayList<>();
        labels.add("Πρόσθεση");
        labels.add("Αφαίρεση");
        labels.add("Πολλαπλασιασμός");
        labels.add("Διαίρεση");
        labels.add("Επαναληπτικό");
        databaseReference.child("User Statistics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                    for(DataSnapshot data2 : data1.getChildren()) {
                        Log.i(TAG, "data1.getKey() " + data1.getKey());
                        if(data2.getKey().equals("Chapter 1")) {
                            ch1TotVal = ch1TotVal + Integer.parseInt(data2.getValue().toString());
                            if(i != 0 )
                                ch1TotVal = ch1TotVal/2;
                            i++;
                        }
                        if(data2.getKey().equals("Chapter 2")) {
                            ch2TotVal = ch2TotVal + Integer.parseInt(data2.getValue().toString());
                            if(j != 0 )
                                ch2TotVal = ch2TotVal/2;
                            j++;
                        }
                        if(data2.getKey().equals("Chapter 3")) {
                            ch3TotVal = ch3TotVal + Integer.parseInt(data2.getValue().toString());
                            if(k != 0 )
                                ch3TotVal = ch3TotVal/2;
                            k++;
                        }
                        if(data2.getKey().equals("Chapter 4")) {
                            ch4TotVal = ch4TotVal + Integer.parseInt(data2.getValue().toString());
                            if(m != 0 )
                                ch4TotVal = ch4TotVal/2;
                            m++;
                        }
                        if(data2.getKey().equals("Chapter Revision")) {
                            chRTotVal = chRTotVal + Integer.parseInt(data2.getValue().toString());
                            if(n != 0 )
                                chRTotVal = chRTotVal/2;
                            n++;
                        }
                       if(i == dataSnapshot.getChildrenCount()) {
                           isDone = true;
                       }
                    }
                }
                if(isDone) {
                    chapterStats.add(new PieEntry(ch1TotVal, labels.get(0)));
                    chapterStats.add(new PieEntry(ch2TotVal, labels.get(1)));
                    chapterStats.add(new PieEntry(ch3TotVal, labels.get(2)));
                    chapterStats.add(new PieEntry(ch4TotVal, labels.get(3)));
                    chapterStats.add(new PieEntry(chRTotVal, labels.get(4)));
                }

                PieDataSet dataset = new PieDataSet(chapterStats, "");
                dataset.setValueTextSize(20);
                PieData data = new PieData(dataset);
                dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
                pieChart1.setData(data);
                pieChart1.animateY(800);
                pieChart1.setCenterText("Επιδόσεις μαθητών/κεφάλαιο");
                pieChart1.setDescription(null);
                /*
                Description description = new Description();
                description.setTextColor(ColorTemplate.VORDIPLOM_COLORS[2]);
                description.setText("Chart Data"); chartView.setDescription(description);
                */
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
































package com.michalis.ekpaideytikologismiko;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;
public class QuestionsHolders extends RecyclerView.ViewHolder  {
    TextView question_item, answer_item, chapter_item;
    public int position;
    public List<Questions> questionsList;

    public QuestionsHolders(final View itemView, final List<Questions> questionsList) {
        super(itemView);
        this.questionsList = questionsList;
        chapter_item = (TextView) itemView.findViewById(R.id.chapter_item);
        question_item = (TextView) itemView.findViewById(R.id.question_item);
        answer_item = (TextView) itemView.findViewById(R.id.answer_item);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),AddQuestionsActivity.class);
                i.putExtra("question", questionsList.get(position));
                v.getContext().startActivity(i);
            }
        });
    }
}

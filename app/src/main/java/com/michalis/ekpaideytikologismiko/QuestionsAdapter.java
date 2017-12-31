package com.michalis.ekpaideytikologismiko;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsHolders> {
    public List<Questions> questionsList;
    protected Context context;

    public QuestionsAdapter(Context context, List<Questions> questionsList) {
        this.questionsList = questionsList;
        this.context = context;
    }

    @Override
    public QuestionsHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        QuestionsHolders viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        viewHolder = new QuestionsHolders(layoutView, questionsList);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(QuestionsHolders holder, int position) {
        QuestionsHolders viewHolder = (QuestionsHolders) holder;
        viewHolder.position = position;
        Questions questions = questionsList.get(position);
        ((QuestionsHolders) holder).chapter_item.setText(questions.getChapter());
        ((QuestionsHolders) holder).question_item.setText(questions.getQuestion());
        ((QuestionsHolders) holder).answer_item.setText(questions.getAnswer());
    }

    @Override
    public int getItemCount() {
        return this.questionsList.size();
    }

}

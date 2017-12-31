package com.michalis.ekpaideytikologismiko;

//implements Serializable //TODO

import java.io.Serializable;
import java.util.HashMap;

public class Questions implements Serializable {

    private String chapter;
    private String question;
    private String answer;
    private String questionID;


    public Questions() {
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestionID() { return questionID;}

    public void setQuestionID(String questionID) { this.questionID = questionID;}

    public HashMap<String,String> toFirebaseObject() {
        HashMap<String,String> questions =  new HashMap<String,String>();
        questions.put("chapter", chapter);
        questions.put("question", question);
        questions.put("answer", answer);
        questions.put("questionID", questionID);
        return questions;
    }
}

package org.skydream.quizcraft;

public class ActiveQuestion {
    private final Question question;
    private final long startTime;

    public ActiveQuestion(Question question, long startTime) {
        this.question = question;
        this.startTime = startTime;
    }

    public Question getQuestion() { return question; }
    public long getStartTime() { return startTime; }
}
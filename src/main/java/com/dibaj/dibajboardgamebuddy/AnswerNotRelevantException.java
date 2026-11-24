package com.dibaj.dibajboardgamebuddy;

public class AnswerNotRelevantException extends RuntimeException {

    public AnswerNotRelevantException(String question, String answer) {
        super("The generated answer is not relevant to the question: " + question);
    }
}

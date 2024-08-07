package com.mpteam1.exception.custom.exception;

public class QuizAlreadySubmittedException extends Exception {
    public QuizAlreadySubmittedException(String message){
        super(message);
    }
}

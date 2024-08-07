package com.mpteam1.exception.custom.exception;

public class QuestionNotBelongedToQuizException extends Exception {
    public QuestionNotBelongedToQuizException(String message) {
        super(message);
    }
}

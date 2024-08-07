package com.mpteam1.exception.custom.exception;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/9/2024, Tue
 **/


public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }
}

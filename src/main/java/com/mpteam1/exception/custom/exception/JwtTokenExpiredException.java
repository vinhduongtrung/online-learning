package com.mpteam1.exception.custom.exception;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/10/2024, Wed
 **/


public class JwtTokenExpiredException extends RuntimeException{
    public JwtTokenExpiredException(String message) {
        super(message);
    }
}

package com.mpteam1.exception.custom.exception;

public class FilesNotFoundException extends RuntimeException{
    public FilesNotFoundException(String message) {
        super(message);
    }
}

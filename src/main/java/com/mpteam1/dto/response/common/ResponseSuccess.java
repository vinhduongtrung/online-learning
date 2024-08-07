package com.mpteam1.dto.response.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ResponseSuccess {
    private int statusCode;
    private String message;

    public ResponseSuccess(HttpStatus httpStatus, String message) {
        this.statusCode = httpStatus.value();
        this.message = message;
    }
}

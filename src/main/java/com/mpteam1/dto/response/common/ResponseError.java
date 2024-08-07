package com.mpteam1.dto.response.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/10/2024, Wed
 **/

@Getter
@Setter
public class ResponseError {
    private int statusCode;
    private String errorMessage;

    public ResponseError(HttpStatus httpStatus, String message) {
        this.statusCode = httpStatus.value();
        this.errorMessage = message;
    }
}

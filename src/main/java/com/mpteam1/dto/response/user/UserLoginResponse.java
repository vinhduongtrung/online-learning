package com.mpteam1.dto.response.user;

import lombok.Getter;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/9/2024, Tue
 **/


@Getter
public class UserLoginResponse {
    private String accessToken;
    private String refreshToken;

    public UserLoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}

package com.mpteam1.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.io.IOException;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/9/2024, Tue
 **/


public interface TokenAuthenticationService {
    long getRefreshTokenExpiration();
    String generateAccessToken(String email);
    String generateRefreshToken();
    String generateForgotPasswordToken(String email);
    String getEmail(String jwt);
    void checkExpirationDate(String jwt);
    Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException;
    String getHEADER_STRING();
}

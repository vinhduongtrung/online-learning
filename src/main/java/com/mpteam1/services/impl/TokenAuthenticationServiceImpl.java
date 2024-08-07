package com.mpteam1.services.impl;

import com.mpteam1.exception.custom.exception.JwtTokenExpiredException;
import com.mpteam1.services.TokenAuthenticationService;
import com.mpteam1.utils.Constants;
import com.mpteam1.utils.Messages;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/9/2024, Tue
 **/


@Component
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {
    private final UserDetailsService userDetailsService;
    @Value("${token.header}")
    private String HEADER_STRING;
    @Value("${token.secret}")
    private String SECRET;
    @Value("${token.expiration.refreshToken}")
    private long REFRESH_TOKEN_EXPIRATION;
    @Value("${token.expiration.accessToken}")
    private int ACCESS_TOKEN_EXPIRATION;
    @Value("${token.expiration.forgotPasswordToken}")
    private int FORGOT_PASSWORD_TOKEN_EXPIRATION;

    @Override
    public long getRefreshTokenExpiration() {
        return REFRESH_TOKEN_EXPIRATION;
    }

    @Override
    public String generateAccessToken(String email) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(Constants.HCM_LOCAL_TIME));
        c.add(Calendar.SECOND, ACCESS_TOKEN_EXPIRATION);
        Date currentDateInHoChiMinh = c.getTime();
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(currentDateInHoChiMinh)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    @Override
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String generateForgotPasswordToken(String email) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(Constants.HCM_LOCAL_TIME));
        c.add(Calendar.SECOND, FORGOT_PASSWORD_TOKEN_EXPIRATION);
        Date currentDateInHoChiMinh = c.getTime();
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(currentDateInHoChiMinh)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    @Override
    public String getEmail(String jwt) {
        String email;
        try {
            email = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(jwt)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new JwtTokenExpiredException(Messages.INVALID_TOKEN);
        }
        return email;
    }

    @Override
    public void checkExpirationDate(String jwt) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(jwt)
                    .getBody()
                    .getExpiration();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new JwtTokenExpiredException(Messages.EXPIRED_ACCESS_TOKEN);
        }
    }

    @Override
    public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String jwt = request.getHeader(HEADER_STRING);
        Authentication authentication = null;
        try {
            if (jwt != null) {
                // Extract email from token
                String email = getEmail(jwt);

                if (email != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.getWriter().write(Messages.EXPIRED_ACCESS_TOKEN);
        }
        return authentication;
    }

    @Override
    public String getHEADER_STRING() {
        return HEADER_STRING;
    }
}
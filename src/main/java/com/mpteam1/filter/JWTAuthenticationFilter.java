package com.mpteam1.filter;

import com.mpteam1.services.TokenAuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static com.mpteam1.utils.Constants.PUBLIC_URL;
import static com.mpteam1.utils.Constants.SWAGGER_URL;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/9/2024, Tue
 **/


@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final TokenAuthenticationService tokenAuthenticationService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // Bypass public uri
        if (Arrays.asList(PUBLIC_URL).contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getRequestURI().startsWith("/ws/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bypass swagger
        if (Arrays.asList(SWAGGER_URL).contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check if token is existed
        Authentication authentication = tokenAuthenticationService.getAuthentication(request, response);
        if (authentication == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}

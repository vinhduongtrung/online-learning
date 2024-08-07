package com.mpteam1.config;


import com.mpteam1.filter.JWTAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import static com.mpteam1.entities.enums.ERole.*;
import static com.mpteam1.utils.Constants.*;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/9/2024, Tue
 **/


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;
    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * Restrict access to specific urls
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF protection
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS protection
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // Configure authorization rules for HTTP requests
                .authorizeHttpRequests(authorize ->
                        authorize
                                // Permit access to public URIs specified by PUBLIC_URI
                                .requestMatchers(PUBLIC_URL).permitAll()

                                // Permit all access to swagger
                                .requestMatchers(SWAGGER_URL).permitAll()

                                // Permit access to all roles
                                .requestMatchers(PERMIT_ALL_ROLE).hasAnyAuthority(TEACHER.name(), ADMIN.name(), STUDENT.name())

                                // Restrict access to student, teacher, and admin
                                .requestMatchers(STUDENT_RESTRICTION).hasAnyAuthority(STUDENT.name(), TEACHER.name(), ADMIN.name())

                                // Restrict access to admin, teacher
                                .requestMatchers(TEACHER_RESTRICTION).hasAnyAuthority(TEACHER.name(), ADMIN.name())

                                // Restrict access to admin only
                                .requestMatchers(ADMIN_RESTRICTION).hasAuthority(ADMIN.name())

                                // Authenticate any other requests
                                .anyRequest().authenticated()
                )

                // Set custom authentication provider
                .authenticationProvider(authenticationProvider)

                // Add custom authentication filter to filter chain
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Configure exception handling for unauthorized and access denied cases
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .build();
    }
}


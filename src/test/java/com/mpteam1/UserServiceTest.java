package com.mpteam1;

import com.mpteam1.dto.request.user.UserLoginRequest;
import com.mpteam1.dto.response.user.UserLoginResponse;
import com.mpteam1.entities.Token;
import com.mpteam1.entities.User;
import com.mpteam1.exception.custom.exception.UserNotFoundException;
import com.mpteam1.repository.TokenRepository;
import com.mpteam1.repository.UserRepository;
import com.mpteam1.services.TokenAuthenticationService;
import com.mpteam1.services.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/11/2024, Thu
 **/


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private TokenAuthenticationService tokenAuthenticationService;

    private static final String TEST_EMAIL = "test@mock.com";
    private static final String TEST_PASSWORD = "123456";


    @Test()
    public void whenLoginWithNotExistingUser_shouldThrowException() {
        // Define repository behaviour
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // Call service method
        assertThrows(UserNotFoundException.class, () -> {
            UserLoginRequest userLoginRequest = new UserLoginRequest();
            userLoginRequest.setEmail(TEST_EMAIL);
            userService.login(userLoginRequest, new MockHttpServletResponse());
        });
    }

    @Test
    public void whenLoginWithValidCredentials_shouldNotThrowException() {
        // 1. Create request and user mock
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);

        User mockUser = new User();
        mockUser.setEmail(TEST_EMAIL);
        mockUser.setPassword("$2a$10$J9hhh2h1BvWUNOEWuVhdPebfC.eAq9CebhrxCNxTa7559nSGKi9Ni");

        Token token = Token.builder().accessToken("mockAccessToken").refreshToken("mockRefreshToken").build();

        // 2. Configure mocks behavior
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(tokenAuthenticationService.generateAccessToken(TEST_EMAIL)).thenReturn("mockAccessToken");
        when(tokenAuthenticationService.generateRefreshToken()).thenReturn("mockRefreshToken");
        when(tokenRepository.save(token)).thenReturn(token);

        // 3. Call method
        UserLoginResponse response = userService.login(request, new MockHttpServletResponse());

        // 4. Verify outcome
        assertTrue(bCryptPasswordEncoder.matches(TEST_PASSWORD, mockUser.getPassword()));
        assertNotNull(response);
        assertEquals("mockAccessToken", response.getAccessToken());
        assertEquals("mockRefreshToken", response.getRefreshToken());
    }

    @Test
    public void whenLogoutWithExistingToken_shouldDeleteToken() {
        // Create request
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        // Define expected token
        String expectedToken = "fakeToken";

        // Define service and repository behaviour
        when(mockRequest.getHeader(anyString())).thenReturn(expectedToken);
        when(tokenAuthenticationService.getHEADER_STRING()).thenReturn("HeaderString");

        // Call service method
        userService.logout(mockRequest);

        // Verify the deleteByAccessToken method was called on the repository
        verify(tokenRepository, times(1)).deleteByAccessToken(expectedToken);
    }
}

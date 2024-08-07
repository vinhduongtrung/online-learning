package com.mpteam1.services.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.mpteam1.dto.request.token.RefreshTokenRequest;
import com.mpteam1.dto.request.user.UserPasswordForgotRequest;
import com.mpteam1.dto.request.user.UserLoginRequest;
import com.mpteam1.dto.request.user.UserPasswordChangeRequest;
import com.mpteam1.dto.request.user.UserPasswordResetRequest;
import com.mpteam1.dto.response.common.ResponseSuccess;
import com.mpteam1.dto.response.user.UserDetailDTOResponse;
import com.mpteam1.dto.response.user.UserLoginResponse;
import com.mpteam1.dto.response.user.UserRoleResponse;
import com.mpteam1.entities.Token;
import com.mpteam1.entities.User;
import com.mpteam1.entities.enums.EEmailStatus;
import com.mpteam1.entities.enums.ETokenType;
import com.mpteam1.exception.custom.exception.InvalidCredentialsException;
import com.mpteam1.exception.custom.exception.JwtTokenExpiredException;
import com.mpteam1.exception.custom.exception.TokenNotFoundException;
import com.mpteam1.exception.custom.exception.UserNotFoundException;
import com.mpteam1.repository.TokenRepository;
import com.mpteam1.repository.UserRepository;

import com.mpteam1.services.EmailService;
import com.mpteam1.services.FirebaseService;
import com.mpteam1.services.UserService;
import com.mpteam1.services.TokenAuthenticationService;
import com.mpteam1.utils.Constants;
import com.mpteam1.utils.Messages;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/9/2024, Tue
 **/


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenAuthenticationService tokenAuthenticationService;
    private final EmailService emailService;
    private final FirebaseService firebaseService;
    String googleClientId;
    String imageUrl;

    @Override
    public UserLoginResponse login(UserLoginRequest userLoginRequest, HttpServletResponse response) {
        // Check if email is existed
        User user = userRepository.findByEmail(userLoginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND));

        // Check if user or password does not match
        if (!bCryptPasswordEncoder.matches(userLoginRequest.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException(Messages.INVALID_CREDENTIAL);

        // Generate access token and refresh token
        Token token = tokenRepository.save(Token.builder()
                .email(user.getEmail())
                .type(ETokenType.BEARER)
                .accessToken(tokenAuthenticationService.generateAccessToken(user.getEmail()))
                .refreshToken(tokenAuthenticationService.generateRefreshToken())
                .refreshExpirationDate(LocalDateTime.now(ZoneId.of(Constants.HCM_LOCAL_TIME))
                        .plusSeconds(tokenAuthenticationService.getRefreshTokenExpiration()))
                .user(user)
                .build());
        return new UserLoginResponse(token.getAccessToken(), token.getRefreshToken());
    }

    @Override
    public UserLoginResponse loginByGoogle(String credential) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
        try {
            GoogleIdToken idToken = verifier.verify(credential);
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND));
            String accessToken = tokenAuthenticationService.generateAccessToken(user.getEmail());
            String refreshToken = tokenAuthenticationService.generateRefreshToken();
            tokenRepository.save(Token.builder()
                    .email(user.getEmail())
                    .type(ETokenType.BEARER)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .refreshExpirationDate(LocalDateTime.now(ZoneId.of(Constants.HCM_LOCAL_TIME))
                            .plusSeconds(tokenAuthenticationService.getRefreshTokenExpiration()))
                    .user(user)
                    .build());
            return new UserLoginResponse(accessToken, refreshToken);
        } catch (GeneralSecurityException | IOException e) {
            throw new Exception(e);
        }
    }

    @Override
    public List<UserDetailDTOResponse> getAll() {
        return userRepository.findAll().stream().map(UserDetailDTOResponse::fromUser).toList();
    }

    @Override
    @Transactional
    public void logout(HttpServletRequest httpServletRequest) {
        tokenRepository.deleteByAccessToken(httpServletRequest.getHeader(tokenAuthenticationService.getHEADER_STRING()));
    }

    @Override
    public UserLoginResponse extendToken(RefreshTokenRequest refreshTokenRequest) {
        // Check if refresh token is existed
        Token token = tokenRepository.findByRefreshToken(refreshTokenRequest.getRefreshToken())
                .orElseThrow(() -> new TokenNotFoundException(Messages.INVALID_TOKEN));

        // Check if refresh token has expired
        if (LocalDateTime.now(ZoneId.of(Constants.HCM_LOCAL_TIME)).isAfter(token.getRefreshExpirationDate())) {
            tokenRepository.delete(token);
            throw new JwtTokenExpiredException(Messages.EXPIRED_REFRESH_TOKEN);
        }

        // Generate access token and refresh token
        Token expected = tokenRepository.save(Token.builder()
                .id(token.getId())
                .email(token.getEmail())
                .type(ETokenType.BEARER)
                .accessToken(tokenAuthenticationService.generateAccessToken(token.getEmail()))
                .refreshToken(tokenAuthenticationService.generateRefreshToken())
                .refreshExpirationDate(LocalDateTime.now().plusSeconds(tokenAuthenticationService.getRefreshTokenExpiration()))
                .user(token.getUser())
                .build());
        return new UserLoginResponse(expected.getAccessToken(), expected.getRefreshToken());
    }

    @Override
    public UserRoleResponse getRole() {
        return userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .map(user -> new UserRoleResponse(user.getId(), user.getEmail(), user.getFullName(), user.getERole().toString(), user.getAvatarName() != null && !user.getAvatarName().isEmpty() ? String.format(imageUrl, user.getAvatarName()) : null)
                ).orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND));

    }

    @Override
    public ResponseSuccess forgotPassword(UserPasswordForgotRequest userPasswordForgotRequest) {
        // Check if email is existed
        User user = userRepository.findByEmail(userPasswordForgotRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND));

        // Generate token for user
        String forgotPasswordToken = tokenAuthenticationService.generateForgotPasswordToken(user.getEmail());
        tokenRepository.save(Token.builder()
                .email(user.getEmail())
                .type(ETokenType.BEARER)
                .accessToken(forgotPasswordToken)
                .user(user)
                .build());
        emailService.sendHtmlMessage(EEmailStatus.RESTORE, user.getEmail(), Messages.FORGOT_PASSWORD_REQUEST, forgotPasswordToken);
        return new ResponseSuccess(HttpStatus.OK, Messages.SUCCESS);
    }

    @Override
    public ResponseSuccess checkAccessTokenExpirationDate(String accessToken) {
        // Check if access token had expired will throw JwtTokenExpiredException
        tokenAuthenticationService.checkExpirationDate(accessToken);
        return new ResponseSuccess(HttpStatus.OK, Messages.VALID_TOKEN);
    }

    @Override
    @Transactional
    public ResponseSuccess resetPassword(UserPasswordResetRequest userPasswordResetRequest) {
        // Check if access token had expired will throw JwtTokenExpiredException
        tokenAuthenticationService.checkExpirationDate(userPasswordResetRequest.getToken());

        // Check if email is existed
        User user = userRepository.findByEmail(tokenAuthenticationService.getEmail(userPasswordResetRequest.getToken()))
                .orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND));

        // Update new password
        user.setPassword(bCryptPasswordEncoder.encode(userPasswordResetRequest.getPassword()));
        userRepository.save(user);
        return new ResponseSuccess(HttpStatus.OK, Messages.SUCCESS);
    }

    @Override
    public ResponseSuccess changePassword(UserPasswordChangeRequest userPasswordChangeRequest) {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Check if user is existed
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND));

        // Check if old password does not match
        if (!bCryptPasswordEncoder.matches(userPasswordChangeRequest.getOldPassword(), user.getPassword()))
            throw new InvalidCredentialsException(Messages.OLD_PASSWORD_NOT_MATCH);

        // Update new password
        user.setPassword(bCryptPasswordEncoder.encode(userPasswordChangeRequest.getNewPassword()));
        userRepository.save(user);
        return new ResponseSuccess(HttpStatus.OK, Messages.SUCCESS);
    }

    @Override
    public ResponseSuccess updateAvatar(MultipartFile file) throws IOException {
        String name = firebaseService.save(file);
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND));
        if (user.getAvatarName() != null && !user.getAvatarName().isEmpty()) {
            firebaseService.delete(user.getAvatarName());
        }
        user.setAvatarName(name);
        userRepository.save(user);
        return new ResponseSuccess(HttpStatus.OK, Messages.SUCCESS);
    }


}


package com.mpteam1.services;

import com.mpteam1.dto.request.token.RefreshTokenRequest;
import com.mpteam1.dto.request.user.UserPasswordForgotRequest;
import com.mpteam1.dto.request.user.UserLoginRequest;
import com.mpteam1.dto.request.user.UserPasswordChangeRequest;
import com.mpteam1.dto.request.user.UserPasswordResetRequest;
import com.mpteam1.dto.response.common.ResponseSuccess;
import com.mpteam1.dto.response.user.UserDetailDTOResponse;
import com.mpteam1.dto.response.user.UserLoginResponse;
import com.mpteam1.dto.response.user.UserRoleResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.io.IOException;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/9/2024, Tue
 **/


public interface UserService {
    UserLoginResponse login(UserLoginRequest userLoginRequest, HttpServletResponse response);
    void logout(HttpServletRequest httpServletRequest);
    UserLoginResponse extendToken(RefreshTokenRequest refreshTokenRequest);
    UserRoleResponse getRole();
    ResponseSuccess forgotPassword(UserPasswordForgotRequest userPasswordForgotRequest);
    ResponseSuccess checkAccessTokenExpirationDate(String accessToken);
    ResponseSuccess resetPassword(UserPasswordResetRequest userPasswordResetRequest);
    ResponseSuccess changePassword(UserPasswordChangeRequest userPasswordChangeRequest);
    ResponseSuccess updateAvatar(MultipartFile file) throws IOException;
    UserLoginResponse loginByGoogle(String credential) throws Exception;

    List<UserDetailDTOResponse> getAll();
}

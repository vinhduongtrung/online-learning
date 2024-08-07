package com.mpteam1.rest;

import com.mpteam1.dto.request.token.RefreshTokenRequest;
import com.mpteam1.dto.request.user.UserPasswordForgotRequest;
import com.mpteam1.dto.request.user.UserLoginRequest;

import com.mpteam1.dto.request.user.UserPasswordChangeRequest;
import com.mpteam1.dto.request.user.UserPasswordResetRequest;
import com.mpteam1.services.UserService;
import com.mpteam1.utils.Api;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/9/2024, Tue
 **/


@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(Api.User.LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest userLoginRequest, HttpServletResponse response){
        return ResponseEntity.ok().body(userService.login(userLoginRequest, response));
    }

    @PostMapping(Api.User.LOGIN_BY_GOOGLE)
    public ResponseEntity<?> loginByGoogle(@RequestParam("credential") String credential) throws Exception {
        return ResponseEntity.ok().body(userService.loginByGoogle(credential));
    }

    @PostMapping(Api.User.LOGOUT)
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest httpServletRequest){
        userService.logout(httpServletRequest);
    }

    @PostMapping(Api.User.TOKEN)
    public ResponseEntity<?> extendToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.ok().body(userService.extendToken(refreshTokenRequest));
    }

    @PostMapping(Api.User.ROLE)
    public ResponseEntity<?> getRole(){
        return ResponseEntity.ok().body(userService.getRole());
    }

    @PostMapping(Api.User.FORGOT_PASSWORD)
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody UserPasswordForgotRequest userPasswordForgotRequest){
        return ResponseEntity.ok().body(userService.forgotPassword(userPasswordForgotRequest));
    }

    @PostMapping(Api.User.RESET_PASSWORD)
    public ResponseEntity<?> resetPassword(@Valid @RequestBody UserPasswordResetRequest userPasswordResetRequest){
        return ResponseEntity.ok().body(userService.resetPassword(userPasswordResetRequest));
    }

    @PostMapping(Api.User.CHECK_TOKEN_EXPIRATION)
    public ResponseEntity<?> checkAccessTokenExpirationDate(@RequestParam String token){
        return ResponseEntity.ok().body(userService.checkAccessTokenExpirationDate(token));
    }

    @PostMapping(Api.User.CHANGE_PASSWORD)
    public ResponseEntity<?> changePassword(@Valid @RequestBody UserPasswordChangeRequest userPasswordChangeRequest){
        return ResponseEntity.ok().body(userService.changePassword(userPasswordChangeRequest));
    }

    @PostMapping(value = Api.User.UPDATE_AVATAR, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@RequestParam(name = "file") MultipartFile file) throws IOException {
        return ResponseEntity.ok().body(userService.updateAvatar(file));
    }

    @GetMapping(Api.User.GET_ALL)
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok().body(userService.getAll());
    }
}

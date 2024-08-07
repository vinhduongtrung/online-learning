package com.mpteam1.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/9/2024, Tue
 **/


@Getter
@Setter
public class UserLoginRequest {
    @Email(message = "{email.message}")
    @NotBlank(message = "{email.message}")
    private String email;
    @NotBlank(message = "{password.message}")
    private String password;
}

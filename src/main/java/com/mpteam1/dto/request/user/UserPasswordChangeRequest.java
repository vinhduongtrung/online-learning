package com.mpteam1.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/13/2024, Sat
 **/


@Getter
@Setter
public class UserPasswordChangeRequest {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String newPassword;
}

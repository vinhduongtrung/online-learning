package com.mpteam1.dto.request.token;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/10/2024, Wed
 **/


@Getter
@Setter
public class RefreshTokenRequest {
    @NotBlank(message = "{refreshToken.message}")
    private String refreshToken;
}

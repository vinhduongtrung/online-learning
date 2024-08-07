package com.mpteam1.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/10/2024, Wed
 **/


@Getter
@AllArgsConstructor
public class UserRoleResponse {
    private long id;
    private String email;
    private String fullName;
    private String role;
    private String avatarUrl;
}

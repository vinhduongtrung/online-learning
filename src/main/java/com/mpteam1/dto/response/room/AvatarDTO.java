package com.mpteam1.dto.response.room;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AvatarDTO {
    private String avatarUrl;
    private Long userId;
    private String userFullName;
}

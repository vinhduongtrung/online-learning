package com.mpteam1.dto.request.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CreateMessageDTO {
    private Long roomId;
    private String content;
    private Long senderId;
}

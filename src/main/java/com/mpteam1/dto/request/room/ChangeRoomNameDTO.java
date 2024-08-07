package com.mpteam1.dto.request.room;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRoomNameDTO {
    private Long roomId;
    private String name;
}

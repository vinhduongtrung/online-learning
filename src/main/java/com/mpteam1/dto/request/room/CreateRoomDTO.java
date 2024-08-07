package com.mpteam1.dto.request.room;

import lombok.*;

import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomDTO {
    private List<Long> userIds;
}

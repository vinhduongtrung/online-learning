package com.mpteam1.dto.response.room;


import com.mpteam1.entities.Room;
import com.mpteam1.entities.enums.ERoomType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Builder
@Getter
@Setter
public class RoomDTO {
    private Long id;
    private String name;
    private String type;
    private List<AvatarDTO> avatars;

    public static RoomDTO toDTO(Room room) {
        List<AvatarDTO> avatars = null;
        avatars = room.getUsers().stream().map(user -> {
            if (user.getAvatarName() == null || user.getAvatarName().isEmpty()) return AvatarDTO.builder()
                    .userId(user.getId())
                    .avatarUrl(null)
                    .userFullName(user.getFullName())
                    .build();
            return AvatarDTO.builder()
                    .userId(user.getId())
                    .avatarUrl("https://storage.googleapis.com/test-firebase-storage-d1b61.appspot.com/" + user.getAvatarName())
                    .userFullName(user.getFullName())
                    .build();
        }).toList();
        return RoomDTO.builder()
                .id(room.getId())
                .name(room.getName())
                .type(String.valueOf(room.getType()))
                .avatars(avatars)
                .build();
    }
}

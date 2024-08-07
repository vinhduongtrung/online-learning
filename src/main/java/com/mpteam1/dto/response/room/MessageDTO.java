package com.mpteam1.dto.response.room;

import com.mpteam1.dto.response.user.UserDetailDTOResponse;
import com.mpteam1.entities.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Builder
@Getter
@Setter
public class MessageDTO {
    private Long id;
    private String content;
    private Instant timeCreated;
    private RoomDTO room;
    private UserDetailDTOResponse sender;


    public static MessageDTO toDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .timeCreated(message.getTimeCreated())
                .room(RoomDTO.toDTO(message.getRoom()))
                .sender(UserDetailDTOResponse.fromUser(message.getSender()))
                .build();
    }
}

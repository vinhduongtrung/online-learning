package com.mpteam1.services;

import com.google.firebase.database.core.view.Change;
import com.mpteam1.dto.request.room.ChangeRoomNameDTO;
import com.mpteam1.dto.request.room.CreateMessageDTO;
import com.mpteam1.dto.request.room.CreateRoomDTO;
import com.mpteam1.dto.response.common.ResponseSuccess;
import com.mpteam1.dto.response.room.MessageDTO;
import com.mpteam1.dto.response.room.RoomDTO;

import java.util.*;

public interface RoomService {
    RoomDTO create(CreateRoomDTO createRoomDTO);
    MessageDTO createMessage(CreateMessageDTO createMessageDTO);
    List<MessageDTO> getAllMessagesInRoom(Long roomId);
    List<RoomDTO> getAllRoomsOfUser(Long userId);

    ResponseSuccess changeRoomName(ChangeRoomNameDTO changeRoomNameDTO);
}

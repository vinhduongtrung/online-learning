package com.mpteam1.rest;

import com.mpteam1.dto.request.room.ChangeRoomNameDTO;
import com.mpteam1.dto.request.room.CreateMessageDTO;
import com.mpteam1.dto.request.room.CreateRoomDTO;
import com.mpteam1.dto.response.room.MessageDTO;
import com.mpteam1.entities.Room;
import com.mpteam1.entities.User;
import com.mpteam1.repository.RoomRepository;
import com.mpteam1.services.RoomService;
import com.mpteam1.utils.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@AllArgsConstructor
public class RoomController {
    private RoomService roomService;
    private SimpMessagingTemplate simpMessagingTemplate;
    private RoomRepository roomRepository;

    @MessageMapping("/message")
    public void receiveMessage(@Payload CreateMessageDTO createMessageDTO){
        MessageDTO msgResponse = roomService.createMessage(createMessageDTO);
        Room room = roomRepository.findById(createMessageDTO.getRoomId()).orElse(null);
        if(room!= null) {
            List<User> members = room.getUsers();
            members.forEach(member -> {
                simpMessagingTemplate.convertAndSendToUser(String.valueOf(member.getId()), "private",msgResponse);
            });
        }
    }

    @PostMapping(Api.Room.CREATE)
    public ResponseEntity<?> create(@RequestBody CreateRoomDTO createRoomDTO) {
        return ResponseEntity.ok(roomService.create(createRoomDTO));
    }

    @GetMapping(Api.Room.GET_ALL_MSG_IN_ROOM + "/{roomId}")
    public ResponseEntity<?> getAllMsgInRoom(@PathVariable("roomId") Long roomId) {
        return ResponseEntity.ok(roomService.getAllMessagesInRoom(roomId));
    }

    @GetMapping(Api.Room.GET_ALL_ROOMS_OF_USER + "/{userId}")
    public ResponseEntity<?> getAllRoomsOfUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(roomService.getAllRoomsOfUser(userId));
    }

    @PostMapping(Api.Room.CHANGE_ROOM_NAME)
    public ResponseEntity<?> changeRoomName(@RequestBody ChangeRoomNameDTO changeRoomNameDTO) {
        return ResponseEntity.ok(roomService.changeRoomName(changeRoomNameDTO));
    }
}

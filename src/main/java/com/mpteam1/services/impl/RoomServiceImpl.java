package com.mpteam1.services.impl;

import com.mpteam1.dto.request.room.ChangeRoomNameDTO;
import com.mpteam1.dto.request.room.CreateMessageDTO;
import com.mpteam1.dto.request.room.CreateRoomDTO;
import com.mpteam1.dto.response.common.ResponseSuccess;
import com.mpteam1.dto.response.room.MessageDTO;
import com.mpteam1.dto.response.room.RoomDTO;
import com.mpteam1.entities.Message;
import com.mpteam1.entities.Room;
import com.mpteam1.entities.User;
import com.mpteam1.entities.enums.ERoomType;
import com.mpteam1.repository.MessageRepository;
import com.mpteam1.repository.RoomRepository;
import com.mpteam1.repository.UserRepository;
import com.mpteam1.services.RoomService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class RoomServiceImpl implements RoomService {
    private UserRepository userRepository;
    private RoomRepository roomRepository;
    private MessageRepository messageRepository;

    @Override
    public RoomDTO create(CreateRoomDTO createRoomDTO) {
        if (createRoomDTO.getUserIds().size() == 2) {
            Room existRoom = roomRepository.findSingleRoomWithUserIds(createRoomDTO.getUserIds().get(0), createRoomDTO.getUserIds().get(1)).orElse(null);
            if (existRoom != null) throw new RuntimeException("This room already exists");
        }
        List<User> users = userRepository.findAllById(createRoomDTO.getUserIds());
        Room room = Room.builder()
                .name(String.join(",", users.stream().map(User::getFullName).toList()))
                .type(users.size() > 2 ? ERoomType.MULTIPLE : ERoomType.SINGLE)
                .users(users)
                .build();
        users.forEach(user -> {
            user.getRooms().add(room);
        });
        roomRepository.save(room);
        return RoomDTO.toDTO(room);
    }

    @Override
    public MessageDTO createMessage(CreateMessageDTO createMessageDTO) {
        Room room = roomRepository.findById(createMessageDTO.getRoomId()).orElseThrow(() -> new RuntimeException("Room not found"));
        User sender = userRepository.findById(createMessageDTO.getSenderId()).orElseThrow(() -> new RuntimeException("Sender not found"));
        Message message = Message.builder()
                .content(createMessageDTO.getContent())
                .sender(sender)
                .room(room)
                .build();
        room.getMessages().add(message);
        messageRepository.save(message);
        return MessageDTO.toDTO(message);
    }

    @Override
    public List<MessageDTO> getAllMessagesInRoom(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        return room.getMessages().stream().map(MessageDTO::toDTO).toList();
    }

    @Override
    public List<RoomDTO> getAllRoomsOfUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<Room> rooms = roomRepository.findRoomsByUserSortedByNewestMessage(user);
        return rooms.stream().map(RoomDTO::toDTO).toList();
    }

    @Override
    public ResponseSuccess changeRoomName(ChangeRoomNameDTO changeRoomNameDTO) {
        Room room = roomRepository.findById(changeRoomNameDTO.getRoomId()).orElseThrow(() -> new RuntimeException("Room not found"));
        room.setName(changeRoomNameDTO.getName());
        roomRepository.save(room);
        return new ResponseSuccess(HttpStatus.OK, "Change room name success");
    }
}

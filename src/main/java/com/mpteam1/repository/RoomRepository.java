package com.mpteam1.repository;

import com.mpteam1.entities.Room;
import com.mpteam1.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT DISTINCT r FROM Room r " +
            "JOIN r.users u " +
            "LEFT JOIN Message m ON m.room = r " +
            "WHERE u = :user " +
            "GROUP BY r.id " +
            "ORDER BY MAX(m.timeCreated) DESC")
    List<Room> findRoomsByUserSortedByNewestMessage(@Param("user") User user);

    @Query("SELECT r FROM Room r " +
            "JOIN r.users u " +
            "WHERE r.type = 'SINGLE' " +
            "AND u.id IN (:userId1, :userId2) " +
            "GROUP BY r.id " +
            "HAVING COUNT(u.id) = 2")
    Optional<Room> findSingleRoomWithUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}

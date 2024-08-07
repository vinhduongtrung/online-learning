package com.mpteam1.repository;

import com.mpteam1.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/9/2024, Tue
 **/


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT MAX(u.account_name) FROM user u WHERE u.account_name REGEXP CONCAT(:accountName, '[0-9]*$')", nativeQuery = true)
    String findMaxAccountName(String accountName);
}

package com.mpteam1.repository;

import com.mpteam1.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/10/2024, Wed
 **/

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByAccessToken(String accessToken);
    Optional<Token> findByRefreshToken(String refreshToken);
    void deleteByAccessToken(String accessToken);
    void deleteByUser_Id(Long id);
}

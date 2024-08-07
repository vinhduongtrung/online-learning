package com.mpteam1.entities;

import com.mpteam1.entities.enums.ETokenType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/10/2024, Wed
 **/

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;
    @Enumerated(EnumType.STRING)
    public ETokenType type;
    private String accessToken;
    private String refreshToken;
    @CreationTimestamp
    private LocalDateTime accessTokenCreatedDate;
    private LocalDateTime refreshExpirationDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;
}

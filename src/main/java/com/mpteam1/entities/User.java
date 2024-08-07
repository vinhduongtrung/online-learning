package com.mpteam1.entities;

import com.mpteam1.entities.enums.ERole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/9/2024, Tue
 **/

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "dbo", name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "address", nullable = false)
    private String address;
    private LocalDate dateOfBirth;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    private boolean gender;
    @Enumerated(EnumType.STRING)
    private ERole eRole;
    private String avatarName;

    @CreationTimestamp
    private Instant createdDate;

    @Column(name = "account_name", nullable = false, unique = true)
    private String accountName;

    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private Set<Room> rooms = new HashSet<>();


    public User(String email, String password, String fullName, String address, LocalDate dateOfBirth, String phoneNumber, boolean gender, ERole eRole, String accountName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.eRole = eRole;
        this.accountName = accountName;
    }
}

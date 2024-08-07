package com.mpteam1.dto.response.student;

import com.mpteam1.entities.Student;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class StudentBasicInfoDTO {
    private Long id;

    private String fullName;

    private LocalDate dateOfBirth;

    private boolean gender;

    private String email;

    private String phoneNumber;

    private String address;

    private String accountName;

    public static StudentBasicInfoDTO toDTO(Student student) {
        return StudentBasicInfoDTO.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .dateOfBirth(student.getDateOfBirth())
                .gender(student.isGender())
                .email(student.getEmail())
                .phoneNumber(student.getPhoneNumber())
                .address(student.getAddress())
                .accountName(student.getAccountName())
                .build();
    }
}

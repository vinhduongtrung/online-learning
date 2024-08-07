package com.mpteam1.dto.response.teacher;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TeacherDTOResponse {
    private Long id;
    private int age;
    private String qualification;
    private String employmentStatus;
    private String startDate;
    private String email;
    private String fullName;
    private String address;
    private String dateOfBirth;
    private String phoneNumber;
    private boolean gender;
    private String role;
    private List<String> subjects;
    private String avatarUrl;
    private String accountName;
}

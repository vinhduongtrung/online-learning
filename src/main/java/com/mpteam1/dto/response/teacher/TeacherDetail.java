package com.mpteam1.dto.response.teacher;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TeacherDetail {
    private Long id;
//    private int age;
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
    private String avatarUrl;
}

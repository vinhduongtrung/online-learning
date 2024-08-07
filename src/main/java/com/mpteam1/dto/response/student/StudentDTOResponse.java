package com.mpteam1.dto.response.student;

import com.mpteam1.dto.response.clazz.ClazzAndTeacher;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentDTOResponse {
    private Long id;

    private String fullName;

    private String dateOfBirth;

    private boolean gender;

    private String email;

    private String phoneNumber;

    private String address;

    private int classCount;

    private List<ClazzAndTeacher> classes;

    private String avatarUrl;

    private String accountName;
}

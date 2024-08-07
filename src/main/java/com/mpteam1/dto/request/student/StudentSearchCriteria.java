package com.mpteam1.dto.request.student;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudentSearchCriteria {
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private String className;
    private String teacherName;
}

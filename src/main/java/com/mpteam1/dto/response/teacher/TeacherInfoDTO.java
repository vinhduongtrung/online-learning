package com.mpteam1.dto.response.teacher;

import com.mpteam1.entities.Teacher;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
public class TeacherInfoDTO {
    private Long id;
    private String name;
    private int age;
    private String accountName;

    public static TeacherInfoDTO toDTO(Teacher teacher) {
        if(teacher == null) {
            return null;
        }
        TeacherInfoDTO dto = new TeacherInfoDTO();
        dto.setId(teacher.getId());
        dto.setName(teacher.getFullName());
        dto.setAge(Period.between(teacher.getDateOfBirth(), LocalDate.now()).getYears());
        dto.setAccountName(teacher.getAccountName());
        return dto;
    }
}
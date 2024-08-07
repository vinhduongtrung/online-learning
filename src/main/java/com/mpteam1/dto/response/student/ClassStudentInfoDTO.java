package com.mpteam1.dto.response.student;

import com.mpteam1.entities.Student;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassStudentInfoDTO {
    private Long id;

    private String name;

    private String email;

    private String dateOfBirth;

    private String address;

    private boolean gender;

    public static ClassStudentInfoDTO fromStudent(Student student) {
        ClassStudentInfoDTO dto = new ClassStudentInfoDTO();
        dto.setId(student.getId());
        dto.setName(student.getFullName());
        dto.setEmail(student.getEmail());
        dto.setDateOfBirth(student.getDateOfBirth().toString());
        dto.setAddress(student.getAddress());
        dto.setGender(student.isGender());
        return dto;
    }
}

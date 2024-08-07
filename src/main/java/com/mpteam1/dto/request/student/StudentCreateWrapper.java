package com.mpteam1.dto.request.student;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentCreateWrapper {
    @Valid
    @Size(min = 1, message = "must be contain at least one student")
    private List<StudentDTOCreate> students;
}

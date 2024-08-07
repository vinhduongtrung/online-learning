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
public class StudentDTODeleteWrapper {
    @Valid
    @Size(min = 1, message = "must contain at least one id")
    List<StudentDTODelete> students;
}

package com.mpteam1.dto.request.teacher;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TeacherDTODelete {
    @NotBlank
    @Size(min = 1, message = "must contain at least one id")
    private Long id;
}

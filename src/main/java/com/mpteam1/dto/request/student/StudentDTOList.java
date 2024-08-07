package com.mpteam1.dto.request.student;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDTOList {
    @NotNull
    private Long classId;
}

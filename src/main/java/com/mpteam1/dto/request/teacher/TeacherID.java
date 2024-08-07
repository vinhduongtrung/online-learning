package com.mpteam1.dto.request.teacher;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherID {
    @NotNull
    Long teacherId;
}

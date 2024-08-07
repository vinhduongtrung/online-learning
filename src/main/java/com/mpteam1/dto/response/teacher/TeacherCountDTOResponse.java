package com.mpteam1.dto.response.teacher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherCountDTOResponse {
    private long activeTeacherCount;
    private long totalTeacherCount;
}

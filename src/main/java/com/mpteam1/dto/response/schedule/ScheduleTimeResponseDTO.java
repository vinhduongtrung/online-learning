package com.mpteam1.dto.response.schedule;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleTimeResponseDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String className;
    private String classCode;
    private String teacherName;
}

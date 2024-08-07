package com.mpteam1.dto.request.schedule;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ScheduleTimeDTO {
    private LocalDate startDate;
    private LocalDate endDate;
}

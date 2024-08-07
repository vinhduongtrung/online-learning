package com.mpteam1.services;

import com.mpteam1.dto.response.schedule.ScheduleTimeResponseDTO;

import java.util.*;
import java.time.LocalDate;

public interface ScheduleService {
    List<ScheduleTimeResponseDTO> getSchedulesForTeacher(LocalDate startDate, LocalDate endDate);

    List<ScheduleTimeResponseDTO> getSchedulesForStudent(LocalDate startDate, LocalDate endDate);

    List<ScheduleTimeResponseDTO> getSchedules(LocalDate startDate, LocalDate endDate);
}

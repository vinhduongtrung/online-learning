package com.mpteam1.dto.request.clazz;

import com.mpteam1.dto.response.schedule.ScheduleInfoDTO;
import com.mpteam1.entities.Schedule;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class ClazzDTOUpdate {
    @NotNull
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String clazzCode;
    private Long teacherId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private Set<ScheduleInfoDTO> schedules = new HashSet<>();
    private String description;
    @Max(value = 30)
    private Integer maximumCapacity;
}

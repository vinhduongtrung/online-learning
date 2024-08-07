package com.mpteam1.dto.request.clazz;


import com.mpteam1.dto.response.schedule.ScheduleInfoDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ClazzDTOCreate {
    @NotBlank
    private String name;
    @NotBlank
    private String clazzCode;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Set<ScheduleInfoDTO> schedules = new HashSet<>();
    private String description;
    @Max(value = 30)
    private Integer maximumCapacity;
}

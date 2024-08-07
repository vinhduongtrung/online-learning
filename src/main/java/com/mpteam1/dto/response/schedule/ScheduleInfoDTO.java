package com.mpteam1.dto.response.schedule;

import com.mpteam1.entities.Schedule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ScheduleInfoDTO {
    private String dayOfWeek;
    @DateTimeFormat(pattern = "HH:mm")
    private String startTime;
    @DateTimeFormat(pattern = "HH:mm")
    private String endTime;

    public ScheduleInfoDTO() {}

    public ScheduleInfoDTO(String dayOfWeek, String startTime, String endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static ScheduleInfoDTO toDTO(Schedule schedule) {
        ScheduleInfoDTO dto = new ScheduleInfoDTO();
        dto.setDayOfWeek(schedule.getDayOfWeek().toString());
        dto.setStartTime(schedule.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        dto.setEndTime(schedule.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        return dto;
    }

    @Override
    public String toString() {
        return "at " + dayOfWeek + ", from " + startTime
                + " to " + endTime;
    }
}

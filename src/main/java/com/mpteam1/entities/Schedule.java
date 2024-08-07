package com.mpteam1.entities;

import com.mpteam1.dto.response.schedule.ScheduleInfoDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, name = "day_of_week")
    private DayOfWeek dayOfWeek;
    @Column(nullable = false, name = "start_time")
    private LocalTime startTime;
    @Column(nullable = false, name = "end_time")
    private LocalTime endTime;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "clazz_id", nullable = false)
    private Clazz clazz;

    public boolean isScheduleConflicted(Schedule schedule) {
        if(this.dayOfWeek == schedule.dayOfWeek){
            if(this.startTime.isBefore(schedule.startTime) && !this.endTime.isAfter(schedule.startTime)) return false;
            else if(!this.startTime.isBefore(schedule.endTime) && this.endTime.isAfter(schedule.endTime)) return false;
            else return true;
        }
        else return false;
    }

    public boolean isSameSchedule(ScheduleInfoDTO scheduleInfoDTO) {
        if(this.dayOfWeek == DayOfWeek.valueOf(scheduleInfoDTO.getDayOfWeek())){
            return this.startTime.format(DateTimeFormatter.ofPattern("HH:mm")).equals(scheduleInfoDTO.getStartTime())
                    && this.endTime.format(DateTimeFormatter.ofPattern("HH:mm")).equals(scheduleInfoDTO.getEndTime());
        }
        else return false;
    }

}

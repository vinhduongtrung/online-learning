package com.mpteam1.rest;


import com.mpteam1.dto.request.schedule.ScheduleTimeDTO;
import com.mpteam1.services.ScheduleService;
import com.mpteam1.utils.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class ScheduleController {
    private ScheduleService scheduleService;

    @PostMapping(Api.Schedule.GET_TEACHER_SCHEDULES)
    public ResponseEntity<?> getTeacherSchedules(@RequestBody ScheduleTimeDTO scheduleTimeDTO) {
        return ResponseEntity.ok(scheduleService.getSchedulesForTeacher(scheduleTimeDTO.getStartDate(), scheduleTimeDTO.getEndDate()));
    }

    @PostMapping(Api.Schedule.GET_STUDENT_SCHEDULES)
    public ResponseEntity<?> getStudentSchedules(@RequestBody ScheduleTimeDTO scheduleTimeDTO) {
        return ResponseEntity.ok(scheduleService.getSchedulesForStudent(scheduleTimeDTO.getStartDate(), scheduleTimeDTO.getEndDate()));
    }

    @PostMapping(Api.Schedule.GET_SCHEDULES)
    public ResponseEntity<?> getSchedules(@RequestBody ScheduleTimeDTO scheduleTimeDTO) {
        return ResponseEntity.ok(scheduleService.getSchedules(scheduleTimeDTO.getStartDate(), scheduleTimeDTO.getEndDate()));
    }

}

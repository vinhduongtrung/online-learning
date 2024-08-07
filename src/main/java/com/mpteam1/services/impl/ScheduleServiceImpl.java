package com.mpteam1.services.impl;

import com.mpteam1.dto.response.schedule.ScheduleTimeResponseDTO;
import com.mpteam1.entities.Schedule;
import com.mpteam1.entities.Student;
import com.mpteam1.entities.Teacher;
import com.mpteam1.exception.custom.exception.UserNotFoundException;
import com.mpteam1.repository.ScheduleRepository;
import com.mpteam1.repository.StudentRepository;
import com.mpteam1.repository.TeacherRepository;
import com.mpteam1.services.ScheduleService;
import com.mpteam1.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public List<ScheduleTimeResponseDTO> filterAvailableSchedules(List<Schedule> schedules, LocalDate startDate, LocalDate endDate) {
        List<Schedule> schedulesFilter = schedules.stream().filter(schedule -> {
            LocalDate dateOfSchedule = startDate.plusDays(schedule.getDayOfWeek().getValue() - startDate.getDayOfWeek().getValue());
            return !startDate.isAfter(dateOfSchedule) && !dateOfSchedule.isAfter(endDate) &&
                    !schedule.getClazz().getStartDate().isAfter(dateOfSchedule)
                    && !dateOfSchedule.isAfter(schedule.getClazz().getEndDate());
        }).toList();

        return schedulesFilter.stream().map(schedule -> {
            ScheduleTimeResponseDTO scheduleTimeResponseDTO = new ScheduleTimeResponseDTO();
            LocalDate dateOfSchedule = startDate.plusDays(schedule.getDayOfWeek().getValue() - startDate.getDayOfWeek().getValue());
            scheduleTimeResponseDTO.setStartTime(dateOfSchedule.atTime(schedule.getStartTime()));
            scheduleTimeResponseDTO.setEndTime(dateOfSchedule.atTime(schedule.getEndTime()));
            if(schedule.getClazz().getTeacher() != null) scheduleTimeResponseDTO.setTeacherName(schedule.getClazz().getTeacher().getFullName());
            else scheduleTimeResponseDTO.setTeacherName(null);
            scheduleTimeResponseDTO.setClassName(schedule.getClazz().getName());
            scheduleTimeResponseDTO.setClassCode(schedule.getClazz().getClazzCode());
            return scheduleTimeResponseDTO;
        }).toList();
    }

    @Override
    public List<ScheduleTimeResponseDTO> getSchedulesForTeacher(LocalDate startDate, LocalDate endDate) {
        Teacher teacher = teacherRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if(teacher == null) {
            throw new UserNotFoundException(Messages.USER_NOT_FOUND);
        }
        List<Schedule> schedules = scheduleRepository.findTeacherSchedules(teacher.getId(), startDate, endDate);
        return filterAvailableSchedules(schedules, startDate, endDate);
    }

    @Override
    public List<ScheduleTimeResponseDTO> getSchedulesForStudent(LocalDate startDate, LocalDate endDate) {
        Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new UserNotFoundException(Messages.USER_NOT_FOUND));
        List<Schedule> schedules = scheduleRepository.findStudentSchedules(student.getId(), startDate, endDate);
        return filterAvailableSchedules(schedules, startDate, endDate);
    }

    @Override
    public List<ScheduleTimeResponseDTO> getSchedules(LocalDate startDate, LocalDate endDate) {
        List<Schedule> schedules = scheduleRepository.findSchedules(startDate, endDate);
        return filterAvailableSchedules(schedules, startDate, endDate);
    }
}

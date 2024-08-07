package com.mpteam1;

import com.mpteam1.dto.response.schedule.ScheduleTimeResponseDTO;
import com.mpteam1.entities.Clazz;
import com.mpteam1.entities.Schedule;
import com.mpteam1.entities.Student;
import com.mpteam1.entities.Teacher;
import com.mpteam1.entities.enums.ERole;
import com.mpteam1.repository.ScheduleRepository;
import com.mpteam1.repository.StudentRepository;
import com.mpteam1.repository.TeacherRepository;
import com.mpteam1.services.impl.ScheduleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {
    @InjectMocks
    private ScheduleServiceImpl scheduleService;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private StudentRepository studentRepository;


    @Test
    public void testGetSchedulesForTeacher() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("teacher@email.com",
                "teacher@email.com", Collections.singletonList(new SimpleGrantedAuthority("TEACHER")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Teacher mockTeacher = new Teacher();
        mockTeacher.setId(1L);
        mockTeacher.setEmail("teacher@email.com");
        when(teacherRepository.findByEmail("teacher@email.com")).thenReturn(mockTeacher);

        LocalDate startDate = LocalDate.of(2024,4,22);
        LocalDate endDate = startDate.plusDays(7);

        Schedule schedule1 = new Schedule();
        schedule1.setId(1L);
        schedule1.setDayOfWeek(DayOfWeek.MONDAY);
        schedule1.setStartTime(LocalTime.of(9, 0));
        schedule1.setEndTime(LocalTime.of(11, 0));

        Clazz clazz1 = new Clazz();
        clazz1.setId(1L);
        clazz1.setName("Math");
        clazz1.setClazzCode("MTH101");
        clazz1.setStartDate(startDate);
        clazz1.setEndDate(endDate);

        schedule1.setClazz(clazz1);

        List<Schedule> mockSchedules = List.of(schedule1);

        when(scheduleRepository.findTeacherSchedules(1L, startDate, endDate)).thenReturn(mockSchedules);

        List<ScheduleTimeResponseDTO> actualSchedules = scheduleService.getSchedulesForTeacher(startDate, endDate);

        assertNotNull(actualSchedules);
        assertEquals(1, actualSchedules.size());
    }

    @Test
    public void testGetSchedulesForStudent() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("student@email.com",
                "student@email.com", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Student mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setEmail("student@email.com");
        mockStudent.setERole(ERole.STUDENT);
        when(studentRepository.findByEmail("student@email.com")).thenReturn(java.util.Optional.of(mockStudent));

        LocalDate startDate = LocalDate.of(2024, 4,22);
        LocalDate endDate = startDate.plusDays(7);

        Schedule schedule1 = new Schedule();
        schedule1.setId(1L);
        schedule1.setDayOfWeek(DayOfWeek.MONDAY);
        schedule1.setStartTime(LocalTime.of(9, 0));
        schedule1.setEndTime(LocalTime.of(11, 0));

        Clazz clazz1 = new Clazz();
        clazz1.setId(1L);
        clazz1.setName("Math");
        clazz1.setClazzCode("MTH101");
        clazz1.setStartDate(startDate);
        clazz1.setEndDate(endDate);

        schedule1.setClazz(clazz1);

        List<Schedule> mockSchedules = List.of(schedule1);

        when(scheduleRepository.findStudentSchedules(1L, startDate, endDate)).thenReturn(mockSchedules);

        List<ScheduleTimeResponseDTO> actualSchedules = scheduleService.getSchedulesForStudent(startDate, endDate);

        assertNotNull(actualSchedules);
        assertEquals(1, actualSchedules.size());
    }

    @Test
    public void testGetSchedules() {
        LocalDate startDate = LocalDate.of(2024, 4,22);
        LocalDate endDate = startDate.plusDays(7);

        Schedule schedule1 = new Schedule();
        schedule1.setId(1L);
        schedule1.setDayOfWeek(DayOfWeek.MONDAY);
        schedule1.setStartTime(LocalTime.of(9, 0));
        schedule1.setEndTime(LocalTime.of(11, 0));

        Clazz clazz1 = new Clazz();
        clazz1.setId(1L);
        clazz1.setName("Math");
        clazz1.setClazzCode("MTH101");
        clazz1.setStartDate(startDate);
        clazz1.setEndDate(endDate);

        schedule1.setClazz(clazz1);

        List<Schedule> mockSchedules = List.of(schedule1);

        when(scheduleRepository.findSchedules(startDate, endDate)).thenReturn(mockSchedules);

        List<ScheduleTimeResponseDTO> actualSchedules = scheduleService.getSchedules(startDate, endDate);

        assertNotNull(actualSchedules);
        assertEquals(1, actualSchedules.size());
    }
}

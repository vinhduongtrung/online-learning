package com.mpteam1;

import com.mpteam1.dto.request.clazz.ClazzDTOCreate;
import com.mpteam1.dto.request.clazz.ClazzDTOUpdate;
import com.mpteam1.dto.response.clazz.ClazzDTOResponse;
import com.mpteam1.dto.response.schedule.ScheduleInfoDTO;
import com.mpteam1.entities.Clazz;
import com.mpteam1.entities.Schedule;
import com.mpteam1.exception.custom.exception.*;
import com.mpteam1.repository.ClassRepository;
import com.mpteam1.repository.ScheduleRepository;
import com.mpteam1.repository.StudentRepository;
import com.mpteam1.repository.TeacherRepository;
import com.mpteam1.services.impl.ClazzServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ClazzServiceTest {
    @Mock
    private ClassRepository classRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @InjectMocks
    private ClazzServiceImpl clazzService;
    @Mock
    private SecurityContextHolder securityContextHolder;
    @Mock
    private Pageable mockPageable;

    // Testing Get Clazz
    @Test
    public void whenAdminGetClazzDetailWithNotExistingId_ShouldThrowClazzNotFoundException() {
       when(classRepository.findById(100L)).thenReturn(Optional.empty());
       assertThrows(ClazzNotFoundException.class, () -> {clazzService.findById(100L);});
    }

    @Test
    public void whenAdminGetClazzDetailWithExistingId_ShouldReturnClazzDetail() throws ClazzNotFoundException {
        Clazz clazz = new Clazz();
        clazz.setId(1L);
        clazz.setName("Test");
        clazz.setClazzCode("TEST");
        clazz.setDescription("Test");
        clazz.setCreatedDate(Instant.now());
        clazz.setStartDate(LocalDate.now());
        clazz.setEndDate(LocalDate.now().plusDays(1));
        clazz.setMaximumCapacity(30);
        when(classRepository.findById(1L)).thenReturn(Optional.of(clazz));
        ClazzDTOResponse clazzDTOResponse = clazzService.findById(1L);
        assertNotNull(clazzDTOResponse);
        assertEquals(1L, clazzDTOResponse.getId());
        assertEquals("Test", clazzDTOResponse.getName());
    }

    // Testing Add Clazz
    @Test
    public void whenAddClazzWithExistingName_ShouldThrowClassDuplicateException() {
        ClazzDTOCreate clazzDTOCreate = new ClazzDTOCreate();
        clazzDTOCreate.setName("Test");

        when(classRepository.findByNameIgnoreCase("Test")).thenReturn(Optional.of(new Clazz()));
        assertThrows(ClassDuplicateException.class, () -> {clazzService.add(clazzDTOCreate);});
    }

    @Test
    public void whenAddClazzWithExistingClassCode_ShouldThrowClassDuplicateException() {
        ClazzDTOCreate clazzDTOCreate = new ClazzDTOCreate();
        clazzDTOCreate.setName("Test");
        clazzDTOCreate.setClazzCode("TEST");

        when(classRepository.findByNameIgnoreCase("Test")).thenReturn(Optional.empty());
        when(classRepository.findByClazzCodeIgnoreCase("TEST")).thenReturn(Optional.of(new Clazz()));
        assertThrows(ClassDuplicateException.class, () -> {clazzService.add(clazzDTOCreate);});
    }

    @Test
    public void whenAddClazzWithStartDateAfterEndDate_ShouldThrowDateTimeException() {
        ClazzDTOCreate clazzDTOCreate = new ClazzDTOCreate();
        clazzDTOCreate.setName("Test");
        clazzDTOCreate.setClazzCode("TEST");
        clazzDTOCreate.setStartDate(LocalDate.now().plusDays(1));
        clazzDTOCreate.setEndDate(LocalDate.now());

        when(classRepository.findByNameIgnoreCase("Test")).thenReturn(Optional.empty());
        when(classRepository.findByClazzCodeIgnoreCase("TEST")).thenReturn(Optional.empty());
        assertThrows(DateTimeException.class, () -> {clazzService.add(clazzDTOCreate);});
    }

    @Test
    public void whenAddClazzWithInvalidDayOfWeek_ShouldThrowException() {
        ClazzDTOCreate clazzDTOCreate = new ClazzDTOCreate();
        clazzDTOCreate.setName("Test");
        clazzDTOCreate.setClazzCode("TEST");
        clazzDTOCreate.setStartDate(LocalDate.now().plusDays(1));
        clazzDTOCreate.setEndDate(LocalDate.now());
        clazzDTOCreate.setMaximumCapacity(20);
        clazzDTOCreate.setDescription("Test");
        ScheduleInfoDTO scheduleInfoDTO = new ScheduleInfoDTO();
        scheduleInfoDTO.setDayOfWeek("fadfdsa");
        scheduleInfoDTO.setStartTime("09:00");
        scheduleInfoDTO.setEndTime("10:00");
        clazzDTOCreate.getSchedules().add(scheduleInfoDTO);

        when(classRepository.findByNameIgnoreCase("Test")).thenReturn(Optional.empty());
        when(classRepository.findByClazzCodeIgnoreCase("TEST")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> {clazzService.add(clazzDTOCreate);});
    }

    @Test
    public void whenAddClazzWithInvalidStartTime_ShouldThrowException() {
        ClazzDTOCreate clazzDTOCreate = new ClazzDTOCreate();
        clazzDTOCreate.setName("Test");
        clazzDTOCreate.setClazzCode("TEST");
        clazzDTOCreate.setStartDate(LocalDate.now().plusDays(1));
        clazzDTOCreate.setEndDate(LocalDate.now());
        clazzDTOCreate.setMaximumCapacity(20);
        clazzDTOCreate.setDescription("Test");
        ScheduleInfoDTO scheduleInfoDTO = new ScheduleInfoDTO();
        scheduleInfoDTO.setDayOfWeek("MONDAY");
        scheduleInfoDTO.setStartTime("afdsaf");
        scheduleInfoDTO.setEndTime("10:00");
        clazzDTOCreate.getSchedules().add(scheduleInfoDTO);

        when(classRepository.findByNameIgnoreCase("Test")).thenReturn(Optional.empty());
        when(classRepository.findByClazzCodeIgnoreCase("TEST")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> {clazzService.add(clazzDTOCreate);});
    }

    @Test
    public void whenAddClazzWithInvalidEndTime_ShouldThrowException() {
        ClazzDTOCreate clazzDTOCreate = new ClazzDTOCreate();
        clazzDTOCreate.setName("Test");
        clazzDTOCreate.setClazzCode("TEST");
        clazzDTOCreate.setStartDate(LocalDate.now().plusDays(1));
        clazzDTOCreate.setEndDate(LocalDate.now());
        clazzDTOCreate.setMaximumCapacity(20);
        clazzDTOCreate.setDescription("Test");
        ScheduleInfoDTO scheduleInfoDTO = new ScheduleInfoDTO();
        scheduleInfoDTO.setDayOfWeek("MONDAY");
        scheduleInfoDTO.setStartTime("09:00");
        scheduleInfoDTO.setEndTime("afdsaf");
        clazzDTOCreate.getSchedules().add(scheduleInfoDTO);

        when(classRepository.findByNameIgnoreCase("Test")).thenReturn(Optional.empty());
        when(classRepository.findByClazzCodeIgnoreCase("TEST")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> {clazzService.add(clazzDTOCreate);});
    }

    @Test
    public void whenAddClazzWithValidInformation_ShouldSuccess() throws TeacherNotFoundException, ClassDuplicateException, StudentNotFoundException {
        ClazzDTOCreate clazzDTOCreate = new ClazzDTOCreate();
        clazzDTOCreate.setName("Test");
        clazzDTOCreate.setClazzCode("TEST");
        clazzDTOCreate.setStartDate(LocalDate.now());
        clazzDTOCreate.setEndDate(LocalDate.now().plusMonths(3));
        clazzDTOCreate.setMaximumCapacity(20);
        clazzDTOCreate.setDescription("Test");
        ScheduleInfoDTO scheduleInfoDTO = new ScheduleInfoDTO();
        scheduleInfoDTO.setDayOfWeek("MONDAY");
        scheduleInfoDTO.setStartTime("09:00");
        scheduleInfoDTO.setEndTime("10:00");
        clazzDTOCreate.getSchedules().add(scheduleInfoDTO);

        when(classRepository.findByNameIgnoreCase("Test")).thenReturn(Optional.empty());
        when(classRepository.findByClazzCodeIgnoreCase("TEST")).thenReturn(Optional.empty());
        clazzService.add(clazzDTOCreate);
        verify(classRepository, times(1)).save(any());
    }

    // Testing update clazz
    @Test
    public void whenUpdateClazzWithNotFoundId_ShouldThrowClassNotFoundException() {
        ClazzDTOUpdate clazzDTOUpdate = new ClazzDTOUpdate();
        clazzDTOUpdate.setId(1L);

        when(classRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ClazzNotFoundException.class, () -> clazzService.update(clazzDTOUpdate));
    }

    @Test
    public void whenUpdateClazzWithExistingName_ShouldThrowClassDuplicateException() {
        ClazzDTOUpdate clazzDTOUpdate = new ClazzDTOUpdate();
        clazzDTOUpdate.setId(1L);
        clazzDTOUpdate.setName("Test");

        when(classRepository.findById(1L)).thenReturn(Optional.of(new Clazz()));
        when(classRepository.findByNameIgnoreCase("Test")).thenReturn(Optional.of(new Clazz()));
        assertThrows(ClassDuplicateException.class, () -> {clazzService.update(clazzDTOUpdate);});
    }

    @Test
    public void whenUpdateClazzWithExistingClassCode_ShouldThrowClassDuplicateException() {
        ClazzDTOUpdate clazzDTOUpdate = new ClazzDTOUpdate();
        clazzDTOUpdate.setId(1L);
        clazzDTOUpdate.setName("Test");
        clazzDTOUpdate.setClazzCode("TEST");

        when(classRepository.findById(1L)).thenReturn(Optional.of(new Clazz()));
        when(classRepository.findByNameIgnoreCase("Test")).thenReturn(Optional.empty());
        when(classRepository.findByClazzCodeIgnoreCase("TEST")).thenReturn(Optional.of(new Clazz()));
        assertThrows(ClassDuplicateException.class, () -> {clazzService.update(clazzDTOUpdate);});
    }

    @Test
    public void whenUpdateClazzWithStartDateAfterEndDate_ShouldThrowDateTimeException() {
        ClazzDTOUpdate clazzDTOUpdate = new ClazzDTOUpdate();
        clazzDTOUpdate.setId(1L);
        clazzDTOUpdate.setName("Test");
        clazzDTOUpdate.setClazzCode("TEST");
        clazzDTOUpdate.setStartDate(LocalDate.now().plusDays(1));
        clazzDTOUpdate.setEndDate(LocalDate.now());

        when(classRepository.findById(1L)).thenReturn(Optional.of(new Clazz()));
        when(classRepository.findByNameIgnoreCase("Test")).thenReturn(Optional.empty());
        when(classRepository.findByClazzCodeIgnoreCase("TEST")).thenReturn(Optional.empty());
        assertThrows(DateTimeException.class, () -> {clazzService.update(clazzDTOUpdate);});
    }

    @Test
    public void whenUpdateClazzWithInvalidDayOfWeek_ShouldThrowException() {
        ClazzDTOUpdate clazzDTOUpdate = new ClazzDTOUpdate();
        clazzDTOUpdate.setId(1L);
        clazzDTOUpdate.setName("Test");
        clazzDTOUpdate.setClazzCode("TEST");
        clazzDTOUpdate.setStartDate(LocalDate.now());
        clazzDTOUpdate.setEndDate(LocalDate.now().plusDays(1));
        clazzDTOUpdate.setMaximumCapacity(20);
        clazzDTOUpdate.setDescription("Test");
        ScheduleInfoDTO scheduleInfoDTO = new ScheduleInfoDTO();
        scheduleInfoDTO.setDayOfWeek("fadfdsa");
        scheduleInfoDTO.setStartTime("09:00");
        scheduleInfoDTO.setEndTime("10:00");
        clazzDTOUpdate.getSchedules().add(scheduleInfoDTO);

        when(classRepository.findById(1L)).thenReturn(Optional.of(new Clazz()));
        when(classRepository.findByNameIgnoreCase("Test")).thenReturn(Optional.empty());
        when(classRepository.findByClazzCodeIgnoreCase("TEST")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> {clazzService.update(clazzDTOUpdate);});
    }

    @Test
    public void whenUpdateClazzWithInvalidStartTime_ShouldThrowException() {
        ClazzDTOUpdate clazzDTOUpdate = new ClazzDTOUpdate();
        clazzDTOUpdate.setId(1L);
        clazzDTOUpdate.setName("Test");
        clazzDTOUpdate.setClazzCode("TEST");
        clazzDTOUpdate.setStartDate(LocalDate.now());
        clazzDTOUpdate.setEndDate(LocalDate.now().plusDays(1));
        clazzDTOUpdate.setMaximumCapacity(20);
        clazzDTOUpdate.setDescription("Test");
        ScheduleInfoDTO scheduleInfoDTO = new ScheduleInfoDTO();
        scheduleInfoDTO.setDayOfWeek("MONDAY");
        scheduleInfoDTO.setStartTime("dfsadf");
        scheduleInfoDTO.setEndTime("10:00");
        clazzDTOUpdate.getSchedules().add(scheduleInfoDTO);

        when(classRepository.findById(1L)).thenReturn(Optional.of(new Clazz()));
        when(classRepository.findByNameIgnoreCase("Test")).thenReturn(Optional.empty());
        when(classRepository.findByClazzCodeIgnoreCase("TEST")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> {clazzService.update(clazzDTOUpdate);});
    }

    @Test
    public void whenUpdateClazzWithInvalidEndTime_ShouldThrowException() {
        ClazzDTOUpdate clazzDTOUpdate = new ClazzDTOUpdate();
        clazzDTOUpdate.setId(1L);
        clazzDTOUpdate.setName("Test");
        clazzDTOUpdate.setClazzCode("TEST");
        clazzDTOUpdate.setStartDate(LocalDate.now());
        clazzDTOUpdate.setEndDate(LocalDate.now().plusDays(1));
        clazzDTOUpdate.setMaximumCapacity(20);
        clazzDTOUpdate.setDescription("Test");
        ScheduleInfoDTO scheduleInfoDTO = new ScheduleInfoDTO();
        scheduleInfoDTO.setDayOfWeek("MONDAY");
        scheduleInfoDTO.setStartTime("09:00");
        scheduleInfoDTO.setEndTime("asdfasdf");
        clazzDTOUpdate.getSchedules().add(scheduleInfoDTO);

        when(classRepository.findById(1L)).thenReturn(Optional.of(new Clazz()));
        when(classRepository.findByNameIgnoreCase("Test")).thenReturn(Optional.empty());
        when(classRepository.findByClazzCodeIgnoreCase("TEST")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> {clazzService.update(clazzDTOUpdate);});
    }

    @Test
    public void whenUpdateClazzWithValidInformation_ShouldSuccess() throws TeacherNotFoundException, ClazzNotFoundException, ClassDuplicateException, ConflictedScheduleException, StudentNotFoundException {
        // test data
        ClazzDTOUpdate clazzDTOUpdate = new ClazzDTOUpdate();
        clazzDTOUpdate.setId(1L);
        clazzDTOUpdate.setName("Test");
        clazzDTOUpdate.setClazzCode("TEST");
        clazzDTOUpdate.setStartDate(LocalDate.now());
        clazzDTOUpdate.setEndDate(LocalDate.now().plusDays(1));
        clazzDTOUpdate.setMaximumCapacity(20);
        clazzDTOUpdate.setDescription("Test");
        ScheduleInfoDTO scheduleInfoDTO = new ScheduleInfoDTO();
        scheduleInfoDTO.setDayOfWeek("MONDAY");
        scheduleInfoDTO.setStartTime("09:00");
        scheduleInfoDTO.setEndTime("10:00");
        clazzDTOUpdate.getSchedules().add(scheduleInfoDTO);

        // test result
        Clazz clazz = new Clazz();
        clazz.setId(1L);
        clazz.setName("Test");
        clazz.setClazzCode("TEST");
        clazz.setStartDate(LocalDate.now());
        clazz.setEndDate(LocalDate.now().plusDays(1));
        clazz.setMaximumCapacity(20);
        clazz.setDescription("Test");
        Schedule schedule = new Schedule();
        schedule.setId(1L);
        schedule.setDayOfWeek(DayOfWeek.MONDAY);
        schedule.setStartTime(LocalTime.parse("09:00"));
        schedule.setEndTime(LocalTime.parse("10:00"));
        schedule.setClazz(clazz);
        clazz.getSchedules().add(schedule);

        when(classRepository.findById(1L)).thenReturn(Optional.of(new Clazz()));
        when(classRepository.findByNameIgnoreCase("Test")).thenReturn(Optional.empty());
        when(classRepository.findByClazzCodeIgnoreCase("TEST")).thenReturn(Optional.empty());
        when(classRepository.save(any())).thenReturn(clazz);
        clazzService.update(clazzDTOUpdate);
        verify(classRepository, times(1)).save(any());
    }

    // testing delete clazz
    @Test
    public void whenDeleteClazzWithInvalidId_ShouldThrowClazzNotFoundException() {
        when(classRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(ClazzNotFoundException.class, () -> {clazzService.findById(100L);});
    }

    @Test
    public void whenDeleteClazzWithValidId_ShouldSuccess() throws ClazzNotFoundException {
        when(classRepository.findById(1L)).thenReturn(Optional.of(new Clazz()));
        assertTrue(clazzService.deleteById(1L));
    }
}

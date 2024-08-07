package com.mpteam1;

import com.mpteam1.dto.request.teacher.TeacherDTOCreate;
import com.mpteam1.dto.request.teacher.TeacherDTOUpdate;
import com.mpteam1.dto.response.common.PageForView;
import com.mpteam1.dto.response.teacher.TeacherDTOResponse;
import com.mpteam1.dto.response.teacher.TeacherDetail;
import com.mpteam1.entities.Subjects;
import com.mpteam1.entities.Teacher;
import com.mpteam1.entities.enums.EEmploymentStatus;
import com.mpteam1.entities.enums.ERole;
import com.mpteam1.exception.custom.exception.TeacherDuplicateException;
import com.mpteam1.exception.custom.exception.TeacherNotFoundException;
import com.mpteam1.repository.SubjectRepository;
import com.mpteam1.repository.TeacherRepository;
import com.mpteam1.repository.TokenRepository;
import com.mpteam1.services.EmailService;
import com.mpteam1.services.impl.TeacherServiceImpl;
import com.mpteam1.utils.GenerateAccountName;
import com.mpteam1.utils.impl.ConvertToEnglishNameImpl;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @InjectMocks
    private TeacherServiceImpl teacherService;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private GenerateAccountName generateAccountName;


    @Test
    public void testAddTeacherSuccess() throws TeacherDuplicateException {
        TeacherDTOCreate teacherDTOCreate = new TeacherDTOCreate();
        teacherDTOCreate.setEmail("test@email.com");
        teacherDTOCreate.setPassword(bCryptPasswordEncoder.encode("password"));
        teacherDTOCreate.setFullName("Test Teacher");
        teacherDTOCreate.setSubjects(Arrays.asList("Math", "Physics"));
        teacherDTOCreate.setDateOfBirth(LocalDate.of(2020,10,10));

        when(teacherRepository.findByEmail(anyString())).thenReturn(null);
        when(subjectRepository.findByName(anyString())).thenReturn(Optional.of(new Subjects("Math")));
        when(generateAccountName.generate(ConvertToEnglishNameImpl.convertToEnglishName(teacherDTOCreate.getFullName()))).thenReturn("abc");
        TeacherDTOResponse response = teacherService.add(teacherDTOCreate);

        assertNotNull(response);
        assertEquals("Test Teacher", response.getFullName());
        verify(teacherRepository, times(1)).save(any());
        verify(emailService, times(1)).sendHtmlMessage(any(), any(), any(), any());
    }
    @Test
    public void testAddTeacherDuplicateEmail() {
        TeacherDTOCreate teacherDTOCreate = new TeacherDTOCreate();
        teacherDTOCreate.setEmail("duplicate@email.com");

        when(teacherRepository.findByEmail(anyString())).thenReturn(new Teacher());

        assertThrows(TeacherDuplicateException.class, () -> teacherService.add(teacherDTOCreate));
    }

    @Test
    public void testFindTeacherByIdSuccess() throws TeacherNotFoundException {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFullName("Test Teacher");
        teacher.setEmail("test@email.com");
        teacher.setDateOfBirth(LocalDate.of(2020,10,10));
        teacher.setEEmploymentStatus(EEmploymentStatus.ACTIVE);
        teacher.setERole(ERole.TEACHER);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        TeacherDTOResponse response = teacherService.findById(1L);

        assertNotNull(response);
        assertEquals("Test Teacher", response.getFullName());
        assertEquals("test@email.com", response.getEmail());
    }

    @Test
    public void testFindTeacherByIdNotFound() {
        when(teacherRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(TeacherNotFoundException.class, () -> teacherService.findById(2L));
    }

    @Test
    public void testListTeacherWithKeyword() {
        Pageable pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");

        Teacher teacher1 = new Teacher();
        teacher1.setId(1L);
        teacher1.setFullName("Teacher 1");
        teacher1.setEmail("teacher1@email.com");
        teacher1.setEEmploymentStatus(EEmploymentStatus.ACTIVE);
        teacher1.setERole(ERole.TEACHER);
        teacher1.setDateOfBirth(LocalDate.of(2020,10,10));

        Teacher teacher2 = new Teacher();
        teacher2.setId(1L);
        teacher2.setFullName("Teacher 2");
        teacher2.setEmail("teacher2@email.com");
        teacher2.setEEmploymentStatus(EEmploymentStatus.ACTIVE);
        teacher2.setERole(ERole.TEACHER);
        teacher2.setDateOfBirth(LocalDate.of(2020,10,10));

        Page<Teacher> teacherPage = new PageImpl<>(Arrays.asList(teacher1, teacher2), pageable, 2);
        when(teacherRepository.findAllByKeyword("java", pageable)).thenReturn(teacherPage);

        PageForView<TeacherDTOResponse> result = teacherService.list(0, 5, "java");

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2L, result.getTotalElement());
        assertEquals(5, result.getSize());
        assertEquals(0, result.getPage());
    }

    @Test
    public void testListAllAvailableTeacher() {
        List<Tuple> tuples = Arrays.asList(
                mockTuple(1L, 30, "MSc", "ACTIVE", new Date(), "teacher1@email.com", "Teacher One", "Address 1", new Date(), "123456789", true, "avatar1.jpg"),
                mockTuple(2L, 35, "PhD", "INACTIVE", new Date(), "teacher2@email.com", "Teacher Two", "Address 2", new Date(), "987654321", false, "avatar2.jpg")
        );

        when(teacherRepository.fetchAllAvailableTeacher(anyInt(), anyInt(), anyLong())).thenReturn(tuples);
        when(teacherRepository.countTeacherOutSideClazz(anyLong())).thenReturn(2L);

        PageForView<TeacherDetail> results = teacherService.fetchAllAvailableTeacher(0, 5, 1L);
        assertEquals(2, results.getContent().size());
        assertEquals(0, results.getPage());
        assertEquals(2, results.getSize());
        assertEquals(2L, results.getTotalElement());
    }


    private Tuple mockTuple(Long teacherId, Integer age, String qualification, String employmentStatus, Date startDate,
                            String email, String fullName, String address, Date dateOfBirth, String phoneNumber,
                            Boolean gender, String avatarUrl) {
        Tuple tuple = mock(Tuple.class);
        when(tuple.get("teacherId")).thenReturn(teacherId);
//        when(tuple.get("age")).thenReturn(age);
        when(tuple.get("qualification")).thenReturn(qualification);
        when(tuple.get("employmentStatus")).thenReturn(employmentStatus);
        when(tuple.get("startDate")).thenReturn(startDate);
        when(tuple.get("email")).thenReturn(email);
        when(tuple.get("fullName")).thenReturn(fullName);
        when(tuple.get("address")).thenReturn(address);
        when(tuple.get("dateOfBirth")).thenReturn(dateOfBirth);
        when(tuple.get("phoneNumber")).thenReturn(phoneNumber);
        when(tuple.get("gender")).thenReturn(gender);
        when(tuple.get("role")).thenReturn("ROLE_TEACHER");
        when(tuple.get("avatarUrl")).thenReturn(avatarUrl);
        return tuple;
    }

    @Test
    public void testEditTeacher() {
        TeacherDTOUpdate teacherDTOUpdate = new TeacherDTOUpdate();
        teacherDTOUpdate.setId(1L);
        teacherDTOUpdate.setEmploymentStatus("ACTIVE");
        teacherDTOUpdate.setFullName("Updated");
        teacherDTOUpdate.setAddress("123 Main St");
        teacherDTOUpdate.setPhoneNumber("1234567890");
        teacherDTOUpdate.setDateOfBirth(LocalDate.of(2020,10,10));
        Set<String> subjects = new HashSet<>();
        subjects.add("Math");
        teacherDTOUpdate.setSubjects(subjects);

        Teacher teacherEntity = new Teacher();
        teacherEntity.setFullName("John Doe");
        teacherEntity.setEEmploymentStatus(EEmploymentStatus.ACTIVE);
        teacherEntity.setERole(ERole.TEACHER);


        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacherEntity));
        when(subjectRepository.findByName(anyString())).thenReturn(Optional.of(new Subjects("Math")));

        try {
            TeacherDTOResponse response = teacherService.edit(teacherDTOUpdate);

            assertNotNull(teacherDTOUpdate.getFullName(), response.getFullName());

        } catch (TeacherNotFoundException e) {
            fail("TeacherNotFoundException should not be thrown");
        }
    }

    @Test
    public void testDeleteTeacherByIdSuccess() {
        long id = 1L;

        Teacher teacherEntity = new Teacher();

        when(teacherRepository.findById(id)).thenReturn(Optional.of(teacherEntity));

        try {
            boolean deleted = teacherService.deleteById(id);

            assertTrue(deleted);
            verify(tokenRepository).deleteByUser_Id(id);
            verify(teacherRepository).deleteById(id);

        } catch (TeacherNotFoundException e) {
            fail("TeacherNotFoundException should not be thrown");
        }
    }

    @Test
    public void testDeleteTeacherByIdNotFound() {
        long id = 1L;

        when(teacherRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TeacherNotFoundException.class, () -> teacherService.deleteById(id));
        verify(tokenRepository, never()).deleteByUser_Id(id);
        verify(teacherRepository, never()).deleteById(id);
    }

    @Test
    public void testCountTeachersThatHaveClasses() {
        long expectedCount = 5L;

        when(teacherRepository.countByClazzesNotEmpty()).thenReturn(expectedCount);

        long actualCount = teacherService.countTeachersThatHaveClasses();

        assertEquals(expectedCount, actualCount);
        verify(teacherRepository).countByClazzesNotEmpty();
    }

    @Test
    public void testCountTeachers() {
        long expectedCount = 10L;

        when(teacherRepository.count()).thenReturn(expectedCount);

        long actualCount = teacherService.countTeachers();

        assertEquals(expectedCount, actualCount);
        verify(teacherRepository).count();
    }

}

package com.mpteam1;

import com.mpteam1.dto.request.student.*;
import com.mpteam1.dto.response.common.PageForView;
import com.mpteam1.dto.response.student.StudentDTOResponse;
import com.mpteam1.entities.Clazz;
import com.mpteam1.entities.Student;
import com.mpteam1.exception.custom.exception.StudentNotFoundException;
import com.mpteam1.repository.ClassRepository;
import com.mpteam1.repository.StudentRepository;
import com.mpteam1.repository.TeacherRepository;
import com.mpteam1.repository.TokenRepository;
import com.mpteam1.services.impl.StudentServiceImpl;
import com.mpteam1.utils.GenerateAccountName;
import com.mpteam1.utils.impl.ConvertToEnglishNameImpl;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    @InjectMocks
    private StudentServiceImpl studentService;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private ClassRepository classRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContextHolder securityContextHolder;

    @Mock
    private GenerateAccountName generateAccountName;

    @Mock
    private TokenRepository tokenRepository;



    @Test
    public void testBulkInsert() throws Exception {
        String email1 = "email1@example.com";
        String email2 = "email2@example.com";

        StudentDTOCreate student1 = new StudentDTOCreate();
        student1.setFullName("test");
        student1.setEmail(email1);
        student1.setClazzName(new ArrayList<>());
        student1.getClazzName().add("clazz A");

        StudentDTOCreate student2 = new StudentDTOCreate();
        student2.setFullName("test");
        student2.setClazzName(new ArrayList<>());
        student2.setEmail(email2);
        student2.getClazzName().add("clazz B");

        List<StudentDTOCreate> students = Arrays.asList(student1, student2);
        Clazz clazzA = new Clazz();
        clazzA.setName("clazz A");

        Clazz clazzB = new Clazz();
        clazzB.setName("clazz B");

        when(studentRepository.findByEmail(email1)).thenReturn(Optional.empty());
        when(studentRepository.findByEmail(email2)).thenReturn(Optional.empty());
        when(classRepository.findByName("clazz A")).thenReturn(Optional.of(clazzA));
        when(classRepository.findByName("clazz B")).thenReturn(Optional.of(clazzB));
        when(generateAccountName.generate(ConvertToEnglishNameImpl.convertToEnglishName(student1.getFullName()))).thenReturn("abc");
        when(generateAccountName.generate(ConvertToEnglishNameImpl.convertToEnglishName(student2.getFullName()))).thenReturn("abc");

        StudentCreateWrapper studentCreateWrapper = new StudentCreateWrapper();
        studentCreateWrapper.setStudents(students);

        studentService.bulkInsert(studentCreateWrapper);

        verify(studentRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testEdit() throws Exception {
        long studentId = 1L;
        String fullName = "John Doe";
        String dateOfBirth = "1990-01-01";
        boolean gender = true;
        String phoneNumber = "1234567890";
        String address = "123 Main St";

        StudentDTOUpdate studentDTOUpdate = new StudentDTOUpdate();
        studentDTOUpdate.setId(studentId);
        studentDTOUpdate.setFullName(fullName);
        studentDTOUpdate.setDateOfBirth(LocalDate.parse(dateOfBirth));
        studentDTOUpdate.setGender(gender);
        studentDTOUpdate.setPhoneNumber(phoneNumber);
        studentDTOUpdate.setAddress(address);

        Student student = new Student();
        student.setId(studentId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(studentRepository.save(any())).thenReturn(student);

        studentService.edit(studentDTOUpdate);

        verify(studentRepository, times(1)).findById(studentId);
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    public void testBulkDelete() {
        // Given
        List<StudentDTODelete> studentDTOList = Arrays.asList(
                new StudentDTODelete(1L),
                new StudentDTODelete(2L),
                new StudentDTODelete(3L)
        );

        Student student1 = new Student();
        student1.setId(1L);

        Student student2 = new Student();
        student2.setId(2L);

        Student student3 = new Student();
        student3.setId(3L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(student2));
        when(studentRepository.findById(3L)).thenReturn(Optional.of(student3));

        // When
        try {
            studentService.bulkDelete(new StudentDTODeleteWrapper(studentDTOList));
        } catch (StudentNotFoundException e) {
            fail("No exception should be thrown");
        }

        // Then
        verify(studentRepository, times(1)).deleteById(1L);
        verify(studentRepository, times(1)).deleteById(2L);
        verify(studentRepository, times(1)).deleteById(3L);
    }

    @Test
    public void testFindById() throws StudentNotFoundException {
        // Given
        Long studentId = 1L;
        Tuple mockTuple = mockTuple(studentId, new Date(), "student1@email.com", "Student One", "Address 1", new Date(), "avatar1.jpg", "abc");
        when(studentRepository.findStudentById(studentId)).thenReturn(List.of(mockTuple));

        // When
        StudentDTOResponse result = studentService.findById(studentId);

        // Then
        assertNotNull(result);
        assertEquals(studentId, result.getId());
    }

    @Test
    public void testFindByIdNotFound() {
        Long studentId = 1L;
        when(studentRepository.findStudentById(studentId)).thenReturn(Collections.emptyList());

        assertThrows(StudentNotFoundException.class, ()->studentService.findById(studentId));

    }

    private Tuple mockTuple(Long id, Date startDate,
                            String email, String fullName, String address, Date dateOfBirth,
                            String avatarUrl, String accountName) {
        Tuple tuple = mock(Tuple.class);
        when(tuple.get("id")).thenReturn(id);
        when(tuple.get("startDate")).thenReturn(startDate);
        when(tuple.get("email")).thenReturn(email);
        when(tuple.get("fullName")).thenReturn(fullName);
        when(tuple.get("address")).thenReturn(address);
        when(tuple.get("birthday")).thenReturn(dateOfBirth);
        when(tuple.get("phoneNumber")).thenReturn("111111111");
        when(tuple.get("gender")).thenReturn(true);
        when(tuple.get("avatarName")).thenReturn(avatarUrl);
        when(tuple.get("clazzId")).thenReturn(1L);
        when(tuple.get("clazzName")).thenReturn("Math");
        when(tuple.get("createdDate")).thenReturn(new Date());
        when(tuple.get("startDate")).thenReturn(new Date());
        when(tuple.get("endDate")).thenReturn(new Date());
        when(tuple.get("teacherId")).thenReturn(1L);
        when(tuple.get("teacherName")).thenReturn("Teacher Name");
        when(tuple.get("accountName")).thenReturn("abc");
        return tuple;
    }

    @Test
    public void testFindAllStudentInSideClazz() {
        List<Tuple> mockStudents = List.of(
                mockTuple(1L, new Date(), "student1@email.com", "Student One", "Address 1", new Date(), "avatar1.jpg", "abc")
        );

        when(studentRepository.findAllStudentInSideClazz(anyInt(), anyInt(), anyLong(), anyString())).thenReturn(mockStudents);
        when(studentRepository.countFindAllStudentInSideClazz(anyLong(), anyString())).thenReturn(1L);

        PageForView<StudentDTOResponse> result = studentService.findAllStudentInSideClazz(0, 10, 1L, "keyword");

        assertEquals(0, result.getPage());
        assertEquals(1, result.getSize());
        assertEquals(1L, result.getTotalElement());
        assertEquals(1, result.getContent().size());
    }

    @Test
    public void testFindAllStudentOutSideClazz() {
        List<Tuple> mockStudents = Arrays.asList(
                mockTuple(1L, new Date(), "student1@email.com", "Student One", "Address 1", new Date(), "avatar1.jpg", "abc"),
                mockTuple(2L, new Date(), "student2@email.com", "Student Two", "Address 2", new Date(), "avatar2.jpg", "abc")
        );

        when(studentRepository.findAllStudentOutSideClazz(anyInt(), anyInt(), anyLong(), anyString())).thenReturn(mockStudents);
        when(studentRepository.countAllStudentOutSideClazz(anyLong(), anyString())).thenReturn(2L);

        PageForView<StudentDTOResponse> result = studentService.findAllStudentOutSideClazz(0, 10, 1L, "keyword");

        assertEquals(0, result.getPage());
        assertEquals(2, result.getSize());
        assertEquals(2L, result.getTotalElement());
        assertEquals(2, result.getContent().size());

    }

    @Test
    public void testFindAllStudentByTeacher() {
        List<Tuple> mockStudents = Arrays.asList(
                mockTuple(1L, new Date(), "student1@email.com", "Student One", "Address 1", new Date(), "avatar1.jpg", "abc"),
                mockTuple(2L, new Date(), "student2@email.com", "Student Two", "Address 2", new Date(), "avatar2.jpg", "abc")
        );

        when(studentRepository.findAllStudentByTeacher(anyInt(), anyInt(), anyLong(), anyString())).thenReturn(mockStudents);
        when(studentRepository.countFindAllStudentByTeacher(anyLong(), anyString())).thenReturn(2L);

        PageForView<StudentDTOResponse> result = studentService.findAllByTeacher(0, 10, 1L, "keyword");

        assertEquals(0, result.getPage());
        assertEquals(2, result.getSize());
        assertEquals(2L, result.getTotalElement());
        assertEquals(2, result.getContent().size());

    }


}

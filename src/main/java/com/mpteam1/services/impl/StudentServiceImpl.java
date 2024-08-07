package com.mpteam1.services.impl;

import com.mpteam1.dto.request.student.*;
import com.mpteam1.dto.response.clazz.ClazzAndTeacher;
import com.mpteam1.dto.response.common.Converter;
import com.mpteam1.dto.response.common.PageForView;
import com.mpteam1.dto.response.student.StudentDTOResponse;
import com.mpteam1.dto.response.student.StudentStatus;
import com.mpteam1.dto.response.teacher.TeacherInfoDTO;
import com.mpteam1.entities.Clazz;
import com.mpteam1.entities.Student;
import com.mpteam1.entities.Teacher;
import com.mpteam1.exception.custom.exception.StudentDuplicateException;
import com.mpteam1.exception.custom.exception.ClassNotFoundException;
import com.mpteam1.exception.custom.exception.StudentNotFoundException;
import com.mpteam1.exception.custom.exception.TeacherNotFoundException;
import com.mpteam1.repository.ClassRepository;
import com.mpteam1.repository.StudentRepository;
import com.mpteam1.repository.TeacherRepository;
import com.mpteam1.repository.TokenRepository;
import com.mpteam1.services.StudentService;
import com.mpteam1.utils.GenerateAccountName;
import com.mpteam1.utils.impl.ConvertToEnglishNameImpl;
import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {
    private StudentRepository studentRepository;
    private ClassRepository classRepository;
    private TeacherRepository teacherRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private GenerateAccountName generateAccountName;
    private TokenRepository tokenRepository;
    String imageUrl;

    @Override
    @Transactional
    public void bulkInsert(StudentCreateWrapper studentCreateWrapper) throws StudentDuplicateException,
            ClassNotFoundException {
        List<StudentDTOCreate> studentDTOList = studentCreateWrapper.getStudents();
        List<Student> studentList = new ArrayList<>();
        Set<String> emailSet = new HashSet<>();
        for (StudentDTOCreate dto : studentDTOList) {
            emailSet.add(dto.getEmail());
            if (studentRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new StudentDuplicateException("found duplicate student with email :" + dto.getEmail());
            }

            String accountName = generateAccountName.generate(ConvertToEnglishNameImpl.convertToEnglishName(dto.getFullName()));

            Set<Clazz> classes = new HashSet<>();
            List<String> clazzNames = dto.getClazzName();
            if (!clazzNames.isEmpty()) {
                for (String clazzName : clazzNames) {
                    classes.add(classRepository.findByName(clazzName)
                            .orElseThrow(() -> new ClassNotFoundException("Class not found with name : " + clazzName)));
                }
            }
            studentList.add(Converter.ConvertStudentDTOToEntity(accountName, dto, classes, bCryptPasswordEncoder));
        }
        if (emailSet.size() != studentDTOList.size()) {
            throw new StudentDuplicateException("insert students with duplicate email.");
        }
        studentRepository.saveAll(studentList);
    }

    @Override
    @Transactional
    public void edit(StudentDTOUpdate studentDTOUpdate) throws StudentNotFoundException {
        Optional<Student> optionalStudent = studentRepository.findById(studentDTOUpdate.getId());
        if (optionalStudent.isEmpty()) {
            throw new StudentNotFoundException("Student not found with ID: " + studentDTOUpdate.getId());
        }
        Student student = optionalStudent.get();
        student.setFullName(studentDTOUpdate.getFullName());
        student.setDateOfBirth(studentDTOUpdate.getDateOfBirth());
        student.setGender(studentDTOUpdate.isGender());
        student.setPhoneNumber(studentDTOUpdate.getPhoneNumber());
        student.setAddress(studentDTOUpdate.getAddress());
        studentRepository.save(student);
    }

    @Override
    @Transactional
    public void bulkDelete(StudentDTODeleteWrapper studentDeleteWrapper) throws StudentNotFoundException {
        for (StudentDTODelete dto : studentDeleteWrapper.getStudents()) {
            Student student = studentRepository.findById(dto.getId())
                    .orElseThrow(() -> new StudentNotFoundException("Delete student not found with ID: " + dto.getId()));
            tokenRepository.deleteByUser_Id(dto.getId());
            student.remove();
            studentRepository.deleteById(dto.getId());
        }
    }

    @Override
    public StudentDTOResponse findById(Long id) throws StudentNotFoundException {
        List<Tuple> students = studentRepository.findStudentById(id);
        if (students == null || students.isEmpty()) {
            throw new StudentNotFoundException("Student was not found with ID: " + id);
        }
        return studentMapper(students).getContent().get(0);
    }

    @Override
    public PageForView<StudentDTOResponse> list(int pageNumber, int pageSize, String keyword)
            throws StudentNotFoundException, TeacherNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String email = authentication.getName();
        if (role.equals("ADMIN")) {
            List<Tuple> students = studentRepository.findAllWithFilter(pageNumber, pageSize, keyword);
            PageForView<StudentDTOResponse> listStudent = studentMapper(students);
            listStudent.setPage(pageNumber);
            listStudent.setTotalElement(studentRepository.countFindAllWithFilter(keyword));
            return listStudent;
        }else if(role.equals("TEACHER")) {
            Teacher teacher = teacherRepository.findByEmail(email);
            if(teacher == null) {
                throw new TeacherNotFoundException("Teacher not found with email: " + email);
            }
            return findAllByTeacher(pageNumber, pageSize, teacher.getId(), keyword);
        }
        return null;
    }

    @Override
    public PageForView<StudentDTOResponse> findAllStudentOutSideClazz(int pageNumber, int pageSize, Long clazzId, String keyword) {
        List<Tuple> students = studentRepository.findAllStudentOutSideClazz(pageNumber, pageSize, clazzId, keyword);
        PageForView<StudentDTOResponse> listStudent = studentMapper(students);
        listStudent.setPage(pageNumber);
        listStudent.setTotalElement(studentRepository.countAllStudentOutSideClazz(clazzId, keyword));
        return listStudent;
    }

    @Override
    public PageForView<StudentDTOResponse> findAllStudentInSideClazz(int pageNumber, int pageSize, Long clazzId, String keyword) {
        List<Tuple> students = studentRepository.findAllStudentInSideClazz(pageNumber, pageSize, clazzId, keyword);
        PageForView<StudentDTOResponse> listStudent = studentMapper(students);
        listStudent.setPage(pageNumber);
        listStudent.setTotalElement(studentRepository.countFindAllStudentInSideClazz(clazzId, keyword));
        return listStudent;
    }

    @Override
    public PageForView<StudentDTOResponse> findAllByTeacher(int pageNumber, int pageSize, Long teacherId, String keyword) {
        List<Tuple> students = studentRepository.findAllStudentByTeacher(pageNumber, pageSize, teacherId, keyword);
        PageForView<StudentDTOResponse> listStudent = studentMapper(students);
        listStudent.setPage(pageNumber);
        listStudent.setTotalElement(studentRepository.countFindAllStudentByTeacher(teacherId, keyword));
        return listStudent;
    }

    @Override
    public StudentStatus getStudentStatus() {
        long totalCount = studentRepository.count();
        long inActiveCount = studentRepository.countInactiveStudent();
        return new StudentStatus(inActiveCount, totalCount - inActiveCount, totalCount);
    }

    public PageForView<StudentDTOResponse> studentMapper(List<Tuple> students) {
        Map<Long, StudentDTOResponse> studentMap = new LinkedHashMap<>();
        for (Tuple tuple : students) {
            Long studentId = (Long) tuple.get("id");
            StudentDTOResponse student = studentMap.get(studentId);
            if (student == null) {
                student = new StudentDTOResponse();
                student.setId(studentId);
                student.setFullName((String) tuple.get("fullName"));
                student.setEmail((String) tuple.get("email"));
                student.setDateOfBirth(Converter.dateToString((Date) tuple.get("birthday")));
                student.setAddress((String) tuple.get("address"));
                student.setGender((Boolean) tuple.get("gender"));
                student.setPhoneNumber((String) tuple.get("phoneNumber"));
                String avatarName = (String) tuple.get("avatarName");
                student.setAccountName((String) tuple.get("accountName"));
                if (avatarName != null && !avatarName.isEmpty() && imageUrl != null) {
                    student.setAvatarUrl(String.format(imageUrl, avatarName));
                }
                student.setClasses(new ArrayList<>());
                studentMap.put(studentId, student);
            }
            Long classId = (Long) tuple.get("clazzId");
            ClazzAndTeacher clazzAndTeacher = new ClazzAndTeacher();
            if (classId != null) {
                clazzAndTeacher.setId(classId);
                clazzAndTeacher.setName((String) tuple.get("clazzName"));
                clazzAndTeacher.setCreatedDate(Converter.dateToString((Date) tuple.get("createdDate")));
                clazzAndTeacher.setStartTime((Converter.dateToString((Date) tuple.get("startDate"))));
                clazzAndTeacher.setEndTime((Converter.dateToString((Date) tuple.get("endDate"))));

                TeacherInfoDTO teacherInfo = new TeacherInfoDTO();
                teacherInfo.setId((Long) tuple.get("teacherId"));
                teacherInfo.setName((String) tuple.get("teacherName"));

                Optional<ClazzAndTeacher> existingClazz = student.getClasses().stream()
                        .filter(clazz -> clazz.getId().equals(classId)).findFirst();
                if (existingClazz.isPresent()) {
                    existingClazz.get().setTeacher(teacherInfo);
                } else {
                    clazzAndTeacher.setTeacher(teacherInfo);
                    student.getClasses().add(clazzAndTeacher);
                }
            }
            student.setClassCount(student.getClasses().size());
        }
        PageForView<StudentDTOResponse> listStudent = new PageForView<>();
        listStudent.setContent(new ArrayList<>(studentMap.values()));
        listStudent.setSize(studentMap.values().size());
        return listStudent;
    }

}

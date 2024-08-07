package com.mpteam1.services.impl;

import com.mpteam1.dto.request.teacher.TeacherDTOCreate;
import com.mpteam1.dto.request.teacher.TeacherDTODelete;
import com.mpteam1.dto.request.teacher.TeacherDTOUpdate;
import com.mpteam1.dto.response.common.Converter;
import com.mpteam1.dto.response.common.PageForView;
import com.mpteam1.dto.response.teacher.TeacherDTOResponse;
import com.mpteam1.dto.response.teacher.TeacherDetail;
import com.mpteam1.entities.Subjects;
import com.mpteam1.entities.Teacher;
import com.mpteam1.entities.enums.EEmailStatus;
import com.mpteam1.entities.enums.EEmploymentStatus;
import com.mpteam1.entities.enums.ERole;
import com.mpteam1.exception.custom.exception.TeacherDuplicateException;
import com.mpteam1.exception.custom.exception.TeacherNotFoundException;
import com.mpteam1.repository.SubjectRepository;
import com.mpteam1.repository.TeacherRepository;
import com.mpteam1.repository.TokenRepository;
import com.mpteam1.services.EmailService;
import com.mpteam1.services.TeacherService;
import com.mpteam1.utils.GenerateAccountName;
import com.mpteam1.utils.impl.ConvertToEnglishNameImpl;
import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TeacherServiceImpl implements TeacherService {
    private TeacherRepository teacherRepository;
    private SubjectRepository subjectRepository;
    private TokenRepository tokenRepository;
    private EmailService emailService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private GenerateAccountName generateAccountName;

    @Override
    @Transactional
    public TeacherDTOResponse add(TeacherDTOCreate teacherDTOCreate) throws TeacherDuplicateException {
        if (teacherRepository.findByEmail(teacherDTOCreate.getEmail()) != null) {
            throw new TeacherDuplicateException("Found duplicate teacher with email :" + teacherDTOCreate.getEmail());
        }

        String accountName = generateAccountName.generate(ConvertToEnglishNameImpl.convertToEnglishName(teacherDTOCreate.getFullName()));

        Teacher teacher = new Teacher(
                bCryptPasswordEncoder.encode(teacherDTOCreate.getPassword()),
                teacherDTOCreate.getQualification(),
                EEmploymentStatus.ACTIVE,
                teacherDTOCreate.getStartDate(),
                teacherDTOCreate.getEmail(),
                teacherDTOCreate.getFullName(),
                teacherDTOCreate.getAddress(),
                teacherDTOCreate.getDateOfBirth(),
                teacherDTOCreate.getPhoneNumber(),
                teacherDTOCreate.isGender(),
                ERole.TEACHER,
                accountName
        );
        List<String> subjectNameList = teacherDTOCreate.getSubjects();
        if (!subjectNameList.isEmpty()) {
            for (String subjectName : subjectNameList) {
                Subjects subject = subjectRepository.findByName(subjectName.toLowerCase())
                        .orElseGet(() -> new Subjects(subjectName.toLowerCase()));
                teacher.addSubject(subject);
            }
        }
        teacherRepository.save(teacher);
        emailService.sendHtmlMessage(EEmailStatus.NEW, teacher.getEmail(), "Completed registration", "");
        return Converter.convertTeacherEntityToDTO(teacher);
    }

    @Override
    public TeacherDTOResponse findById(long id) throws TeacherNotFoundException {
        Optional<Teacher> optionalTeacher = teacherRepository.findById(id);
        if (optionalTeacher.isPresent()) {
            return Converter.convertTeacherEntityToDTO(optionalTeacher.get());
        } else {
            throw new TeacherNotFoundException("Teacher not found with ID: " + id);
        }
    }

    @Override
    @Transactional
    public TeacherDTOResponse edit(TeacherDTOUpdate teacherDTOUpdate) throws TeacherNotFoundException {
        System.out.println(teacherDTOUpdate);
        Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherDTOUpdate.getId());
        if (optionalTeacher.isPresent()) {
            Teacher teacher = optionalTeacher.get();
            teacher.setQualification(teacherDTOUpdate.getQualification());
            teacher.setEEmploymentStatus(EEmploymentStatus.valueOf(teacherDTOUpdate.getEmploymentStatus()));
            teacher.setStartDate(teacherDTOUpdate.getStartDate());
            teacher.setFullName(teacherDTOUpdate.getFullName());
            teacher.setAddress(teacherDTOUpdate.getAddress());
            teacher.setDateOfBirth(teacherDTOUpdate.getDateOfBirth());
            teacher.setPhoneNumber(teacherDTOUpdate.getPhoneNumber());
            teacher.setGender(teacherDTOUpdate.isGender());

            Set<String> subjectNameList = teacherDTOUpdate.getSubjects();
            if (!subjectNameList.isEmpty()) {
                Set<Subjects> subjects = new HashSet<>();
                for (String subjectName : subjectNameList) {
                    Subjects subject = subjectRepository.findByName(subjectName.toLowerCase())
                            .orElseGet(() -> new Subjects(subjectName.toLowerCase()));
                    subjects.add(subject);
                }
                teacher.setSubjects(subjects);
            }
            teacherRepository.save(teacher);
            return Converter.convertTeacherEntityToDTO(teacher);
        } else {
            throw new TeacherNotFoundException("Teacher not found with ID: " + teacherDTOUpdate.getId());
        }
    }

    @Override
    public PageForView<TeacherDTOResponse> list(int pageNum, int pageSize, String keyword) {
        PageForView<TeacherDTOResponse> listTeacher = new PageForView<>();
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(order));
        Page<Teacher> page = teacherRepository.findAllByKeyword(keyword, pageable);
        if (page != null) {
            listTeacher.setContent(page.getContent().stream().map(Converter::convertTeacherEntityToDTO).collect(Collectors.toList()));
            listTeacher.setTotalElement(page.getTotalElements());
            listTeacher.setSize(page.getSize());
            listTeacher.setPage(page.getNumber());
        }
        return listTeacher;
    }

    @Override
    public PageForView<TeacherDetail> fetchAllByCLazzId(int pageNum, int pageSize, Long clazzId) {
        List<Tuple> results = teacherRepository.fetchTeacherInsideClazz(pageNum, pageSize,clazzId);
        PageForView<TeacherDetail> listTeacher = teacherMapper(results, pageNum);
        listTeacher.setTotalElement(teacherRepository.countTeacherInsideClazz(clazzId));
        return listTeacher;
    }
    @Override
    public PageForView<TeacherDetail> fetchAllAvailableTeacher(int pageNum, int pageSize, Long clazzId) {
        List<Tuple> results = teacherRepository.fetchAllAvailableTeacher(pageNum, pageSize,clazzId);
        PageForView<TeacherDetail> listTeacher = teacherMapper(results, pageNum);
        listTeacher.setTotalElement(teacherRepository.countTeacherOutSideClazz(clazzId));
        return listTeacher;
    }

    public PageForView<TeacherDetail> teacherMapper(List<Tuple> tuples, int pageNum) {
        List<TeacherDetail> responseList = new ArrayList<>();
        for(Tuple tuple : tuples) {
            TeacherDetail teacher = new TeacherDetail();
            teacher.setId((Long) tuple.get("teacherId"));
//            teacher.setAge((Integer) tuple.get("age"));
            teacher.setQualification((String) tuple.get("qualification"));
            teacher.setEmploymentStatus((tuple.get("employmentStatus")).toString());
            teacher.setStartDate(Converter.dateToString((Date)tuple.get("startDate")));
            teacher.setEmail((String) tuple.get("email"));
            teacher.setFullName((String) tuple.get("fullName"));
            teacher.setAddress((String) tuple.get("address"));
            teacher.setDateOfBirth(Converter.dateToString((Date) tuple.get("dateOfBirth")));
            teacher.setPhoneNumber((String) tuple.get("phoneNumber"));
            teacher.setGender((Boolean) tuple.get("gender"));
            teacher.setRole(tuple.get("role").toString());
            teacher.setAvatarUrl((String)tuple.get("avatarUrl"));
            responseList.add(teacher);
        }
        PageForView<TeacherDetail> listTeacher = new PageForView<>();
        listTeacher.setContent(responseList);
        listTeacher.setSize(responseList.size());
        listTeacher.setPage(pageNum);
        return listTeacher;
    }

    @Override
    @Transactional
    public boolean deleteById(long id) throws TeacherNotFoundException {
        Optional<Teacher> optionalTeacher = teacherRepository.findById(id);
        if (optionalTeacher.isPresent()) {
            tokenRepository.deleteByUser_Id(id);
            Teacher teacher = optionalTeacher.get();
            teacher.remove();
            teacherRepository.deleteById(id);
            return true;
        } else {
            throw new TeacherNotFoundException("Teacher not found with ID: " + id);
        }
    }

    @Override
    @Transactional
    public void bulkDelete(List<TeacherDTODelete> teacherDTODeletes) throws TeacherNotFoundException {
        for (TeacherDTODelete teacherDTO : teacherDTODeletes) {
            System.out.println(teacherDTO);
            Teacher teacher = teacherRepository.findById(teacherDTO.getId())
                    .orElseThrow(() -> new TeacherNotFoundException("Delete teacher not found with ID: " + teacherDTO.getId()));
            teacher.remove();
            tokenRepository.deleteByUser_Id(teacherDTO.getId());
            teacherRepository.delete(teacher);
        }
    }

    @Override
    public long countTeachersThatHaveClasses() {
        return teacherRepository.countByClazzesNotEmpty();
    }

    @Override
    public long countTeachers() {
        return teacherRepository.count();
    }


}

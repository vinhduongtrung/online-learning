package com.mpteam1.dto.response.common;

import com.mpteam1.dto.request.clazz.ClazzDTOCreate;
import com.mpteam1.dto.request.student.StudentDTOCreate;
import com.mpteam1.dto.response.teacher.TeacherDTOResponse;
import com.mpteam1.entities.*;
import com.mpteam1.entities.enums.ERole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class Converter {

    public static boolean IsQuizBeingTakenByStudents(TakenQuiz takenQuiz, Instant now) {
        Instant startTime = takenQuiz.getStartTime();
        Instant endTime = takenQuiz.getEndTime();
        if(now.isAfter(startTime) || now.equals(startTime)){
            return now.isBefore(endTime) || now.equals(endTime);
        }
        return false;
    }
    public static void isValidOpenAndCloseTime(Instant open, Instant close, int duration) {
        if(!close.isAfter(open)) {
            throw new DateTimeException("close time need to be after open time");
        }
        int gap = (int) Duration.between(open, close).toMinutes();
        if(duration > gap) {
            throw new DateTimeException("time duration " + duration + " was out of range");
        }
    }
    public static String dateToString(Date date) {
        if (date == null) {
            return "No time provided";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
    public static TeacherDTOResponse convertTeacherEntityToDTO(Teacher teacher) {
        TeacherDTOResponse dto = new TeacherDTOResponse();
        dto.setId(teacher.getId());
        dto.setAge(Period.between(teacher.getDateOfBirth(), LocalDate.now()).getYears());
        dto.setQualification(teacher.getQualification());
        dto.setEmploymentStatus(teacher.getEEmploymentStatus().toString());
        dto.setStartDate(String.valueOf(teacher.getStartDate()));
        dto.setEmail(teacher.getEmail());
        dto.setFullName(teacher.getFullName());
        dto.setAddress(teacher.getAddress());
        dto.setDateOfBirth(String.valueOf(teacher.getDateOfBirth()));
        dto.setPhoneNumber(teacher.getPhoneNumber());
        dto.setGender(teacher.isGender());
        dto.setSubjects(teacher.getSubjects().stream().map(Subjects::getName).collect(Collectors.toList()));
        dto.setRole(teacher.getERole().toString());
        dto.setAccountName(teacher.getAccountName());
        if (teacher.getAvatarName() != null)
            dto.setAvatarUrl("https://storage.googleapis.com/test-firebase-storage-d1b61.appspot.com/" + teacher.getAvatarName());
        return dto;
    }

    public static Clazz ConvertClazzDTOToEntity(ClazzDTOCreate clazzDTOCreate, Teacher teacher) {
        Clazz clazz = new Clazz();
        clazz.setName(clazzDTOCreate.getName());
        Teacher existingTeacher = clazz.getTeacher();
        if (existingTeacher == null) {
            clazz.setTeacher(teacher);
        }
        return clazz;
    }

    public static Student ConvertStudentDTOToEntity(String accountName,StudentDTOCreate dto, Set<Clazz> clazzes, BCryptPasswordEncoder bCryptPasswordEncoder) {
        Student student = new Student();
        student.setFullName(dto.getFullName());
        student.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        student.setClazzes(clazzes);
        student.setAddress(dto.getAddress());
        student.setGender(dto.isGender());
        student.setEmail(dto.getEmail());
        student.setDateOfBirth(dto.getDateOfBirth());
        student.setPhoneNumber(dto.getPhoneNumber());
        student.setERole(ERole.STUDENT);
        student.setAccountName(accountName);
        return student;
    }

    public static String convertInstantToString(Instant instant) {
        if (instant == null) {
            return "No time provided";
        }
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }

    public static String convertLocalDateToString(LocalDate localDate) {
        if (localDate == null) {
            return "No time provided";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(formatter);
    }
}

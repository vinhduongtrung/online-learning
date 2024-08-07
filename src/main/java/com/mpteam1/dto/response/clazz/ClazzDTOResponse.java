package com.mpteam1.dto.response.clazz;

import com.mpteam1.dto.response.schedule.ScheduleInfoDTO;
import com.mpteam1.dto.response.student.ClassStudentInfoDTO;
import com.mpteam1.dto.response.teacher.TeacherInfoDTO;
import com.mpteam1.entities.Clazz;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClazzDTOResponse {
    private Long id;
    private String name;
    private String classCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private String activeState;
    private Instant createdDate;
    private String description;
    private Integer maximumCapacity;
    private Integer currentCapacity;
    private TeacherInfoDTO teacherInfo;
    private Set<ScheduleInfoDTO> scheduleInfos;
    private Set<ClassStudentInfoDTO> students;

    public static ClazzDTOResponse convertClazzToDTO(Clazz clazz) {
        ClazzDTOResponse dto = new ClazzDTOResponse();
        dto.setId(clazz.getId());
        dto.setName(clazz.getName());
        dto.setTeacherInfo(TeacherInfoDTO.toDTO(clazz.getTeacher()));
        dto.setClassCode(clazz.getClazzCode());
        dto.setStartDate(clazz.getStartDate());
        dto.setEndDate(clazz.getEndDate());
        dto.setCreatedDate(clazz.getCreatedDate());
        if (!dto.getStartDate().isAfter(LocalDate.now()) && !dto.getEndDate().isBefore(LocalDate.now())) {
            dto.setActiveState("ACTIVE");
        } else {
            dto.setActiveState("INACTIVE");
        }
        dto.setDescription(clazz.getDescription());
        dto.setMaximumCapacity(clazz.getMaximumCapacity());
        dto.setCurrentCapacity(clazz.getCurrentCapacity());
        if (clazz.getStudents() != null && !clazz.getStudents().isEmpty())
            dto.setStudents(clazz.getStudents().stream().map(ClassStudentInfoDTO::fromStudent).collect(Collectors.toSet()));
        if(clazz.getSchedules() != null && !clazz.getSchedules().isEmpty())
            dto.setScheduleInfos(clazz.getSchedules().stream().map(ScheduleInfoDTO::toDTO).collect(Collectors.toSet()));
        return dto;
    }
}

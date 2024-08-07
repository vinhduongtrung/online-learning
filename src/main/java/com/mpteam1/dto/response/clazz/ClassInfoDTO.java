package com.mpteam1.dto.response.clazz;

import com.mpteam1.dto.response.teacher.TeacherInfoDTO;
import com.mpteam1.entities.Clazz;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClassInfoDTO {
    private Long id;
    private String name;
    private String startTime;
    private String endTime;
    private String createdDate;
    private TeacherInfoDTO teacher;

    public static ClassInfoDTO toDTO(Clazz clazz) {
        return ClassInfoDTO.builder()
                .id(clazz.getId())
                .name(clazz.getName())
                .build();
    }
}

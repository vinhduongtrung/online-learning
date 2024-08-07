package com.mpteam1.dto.response.clazz;

import com.mpteam1.dto.response.teacher.TeacherInfoDTO;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ClazzAndTeacher {
    private Long id;
    private String name;
    private String startTime;
    private String endTime;
    private String createdDate;
    private TeacherInfoDTO teacher;
}

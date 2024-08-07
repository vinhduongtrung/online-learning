package com.mpteam1.dto.request.clazz;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ClazzDTOAssignTeachers {
    private Long classId;
    private Long teacherId;
}

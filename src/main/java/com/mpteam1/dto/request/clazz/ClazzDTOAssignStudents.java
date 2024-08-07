package com.mpteam1.dto.request.clazz;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ClazzDTOAssignStudents {
    private Long classId;
    private Set<Long> studentIds;
}

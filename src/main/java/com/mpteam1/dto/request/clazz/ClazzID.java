package com.mpteam1.dto.request.clazz;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClazzID {
    @NotNull
    private Long classId;
}

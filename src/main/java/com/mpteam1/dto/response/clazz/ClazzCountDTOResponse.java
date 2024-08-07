package com.mpteam1.dto.response.clazz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClazzCountDTOResponse {
    private long classCount;
    private long activeClassCount;
}

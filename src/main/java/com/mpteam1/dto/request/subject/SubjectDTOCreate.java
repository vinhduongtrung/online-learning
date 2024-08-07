package com.mpteam1.dto.request.subject;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectDTOCreate {

    @NotBlank
    private String name;
}

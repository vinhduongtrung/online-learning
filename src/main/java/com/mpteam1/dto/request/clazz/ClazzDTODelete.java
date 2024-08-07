package com.mpteam1.dto.request.clazz;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClazzDTODelete {
    @NotBlank
    private Long id;
}

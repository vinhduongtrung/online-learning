package com.mpteam1.dto.request.quiz;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizIdDTO {
    @NotNull
    private Long id;
}

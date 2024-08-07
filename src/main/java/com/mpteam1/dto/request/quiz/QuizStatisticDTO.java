package com.mpteam1.dto.request.quiz;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizStatisticDTO {
    @NotNull
    private Long clazzId;
    @NotNull
    private Long quizId;
}

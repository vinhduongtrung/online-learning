package com.mpteam1.dto.request.answer;

import com.mpteam1.entities.Answer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnswerReqDTO {
    @NotBlank(message = "Content of answer is mandatory")
    private String content;

    @NotNull(message = "IsCorrect of answer is mandatory")
    private Boolean isCorrect;

    public Answer convertTo() {
        return Answer.builder()
                .content(content)
                .isCorrect(isCorrect)
                .build();
    }
}

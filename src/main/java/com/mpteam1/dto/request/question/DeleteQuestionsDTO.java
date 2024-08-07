package com.mpteam1.dto.request.question;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteQuestionsDTO {
    @NotNull
    private Long quizId;
    @Size(min = 1, message = "Must have at least 1 question")
    private Set<Long> questionIds;
}

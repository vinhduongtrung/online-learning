package com.mpteam1.dto.request.question;

import com.mpteam1.dto.request.answer.AnswerReqDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateQuestionDTO {
    @NotNull
    private Long quizId;

    @NotNull
    private Long questionId;

    @Pattern(regexp = "SINGLE_CHOICE|MULTIPLE_CHOICE|TRUE_FALSE", message = "Invalid type of question")
    private String type;

    @NotBlank
    private String content;

    private List<AnswerReqDTO> answers;
}

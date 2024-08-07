package com.mpteam1.dto.request.question;

import com.mpteam1.dto.request.answer.AnswerReqDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
public class QuestionReqDTO {
    @Pattern(regexp = "SINGLE_CHOICE|MULTIPLE_CHOICE|TRUE_FALSE", message = "Invalid type of question")
    private String type;

    @NotBlank
    private String content;
    private List<AnswerReqDTO> answers;
}

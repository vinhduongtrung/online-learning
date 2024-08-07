package com.mpteam1.dto.response.question;


import com.mpteam1.dto.response.answer.AnswerDetailDTOResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class QuestionResultDTO {
    private long id;
    private String type;
    private String content;
    private List<AnswerDetailDTOResponse> answers;
    private List<Long> chosenAnswers;
}

package com.mpteam1.dto.request.taken_quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
public class GradeQuizDTO {
    private Long takenQuizId;
    private List<TakenAnswerReqDTO> answers = new ArrayList<>();
}



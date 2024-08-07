package com.mpteam1.dto.response.answer;

import com.mpteam1.entities.Answer;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnswerDetailDTOResponse {
    private Long id;

    private String content;

    private Boolean isCorrect;

    public static AnswerDetailDTOResponse toDTO(Answer answer) {
        AnswerDetailDTOResponse answerDetailDTOResponse = new AnswerDetailDTOResponse();
        answerDetailDTOResponse.setId(answer.getId());
        answerDetailDTOResponse.setContent(answer.getContent());
        answerDetailDTOResponse.setIsCorrect(answer.getIsCorrect());
        return answerDetailDTOResponse;
    }
}

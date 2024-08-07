package com.mpteam1.dto.response.question;

import com.mpteam1.dto.response.answer.AnswerDetailDTOResponse;
import com.mpteam1.entities.Answer;
import com.mpteam1.entities.Question;
import com.mpteam1.entities.enums.EQuestionType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class QuestionDetailDTOResponse {

    private long id;

    private EQuestionType type;

    private String content;

    private List<AnswerDetailDTOResponse> answers = new ArrayList<>();

    public static QuestionDetailDTOResponse toDTO(Question question) {
        QuestionDetailDTOResponse dto = new QuestionDetailDTOResponse();
        dto.setId(question.getId());
        dto.setType(question.getType());
        dto.setContent(question.getContent());
        for(Answer answer : question.getAnswers()) {
            dto.getAnswers().add(AnswerDetailDTOResponse.toDTO(answer));
        }
        return dto;
    }
}

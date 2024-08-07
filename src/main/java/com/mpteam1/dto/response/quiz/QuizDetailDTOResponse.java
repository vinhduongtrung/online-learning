package com.mpteam1.dto.response.quiz;

import com.mpteam1.dto.response.clazz.ClassInfoDTO;
import com.mpteam1.dto.response.question.QuestionDetailDTOResponse;
import com.mpteam1.dto.response.user.UserDetailDTOResponse;
import com.mpteam1.entities.Question;
import com.mpteam1.entities.Quiz;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class QuizDetailDTOResponse {
    private long id;

    private List<QuestionDetailDTOResponse> questions = new ArrayList<>();

    private ClassInfoDTO clazz;

    private Integer duration;

    private Instant openTime;

    private Instant closedTime;

    private String title;

    private String description;

    private String status;

    private Instant createdDate;

    private UserDetailDTOResponse createdBy;

    public static QuizDetailDTOResponse toDTO(Quiz quiz, boolean isGetQuestions) {
        QuizDetailDTOResponse dto = new QuizDetailDTOResponse();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setDuration(quiz.getDuration());
        dto.setOpenTime(quiz.getOpenTime());
        dto.setClosedTime(quiz.getClosedTime());
        dto.setClazz(ClassInfoDTO.toDTO(quiz.getClazz()));
        dto.setStatus(String.valueOf(quiz.getStatus()));
        dto.setCreatedDate(quiz.getCreatedDate());
        dto.setCreatedBy(UserDetailDTOResponse.fromUser(quiz.getCreatedBy()));
        if (isGetQuestions)
            dto.setQuestions(quiz.getQuestions().stream().map(QuestionDetailDTOResponse::toDTO).toList());
        return dto;
    }
}

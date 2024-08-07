package com.mpteam1.dto.response.quiz;

import com.mpteam1.dto.response.clazz.ClassInfoDTO;
import com.mpteam1.dto.response.user.UserDetailDTOResponse;
import com.mpteam1.entities.Quiz;
import com.mpteam1.entities.TakenQuiz;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
public class QuizBasicInfoDTO {
    private long id;

    private ClassInfoDTO clazz;

    private Integer duration;

    private Instant openTime;

    private Instant closedTime;

    private String title;

    private String description;

    private String status;

    private Instant createdDate;

    private UserDetailDTOResponse createdBy;

    private int numberOfQuestions;

    private Double score;

    public static QuizBasicInfoDTO toDTO(Quiz quiz) {
        QuizBasicInfoDTO dto = new QuizBasicInfoDTO();
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
        dto.setNumberOfQuestions(quiz.getQuestions().size());
        return dto;
    }

    public static QuizBasicInfoDTO toDTO(Quiz quiz, TakenQuiz takenQuiz) {
        QuizBasicInfoDTO quizBasicInfoDTO = toDTO(quiz);
        if(takenQuiz != null) {
            quizBasicInfoDTO.setStatus("SUBMITTED");
            quizBasicInfoDTO.setScore(takenQuiz.getScore());
        } else {
            if(quizBasicInfoDTO.getOpenTime().isAfter(Instant.now())) quizBasicInfoDTO.setStatus("NOT AVAILABLE");
            else {
                if(quizBasicInfoDTO.getClosedTime().isBefore(Instant.now())) {
                    quizBasicInfoDTO.setStatus("MISSED");
                    quizBasicInfoDTO.setScore(0D);
                }
                else quizBasicInfoDTO.setStatus("AVAILABLE");
            }
        }
        return quizBasicInfoDTO;
    }
}

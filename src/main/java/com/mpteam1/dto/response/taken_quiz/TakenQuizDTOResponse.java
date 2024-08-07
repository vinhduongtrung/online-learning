package com.mpteam1.dto.response.taken_quiz;

import com.mpteam1.dto.response.quiz.QuizDetailDTOResponse;
import com.mpteam1.entities.TakenQuiz;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TakenQuizDTOResponse {
    private Long id;
    private QuizDetailDTOResponse quiz;
    private Instant startTime;
    private Instant endTime;
    private Double score;
    private Integer numOfTabLeaves;

    public static TakenQuizDTOResponse toDTO(TakenQuiz takenQuiz) {
        return TakenQuizDTOResponse.builder()
                .id(takenQuiz.getId())
                .startTime(takenQuiz.getStartTime())
                .endTime(takenQuiz.getEndTime())
                .score(takenQuiz.getScore())
                .numOfTabLeaves(takenQuiz.getNumOfTabLeaves())
                .quiz(QuizDetailDTOResponse.toDTO(takenQuiz.getQuiz(), true))
                .build();
    }
}

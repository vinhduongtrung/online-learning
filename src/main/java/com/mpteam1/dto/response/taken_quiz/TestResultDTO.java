package com.mpteam1.dto.response.taken_quiz;

import com.mpteam1.dto.response.answer.AnswerDetailDTOResponse;
import com.mpteam1.dto.response.question.QuestionResultDTO;
import com.mpteam1.dto.response.quiz.QuizBasicInfoDTO;
import com.mpteam1.dto.response.quiz.QuizDetailDTOResponse;
import com.mpteam1.dto.response.student.StudentBasicInfoDTO;
import com.mpteam1.entities.Answer;
import com.mpteam1.entities.Question;
import com.mpteam1.entities.TakenQuiz;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TestResultDTO {
    private Long id;
    private Instant startTime;
    private Instant endTime;
    private Double score;
    private StudentBasicInfoDTO student;
    private List<QuestionResultDTO> questions;
    private QuizDetailDTOResponse quiz;

    public static TestResultDTO toDTO(TakenQuiz takenQuiz) {
        TestResultDTO testResult = TestResultDTO.builder()
                .id(takenQuiz.getId())
                .startTime(takenQuiz.getStartTime())
                .endTime(takenQuiz.getEndTime())
                .score(takenQuiz.getScore())
                .student(StudentBasicInfoDTO.toDTO(takenQuiz.getStudent()))
                .quiz(QuizDetailDTOResponse.toDTO(takenQuiz.getQuiz(), false))
                .build();
        List<QuestionResultDTO> questions = takenQuiz.getTakenAnswers().stream().map(takenAnswer -> {
            Question questionEntity = takenAnswer.getQuestion();
            QuestionResultDTO question = QuestionResultDTO.builder()
                    .id(questionEntity.getId())
                    .type(String.valueOf(questionEntity.getType()))
                    .content(questionEntity.getContent())
                    .answers(questionEntity.getAnswers().stream().map(AnswerDetailDTOResponse::toDTO).toList())
                    .chosenAnswers(takenAnswer.getAnswers().stream().map(Answer::getId).toList())
                    .build();
            return question;
        }).toList();
        testResult.setQuestions(questions);
        return testResult;
    }
}

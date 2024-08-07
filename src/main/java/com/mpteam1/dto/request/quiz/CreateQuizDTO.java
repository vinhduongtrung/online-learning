package com.mpteam1.dto.request.quiz;

import com.mpteam1.dto.request.question.QuestionReqDTO;
import com.mpteam1.entities.Answer;
import com.mpteam1.entities.Question;
import com.mpteam1.entities.Quiz;
import com.mpteam1.entities.enums.EQuestionType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CreateQuizDTO {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    @Min(value = 1, message = "Must be greater than 0")
    private Integer duration;

    @NotNull
    @FutureOrPresent(message = "Must be in future")
    private Instant openTime;

    @NotNull
    @FutureOrPresent(message = "Must be in future")
    private Instant closedTime;

    @NotNull
    private Long clazzId;

    private List<QuestionReqDTO> questions;

    public Quiz convertTo() {
        Quiz quiz = Quiz.builder()
                .title(title)
                .description(description)
                .duration(duration)
                .openTime(openTime)
                .closedTime(closedTime)
                .build();
        if(this.getQuestions() != null && !this.getQuestions().isEmpty()) {
            List<Question> questions = this.getQuestions().stream().map(questionDTO -> {
                Question question = Question.builder()
                        .type(EQuestionType.valueOf(questionDTO.getType()))
                        .content(questionDTO.getContent())
                        .build();
                List<Answer> answers = questionDTO.getAnswers().stream().map(answerDTO -> {
                    Answer answer = answerDTO.convertTo();
                    answer.setQuestion(question);
                    return answer;
                }).toList();
                question.setAnswers(answers);
                question.setQuiz(quiz);
                return question;
            }).toList();
            quiz.setQuestions(questions);
        }
        return quiz;
    }
}

package com.mpteam1.dto.response.quiz;

import lombok.*;

import java.time.Instant;


@Builder
@Getter
@Setter
public class QuizStatisticResponse {
    private Long studentId;

    private String studentName;

    private String accountName;

    private String avatarName;

    private Double score;

    private Instant startTime;

    private Instant endTime;

    private String takenTime;

    private int numOfTabLeaves;

    private String quizStatus;
}

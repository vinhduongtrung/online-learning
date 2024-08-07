package com.mpteam1.dto.request.quiz;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateQuizBasicInfo {
    @NotNull
    private Long id;

    @NotBlank
    @Pattern(regexp = "PUBLISH|PRIVATE", message = "{quizStatus.message}")
    private String quizStatus;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    @Min(value = 1, message = "Must be greater than 0")
    private Integer duration;

    @NotNull
    @FutureOrPresent
    private Instant openTime;

    @NotNull
    @FutureOrPresent
    private Instant closedTime;


    public void validate() {
        int gap = (int) Duration.between(openTime, closedTime).toMinutes();
        Instant now = Instant.now();
        if(!this.closedTime.isAfter(this.openTime)){
            throw new DateTimeException("closed time need to be after open time");
        }
        if(now.isAfter(openTime)){
            throw new DateTimeException("invalid open time");
        }
        if(duration > gap) {
            throw new DateTimeException("time duration " + duration + " was out of range");
        }
    }
}

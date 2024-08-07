package com.mpteam1.dto.response.student;

import com.mpteam1.dto.response.answer.AnswerDetailDTOResponse;
import com.mpteam1.entities.Answer;
import com.mpteam1.entities.Student;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
public class StudentGiveWrongAnswer {

    private Long id;

    private String fullName;

    private String accountName;

    private List<AnswerDetailDTOResponse> wrongAnswers;
    public static StudentGiveWrongAnswer toDTO(Student student, List<Answer> wrongAnswers) {
        StudentGiveWrongAnswer dto =  StudentGiveWrongAnswer.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .accountName(student.getAccountName())
                .build();

        for(Answer answer : wrongAnswers) {
            dto.setWrongAnswers(new ArrayList<>());
            dto.getWrongAnswers().add(AnswerDetailDTOResponse.toDTO(answer));
        }
        return dto;
    }
}

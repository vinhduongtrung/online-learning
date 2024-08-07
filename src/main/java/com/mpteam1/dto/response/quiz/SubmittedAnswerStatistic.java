package com.mpteam1.dto.response.quiz;

import com.mpteam1.dto.response.question.QuestionDetailDTOResponse;
import com.mpteam1.dto.response.student.StudentGiveWrongAnswer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class SubmittedAnswerStatistic {

    private QuestionDetailDTOResponse question;

    private int totalStudents;

    private int totalStudentsTakeTheTest;

    private int totalStudentsGiveCorrectAnswer;

    private int totalStudentGiveWrongAnswer;

    List<StudentGiveWrongAnswer> givenWrongAnswerStudents;
}

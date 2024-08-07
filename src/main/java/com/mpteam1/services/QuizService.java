package com.mpteam1.services;

import com.mpteam1.dto.request.question.AddQuestionsDTO;
import com.mpteam1.dto.request.question.DeleteQuestionsDTO;
import com.mpteam1.dto.request.question.ImportQuestionDTO;
import com.mpteam1.dto.request.question.UpdateQuestionDTO;
import com.mpteam1.dto.request.quiz.CreateQuizDTO;
import com.mpteam1.dto.request.quiz.QuizStatisticDTO;
import com.mpteam1.dto.request.taken_quiz.GradeQuizDTO;
import com.mpteam1.dto.request.quiz.QuizIdDTO;
import com.mpteam1.dto.request.quiz.UpdateQuizBasicInfo;
import com.mpteam1.dto.response.common.PageForView;
import com.mpteam1.dto.response.common.ResponseSuccess;
import com.mpteam1.dto.response.question.QuestionDetailDTOResponse;
import com.mpteam1.dto.response.quiz.QuizBasicInfoDTO;
import com.mpteam1.dto.response.quiz.QuizDetailDTOResponse;
import com.mpteam1.dto.response.quiz.QuizStatisticResponse;
import com.mpteam1.dto.response.taken_quiz.TakenQuizDTOResponse;
import com.mpteam1.dto.response.taken_quiz.TestResultDTO;
import com.mpteam1.entities.Quiz;
import com.mpteam1.exception.custom.exception.*;

import java.util.Map;

public interface QuizService {
    ResponseSuccess create(CreateQuizDTO createQuizDTO);

    ResponseSuccess addQuestions(AddQuestionsDTO addQuestionsDTO);

    QuizDetailDTOResponse getQuizDetail(Long id, int pageNum, int pageSize) throws QuizNotFoundException;

    void canEnterQuiz(Long id) throws QuizAlreadySubmittedException;

    TakenQuizDTOResponse enterQuiz(Long id);

    boolean canUpdateQuiz(Quiz quiz);

    TestResultDTO viewTestResult(Long takenQuizId);

    TestResultDTO viewTestResult2(Long studentId, Long quizId);
    ResponseSuccess importQuestions(ImportQuestionDTO importQuestionDTO);

    boolean deleteQuestions(DeleteQuestionsDTO deleteQuestionsDTO) throws QuizNotFoundException, QuestionNotFoundException, QuestionNotBelongedToQuizException, PermissionException;

    void editBasicInfo(UpdateQuizBasicInfo updateQuizBasicInfo) throws QuizNotFoundException, QuizIsBeingTakenException, QuizAlreadySubmittedException;

    void updateQuestion(UpdateQuestionDTO updateQuestionDTO) throws QuizIsBeingTakenException, QuizAlreadySubmittedException, QuizNotFoundException, QuestionNotFoundException;

    void publishQuiz(QuizIdDTO quizIdDTO) throws QuizNotFoundException;

    ResponseSuccess gradeQuiz(GradeQuizDTO gradeQuizDTO);

    void closeQuiz(QuizIdDTO quizIdDTO) throws QuizNotFoundException;

    PageForView<QuizStatisticResponse> getStatistic(QuizStatisticDTO quizStatisticDTO, int pageNum,int pageSize, String keyword) throws QuizNotFoundException;

    Map<String, Long> gradingResultStatistic(QuizStatisticDTO quizStatisticDTO) throws QuizNotFoundException;

    ResponseSuccess raiseTabLeavesNumber(Long takenQuizId);

    PageForView<QuizBasicInfoDTO> getAllQuizzesBelongToClazz(Long clazzId, int pageNum, int pageSize, String keyword) throws ClazzNotFoundException, StudentNotFoundException, PermissionException;


    QuizBasicInfoDTO getQuizBasicInfo(Long quizId) throws QuizNotFoundException, PermissionException, StudentNotFoundException;

    PageForView<QuestionDetailDTOResponse> getQuestions(Long quizId, int pageNum, int pageSize, String keyword) throws QuizNotFoundException, PermissionException, StudentNotFoundException;
}

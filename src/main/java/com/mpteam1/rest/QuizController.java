package com.mpteam1.rest;

import com.mpteam1.dto.request.question.AddQuestionsDTO;
import com.mpteam1.dto.request.question.DeleteQuestionsDTO;
import com.mpteam1.dto.request.question.ImportQuestionDTO;
import com.mpteam1.dto.request.question.UpdateQuestionDTO;
import com.mpteam1.dto.request.quiz.CreateQuizDTO;
import com.mpteam1.dto.request.quiz.QuizStatisticDTO;
import com.mpteam1.dto.request.taken_quiz.GradeQuizDTO;
import com.mpteam1.dto.request.quiz.QuizIdDTO;
import com.mpteam1.dto.request.quiz.UpdateQuizBasicInfo;
import com.mpteam1.dto.response.common.ResponseError;
import com.mpteam1.dto.response.common.ResponseSuccess;
import com.mpteam1.exception.custom.exception.*;
import com.mpteam1.services.QuizService;
import com.mpteam1.utils.Api;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Slf4j
public class QuizController {
    private QuizService quizService;

    @PostMapping(Api.Quiz.CREATE)
    public ResponseEntity<?> create(@Valid @RequestBody CreateQuizDTO createQuizDTO) {
        return ResponseEntity.ok(quizService.create(createQuizDTO));
    }

    @PostMapping(Api.Quiz.ADD_QUESTIONS)
    public ResponseEntity<?> addQuestions(@Valid @RequestBody AddQuestionsDTO addQuestionsDTO) {
        return ResponseEntity.ok(quizService.addQuestions(addQuestionsDTO));
    }

    @GetMapping(Api.Quiz.DETAIL)
    public ResponseEntity<?> getQuizById(@RequestParam Long id) throws QuizNotFoundException, StudentNotFoundException, PermissionException {
        return ResponseEntity.ok().body(quizService.getQuizBasicInfo(id));
    }

    @GetMapping(Api.Quiz.QUESTIONS)
    public ResponseEntity<?> getQuestions(@RequestParam Long quizId, @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "") String keyword) throws QuizNotFoundException, StudentNotFoundException, PermissionException {
        return ResponseEntity.ok(quizService.getQuestions(quizId, page, size, keyword));
    }

    @GetMapping(Api.Quiz.CAN_ENTER_QUIZ + "/{quiz_id}")
    public ResponseEntity<?> canEnterQuiz(@PathVariable("quiz_id") Long id) throws QuizAlreadySubmittedException {
        quizService.canEnterQuiz(id);
        return ResponseEntity.ok().body(Map.of("message", "The quiz is available"));
    }

    @GetMapping(Api.Quiz.ENTER_QUIZ + "/{quiz_id}")
    public ResponseEntity<?> enterQuiz(@PathVariable("quiz_id") Long id) {
        return ResponseEntity.ok().body(quizService.enterQuiz(id));
    }

    @GetMapping(Api.Quiz.VIEW_TEST_RESULT + "/{taken_quiz_id}")
    public ResponseEntity<?> viewTestResult(@PathVariable("taken_quiz_id") Long takenQuizId) {
        return ResponseEntity.ok().body(quizService.viewTestResult(takenQuizId));
    }

    @GetMapping(Api.Quiz.VIEW_TEST_RESULT2)
    public ResponseEntity<?> viewTestResult2(@RequestParam("studentId") Long studentId, @RequestParam("quizId") Long quizId) {
        return ResponseEntity.ok().body(quizService.viewTestResult2(studentId, quizId));
    }

    @PostMapping(value = Api.Quiz.IMPORT_QUESTIONS, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importQuestions(@ModelAttribute @Valid ImportQuestionDTO importQuestionDTO) {
        return ResponseEntity.ok().body(quizService.importQuestions(importQuestionDTO));
    }

    @PostMapping(Api.Quiz.EDIT)
    public ResponseEntity<?> edit(@RequestBody @Valid UpdateQuizBasicInfo quizBasicInfo)
            throws QuizNotFoundException, QuizIsBeingTakenException, QuizAlreadySubmittedException {
        quizService.editBasicInfo(quizBasicInfo);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseSuccess(HttpStatus.OK,
                "Quiz id " + quizBasicInfo.getId() + " was updated successfully"));

    }

    @PostMapping(Api.Quiz.UPDATE_QUESTION)
    public ResponseEntity<?> updateQuestion(@RequestBody @Valid UpdateQuestionDTO updateQuestionDTO)
            throws QuizNotFoundException, QuestionNotFoundException, QuizAlreadySubmittedException, QuizIsBeingTakenException {
            quizService.updateQuestion(updateQuestionDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseSuccess(HttpStatus.OK, "Updated successfully"));
    }

    @PostMapping(Api.Quiz.PUBLISH_QUIZ)
    public ResponseEntity<?> publishQuiz(@RequestBody @Valid QuizIdDTO quizIdDTO) throws QuizNotFoundException {
        quizService.publishQuiz(quizIdDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseSuccess(HttpStatus.OK,
                "Publish successfully"));

    }

    @PostMapping(Api.Quiz.CLOSE_QUIZ)
    public ResponseEntity<?> closeQuiz(@RequestBody @Valid QuizIdDTO quizIdDTO) throws QuizNotFoundException {
        quizService.closeQuiz(quizIdDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseSuccess(HttpStatus.OK,
                "Close successfully"));

    }


    @PostMapping(Api.Quiz.GRADE_QUIZ)
    public ResponseEntity<?> gradeQuiz(@RequestBody GradeQuizDTO gradeQuizDTO) {
        return ResponseEntity.ok().body(quizService.gradeQuiz(gradeQuizDTO));
    }

    @PostMapping(Api.Quiz.RAISE_TAB_LEAVES_NUMBER + "/{taken_quiz_id}")
    public ResponseEntity<?> raiseTabLeavesNumber(@PathVariable("taken_quiz_id") Long takenQuizId) {
        return ResponseEntity.ok().body(quizService.raiseTabLeavesNumber(takenQuizId));
    }

    @DeleteMapping(value = Api.Quiz.DELETE_QUESTIONS)
    public ResponseEntity<?> deleteQuestions(@RequestBody @Valid DeleteQuestionsDTO deleteQuestionsDTO) throws QuestionNotFoundException, QuizNotFoundException, QuestionNotBelongedToQuizException, PermissionException {
        try {
            quizService.deleteQuestions(deleteQuestionsDTO);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseSuccess(HttpStatus.OK,
                            "Delete questions successfully"));
        } catch (QuestionNotFoundException e) {
            throw new QuestionNotFoundException(e.getMessage());
        } catch (QuizNotFoundException e) {
            throw new QuizNotFoundException(e.getMessage());
        } catch (QuestionNotBelongedToQuizException e) {
            throw new QuestionNotBelongedToQuizException(e.getMessage());
        } catch (PermissionException e) {
            throw new PermissionException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage() + "\n" + Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "#" + stackTraceElement.getLineNumber() + "\n").collect(Collectors.joining()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to delete questions"));
        }
    }

    @GetMapping(value = Api.Quiz.CLASS_QUIZZES)
    public ResponseEntity<?> getClassQuizzes(@RequestParam Long id, @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(defaultValue = "") String keyword) throws ClazzNotFoundException, StudentNotFoundException {
        return ResponseEntity.ok().body(quizService.getAllQuizzesBelongToClazz(id, page, size, keyword));
    }

    @PostMapping(Api.Quiz.STATISTIC)
    public ResponseEntity<?> getStatistic(@RequestBody @Valid QuizStatisticDTO quizStatisticDTO,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "5") int size,
                                          @RequestParam(defaultValue = "") String keyword) throws QuizNotFoundException {
        return new ResponseEntity<>(quizService.getStatistic(quizStatisticDTO, page, size, keyword), HttpStatus.OK);
    }

    @PostMapping(Api.Quiz.GRADE_STATISTIC)
    public ResponseEntity<?> gradingResultStatistic(@RequestBody @Valid QuizStatisticDTO quizStatisticDTO) throws QuizNotFoundException {
        return new ResponseEntity<>(quizService.gradingResultStatistic(quizStatisticDTO), HttpStatus.OK);
    }

}

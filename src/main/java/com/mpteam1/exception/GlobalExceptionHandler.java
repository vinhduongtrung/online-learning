package com.mpteam1.exception;

import com.mpteam1.dto.response.common.APIErrorResponse;
import com.mpteam1.dto.response.common.ResponseError;
import com.mpteam1.exception.custom.exception.*;
import com.mpteam1.exception.custom.exception.ClassNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/9/2024, Tue
 **/

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ResponseError> handleClassNotFound(StudentNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseError(HttpStatus.NOT_FOUND, e.getMessage()));
    }
    @ExceptionHandler(StudentDuplicateException.class)
    public ResponseEntity<ResponseError> handleStudentDuplicate(StudentDuplicateException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(ClassNotFoundException.class)
    public ResponseEntity<ResponseError> handleClassNotFound(ClassNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseError(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(ClazzNotFoundException.class)
    public ResponseEntity<ResponseError> handleClazzNotFound(ClazzNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseError(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity<ResponseError> handleDateTimeException(DateTimeException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(ClassDuplicateException.class)
    public ResponseEntity<ResponseError> handleClassDuplicate(ClassDuplicateException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(MaximumCapacityReachedException.class)
    public ResponseEntity<ResponseError> handleMaximumCapacityReachedException(MaximumCapacityReachedException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(ConflictedScheduleException.class)
    public ResponseEntity<ResponseError> handleConflictedSchedule(ConflictedScheduleException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(SubjectDuplicateException.class)
    public ResponseEntity<ResponseError> handleSubjectDuplicate(SubjectDuplicateException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(SubjectNotFoundException.class)
    public ResponseEntity<ResponseError> handleSubjectNotFound(SubjectNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseError(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public APIErrorResponse<List<String>> handleInvalidArgument(MethodArgumentNotValidException e) {
        List<String> list = new ArrayList<>();
        APIErrorResponse<List<String>> responseList = new APIErrorResponse<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            if (error.getField().contains("[")) {
                String [] parts = error.getField().split("\\.");
                String propertyName = parts[parts.length-1];
                list.add(propertyName + " " + error.getDefaultMessage());
            } else {
                list.add(error.getField() + " " + error.getDefaultMessage());
            }
        });
        responseList.setCount(list.size());
        responseList.setMessages(list);
        responseList.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return responseList;
    }

    @ExceptionHandler(TeacherNotFoundException.class)
    public ResponseEntity<ResponseError> handleTeacherNotFound(TeacherNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseError(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(TeacherDuplicateException.class)
    public ResponseEntity<ResponseError> handleTeacherDuplicate(TeacherDuplicateException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(QuizNotFoundException.class)
    public ResponseEntity<ResponseError> handlQuizNotFound(QuizNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(QuizIsBeingTakenException.class)
    public ResponseEntity<ResponseError> quizIsBeingTaken(QuizIsBeingTakenException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> userNotFoundException(UserNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<?> tokenNotFoundException(TokenNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(JwtTokenExpiredException.class)
    public ResponseEntity<?> jwtTokenExpiredException(JwtTokenExpiredException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> invalidCredentialsException(InvalidCredentialsException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(FilesNotFoundException.class)
    public ResponseEntity<?> fileNotFoundException(FilesNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<?> questionNotFoundException(QuestionNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<?> permissionException(PermissionException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ResponseError(HttpStatus.FORBIDDEN, e.getMessage()));
    }

    @ExceptionHandler(QuizAlreadySubmittedException.class)
    public ResponseEntity<?> quizAlreadySubmittedException(QuizAlreadySubmittedException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(QuestionNotBelongedToQuizException.class)
    public ResponseEntity<?> handleQuestionNotBelongedToQuizException(QuestionNotBelongedToQuizException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage() + "\n" + Arrays.stream(e.getStackTrace()).map(stackTraceElement -> stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "#" + stackTraceElement.getLineNumber() + "\n").collect(Collectors.joining()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, e.getMessage()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, "There was an error, Please contact the administrator to resolve this problem."));
    }
}

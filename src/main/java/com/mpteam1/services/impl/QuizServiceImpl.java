package com.mpteam1.services.impl;

import com.mpteam1.dto.request.question.AddQuestionsDTO;
import com.mpteam1.dto.request.question.DeleteQuestionsDTO;
import com.mpteam1.dto.request.question.ImportQuestionDTO;
import com.mpteam1.dto.request.question.UpdateQuestionDTO;
import com.mpteam1.dto.request.quiz.CreateQuizDTO;
import com.mpteam1.dto.request.quiz.QuizStatisticDTO;
import com.mpteam1.dto.request.taken_quiz.GradeQuizDTO;
import com.mpteam1.dto.request.taken_quiz.TakenAnswerReqDTO;
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
import com.mpteam1.entities.*;
import com.mpteam1.entities.enums.EQuestionType;
import com.mpteam1.entities.enums.EQuizStatus;
import com.mpteam1.entities.enums.ERole;
import com.mpteam1.exception.custom.exception.*;
import com.mpteam1.repository.*;
import com.mpteam1.services.QuizService;
import com.mpteam1.utils.ExcelHelper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.mpteam1.entities.Quiz;


@Service
@AllArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {
    private final TeacherRepository teacherRepository;
    private QuizRepository quizRepository;
    private ClassRepository classRepository;
    private UserRepository userRepository;
    private QuestionRepository questionRepository;
    private TakenQuizRepository takenQuizRepository;
    private AnswerRepository answerRepository;
    private TakenAnswerRepository takenAnswerRepository;
    private StudentRepository studentRepository;

    @Override
    @Transactional
    public ResponseSuccess create(CreateQuizDTO createQuizDTO) {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new UserNotFoundException("User not found"));
        Clazz clazz = classRepository.findById(createQuizDTO.getClazzId()).orElseThrow(() -> new RuntimeException("Class not found"));
        if (user.getERole().equals(ERole.TEACHER) && !user.getId().equals(clazz.getTeacher().getId()))
            throw new PermissionException("You dont have permission");
        if (createQuizDTO.getOpenTime().isAfter(createQuizDTO.getClosedTime()))
            throw new RuntimeException("Open time must be before closed time");

        int gap = (int) Duration.between(createQuizDTO.getOpenTime(), createQuizDTO.getClosedTime()).toMinutes();
        Instant now = Instant.now();
        if(!createQuizDTO.getClosedTime().isAfter(createQuizDTO.getOpenTime())){
            throw new DateTimeException("closed time need to be after open time");
        }
        if(now.isAfter(createQuizDTO.getOpenTime())){
            throw new DateTimeException("invalid open time");
        }
        if(createQuizDTO.getDuration() > gap) {
            throw new DateTimeException("time duration " + createQuizDTO.getDuration() + " was out of range");
        }

        try {
            Quiz quiz = createQuizDTO.convertTo();
            quiz.setClazz(clazz);
            clazz.getQuizzes().add(quiz);
            quiz.setCreatedBy(user);
            quizRepository.save(quiz);
            return new ResponseSuccess(HttpStatus.OK, "Create quiz success");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Fail to create test");
        }
    }


    @Override
    @Transactional
    public ResponseSuccess addQuestions(AddQuestionsDTO addQuestionsDTO) {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new UserNotFoundException("User not found"));
        Quiz quiz = quizRepository.findById(addQuestionsDTO.getQuizId()).orElseThrow(() -> new RuntimeException("Quiz not found"));
        if (user.getERole().equals(ERole.TEACHER) && !user.getId().equals(quiz.getClazz().getTeacher().getId()))
            throw new PermissionException("You dont have permission");
        if (!canUpdateQuiz(quiz)) throw new RuntimeException("Can not update the test because it is started");
        try {
            List<Question> questions = addQuestionsDTO.getQuestions().stream().map(questionDTO -> {
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
            quiz.getQuestions().addAll(questions);
            questionRepository.saveAll(questions);
            return new ResponseSuccess(HttpStatus.OK, "Add questions to quiz success");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Fail add questions to the test");
        }
    }

    @Override
    public QuizDetailDTOResponse getQuizDetail(Long id, int pageNum, int pageSize) throws QuizNotFoundException {
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new QuizNotFoundException("Quiz not found"));
        QuizDetailDTOResponse dtoResponse = QuizDetailDTOResponse.toDTO(quiz, false);
        // questions page
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(order));
        Page<Question> questionPage = questionRepository.findAllByQuiz_Id(quiz.getId(), pageable);
        for (Question question : questionPage) {
            dtoResponse.getQuestions().add(QuestionDetailDTOResponse.toDTO(question));
        }
        return dtoResponse;
    }

    @Override
    @Transactional
    public void editBasicInfo(UpdateQuizBasicInfo updateQuizBasicInfo) throws QuizNotFoundException, QuizIsBeingTakenException, QuizAlreadySubmittedException {
        Quiz quiz = quizRepository.findById(updateQuizBasicInfo.getId())
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id : " + updateQuizBasicInfo.getId()));

        permissionFilter(quiz.getId());

        updateQuizBasicInfo.validate();

        dateTimeValidate(quiz);

        quiz.setTitle(updateQuizBasicInfo.getTitle());
        quiz.setDescription(updateQuizBasicInfo.getDescription());
        quiz.setDuration(updateQuizBasicInfo.getDuration());
        quiz.setOpenTime(updateQuizBasicInfo.getOpenTime());
        quiz.setClosedTime(updateQuizBasicInfo.getClosedTime());
        quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public void updateQuestion(UpdateQuestionDTO updateQuestionDTO) throws QuizIsBeingTakenException, QuizAlreadySubmittedException, QuizNotFoundException, QuestionNotFoundException {
        Quiz quiz = quizRepository.findById(updateQuestionDTO.getQuizId())
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id : " + updateQuestionDTO.getQuizId()));

        permissionFilter(quiz.getId());

        Question question = questionRepository.findById(updateQuestionDTO.getQuestionId())
                .orElseThrow(() -> new QuestionNotFoundException("Question not found with id :" + updateQuestionDTO.getQuestionId()));

        dateTimeValidate(quiz);

        question.setType(EQuestionType.valueOf(updateQuestionDTO.getType()));
        question.setContent(updateQuestionDTO.getContent());

        List<Answer> existedAnswers  = answerRepository.findAllByQuestion_Id(updateQuestionDTO.getQuestionId());

        for(Answer answer : existedAnswers) {
            answer.setQuestion(null);
        }
        answerRepository.saveAll(existedAnswers);

        List<Answer> newAnswers = updateQuestionDTO.getAnswers().stream().map(answerDTO -> {
            Answer answer = answerDTO.convertTo();
            answer.setQuestion(question);
            return answer;
        }).toList();
        question.getAnswers().addAll(newAnswers);
        questionRepository.save(question);
    }

    @Override
    public void publishQuiz(QuizIdDTO quizIdDTO) throws QuizNotFoundException {
        Quiz quiz = quizRepository.findById(quizIdDTO.getId())
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id : " + quizIdDTO.getId()));
        quiz.setStatus(EQuizStatus.PUBLISH);
        quizRepository.save(quiz);
    }


    @Override
    @Transactional
    public void canEnterQuiz(Long id) throws QuizAlreadySubmittedException {
        Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new RuntimeException("Student not found"));
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));

        Instant currentTime = Instant.now();
        if (currentTime.isAfter(quiz.getClosedTime())) throw new RuntimeException("Quiz is closed already");
        if (currentTime.isBefore(quiz.getOpenTime())) throw new RuntimeException("Quiz is not open yet");

        TakenQuiz takenQuiz = takenQuizRepository.findByStudentIdAndQuizId(student.getId(), quiz.getId()).orElse(null);

        if (takenQuiz != null) {
            throw new QuizAlreadySubmittedException("Quiz is already submitted");
        } else {
            try {
                TakenQuiz createdTakenQuiz = TakenQuiz.builder()
                        .student(student)
                        .quiz(quiz)
                        .startTime(Instant.now())
                        .takenAnswers(new ArrayList<>())
                        .score(0.0)
                        .build();
                takenQuizRepository.save(createdTakenQuiz);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException("There is an error while entering the test");
            }
        }
    }

    @Override
    public TakenQuizDTOResponse enterQuiz(Long id) {
        Student student = studentRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new RuntimeException("Student not found"));
        TakenQuiz takenQuiz = takenQuizRepository.findByStudentIdAndQuizId(student.getId(), id).orElseThrow(() -> new RuntimeException("The taken quiz not found"));
        TakenQuizDTOResponse takenQuizDTO = TakenQuizDTOResponse.toDTO(takenQuiz);
        takenQuizDTO.getQuiz().getQuestions().forEach(question -> {
            question.getAnswers().forEach(answer -> {
                answer.setIsCorrect(null);
            });
        });
        return takenQuizDTO;
    }

    @Override
    public boolean canUpdateQuiz(Quiz quiz) {
        return quiz.getOpenTime().isAfter(Instant.now());
    }

    @Override
    public TestResultDTO viewTestResult(Long takenQuizId) {
        TakenQuiz takenQuiz = takenQuizRepository.findById(takenQuizId).orElseThrow(() -> new RuntimeException("Submission not found"));
        return TestResultDTO.toDTO(takenQuiz);
    }

    @Override
    public TestResultDTO viewTestResult2(Long studentId, Long quizId) {
        TakenQuiz takenQuiz = takenQuizRepository.findByStudentIdAndQuizId(studentId, quizId).orElseThrow(() -> new RuntimeException("Submission not found"));
        return TestResultDTO.toDTO(takenQuiz);
    }

    @Override
    @Transactional
    public ResponseSuccess importQuestions(ImportQuestionDTO importQuestionDTO) {
        Quiz quiz = quizRepository.findById(importQuestionDTO.getQuizId()).orElseThrow(() -> new RuntimeException("Quiz not found"));
        if (!canUpdateQuiz(quiz)) throw new RuntimeException("Can not update the test because it is started");
        if (!ExcelHelper.hasExcelFormat(importQuestionDTO.getFile())) throw new RuntimeException("Invalid excel file");
        List<Question> questions = ExcelHelper.excelToQuestions(importQuestionDTO.getFile(), quiz);
        try {
            questionRepository.saveAll(questions);
            return new ResponseSuccess(HttpStatus.OK, "Import questions to quiz success");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Fail to import questions");
        }
    }

    @Override
    @Transactional
    public ResponseSuccess gradeQuiz(GradeQuizDTO gradeQuizDTO) {
        TakenQuiz takenQuiz = takenQuizRepository.findById(gradeQuizDTO.getTakenQuizId()).orElseThrow(() -> new RuntimeException("Submission not found"));
        if (takenQuiz.getEndTime() != null) throw new RuntimeException("You have submitted this test already");

        takenQuiz.setEndTime(Instant.now());

        int numOfQuestion = takenQuiz.getQuiz().getQuestions().size();
        double pointEachQuestion = 10.0 / numOfQuestion;
        double totalMark = 0;

        List<TakenAnswer> takenAnswers = new ArrayList<>();
        for (TakenAnswerReqDTO answerDTO : gradeQuizDTO.getAnswers()) {
            Question question = questionRepository.findById(answerDTO.getQuestionId()).orElseThrow(() -> new RuntimeException("Question id " + answerDTO.getQuestionId() + " not found"));
            List<Long> trueAnswerIds = question.getAnswers().stream().filter(answer -> answer.getIsCorrect().equals(true)).map(Answer::getId).toList();
            List<Long> trueChosenAnswerIds = answerDTO.getChosenAnswerIds().stream().filter(trueAnswerIds::contains).toList();
            List<Long> wrongChosenAnswerIds = answerDTO.getChosenAnswerIds().stream().filter(chosenId -> !trueAnswerIds.contains(chosenId)).toList();
            if (wrongChosenAnswerIds.isEmpty()) totalMark += (double) trueChosenAnswerIds.size() / trueAnswerIds.size() * pointEachQuestion;
            List<Answer> chosenAnswers = answerRepository.findAllById(answerDTO.getChosenAnswerIds());
            TakenAnswer takenAnswer = TakenAnswer.builder()
                    .takenQuiz(takenQuiz)
                    .question(question)
                    .answers(chosenAnswers)
                    .build();
            takenQuiz.getTakenAnswers().add(takenAnswer);
            takenAnswers.add(takenAnswer);
        }
        try {
            takenAnswerRepository.saveAll(takenAnswers);

            takenQuiz.setScore(totalMark);
            takenQuizRepository.save(takenQuiz);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Grade quiz fail");
        }
        return new ResponseSuccess(HttpStatus.OK, "Grade quiz success");
    }

    @Override
    public void closeQuiz(QuizIdDTO quizIdDTO) throws QuizNotFoundException {
        Quiz quiz = quizRepository.findById(quizIdDTO.getId())
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id : " + quizIdDTO.getId()));
        quiz.setStatus(EQuizStatus.PRIVATE);
        quizRepository.save(quiz);
    }

    @Override
    public PageForView<QuizBasicInfoDTO> getAllQuizzesBelongToClazz(Long clazzId, int pageNum, int pageSize, String keyword) throws ClazzNotFoundException, StudentNotFoundException, PermissionException {
        // Check for existence of a class
        Clazz clazz = classRepository.findById(clazzId).orElseThrow(() -> new ClazzNotFoundException("Clazz not found"));

        // Authorization
        String authority = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // ADMIN can see all tests of a class
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(order));
        List<QuizBasicInfoDTO> quizBasicInfoDTOs = new ArrayList<>();
        PageForView<QuizBasicInfoDTO> page = new PageForView<>();
        if (authority.equals("ADMIN")) {
            Page<Quiz> quizPage = quizRepository.findByClazz_Id(clazzId, keyword, pageable);
            List<Quiz> quizzes = quizPage.getContent();
            for (Quiz quiz : quizzes) {
                quizBasicInfoDTOs.add(QuizBasicInfoDTO.toDTO(quiz));
            }
            page.setContent(quizBasicInfoDTOs);
            page.setPage(pageNum);
            page.setSize(pageSize);
            page.setTotalElement(quizRepository.countByClazz_Id(clazzId));
            return page;
        } else if (authority.equals("TEACHER")) {
            // Teacher can only see tests of their classes
            Teacher teacher = teacherRepository.findByEmail(userEmail);
            if (teacher.getId().equals(clazz.getTeacher().getId())) {
                Page<Quiz> quizPage = quizRepository.findByClazz_Id(clazzId, keyword, pageable);
                List<Quiz> quizzes = quizPage.getContent();
                for (Quiz quiz : quizzes) {
                    quizBasicInfoDTOs.add(QuizBasicInfoDTO.toDTO(quiz));
                }
                page.setContent(quizBasicInfoDTOs);
                page.setPage(pageNum);
                page.setSize(pageSize);
                page.setTotalElement(quizRepository.countByClazz_Id(clazzId));
            }
            return page;
        } else if (authority.equals("STUDENT")) {
            // Student can only see tests of their classes
            Student student = studentRepository.findByEmail(userEmail).orElseThrow(() -> new StudentNotFoundException("Student not found"));
            Set<Long> studentIds = clazz.getStudents().stream().map(Student::getId).collect(Collectors.toSet());
            if (studentIds.contains(student.getId())) {
                Page<Quiz> quizPage = quizRepository.findByClazz_Id(clazzId, keyword, pageable);
                List<Quiz> quizzes = quizPage.getContent();
                for (Quiz quiz : quizzes) {
                    // Student can only see published test
                    TakenQuiz takenQuiz = takenQuizRepository.findByStudentIdAndQuizId(student.getId(), quiz.getId()).orElse(null);
                    if (quiz.getStatus() == EQuizStatus.PUBLISH)
                        quizBasicInfoDTOs.add(QuizBasicInfoDTO.toDTO(quiz, takenQuiz));
                }
                page.setContent(quizBasicInfoDTOs);
                page.setPage(pageNum);
                page.setSize(pageSize);
                page.setTotalElement(quizRepository.countByStatusAndClazz_Id(EQuizStatus.PUBLISH, clazzId));
            } else {
                throw new PermissionException("You don't have access to this class");
            }
            return page;
        } else return null;
    }

    @Override
    @Transactional(rollbackOn = {QuizNotFoundException.class, QuestionNotFoundException.class, QuestionNotBelongedToQuizException.class, PermissionException.class})
    public boolean deleteQuestions(DeleteQuestionsDTO deleteQuestionsDTO) throws QuizNotFoundException, QuestionNotFoundException, QuestionNotBelongedToQuizException, PermissionException {
        Quiz quiz = quizRepository.findById(deleteQuestionsDTO.getQuizId()).orElseThrow(() -> new QuizNotFoundException("Quiz not found"));
        String authority = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (authority.equals("ADMIN") ||
                (authority.equals("TEACHER") && teacherRepository.findByEmail(userEmail).getId().equals(quiz.getClazz().getTeacher().getId()))) {
            for (Long questionID : deleteQuestionsDTO.getQuestionIds()) {
                Question question = questionRepository.findById(questionID).orElseThrow(() -> new QuestionNotFoundException("Question not found"));
                if (quiz.getQuestions().contains(question)) {
                    quiz.getQuestions().remove(question);
                    questionRepository.delete(question);
                } else {
                    throw new QuestionNotBelongedToQuizException("Question with ID: " + questionID + " doesn't belong to quiz");
                }
            }
            quizRepository.save(quiz);
            return true;
        } else {
            throw new PermissionException("You do not have permission to delete this quiz");
        }
    }

    @Override
    public ResponseSuccess raiseTabLeavesNumber(Long takenQuizId) {
        TakenQuiz takenQuiz = takenQuizRepository.findById(takenQuizId).orElseThrow(() -> new RuntimeException("Submission not found"));
        takenQuiz.setNumOfTabLeaves(takenQuiz.getNumOfTabLeaves() + 1);
        takenQuizRepository.save(takenQuiz);
        return new ResponseSuccess(HttpStatus.OK, "Raise tab leaves number success");
    }

    public QuizBasicInfoDTO getQuizBasicInfo(Long quizId) throws QuizNotFoundException, PermissionException, StudentNotFoundException {
        // Check for existence of a quiz
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new QuizNotFoundException("Quiz not found"));
        Clazz clazz = quiz.getClazz();

        // Authorization
        String authority = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // ADMIN can see all tests of a class
        switch (authority) {
            case "ADMIN" -> {
                return QuizBasicInfoDTO.toDTO(quiz);
            }
            case "TEACHER" -> {
                // Teacher can only see tests of their classes
                Teacher teacher = teacherRepository.findByEmail(userEmail);
                if (teacher.getId().equals(clazz.getTeacher().getId())) {
                    return QuizBasicInfoDTO.toDTO(quiz);
                } else throw new PermissionException("You don't have access to this class");
            }
            case "STUDENT" -> {
                // Student can only see tests of their classes
                Student student = studentRepository.findByEmail(userEmail).orElseThrow(() -> new StudentNotFoundException("Student not found"));
                Set<Long> studentIds = clazz.getStudents().stream().map(Student::getId).collect(Collectors.toSet());
                if (studentIds.contains(student.getId())) {
                    if (quiz.getStatus() == EQuizStatus.PUBLISH) {
                        TakenQuiz takenQuiz = takenQuizRepository.findByStudentIdAndQuizId(student.getId(), quiz.getId()).orElse(null);
                        return QuizBasicInfoDTO.toDTO(quiz, takenQuiz);
                    } else throw new PermissionException("You don't have access to this quiz");
                } else {
                    throw new PermissionException("You don't have access to this class");
                }
            }
            default -> {
                return null;
            }
        }
    }

    public PageForView<QuestionDetailDTOResponse> getQuestions(Long quizId, int pageNum, int pageSize, String keyword) throws QuizNotFoundException, PermissionException, StudentNotFoundException {
        // Check for existence of a quiz
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new QuizNotFoundException("Quiz not found"));
        Clazz clazz = quiz.getClazz();

        // Authorization
        String authority = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // ADMIN can see all tests of a class
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(order));
        List<QuestionDetailDTOResponse> questionDetailDTOResponses = new ArrayList<>();
        PageForView<QuestionDetailDTOResponse> page = new PageForView<>();
        switch (authority) {
            case "ADMIN" -> {
                Page<Question> questionPage = questionRepository.findByQuizIdAndKeyword(quizId, keyword, pageable);
                List<Question> questions = questionPage.getContent();
                for (Question question : questions) {
                    questionDetailDTOResponses.add(QuestionDetailDTOResponse.toDTO(question));
                }
                page.setContent(questionDetailDTOResponses);
                page.setPage(pageNum);
                page.setSize(pageSize);
                page.setTotalElement(questionRepository.countByQuiz_Id(quizId));
                return page;
            }
            case "TEACHER" -> {
                // Teacher can only see tests of their classes
                Teacher teacher = teacherRepository.findByEmail(userEmail);
                if (teacher.getId().equals(clazz.getTeacher().getId())) {
                    Page<Question> questionPage = questionRepository.findByQuizIdAndKeyword(quizId, keyword, pageable);
                    List<Question> questions = questionPage.getContent();
                    for (Question question : questions) {
                        questionDetailDTOResponses.add(QuestionDetailDTOResponse.toDTO(question));
                    }
                    page.setContent(questionDetailDTOResponses);
                    page.setPage(pageNum);
                    page.setSize(pageSize);
                    page.setTotalElement(questionRepository.countByQuiz_Id(quizId));
                    return page;
                } else throw new PermissionException("You don't have access to this quiz");
            }
            case "STUDENT" -> {
                // Student can only see tests of their classes
                Student student = studentRepository.findByEmail(userEmail).orElseThrow(() -> new StudentNotFoundException("Student not found"));
                Set<Long> studentIds = clazz.getStudents().stream().map(Student::getId).collect(Collectors.toSet());
                if (studentIds.contains(student.getId())) {
                    if (quiz.getStatus() == EQuizStatus.PUBLISH) {
                        Page<Question> questionPage = questionRepository.findByQuizIdAndKeyword(quizId, keyword, pageable);
                        List<Question> questions = questionPage.getContent();
                        for (Question question : questions) {
                            questionDetailDTOResponses.add(QuestionDetailDTOResponse.toDTO(question));
                        }
                        page.setContent(questionDetailDTOResponses);
                        page.setPage(pageNum);
                        page.setSize(pageSize);
                        page.setTotalElement(questionRepository.countByQuiz_Id(quizId));
                        return page;
                    } else throw new PermissionException("You don't have access to this quiz");
                } else {
                    throw new PermissionException("You don't have access to this quiz");
                }
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public PageForView<QuizStatisticResponse> getStatistic(QuizStatisticDTO quizStatisticDTO, int pageNum, int pageSize, String keyword) throws QuizNotFoundException {
        Quiz quiz = quizRepository.findByClazzIdAndId(quizStatisticDTO.getClazzId(), quizStatisticDTO.getQuizId())
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found"));

        permissionFilter(quiz.getId());

        Page<TakenQuiz> takenQuizPage = takenQuizRepository.getAllByQuizId(quiz.getId(), PageRequest.of(pageNum, pageSize), keyword);

        PageForView<QuizStatisticResponse> pageForView = new PageForView<>();

        pageForView.setContent(takenQuizPage.getContent().stream().map(quizTaken -> {
            try {
                Student student = studentRepository.findById(quizTaken.getStudent().getId())
                        .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " +
                                quizTaken.getStudent().getId()));

                return QuizStatisticResponse.builder()
                        .studentId(student.getId())
                        .studentName(student.getFullName())
                        .accountName(student.getAccountName())
                        .avatarName(student.getAvatarName())
                        .score(quizTaken.getScore())
                        .startTime(quizTaken.getStartTime())
                        .endTime(quizTaken.getEndTime())
                        .numOfTabLeaves(quizTaken.getNumOfTabLeaves() == null ? 0 :quizTaken.getNumOfTabLeaves())
                        .takenTime(calculateTimeTaken(quizTaken.getStartTime(), quizTaken.getEndTime()))
                        .quizStatus(getTakenQuizStatus(quiz, quizTaken))
                        .build();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }).toList());

        pageForView.setTotalElement(takenQuizPage.getTotalElements());
        pageForView.setSize(takenQuizPage.getSize());
        pageForView.setPage(takenQuizPage.getNumber());

        return pageForView;
    }


    @Override
    public Map<String, Long> gradingResultStatistic(QuizStatisticDTO quizStatisticDTO) throws QuizNotFoundException {
        Quiz quiz = quizRepository.findByClazzIdAndId(quizStatisticDTO.getClazzId(), quizStatisticDTO.getQuizId())
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found"));

        permissionFilter(quiz.getId());

        List<TakenQuiz> takenQuizList = takenQuizRepository.findByQuiz_Id(quiz.getId());

        return takenQuizList.stream()
                .map(TakenQuiz::getScore)
                .map(score -> {
                    String rate;
                    if (score > 9.3) {
                        rate = "A+";
                    } else if (score >= 8.6) {
                        rate = "A";
                    } else if (score >= 7.2) {
                        rate = "B";
                    } else if (score > 6) {
                        rate = "C";
                    } else {
                        rate = "D";
                    }
                    return rate;
                })
                .collect(Collectors.groupingBy(rate -> rate, Collectors.counting()));
    }

    private void permissionFilter(Long quizId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (role.equals("STUDENT")) {
            throw new PermissionException("You don't have access to this quiz");
        }
        if (role.equals("TEACHER")) {
            boolean isQuizExist = teacherRepository.findByEmail(email)
                    .getClazzes().stream()
                    .flatMap(clazz -> clazz.getQuizzes().stream())
                    .anyMatch(q -> q.getId() == quizId);

            if (!isQuizExist) {
                throw new PermissionException("You don't have access to this quiz");
            }
        }
    }

    private void dateTimeValidate(Quiz quiz) throws QuizIsBeingTakenException, QuizAlreadySubmittedException {
        Instant now = Instant.now();

        if (quiz.getStatus().equals(EQuizStatus.PUBLISH)) {

            if (!now.isBefore(quiz.getOpenTime()) && !now.isAfter(quiz.getClosedTime())) {
                throw new QuizIsBeingTakenException("Quiz is being taken");
            }

            if (now.isAfter(quiz.getClosedTime())) {
                throw new QuizAlreadySubmittedException("Cannot update, quiz has already been closed");
            }
        }
    }

    private String calculateTimeTaken(Instant startTime, Instant endTime) {
        String takenTime = null;
        if (startTime != null && endTime != null) {
            Duration duration = Duration.between(startTime, endTime);
            long hours = duration.toHours();
            long minutes = duration.minusHours(hours).toMinutes();
            long seconds = duration.minusHours(hours).minusMinutes(minutes).getSeconds();
            takenTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return takenTime;
    }

    private String getTakenQuizStatus(Quiz quiz, TakenQuiz takenQuiz) {
        Instant now = Instant.now();
        String status = "";
        if (quiz.getStatus().equals(EQuizStatus.PUBLISH)) {

            if (now.isBefore(quiz.getOpenTime())) {
                status = "AVAILABLE";
            }

            if (!now.isBefore(quiz.getOpenTime()) && !now.isAfter(quiz.getClosedTime())) {
                if (takenQuiz.getEndTime() == null) {
                    status = "IN_PROGRESS";
                } else {
                    status = "SUBMITTED";
                }
            }

            if (now.isAfter(quiz.getClosedTime())) {
                status = "SUBMITTED";
                if (takenQuiz.getEndTime() == null) {
                    status = "MISSED";
                }
            }
        }
        return status;
    }
}

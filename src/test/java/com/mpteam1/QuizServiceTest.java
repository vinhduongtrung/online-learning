package com.mpteam1;

import com.mpteam1.dto.request.answer.AnswerReqDTO;
import com.mpteam1.dto.request.question.AddQuestionsDTO;
import com.mpteam1.dto.request.question.UpdateQuestionDTO;
import com.mpteam1.dto.request.quiz.CreateQuizDTO;
import com.mpteam1.dto.request.quiz.UpdateQuizBasicInfo;
import com.mpteam1.dto.response.common.ResponseSuccess;
import com.mpteam1.entities.*;
import com.mpteam1.entities.enums.EQuestionType;
import com.mpteam1.entities.enums.EQuizStatus;
import com.mpteam1.entities.enums.ERole;
import com.mpteam1.exception.custom.exception.*;
import com.mpteam1.repository.*;
import com.mpteam1.services.impl.QuizServiceImpl;
import com.mpteam1.utils.ExcelHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceTest {
    @InjectMocks
    private QuizServiceImpl quizService;
    @Mock
    private QuizRepository quizRepository;
    @Mock
    private ClassRepository classRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TakenQuizRepository takenQuizRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private ExcelHelper excelHelper;
    @Test
    public void whenCreateQuiz_shouldReturnResponseSuccess() {
        CreateQuizDTO createQuizDTO = CreateQuizDTO.builder()
                .clazzId(1L)
                .openTime(Instant.now())
                .closedTime(Instant.now().plusSeconds(3600))
                .title("Math Exam")
                .description("This is Math exam for students")
                .duration(30)
                .build();

        User user = new User();
        user.setId(1L);
        user.setFullName("Tom");
        user.setEmail("teacher@gmail.com");
        user.setERole(ERole.TEACHER);

        Teacher teacher = new Teacher();
        teacher.setId(user.getId());

        Clazz clazz = new Clazz();
        clazz.setId(1L);
        clazz.setTeacher(teacher);

        Authentication authentication = new UsernamePasswordAuthenticationToken("teacher@gmail.com",
                "teacher@gmail.com", Collections.singletonList(new SimpleGrantedAuthority("TEACHER")));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("teacher@gmail.com")).thenReturn(Optional.of(user));
        when(classRepository.findById(createQuizDTO.getClazzId())).thenReturn(Optional.of(clazz));
        when(quizRepository.save(any())).thenReturn(new Quiz());

        ResponseSuccess createResponse = quizService.create(createQuizDTO);
        ResponseSuccess res = new ResponseSuccess(HttpStatus.OK, "Create quiz success");

        assertEquals(res.getStatusCode(), createResponse.getStatusCode());
        assertEquals(res.getMessage(), createResponse.getMessage());
    }

    @Test
    public void whenCreateTestWithInvalidOpenAndClosedTime_shouldThrowRuntimeException() {
        CreateQuizDTO createQuizDTO = CreateQuizDTO.builder()
                .clazzId(1L)
                .openTime(Instant.now())
                .closedTime(Instant.now().plusSeconds(-3600))
                .title("Math Exam")
                .description("This is Math exam for students")
                .duration(30)
                .build();

        User user = new User();
        user.setId(1L);
        user.setFullName("Tom");
        user.setEmail("teacher@gmail.com");
        user.setERole(ERole.TEACHER);

        Teacher teacher = new Teacher();
        teacher.setId(user.getId());

        Clazz clazz = new Clazz();
        clazz.setId(1L);
        clazz.setTeacher(teacher);

        Authentication authentication = new UsernamePasswordAuthenticationToken("teacher@gmail.com",
                "teacher@gmail.com", Collections.singletonList(new SimpleGrantedAuthority("TEACHER")));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail("teacher@gmail.com")).thenReturn(Optional.of(user));
        when(classRepository.findById(createQuizDTO.getClazzId())).thenReturn(Optional.of(clazz));

        assertThrows(RuntimeException.class, () -> {quizService.create(createQuizDTO);});
    }

    @Test
    public void whenAddQuestions_shouldReturnResponseSuccess() {
        AddQuestionsDTO addQuestionsDTO = new AddQuestionsDTO();
        addQuestionsDTO.setQuizId(1L);
        addQuestionsDTO.setQuestions(new ArrayList<>());

        User user = new User();
        user.setId(1L);
        user.setFullName("Tom");
        user.setEmail("teacher@gmail.com");
        user.setERole(ERole.TEACHER);

        Teacher teacher = new Teacher();
        teacher.setId(user.getId());

        Clazz clazz = new Clazz();
        clazz.setTeacher(teacher);

        Quiz quiz = new Quiz();
        quiz.setClazz(clazz);
        quiz.setOpenTime(Instant.now().plusSeconds(100));
        quiz.setClosedTime(Instant.now().plusSeconds(3600));

        Authentication authentication = new UsernamePasswordAuthenticationToken("teacher@gmail.com",
                "teacher@gmail.com", Collections.singletonList(new SimpleGrantedAuthority("TEACHER")));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(questionRepository.saveAll(any())).thenReturn(List.of(new Question()));

        ResponseSuccess responseSuccess = quizService.addQuestions(addQuestionsDTO);

        assertEquals(responseSuccess.getStatusCode(), HttpStatus.OK.value());
        assertEquals(responseSuccess.getMessage(), "Add questions to quiz success");
    }

    @Test
    public void whenAddQuestions_shouldThrowPermissionException() {
        AddQuestionsDTO addQuestionsDTO = new AddQuestionsDTO();
        addQuestionsDTO.setQuizId(1L);
        addQuestionsDTO.setQuestions(new ArrayList<>());

        User user = new User();
        user.setId(1L);
        user.setFullName("Tom");
        user.setEmail("teacher@gmail.com");
        user.setERole(ERole.TEACHER);

        Teacher teacher = new Teacher();
        teacher.setId(2L);

        Clazz clazz = new Clazz();
        clazz.setTeacher(teacher);

        Quiz quiz = new Quiz();
        quiz.setClazz(clazz);
        quiz.setOpenTime(Instant.now().plusSeconds(100));
        quiz.setClosedTime(Instant.now().plusSeconds(3600));

        Authentication authentication = new UsernamePasswordAuthenticationToken("teacher@gmail.com",
                "teacher@gmail.com", Collections.singletonList(new SimpleGrantedAuthority("TEACHER")));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        assertThrows(PermissionException.class, () -> {quizService.addQuestions(addQuestionsDTO);});
    }

    @Test
    public void canEnterQuiz_shouldNotThrowException() throws QuizAlreadySubmittedException {
        Student student = new Student();
        student.setId(1L);
        student.setFullName("Tom");
        student.setEmail("student@gmail.com");
        student.setERole(ERole.STUDENT);

        Long quizId = 1L;
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setOpenTime(Instant.now().plusSeconds(-100));
        quiz.setClosedTime(Instant.now().plusSeconds(3600));


        Authentication authentication = new UsernamePasswordAuthenticationToken(student.getEmail(),
                student.getEmail(), Collections.singletonList(new SimpleGrantedAuthority(student.getERole().name())));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(studentRepository.findByEmail(student.getEmail())).thenReturn(Optional.of(student));
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(takenQuizRepository.findByStudentIdAndQuizId(student.getId(), quizId)).thenReturn(Optional.empty());
        when(takenQuizRepository.save(any())).thenReturn(new TakenQuiz());

        assertDoesNotThrow(() -> quizService.canEnterQuiz(quizId));
    }

    @Test
    public void canEnterQuiz_shouldThrowQuizAlreadySubmittedException() throws QuizAlreadySubmittedException {
        Student student = new Student();
        student.setId(1L);
        student.setFullName("Tom");
        student.setEmail("student@gmail.com");
        student.setERole(ERole.STUDENT);

        Long quizId = 1L;
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setOpenTime(Instant.now().plusSeconds(-100));
        quiz.setClosedTime(Instant.now().plusSeconds(3600));


        Authentication authentication = new UsernamePasswordAuthenticationToken(student.getEmail(),
                student.getEmail(), Collections.singletonList(new SimpleGrantedAuthority(student.getERole().name())));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(studentRepository.findByEmail(student.getEmail())).thenReturn(Optional.of(student));
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(takenQuizRepository.findByStudentIdAndQuizId(student.getId(), quizId)).thenReturn(Optional.of(new TakenQuiz()));
        when(takenQuizRepository.save(any())).thenReturn(new TakenQuiz());

        assertThrows(QuizAlreadySubmittedException.class, () -> quizService.canEnterQuiz(quizId));
    }

    @Test
    public void canUpdateQuiz_shouldReturnTrue() {
        Quiz quiz = new Quiz();
        quiz.setOpenTime(Instant.now().plusSeconds(100));

        assertTrue(quizService.canUpdateQuiz(quiz));
    }

    @Test
    public void viewTestResult_shouldReturnResultDTO() {
        Long takenQuizId = 1L;
        TakenQuiz takenQuiz = new TakenQuiz();
        takenQuiz.setTakenAnswers(new ArrayList<>());

        when(takenQuizRepository.findById(takenQuizId)).thenReturn(Optional.of(takenQuiz));

        assertNotNull(quizService.viewTestResult(takenQuizId));
    }

    @Test
    public void enterQuiz_shouldReturnDTO() {
        Student student = new Student();
        student.setId(1L);
        student.setFullName("Tom");
        student.setEmail("student@gmail.com");
        student.setERole(ERole.STUDENT);

        Long quizId = 1L;
        Quiz quiz = new Quiz();
        quiz.setQuestions(new ArrayList<>());
        quiz.setClazz(new Clazz());
        quiz.setCreatedBy(new User());

        TakenQuiz takenQuiz = new TakenQuiz();
        takenQuiz.setQuiz(quiz);

        Authentication authentication = new UsernamePasswordAuthenticationToken(student.getEmail(),
                student.getEmail(), Collections.singletonList(new SimpleGrantedAuthority(student.getERole().name())));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(studentRepository.findByEmail(student.getEmail())).thenReturn(Optional.of(student));
        when(takenQuizRepository.findByStudentIdAndQuizId(student.getId(), quizId)).thenReturn(Optional.of(takenQuiz));

        assertNotNull(quizService.enterQuiz(quizId));
    }

    @Test
    public void updateQuestionTest() {
        UpdateQuestionDTO updateQuestionDTO = new UpdateQuestionDTO();
        long quizId = 1L;
        long questionId = 1L;
        List<AnswerReqDTO> answerList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            AnswerReqDTO answer = new AnswerReqDTO();
            answer.setContent("Default content " + (i + 1));
            answer.setIsCorrect(false);
            answerList.add(answer);
        }

        updateQuestionDTO.setQuizId(quizId);
        updateQuestionDTO.setQuestionId(questionId);
        updateQuestionDTO.setType(EQuestionType.MULTIPLE_CHOICE.toString());
        updateQuestionDTO.setContent("abc");
        updateQuestionDTO.setAnswers(answerList);

        Authentication authentication = new UsernamePasswordAuthenticationToken("teacher@gmail.com",
                "teacher@gmail.com", Collections.singletonList(new SimpleGrantedAuthority("TEACHER")));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setOpenTime(Instant.now().plusSeconds(-100));
        quiz.setClosedTime(Instant.now().plusSeconds(3600));
        quiz.setStatus(EQuizStatus.PRIVATE);

        Question question = new Question();
        question.setId(questionId);


        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        try {
            quizService.updateQuestion(updateQuestionDTO);
        } catch (QuizIsBeingTakenException | QuizAlreadySubmittedException | QuizNotFoundException |
                 QuestionNotFoundException e) {
            // Handle exceptions if needed
        }
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    public void TestEditBasicInfo() {
        long quizId = 1L;
        UpdateQuizBasicInfo updateQuizBasicInfo = new UpdateQuizBasicInfo();
        updateQuizBasicInfo.setId(quizId);
        updateQuizBasicInfo.setQuizStatus(EQuizStatus.PRIVATE.toString());
        updateQuizBasicInfo.setOpenTime(Instant.now().plusSeconds(300));
        updateQuizBasicInfo.setClosedTime(Instant.now().plusSeconds(900));
        updateQuizBasicInfo.setDuration(2);

        Authentication authentication = new UsernamePasswordAuthenticationToken("teacher@gmail.com",
                "teacher@gmail.com", Collections.singletonList(new SimpleGrantedAuthority("TEACHER")));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        quiz.setStatus(EQuizStatus.PRIVATE);
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        try{
            quizService.editBasicInfo(updateQuizBasicInfo);
        } catch (QuizNotFoundException | QuizAlreadySubmittedException | QuizIsBeingTakenException e) {
            // Handle exceptions if needed
        }
        verify(quizRepository).save(any(Quiz.class));
    }

}

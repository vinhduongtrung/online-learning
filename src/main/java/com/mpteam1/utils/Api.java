package com.mpteam1.utils;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/12/2024, Fri
 **/


public interface Api {
    String API = "/api/v1";
    String TEACHER = "/teacher";
    String SUBJECTS = "/subjects";
    String CLAZZ = "/clazz";
    String STUDENT = "/student";
    String SCHEDULE = "/schedule";
    String QUESTION = "/question";
    String QUIZ = "/quiz";
    String ROOM = "/room";
    interface User {
        String LOGIN = API + "/login";
        String LOGIN_BY_GOOGLE = API + "/login-by-google";
        String LOGOUT = API + "/logout";
        String FORGOT_PASSWORD = API + "/forgot-password";
        String CHANGE_PASSWORD = API + "/change-password";
        String RESET_PASSWORD = API + "/reset-password";
        String TOKEN = API + "/extendToken";
        String CHECK_TOKEN_EXPIRATION = API + "/check-token";
        String ROLE = API + "/get-role";
        String UPDATE_AVATAR = API + "/update-avatar";
        String GET_ALL = API + "/get-all";
    }
    interface Teacher {
        String ADD = API + TEACHER + "/add";
        String EDIT = API + TEACHER + "/edit";
        String LIST = API + TEACHER + "/list";
        String LIST_INSIDE = API + TEACHER + "/list/inside";
        String LIST_OUTSIDE = API + TEACHER + "/list/outside";
        String BULK_DELETE = API + TEACHER + "/bulkDelete";
        String DELETE = API + TEACHER + "/delete/{id}";
        String DETAIL = API + TEACHER +  "/detail/{id}";
        String COUNT = API + TEACHER +  "/count";
    }
    interface Clazz {
        String ADD = API + CLAZZ + "/add";
        String EDIT = API + CLAZZ + "/edit";
        String LIST = API + CLAZZ + "/list";
        String BULK_DELETE = API + CLAZZ + "/bulkDelete";
        String DELETE = API + CLAZZ + "/delete/{id}";
        String DETAIL = API + CLAZZ +  "/detail/{id}";
        String COUNT = API + CLAZZ +  "/count";
        String ASSIGN_STUDENTS = API + CLAZZ + "/assignStudents";
        String REMOVE_STUDENTS = API + CLAZZ + "/removeStudents";
        String ASSIGN_TEACHERS = API + CLAZZ + "/assignTeachers";
        String REMOVE_TEACHERS = API + CLAZZ + "/removeTeachers";
    }
    interface Subject {
        String LIST = API + SUBJECTS + "/list";
        String BULK_INSERT = API + SUBJECTS + "/bulkInsert";
    }
    interface Student {
        String BULK_INSERT = API + STUDENT + "/bulkInsert";
        String EDIT = API + STUDENT + "/edit";
        String LIST = API + STUDENT + "/list";
        String OUTSIDE_CLASS_LIST = API + STUDENT + "/list/outside";
        String INSIDE_CLASS_LIST = API + STUDENT + "/list/inside";
        String LIST_BY_TEACHER = API + STUDENT + "/listByTeacher";
        String DETAIL = API + STUDENT +  "/detail/{id}";

        String COUNT = API + STUDENT +  "/count";
        String BULK_DELETE = API + STUDENT + "/bulkDelete";
    }
    interface Schedule {
        String GET_TEACHER_SCHEDULES = API + SCHEDULE + "/get-teacher-schedules";
        String GET_STUDENT_SCHEDULES = API + SCHEDULE + "/get-student-schedules";
        String GET_SCHEDULES = API + SCHEDULE + "/get-schedules";
    }

    interface Question {
        String IMPORT_QUESTIONS = API + QUESTION + "/import-questions";
        String GET_RANDOM_QUESTIONS = API + QUESTION + "/get-random-questions";
    }

    interface Quiz {
        String IMPORT_QUESTIONS = API + QUIZ + "/import-questions";
        String VIEW_TEST_RESULT = API + QUIZ + "/view-test-result";
        String VIEW_TEST_RESULT2 = API + QUIZ + "/view-test-result2";
        String CAN_ENTER_QUIZ = API + QUIZ + "/can-enter-quiz";
        String ENTER_QUIZ = API + QUIZ + "/enter-quiz";
        String CREATE = API + QUIZ + "/create";
        String ADD_QUESTIONS = API + QUIZ + "/add-questions";
        String DETAIL = API + QUIZ + "/detail";
        String QUESTIONS = API + QUIZ + "/questions";
        String DELETE_QUESTIONS = API + QUIZ + "/delete-questions";
        String GRADE_QUIZ = API + QUIZ + "/grade-quiz";

        String EDIT = API + QUIZ + "/edit";

        String UPDATE_QUESTION = API + QUIZ + "/update-question";

        String PUBLISH_QUIZ = API + QUIZ + "/publish";

        String CLOSE_QUIZ = API + QUIZ + "/close";

        String STATISTIC = API + QUIZ + "/statistic";

        String GRADE_STATISTIC = API + QUIZ + "/grade-statistic";

        String ANSWER_STATISTIC = API + QUIZ + "/answer-statistic";
        String CLASS_QUIZZES = API + QUIZ + "/class-quizzes";
        String RAISE_TAB_LEAVES_NUMBER = API + QUIZ + "/raise-tab-leaves-number";
    }
    interface Room {
        String CREATE = API + ROOM + "/create";
        String GET_ALL_MSG_IN_ROOM = API + ROOM + "/get-all-msg-in-room";
        String GET_ALL_ROOMS_OF_USER = API + ROOM + "/get-all-rooms-of-user";
        String CHANGE_ROOM_NAME = API + ROOM + "/change-room-name";
    }
}

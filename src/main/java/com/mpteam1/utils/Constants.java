package com.mpteam1.utils;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/25/2024, Thu
 **/


public class Constants {
    public static final String HCM_LOCAL_TIME = "Asia/Ho_Chi_Minh";
    public static final String[] PUBLIC_URL = {"/", "/api/v1/login", "/api/v1/logout", "/api/v1/extendToken", "/api/v1/forgot-password", "/api/v1/reset-password", "/api/v1/check-token", "/api/v1/login-by-google", "/ws/**"};
    public static final String[] SWAGGER_URL = {"/swagger-ui", "/swagger-ui/index.html", "/v3/api-docs", "/swagger-ui/favicon-32x32.png", "/v3/api-docs/swagger-config", "/swagger-ui/swagger-ui.css", "/swagger-ui/swagger-initializer.js", "/swagger-ui/swagger-ui-standalone-preset.js", "/swagger-ui/index.css", "/swagger-ui/swagger-ui-bundle.js"};
    public static final String[] PERMIT_ALL_ROLE = {"/api/v1/change-password", "/api/v1/get-role", "/api/v1/update-avatar"};
    public static final String[] TEACHER_RESTRICTION = {"/api/v1/teacher/edit", "/api/v1/teacher/detail/**", "/api/v1/quiz/view-test-result", "/api/v1/quiz/create", "/api/v1/quiz/add-questions"};
    public static final String[] ADMIN_RESTRICTION = {"/api/v1/teacher/add", "/api/v1/teacher/delete/**", "/api/v1/admin/add", "/api/v1/admin/edit", "/api/v1/admin/delete/**", "/api/v1/admin/detail/**", "/api/v1/get-role", "/api/v1/change-password"};
    public static final String[] STUDENT_RESTRICTION = {"/api/v1/student/edit", "/api/v1/student/detail/**"};

}

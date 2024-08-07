package com.mpteam1.utils;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/14/2024, Sun
 **/


public interface ValidatorPattern {
    // Password must be between 6 and 30 characters long and must contain at least one uppercase letter, one lowercase letter, one digit, and one special character
    String VALID_PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,30}$$";
    // Phone number must be in range 8 - 13
    String PHONE_NUMBER = "\\d{8,13}";
    // dd-MM-yyyy
    String DATE_FORMAT = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[01])$";
    // Email must in be valid format
    String EMAIL_FORMAT = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    // Full name must contain only letters
    String FULL_NAME = "^[A-Za-z ]*$";
    // Username must not be empty and not start with a digit and contain only letters, digits, or underscores
    String USER_NAME = "^[A-Za-z][A-Za-z0-9_]*$";
}

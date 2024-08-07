package com.mpteam1.dto.request.student;

import com.mpteam1.utils.StringListConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class StudentDTOCreate {
    @NotBlank
    @Pattern(regexp = "^[^\\d]+$", message = "Full name cannot contain numbers")
    private String fullName;

    @PastOrPresent
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private boolean gender;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String password;
    @NotBlank
    private String address;

    @StringListConstraint
    private List<String> clazzName;
}
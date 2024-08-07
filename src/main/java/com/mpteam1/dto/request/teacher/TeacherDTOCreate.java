package com.mpteam1.dto.request.teacher;

import com.mpteam1.utils.StringListConstraint;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class TeacherDTOCreate {

    @NotBlank
    @Email
    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotBlank
    @Pattern(regexp = "^\\D+$", message = "Full name cannot contain numbers")
    private String fullName;

    @NotBlank
    private String password;

    @NotBlank
    private String address;

    @PastOrPresent
    @NotNull
    private LocalDate dateOfBirth;

    @NotBlank
    private String phoneNumber;

    private String qualification;

    private boolean gender;

    @StringListConstraint
    List<String> subjects;

}

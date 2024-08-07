package com.mpteam1.dto.request.teacher;

import com.mpteam1.utils.StringSetConstraint;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@ToString
public class TeacherDTOUpdate {

    @NotNull
    private Long id;

    @NotBlank
    @Pattern(regexp = "ACTIVE|RETIRED", message = "{employmentStatus.message}")
    private String employmentStatus;

    private String qualification;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotBlank
    private String fullName;

    @NotBlank
    private String address;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotBlank
    private String phoneNumber;

    private boolean gender;

    @Size(min = 1, message = "{subjects.message}")
    @StringSetConstraint
    Set<String> subjects;

}

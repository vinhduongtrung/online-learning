package com.mpteam1.dto.request.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class StudentDTOUpdate {

    @NotNull
    private Long id;

    @NotBlank
    private String fullName;

    @PastOrPresent
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private boolean gender;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String address;
}

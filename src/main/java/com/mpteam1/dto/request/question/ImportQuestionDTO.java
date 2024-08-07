package com.mpteam1.dto.request.question;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportQuestionDTO {
    @NotNull
    private MultipartFile file;
    @NotNull
    private Long quizId;
}

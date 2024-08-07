package com.mpteam1.rest;

import com.mpteam1.dto.request.subject.SubjectCreateWrapper;
import com.mpteam1.dto.request.subject.SubjectDTOCreate;
import com.mpteam1.dto.response.common.APIListResponse;
import com.mpteam1.dto.response.common.ResponseError;
import com.mpteam1.dto.response.common.ResponseSuccess;
import com.mpteam1.dto.response.subject.SubjectDTOResponse;
import com.mpteam1.exception.custom.exception.SubjectDuplicateException;
import com.mpteam1.services.SubjectService;
import com.mpteam1.utils.Api;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class SubjectController {

    private SubjectService subjectService;

    @GetMapping(Api.Subject.LIST)
    public ResponseEntity<?> list() {
        List<SubjectDTOResponse> subjects = subjectService.getAll();
        if (subjects.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ResponseSuccess(HttpStatus.NO_CONTENT,
                            "found no subjects"));
        }
        APIListResponse<List<SubjectDTOResponse>> listResponse = new APIListResponse<>(subjects.size(), subjects);
        return new ResponseEntity<>(listResponse, HttpStatus.OK);
    }

    @PostMapping(Api.Subject.BULK_INSERT)
    public ResponseEntity<?> bulkInsert(@RequestBody @Valid SubjectCreateWrapper subjectWrapper) throws SubjectDuplicateException {
        List<SubjectDTOCreate> subjects = subjectWrapper.getSubjects();
        if (subjects == null || subjects.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseError(HttpStatus.BAD_REQUEST,
                            "Insert with wrong format, check your input"));
        }
        List<SubjectDTOResponse> subjectList = subjectService.bulkInsert(subjects);
        if (subjectList.isEmpty()) {
            throw new SubjectDuplicateException("No subjects were inserted because of duplicated");
        }
        APIListResponse<List<SubjectDTOResponse>> listResponse = new APIListResponse<>(subjectList.size(), subjectList);
        return new ResponseEntity<>(listResponse, HttpStatus.OK);
    }

}

package com.mpteam1.services;

import com.mpteam1.dto.request.subject.SubjectDTOCreate;
import com.mpteam1.dto.response.subject.SubjectDTOResponse;

import java.util.List;

public interface SubjectService {
    List<SubjectDTOResponse> getAll();
    List<SubjectDTOResponse> bulkInsert(List<SubjectDTOCreate> subjectDTOList);
}

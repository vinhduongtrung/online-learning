package com.mpteam1.services.impl;

import com.mpteam1.dto.request.subject.SubjectDTOCreate;
import com.mpteam1.dto.response.subject.SubjectDTOResponse;
import com.mpteam1.entities.Subjects;
import com.mpteam1.repository.SubjectRepository;
import com.mpteam1.services.SubjectService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private SubjectRepository subjectRepository;
    @Override
    public List<SubjectDTOResponse> getAll() {
        List<Subjects> subjects = subjectRepository.findAll();
        List<SubjectDTOResponse> responseList = new ArrayList<>();
        for (Subjects savedSubject : subjects) {
            responseList.add(new SubjectDTOResponse(savedSubject.getId(), savedSubject.getName()));
        }
        return responseList;
    }

    @Override
    public List<SubjectDTOResponse> bulkInsert(List<SubjectDTOCreate> subjectDTOList) {
        List<SubjectDTOResponse> responseList = new ArrayList<>();
        for(SubjectDTOCreate subjectDTOCreate : subjectDTOList) {
            String subjectName = subjectDTOCreate.getName();
            Subjects existingSubject = subjectRepository.findByName(subjectName).orElse(null);
            if (existingSubject == null) {
                Subjects subject = new Subjects(subjectName);
                Subjects savedSubject = subjectRepository.save(subject);
                responseList.add(new SubjectDTOResponse(savedSubject.getId(), savedSubject.getName()));
            }
        }
        return responseList;
    }
}

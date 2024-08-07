package com.mpteam1.services;

import com.mpteam1.dto.request.teacher.TeacherDTODelete;
import com.mpteam1.dto.response.common.PageForView;
import com.mpteam1.dto.request.teacher.TeacherDTOCreate;
import com.mpteam1.dto.request.teacher.TeacherDTOUpdate;
import com.mpteam1.dto.response.teacher.TeacherDTOResponse;
import com.mpteam1.dto.response.teacher.TeacherDetail;
import com.mpteam1.exception.custom.exception.TeacherDuplicateException;
import com.mpteam1.exception.custom.exception.TeacherNotFoundException;

import java.util.List;


public interface TeacherService {
    TeacherDTOResponse add(TeacherDTOCreate teacherDTOCreate) throws TeacherDuplicateException;
    TeacherDTOResponse findById(long id) throws TeacherNotFoundException;
    TeacherDTOResponse edit(TeacherDTOUpdate teacherDTOUpdate) throws TeacherNotFoundException;
    PageForView<TeacherDTOResponse> list(int pageNum, int pageSize, String keyword);
    PageForView<TeacherDetail> fetchAllByCLazzId(int pageNum, int pageSize, Long clazzId);
    PageForView<TeacherDetail> fetchAllAvailableTeacher(int pageNum, int pageSize, Long clazzId);
    boolean deleteById(long id) throws TeacherNotFoundException;
    void bulkDelete(List<TeacherDTODelete> teacherDTODeletes) throws TeacherNotFoundException;
    long countTeachersThatHaveClasses();
    long countTeachers();
}

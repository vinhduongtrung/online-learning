package com.mpteam1.services;

import com.mpteam1.dto.request.student.StudentCreateWrapper;
import com.mpteam1.dto.request.student.StudentDTODeleteWrapper;
import com.mpteam1.dto.request.student.StudentDTOUpdate;
import com.mpteam1.dto.response.common.PageForView;
import com.mpteam1.dto.response.student.StudentDTOResponse;
import com.mpteam1.dto.response.student.StudentStatus;
import com.mpteam1.exception.custom.exception.StudentDuplicateException;
import com.mpteam1.exception.custom.exception.ClassNotFoundException;
import com.mpteam1.exception.custom.exception.StudentNotFoundException;
import com.mpteam1.exception.custom.exception.TeacherNotFoundException;

public interface StudentService {
    void bulkInsert(StudentCreateWrapper studentCreateWrapper) throws StudentDuplicateException, ClassNotFoundException;
    void edit(StudentDTOUpdate studentDTOUpdate) throws StudentNotFoundException;
    void bulkDelete(StudentDTODeleteWrapper studentDeleteWrapper) throws StudentNotFoundException;
    StudentDTOResponse findById(Long id) throws StudentNotFoundException;
    PageForView<StudentDTOResponse> list(int pageNumber, int pageSize, String keyword) throws StudentNotFoundException, TeacherNotFoundException;
    PageForView<StudentDTOResponse> findAllStudentOutSideClazz(int pageNumber, int pageSize, Long clazzId, String keyword);
    PageForView<StudentDTOResponse> findAllStudentInSideClazz(int pageNumber, int pageSize, Long clazzId, String keyword) throws StudentNotFoundException;
    PageForView<StudentDTOResponse> findAllByTeacher(int page, int size, Long teacherId, String keyword) throws StudentNotFoundException;
    StudentStatus getStudentStatus();


}

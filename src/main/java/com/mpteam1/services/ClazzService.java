package com.mpteam1.services;

import com.mpteam1.dto.request.clazz.*;
import com.mpteam1.dto.response.clazz.ClazzCountDTOResponse;
import com.mpteam1.dto.response.clazz.ClazzDTOResponse;
import com.mpteam1.dto.response.common.PageForView;
import com.mpteam1.exception.custom.exception.*;

import java.time.DateTimeException;
import java.util.List;

public interface ClazzService {

    void addBulkClazz(List<ClazzDTOCreate> clazzes) throws TeacherNotFoundException, ClassDuplicateException;

    PageForView<ClazzDTOResponse> list(int pageNum, int pageSize, String keyword);
    void add(ClazzDTOCreate clazz) throws TeacherNotFoundException, ClassDuplicateException, DateTimeException, StudentNotFoundException;
    ClazzDTOResponse findById(long id) throws ClazzNotFoundException, PermissionException;
    ClazzDTOResponse update(ClazzDTOUpdate clazzDTOUpdate) throws ClazzNotFoundException, TeacherNotFoundException, ClassDuplicateException, DateTimeException, StudentNotFoundException, ConflictedScheduleException;
    boolean deleteById(long id) throws ClazzNotFoundException;
    void bulkDelete(List<ClazzDTODelete> clazzDTODeletes) throws ClazzNotFoundException;
    ClazzCountDTOResponse countClasses();

    void assignStudents(ClazzDTOAssignStudents clazzDTOAssignStudents) throws ClazzNotFoundException, StudentNotFoundException, ConflictedScheduleException, MaximumCapacityReachedException;

    void removeStudents(ClazzDTOAssignStudents clazzDTOAssignStudents) throws ClazzNotFoundException, StudentNotFoundException;

    void assignTeacher(ClazzDTOAssignTeachers clazzDTOAssignTeachers) throws ClazzNotFoundException, TeacherNotFoundException, ConflictedScheduleException;

    void removeTeacher(ClazzDTOAssignTeachers clazzDTOAssignTeachers) throws ClazzNotFoundException, TeacherNotFoundException;
}

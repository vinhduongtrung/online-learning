package com.mpteam1.rest;

import com.mpteam1.dto.request.student.StudentCreateWrapper;
import com.mpteam1.dto.request.student.StudentDTODeleteWrapper;
import com.mpteam1.dto.request.student.StudentDTOList;
import com.mpteam1.dto.request.student.StudentDTOUpdate;
import com.mpteam1.dto.request.teacher.TeacherID;
import com.mpteam1.dto.response.common.PageForView;
import com.mpteam1.dto.response.common.ResponseError;
import com.mpteam1.dto.response.common.ResponseSuccess;
import com.mpteam1.dto.response.student.StudentDTOResponse;
import com.mpteam1.dto.response.student.StudentStatus;
import com.mpteam1.exception.custom.exception.StudentDuplicateException;
import com.mpteam1.exception.custom.exception.ClassNotFoundException;
import com.mpteam1.exception.custom.exception.StudentNotFoundException;
import com.mpteam1.exception.custom.exception.TeacherNotFoundException;
import com.mpteam1.services.StudentService;
import com.mpteam1.utils.Api;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class StudentController {
    private StudentService studentService;

    @PostMapping(Api.Student.BULK_INSERT)
    public ResponseEntity<?> bulkInsertStudent(@RequestBody @Valid StudentCreateWrapper studentCreateWrapper)
            throws StudentDuplicateException, ClassNotFoundException {
        try {
            studentService.bulkInsert(studentCreateWrapper);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseSuccess(HttpStatus.OK,
                            "Students inserted successfully"));
        }
        catch (StudentDuplicateException e) {
            throw new StudentDuplicateException(e.getMessage());
        }
        catch (ClassNotFoundException e) {
            throw new ClassNotFoundException(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to insert students"));
        }
    }

    @PutMapping(Api.Student.EDIT)
    public ResponseEntity<?> edit(@RequestBody @Valid StudentDTOUpdate studentDTOUpdate) throws StudentNotFoundException {
        try {
            studentService.edit(studentDTOUpdate);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseSuccess(HttpStatus.OK,
                            "Student updated successfully"));
        }
        catch (StudentNotFoundException e) {
            throw new StudentNotFoundException(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to edit student with ID: " + studentDTOUpdate.getId()));
        }
    }

    @GetMapping(Api.Student.LIST)
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(defaultValue = "") String keyword)
            throws StudentNotFoundException, TeacherNotFoundException {
        PageForView<StudentDTOResponse> studentList = studentService.list(page, size, keyword);
        return new ResponseEntity<>(studentList, HttpStatus.OK);
    }

    @PostMapping(Api.Student.OUTSIDE_CLASS_LIST)
    public ResponseEntity<?> outSideList(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "") String keyword,
                                         @RequestBody @Valid StudentDTOList studentDTOList) throws StudentNotFoundException {
        PageForView<StudentDTOResponse> studentList = studentService.findAllStudentOutSideClazz(page, size, studentDTOList.getClassId(), keyword);
        return new ResponseEntity<>(studentList, HttpStatus.OK);

    }

    @PostMapping(Api.Student.INSIDE_CLASS_LIST)
    public ResponseEntity<?> inSideList(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "") String keyword,
                                        @RequestBody @Valid StudentDTOList studentDTOList) throws StudentNotFoundException {
        PageForView<StudentDTOResponse> studentList = studentService.findAllStudentInSideClazz(page, size, studentDTOList.getClassId(), keyword);
        return new ResponseEntity<>(studentList, HttpStatus.OK);
    }

    @PostMapping(Api.Student.LIST_BY_TEACHER)
    public ResponseEntity<?> inSideList(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "") String keyword,
                                        @RequestBody @Valid TeacherID teacherID) throws StudentNotFoundException {
        PageForView<StudentDTOResponse> studentList = studentService.findAllByTeacher(page, size, teacherID.getTeacherId(), keyword);
        return new ResponseEntity<>(studentList, HttpStatus.OK);

    }

    @GetMapping(Api.Student.DETAIL)
    public ResponseEntity<?> detail(@PathVariable Long id) throws StudentNotFoundException {
        StudentDTOResponse studentDTOResponse = studentService.findById(id);
        return new ResponseEntity<>(studentDTOResponse, HttpStatus.OK);
    }

    @GetMapping(Api.Student.COUNT)
    public ResponseEntity<?> count() {
        StudentStatus studentStatus = studentService.getStudentStatus();
        return new ResponseEntity<>(studentStatus, HttpStatus.OK);
    }

    @DeleteMapping(Api.Student.BULK_DELETE)
    public ResponseEntity<?> bulkDelete(@RequestBody @Valid StudentDTODeleteWrapper studentDeleteWrapper)
            throws StudentNotFoundException {
        try {
            studentService.bulkDelete(studentDeleteWrapper);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseSuccess(HttpStatus.OK,
                            "Students deleted successfully"));
        }
        catch (StudentNotFoundException e) {
            throw new StudentNotFoundException(e.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to delete students"));

        }
    }
}

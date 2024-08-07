package com.mpteam1.rest;

import com.mpteam1.dto.request.clazz.ClazzID;
import com.mpteam1.dto.request.teacher.TeacherDTOCreate;
import com.mpteam1.dto.request.teacher.TeacherDTODelete;
import com.mpteam1.dto.request.teacher.TeacherDTOUpdate;
import com.mpteam1.dto.response.common.ResponseError;
import com.mpteam1.dto.response.common.ResponseSuccess;
import com.mpteam1.dto.response.teacher.TeacherCountDTOResponse;
import com.mpteam1.dto.response.teacher.TeacherDTOResponse;
import com.mpteam1.exception.custom.exception.TeacherDuplicateException;
import com.mpteam1.exception.custom.exception.TeacherNotFoundException;
import com.mpteam1.services.TeacherService;
import com.mpteam1.utils.Api;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class TeacherController {
    private TeacherService teacherService;

    @PostMapping(Api.Teacher.ADD)
    public ResponseEntity<?> addTeacher(@RequestBody @Valid TeacherDTOCreate teacherDTOCreate)
            throws TeacherDuplicateException {
        try {
            TeacherDTOResponse teacherDTOResponse = teacherService.add(teacherDTOCreate);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseSuccess(HttpStatus.OK,
                            "Teacher added successfully with ID: " + teacherDTOResponse.getId()));
        } catch (TeacherDuplicateException e) {
            throw new TeacherDuplicateException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add teacher"));
        }
    }


    @PutMapping(Api.Teacher.EDIT)
    public ResponseEntity<?> editTeacher(@RequestBody @Valid TeacherDTOUpdate teacherDTOUpdate)
            throws TeacherNotFoundException {
        try {
            TeacherDTOResponse teacherDTOResponse = teacherService.edit(teacherDTOUpdate);
            System.out.println(teacherDTOResponse);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseSuccess(HttpStatus.OK,
                            "Teacher edited successfully with ID: " + teacherDTOResponse.getId()));
        } catch (TeacherNotFoundException e) {
            throw new TeacherNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to edit teacher with ID: " + teacherDTOUpdate.getId()));
        }
    }

    @DeleteMapping(Api.Teacher.DELETE)
    public ResponseEntity<?> deleteTeacher(@PathVariable Long id) throws TeacherNotFoundException {
        try {
            teacherService.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseSuccess(HttpStatus.OK,
                            "Teacher deleted successfully with ID: " + id));
        } catch (TeacherNotFoundException e) {
            throw new TeacherNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to delete teacher with ID: " + id));
        }
    }

    @GetMapping(Api.Teacher.DETAIL)
    public TeacherDTOResponse getTeacherDetail(@PathVariable long id) throws TeacherNotFoundException {
        return teacherService.findById(id);
    }

    @PostMapping(Api.Teacher.LIST)
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(defaultValue = "") String keyword) {
        return ResponseEntity.ok().body(teacherService.list(page, size, keyword));
    }

    @PostMapping(Api.Teacher.LIST_INSIDE)
    public ResponseEntity<?> listInside(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestBody @Valid ClazzID clazz) {
        return ResponseEntity.ok().body(teacherService.fetchAllByCLazzId(page, size, clazz.getClassId()));
    }

    @PostMapping(Api.Teacher.LIST_OUTSIDE)
    public ResponseEntity<?> listOutside(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestBody @Valid ClazzID clazz) {
        return ResponseEntity.ok().body(teacherService.fetchAllAvailableTeacher(page, size, clazz.getClassId()));
    }

    @DeleteMapping(Api.Teacher.BULK_DELETE)
    public ResponseEntity<?> bulkDelete(@RequestBody List<@Valid TeacherDTODelete> teacherDTOList)
            throws TeacherNotFoundException {
        try {
            teacherService.bulkDelete(teacherDTOList);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseSuccess(HttpStatus.OK,
                            "delete successfully."));
        } catch (TeacherNotFoundException e) {
            throw new TeacherNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to delete teacher"));
        }
    }

    @GetMapping(Api.Teacher.COUNT)
    public TeacherCountDTOResponse countTeachers() {
        return new TeacherCountDTOResponse(teacherService.countTeachersThatHaveClasses(), teacherService.countTeachers());
    }
}

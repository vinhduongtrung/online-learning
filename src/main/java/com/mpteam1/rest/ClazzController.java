package com.mpteam1.rest;

import com.mpteam1.dto.request.clazz.*;
import com.mpteam1.dto.response.clazz.ClazzCountDTOResponse;
import com.mpteam1.dto.response.clazz.ClazzDTOResponse;
import com.mpteam1.dto.response.common.PageForView;
import com.mpteam1.dto.response.common.ResponseError;
import com.mpteam1.dto.response.common.ResponseSuccess;
import com.mpteam1.exception.custom.exception.*;
import com.mpteam1.services.ClazzService;
import com.mpteam1.utils.Api;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Slf4j
public class ClazzController {
    private ClazzService clazzService;

    @PostMapping(Api.Clazz.LIST)
    public PageForView<?> list(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "") String keyword) {
        return clazzService.list(page, size, keyword);
    }

    @PostMapping(Api.Clazz.ADD)
    public ResponseEntity<?> addClazz(@RequestBody @Valid ClazzDTOCreate clazzDTOCreate)
            throws TeacherNotFoundException, ClassDuplicateException {
        try {
            clazzService.add(clazzDTOCreate);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseSuccess(HttpStatus.OK,
                            "Clazz inserted successfully"));
        } catch (TeacherNotFoundException e) {
            log.error(e.getMessage());
            throw new TeacherNotFoundException(e.getMessage());
        } catch (ClassDuplicateException e) {
            log.error(e.getMessage());
            throw new ClassDuplicateException(e.getMessage());
        } catch(DateTimeException e) {
            log.error(e.getMessage());
            throw new DateTimeException(e.getMessage());
        } catch(Exception e) {
            log.error(e.getMessage() +Arrays.stream(e.getStackTrace()).map(stackTraceElement -> {
                return stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "#" + stackTraceElement.getLineNumber() + "\n";
            }).collect(Collectors.joining()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to add clazz with name: " + clazzDTOCreate.getName()));
        }
    }

    @PostMapping(Api.Clazz.ASSIGN_STUDENTS)
    public ResponseEntity<?> assignStudents(@RequestBody @Valid ClazzDTOAssignStudents clazzDTOAssignStudents)
            throws ClazzNotFoundException, StudentNotFoundException, ConflictedScheduleException, MaximumCapacityReachedException {
        try {
            clazzService.assignStudents(clazzDTOAssignStudents);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseSuccess(HttpStatus.OK,
                            "Assigned students to class successfully"));
        }  catch (ClazzNotFoundException e) {
            log.error(e.getMessage());
            throw new ClazzNotFoundException(e.getMessage());
        } catch (StudentNotFoundException e) {
            log.error(e.getMessage());
            throw new StudentNotFoundException(e.getMessage());
        } catch (ConflictedScheduleException e) {
            log.error(e.getMessage());
            throw new ConflictedScheduleException(e.getMessage());
        }  catch (MaximumCapacityReachedException e) {
            log.error(e.getMessage());
            throw new MaximumCapacityReachedException(e.getMessage());
        } catch(Exception e) {
            log.error(e.getMessage() + Arrays.stream(e.getStackTrace()).map(stackTraceElement -> {
                return stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "#" + stackTraceElement.getLineNumber() + "\n";
            }).collect(Collectors.joining()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to assign students"));
        }
    }

    @DeleteMapping(Api.Clazz.REMOVE_STUDENTS)
    public ResponseEntity<?> removeStudents(@RequestBody @Valid ClazzDTOAssignStudents clazzDTOAssignStudents)
            throws ClazzNotFoundException, StudentNotFoundException {
        try {
            clazzService.removeStudents(clazzDTOAssignStudents);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseSuccess(HttpStatus.OK,
                            "Removed students from class successfully"));
        }  catch (ClazzNotFoundException e) {
            log.error(e.getMessage());
            throw new ClazzNotFoundException(e.getMessage());
        } catch (StudentNotFoundException e) {
            log.error(e.getMessage());
            throw new StudentNotFoundException(e.getMessage());
        } catch(Exception e) {
            log.error(e.getMessage() +Arrays.stream(e.getStackTrace()).map(stackTraceElement -> {
                return stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "#" + stackTraceElement.getLineNumber() + "\n";
            }).collect(Collectors.joining()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to remove students"));
        }
    }

    @PostMapping(Api.Clazz.ASSIGN_TEACHERS)
    public ResponseEntity<?> assignTeachers(@RequestBody @Valid ClazzDTOAssignTeachers clazzDTOAssignTeachers)
            throws ClazzNotFoundException, TeacherNotFoundException, ConflictedScheduleException {
        try {
            clazzService.assignTeacher(clazzDTOAssignTeachers);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseSuccess(HttpStatus.OK,
                            "Assigned teachers to class successfully"));
        }  catch (ClazzNotFoundException e) {
            log.error(e.getMessage());
            throw new ClazzNotFoundException(e.getMessage());
        } catch (TeacherNotFoundException e) {
            log.error(e.getMessage());
            throw new TeacherNotFoundException(e.getMessage());
        } catch (ConflictedScheduleException e) {
            log.error(e.getMessage());
            throw new ConflictedScheduleException(e.getMessage());
        } catch(Exception e) {
            log.error(e.getMessage() +Arrays.stream(e.getStackTrace()).map(stackTraceElement -> {
                return stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "#" + stackTraceElement.getLineNumber() + "\n";
            }).collect(Collectors.joining()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to assign teachers"));
        }
    }

    @DeleteMapping(Api.Clazz.REMOVE_TEACHERS)
    public ResponseEntity<?> removeTeachers(@RequestBody @Valid ClazzDTOAssignTeachers clazzDTOAssignTeachers)
            throws ClazzNotFoundException, TeacherNotFoundException {
        try {
            clazzService.removeTeacher(clazzDTOAssignTeachers);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseSuccess(HttpStatus.OK,
                            "Removed teachers from class successfully"));
        }  catch (ClazzNotFoundException e) {
            log.error(e.getMessage());
            throw new ClazzNotFoundException(e.getMessage());
        } catch (TeacherNotFoundException e) {
            log.error(e.getMessage());
            throw new TeacherNotFoundException(e.getMessage());
        } catch(Exception e) {
            log.error(e.getMessage() +Arrays.stream(e.getStackTrace()).map(stackTraceElement -> {
                return stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "#" + stackTraceElement.getLineNumber() + "\n";
            }).collect(Collectors.joining()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to remove teachers"));
        }
    }

    @GetMapping(Api.Clazz.DETAIL)
    public ResponseEntity<?> getClazzById(@PathVariable("id") long id) throws ClazzNotFoundException {
        try {
            ClazzDTOResponse dto = clazzService.findById(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (ClazzNotFoundException e) {
            log.error(e.getMessage());
            throw new ClazzNotFoundException(e.getMessage());
        } catch (PermissionException e) {
            log.error(e.getMessage());
            throw new PermissionException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage() +Arrays.stream(e.getStackTrace()).map(stackTraceElement -> {
                return stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "#" + stackTraceElement.getLineNumber() + "\n";
            }).collect(Collectors.joining()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to get clazz detail with id: " + id));
        }
    }

    @PutMapping(Api.Clazz.EDIT)
    public ResponseEntity<Map<String, String>> editClazz(@RequestBody @Valid ClazzDTOUpdate clazzDTOUpdate)
            throws ClazzNotFoundException, TeacherNotFoundException, ConflictedScheduleException {
        Map<String, String> response = new HashMap<>();
        try {
            ClazzDTOResponse clazzDTOResponse = clazzService.update(clazzDTOUpdate);
            response.put("message", "Class updated successfully with ID: " + clazzDTOResponse.getId());
            return ResponseEntity.ok(response);
        } catch (TeacherNotFoundException e) {
            throw new TeacherNotFoundException(e.getMessage());
        } catch (ClazzNotFoundException e) {
            throw new ClazzNotFoundException(e.getMessage());
        } catch(DateTimeException e) {
            log.error(e.getMessage());
            throw new DateTimeException(e.getMessage());
        } catch (ConflictedScheduleException e) {
            log.error(e.getMessage());
            throw new ConflictedScheduleException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage() +Arrays.stream(e.getStackTrace()).map(stackTraceElement -> {
                return stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "#" + stackTraceElement.getLineNumber() + "\n";
            }).collect(Collectors.joining()));
            response.put("err", "Failed to edit class with ID: " + clazzDTOUpdate.getId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping(Api.Clazz.DELETE)
    public ResponseEntity<Map<String, String>> deleteClazz(@PathVariable Long id) throws ClazzNotFoundException {
        Map<String, String> response = new HashMap<>();
        try {
            boolean isDeleted = clazzService.deleteById(id);
            if (isDeleted) {
                response.put("message", "Class deleted successfully with ID: " + id);
                return ResponseEntity.ok(response);
            } else {
                response.put("err", "Failed to delete class with ID: " + id);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (ClazzNotFoundException e) {
            throw new ClazzNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage() +Arrays.stream(e.getStackTrace()).map(stackTraceElement -> {
                return stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "#" + stackTraceElement.getLineNumber() + "\n";
            }).collect(Collectors.joining()));
            response.put("err", "Failed to delete class with ID: " + id);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping(Api.Clazz.BULK_DELETE)
    public ResponseEntity<?> bulkDelete(@RequestBody List<@Valid ClazzDTODelete> clazzDTODeletes) throws ClazzNotFoundException {
        Map<String, String> response = new HashMap<>();
        try {
            clazzService.bulkDelete(clazzDTODeletes);
            response.put("message", "delete successfully.");
            return ResponseEntity.ok(response);
        } catch (ClazzNotFoundException e) {
            throw new ClazzNotFoundException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage() +Arrays.stream(e.getStackTrace()).map(stackTraceElement -> {
                return stackTraceElement.getClassName() + "#" + stackTraceElement.getMethodName() + "#" + stackTraceElement.getLineNumber() + "\n";
            }).collect(Collectors.joining()));
            response.put("err", "Failed to delete class");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(Api.Clazz.COUNT)
    public ClazzCountDTOResponse countClasses() {
        return clazzService.countClasses();
    }
}

package com.mpteam1.services.impl;

import com.mpteam1.dto.request.clazz.*;
import com.mpteam1.dto.response.clazz.ClazzCountDTOResponse;
import com.mpteam1.dto.response.clazz.ClazzDTOResponse;
import com.mpteam1.dto.response.common.PageForView;
import com.mpteam1.dto.response.schedule.ScheduleInfoDTO;
import com.mpteam1.entities.*;
import com.mpteam1.entities.enums.ERole;
import com.mpteam1.exception.custom.exception.*;
import com.mpteam1.repository.*;
import com.mpteam1.services.ClazzService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClazzServiceImpl implements ClazzService {

    private ClassRepository classRepository;
    private TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(rollbackFor = {TeacherNotFoundException.class, ClassDuplicateException.class})
    public void addBulkClazz(List<ClazzDTOCreate> clazzes) throws TeacherNotFoundException, ClassDuplicateException {
        List<Clazz> clazzList = new ArrayList<>();
        for (ClazzDTOCreate dto : clazzes) {
            // if class with this name already exists then throw exception
            if (classRepository.findByNameIgnoreCase(dto.getName().trim()).isPresent())
                throw new ClassDuplicateException("Class with name " + dto.getName() + " already exists");
            else {
                Clazz clazz = new Clazz();
                clazz.setName(dto.getName());
                clazz.setCreatedDate(Instant.now());
                clazz.setStartDate(dto.getStartDate());
                clazz.setEndDate(dto.getEndDate());
                if (dto.getClazzCode() != null) clazz.setClazzCode(dto.getClazzCode());
                clazzList.add(clazz);
                for (ScheduleInfoDTO scheduleInfoDTO : dto.getSchedules()) {
                    Schedule schedule = new Schedule();
                    schedule.setDayOfWeek(DayOfWeek.valueOf(scheduleInfoDTO.getDayOfWeek()));
                    schedule.setEndTime(LocalTime.parse(scheduleInfoDTO.getEndTime()));
                    schedule.setStartTime(LocalTime.parse(scheduleInfoDTO.getStartTime()));
                    schedule.setClazz(clazz);
                    clazz.getSchedules().add(schedule);
                }
            }
        }
        clazzList = classRepository.saveAll(clazzList);

    }

    @Override
    public PageForView<ClazzDTOResponse> list(int pageNum, int pageSize, String keyword) {
        String authority = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        PageForView<ClazzDTOResponse> clazzList = new PageForView<>();
        Sort.Order order = new Sort.Order(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(order));
        if (authority.equals("ADMIN")) {
            Page<Clazz> clazzes = classRepository.findAllByKeyword(keyword, pageable);
            if (clazzes != null) {
                clazzList.setContent(clazzes.getContent().stream().map(ClazzDTOResponse::convertClazzToDTO).collect(Collectors.toList()));
                clazzList.setTotalElement(clazzes.getTotalElements());
                clazzList.setSize(clazzes.getSize());
                clazzList.setPage(clazzes.getNumber());
            }
        } else if (authority.equals("TEACHER")) {
            String teacherEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Page<Clazz> clazzes = classRepository.findAllByTeacher_Email(teacherEmail, keyword, pageable);
            if (clazzes != null) {
                clazzList.setContent(clazzes.getContent().stream().map(ClazzDTOResponse::convertClazzToDTO).collect(Collectors.toList()));
                clazzList.setTotalElement(clazzes.getTotalElements());
                clazzList.setSize(clazzes.getSize());
                clazzList.setPage(clazzes.getNumber());
            }
        } else if (authority.equals("STUDENT")) {
            String studentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Page<Clazz> clazzes = classRepository.findAllByStudent_Email(studentEmail, keyword, pageable);
            if (clazzes != null) {
                clazzList.setContent(clazzes.getContent().stream().map(ClazzDTOResponse::convertClazzToDTO).collect(Collectors.toList()));
                clazzList.setTotalElement(clazzes.getTotalElements());
                clazzList.setSize(clazzes.getSize());
                clazzList.setPage(clazzes.getNumber());
            }
        }
        return clazzList;
    }

    @Override
    @Transactional(rollbackFor = {TeacherNotFoundException.class, ClassDuplicateException.class, DateTimeException.class})
    public void add(ClazzDTOCreate clazzDTOCreate) throws TeacherNotFoundException, ClassDuplicateException, DateTimeException, StudentNotFoundException {
        Clazz clazz = new Clazz();
        // if class with this name already exists then throw exception
        if (classRepository.findByNameIgnoreCase(clazzDTOCreate.getName().trim()).isPresent())
            throw new ClassDuplicateException("Class with name " + clazzDTOCreate.getName() + " already exists");
        else if (classRepository.findByClazzCodeIgnoreCase(clazzDTOCreate.getClazzCode().trim()).isPresent()) {
            throw new ClassDuplicateException("Class with ID: " + clazzDTOCreate.getClazzCode() + " already exists");
        } else {
            clazz.setName(clazzDTOCreate.getName());
            clazz.setCreatedDate(Instant.now());
            if (clazzDTOCreate.getStartDate() != null && clazzDTOCreate.getEndDate() != null) {
                if (clazzDTOCreate.getStartDate().isAfter(clazzDTOCreate.getEndDate()))
                    throw new DateTimeException("Start Date must not be after End Date");
            }
            clazz.setStartDate(clazzDTOCreate.getStartDate());
            clazz.setEndDate(clazzDTOCreate.getEndDate());
            clazz.setClazzCode(clazzDTOCreate.getClazzCode());
            clazz.setDescription(clazzDTOCreate.getDescription());
            clazz.setMaximumCapacity(clazzDTOCreate.getMaximumCapacity());
            clazz.setCurrentCapacity(0);
            for (ScheduleInfoDTO dto : clazzDTOCreate.getSchedules()) {
                Schedule schedule = new Schedule();
                schedule.setDayOfWeek(DayOfWeek.valueOf(dto.getDayOfWeek()));
                schedule.setEndTime(LocalTime.parse(dto.getEndTime()));
                schedule.setStartTime(LocalTime.parse(dto.getStartTime()));
                schedule.setClazz(clazz);
                clazz.getSchedules().add(schedule);
            }
            // only after save class then we could add teachers to class!!!
            clazz = classRepository.save(clazz);
        }
    }

    @Override
    public ClazzDTOResponse findById(long id) throws ClazzNotFoundException, PermissionException {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new UserNotFoundException("User not found"));
        Clazz clazz = classRepository.findById(id).orElseThrow(() -> new ClazzNotFoundException("Class not found with id: " + id));
        if (user.getERole().equals(ERole.TEACHER) && !user.getId().equals(clazz.getTeacher().getId()))
            throw new PermissionException("You don't have permission to access this page");
        else if(user.getERole().equals(ERole.STUDENT) && clazz.getStudents().stream().noneMatch(student -> student.getId().equals(user.getId())))
            throw new PermissionException("You don't have permission to access this page");
        return ClazzDTOResponse.convertClazzToDTO(clazz);
    }

    @Override
    @Transactional(rollbackFor = {TeacherNotFoundException.class, ClazzNotFoundException.class, ClassDuplicateException.class, DateTimeException.class, ConflictedScheduleException.class})
    public ClazzDTOResponse update(ClazzDTOUpdate clazzDTOUpdate) throws
            ClazzNotFoundException, TeacherNotFoundException, ClassDuplicateException, DateTimeException, StudentNotFoundException, ConflictedScheduleException {
        Optional<Clazz> clazzOptional = classRepository.findById(clazzDTOUpdate.getId());
        if (clazzOptional.isPresent()) {
            Clazz clazz = clazzOptional.get();
            // in case changing class name or ID, check if any classes with this name already exist then throw exception
            if (!clazzDTOUpdate.getName().equalsIgnoreCase(clazz.getName())
                    && classRepository.findByNameIgnoreCase(clazzDTOUpdate.getName().trim()).isPresent())
                throw new ClassDuplicateException("Class with name " + clazzDTOUpdate.getName() + " already exists");
            else if (!clazzDTOUpdate.getClazzCode().equalsIgnoreCase(clazz.getClazzCode())
                    && classRepository.findByClazzCodeIgnoreCase(clazzDTOUpdate.getClazzCode().trim()).isPresent()) {
                throw new ClassDuplicateException("Class with ID: " + clazzDTOUpdate.getClazzCode() + " already exists");
            } else {
                clazz.setName(clazzDTOUpdate.getName());
                // update schedules
                List<Schedule> schedules = clazz.getSchedules();
                Set<ScheduleInfoDTO> dtoSchedules = clazzDTOUpdate.getSchedules();
                // check for same schedules in dto
                List<Schedule> sameSchedules = schedules.stream().
                        filter(schedule ->
                                dtoSchedules.stream().anyMatch(schedule::isSameSchedule))
                        .toList();
                // get to-be-updated schedules from dto
                Set<ScheduleInfoDTO> toBeUpdated = dtoSchedules.stream()
                        .filter(dtoSchedule -> schedules.stream().noneMatch(schedule -> schedule.isSameSchedule(dtoSchedule)))
                        .collect(Collectors.toSet());
                if (schedules.size() > sameSchedules.size()) {
                    // update or delete
                    // delete the different schedules from original and add new ones if existed
                    schedules.retainAll(sameSchedules);
                }
                for (ScheduleInfoDTO dto : toBeUpdated) {
                    Schedule schedule = new Schedule();
                    schedule.setDayOfWeek(DayOfWeek.valueOf(dto.getDayOfWeek()));
                    schedule.setEndTime(LocalTime.parse(dto.getEndTime()));
                    schedule.setStartTime(LocalTime.parse(dto.getStartTime()));
                    for (Schedule schedule1 : schedules) {
                        if (schedule.isScheduleConflicted(schedule1))
                            throw new ConflictedScheduleException("Updating schedule " + dto + " is conflicting with existing schedule " + ScheduleInfoDTO.toDTO(schedule1));
                    }
                    schedule.setClazz(clazz);
                    clazz.getSchedules().add(schedule);
                }
                // update teacher
                if (clazzDTOUpdate.getTeacherId() != null) {
                    // if teacher doesn't exist then throw exception
                    Teacher teacher = teacherRepository.findById(clazzDTOUpdate.getTeacherId())
                            .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with ID: " + clazzDTOUpdate.getTeacherId()));
                    if (clazz.getTeacher() == null || !clazzDTOUpdate.getTeacherId().equals(clazz.getTeacher().getId())) {
                        // change teacher
                        clazz.setTeacher(teacher);
                        teacher.getClazzes().add(clazz);
                    }
                    //Get teacher's schedules
                    //if there is any conflict and that conflict doesn't come from this class then throw exception
                    Set<Schedule> teacherSchedules = scheduleRepository.findAllByClazz_Teacher_Id(teacher.getId());
                    for (Schedule schedule : teacherSchedules) {
                        if (clazz.getSchedules().stream().anyMatch(schedule1 -> schedule1.isScheduleConflicted(schedule))
                                && !schedule.getClazz().equals(clazz))
                            throw new ConflictedScheduleException("Teacher " + teacher.getFullName()
                                    + " has conflicted schedule " + ScheduleInfoDTO.toDTO(schedule));
                    }
                } else {
                    Teacher teacher = clazz.getTeacher();
                    if (teacher != null) {
                        teacher.getClazzes().remove(clazz);
                        clazz.setTeacher(null);
                    }
                }

                // check for students' schedule conflict
                for (Student student : clazz.getStudents()) {
                    Set<Schedule> studentSchedules = scheduleRepository.findAllByStudentId(student.getId());
                    for (Schedule studentSchedule : studentSchedules) {
                        if (clazz.getSchedules().stream().anyMatch(schedule -> schedule.isScheduleConflicted(studentSchedule))
                                && !studentSchedule.getClazz().getId().equals(clazz.getId()))
                            throw new ConflictedScheduleException("Student " + student.getFullName()
                                    + " has conflicted schedule " + ScheduleInfoDTO.toDTO(studentSchedule));
                    }
                }

                if (clazzDTOUpdate.getStartDate().isAfter(clazzDTOUpdate.getEndDate()))
                    throw new DateTimeException("Start Date must not be after End Date");
                clazz.setStartDate(clazzDTOUpdate.getStartDate());
                clazz.setEndDate(clazzDTOUpdate.getEndDate());
                if (clazzDTOUpdate.getClazzCode() != null) clazz.setClazzCode(clazzDTOUpdate.getClazzCode());
                clazz.setDescription(clazzDTOUpdate.getDescription());
                clazz.setMaximumCapacity(clazzDTOUpdate.getMaximumCapacity());

                clazz = classRepository.save(clazz);
                return ClazzDTOResponse.convertClazzToDTO(clazz);
            }
        } else {
            throw new ClazzNotFoundException("Class not found with id: " + clazzDTOUpdate.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = ClazzNotFoundException.class)
    public boolean deleteById(long id) throws ClazzNotFoundException {
        Optional<Clazz> clazzOptional = classRepository.findById(id);
        if (clazzOptional.isPresent()) {
            Clazz clazz = clazzOptional.get();
            Teacher teacher = clazz.getTeacher();
            if (teacher != null) {
                teacher.getClazzes().remove(clazz);
                clazz.setTeacher(null);
            }

            for (Schedule schedule : clazz.getSchedules()) {
                scheduleRepository.delete(schedule);
            }
            clazz.getSchedules().clear();

            for (Student student : clazz.getStudents()) {
                student.getClazzes().remove(clazz);
            }
            clazz.getStudents().clear();
            for (Quiz quiz : clazz.getQuizzes()) {
                quiz.setClazz(null);
            }
            clazz.getQuizzes().clear();
            classRepository.delete(clazz);
            return true;
        } else {
            throw new ClazzNotFoundException("Class not found with id: " + id);
        }
    }

    @Override
    @Transactional(rollbackFor = ClazzNotFoundException.class)
    public void bulkDelete(List<ClazzDTODelete> clazzDTODeletes) throws ClazzNotFoundException {
        for (ClazzDTODelete clazzDTODelete : clazzDTODeletes) {
            Clazz clazz = classRepository.findById(clazzDTODelete.getId())
                    .orElseThrow(() -> new ClazzNotFoundException("Class not found with ID: " + clazzDTODelete.getId()));
            Teacher teacher = clazz.getTeacher();
            if (teacher != null) {
                teacher.getClazzes().remove(clazz);
                clazz.setTeacher(null);
            }

            for (Schedule schedule : clazz.getSchedules()) {
                scheduleRepository.delete(schedule);
            }
            clazz.getSchedules().clear();

            for (Student student : clazz.getStudents()) {
                student.getClazzes().remove(clazz);
            }
            clazz.getStudents().clear();
            for (Quiz quiz : clazz.getQuizzes()) {
                quiz.setClazz(null);
            }
            clazz.getQuizzes().clear();
            classRepository.delete(clazz);
        }
    }

    @Override
    public ClazzCountDTOResponse countClasses() {
        ClazzCountDTOResponse classCountDTO = new ClazzCountDTOResponse();
        classCountDTO.setClassCount(classRepository.count());
        long activeClassCount = 0;
        for (Clazz clazz : classRepository.findAll()) {
            if (!clazz.getStartDate().isAfter(LocalDate.now()) && !clazz.getEndDate().isBefore(LocalDate.now())) {
                activeClassCount++;
            }
        }
        classCountDTO.setActiveClassCount(activeClassCount);
        return classCountDTO;
    }

    /**
     * Assigns new students to a class
     *
     * @param clazzDTOAssignStudents
     * @throws ClazzNotFoundException   if class not found
     * @throws StudentNotFoundException if student not found
     */
    @Override
    @Transactional(rollbackFor = {ClazzNotFoundException.class, StudentNotFoundException.class, ConflictedScheduleException.class})
    public void assignStudents(ClazzDTOAssignStudents clazzDTOAssignStudents) throws ClazzNotFoundException, StudentNotFoundException, ConflictedScheduleException, MaximumCapacityReachedException {
        Clazz clazz = classRepository.findById(clazzDTOAssignStudents.getClassId())
                .orElseThrow(() -> new ClazzNotFoundException("Class not found with ID: " + clazzDTOAssignStudents.getClassId()));
        if (clazz.getCurrentCapacity() < clazz.getMaximumCapacity()) {
            Set<Long> assigningStudentIds = clazzDTOAssignStudents.getStudentIds();
            for (Long studentId : assigningStudentIds) {
                Student student = studentRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));
                Set<Schedule> studentSchedules = scheduleRepository.findAllByStudentId(student.getId());
                for (Schedule schedule : studentSchedules) {
                    if (clazz.getSchedules().stream().anyMatch(schedule1 -> schedule1.isScheduleConflicted(schedule)))
                        throw new ConflictedScheduleException("Student " + student.getFullName() + " has conflicted schedule " + ScheduleInfoDTO.toDTO(schedule));
                }
                if (!clazz.getStudents().contains(student)) {
                    clazz.getStudents().add(student);
                    student.getClazzes().add(clazz);
                }
            }
            clazz.setCurrentCapacity(clazz.getStudents().size());
            classRepository.save(clazz);
        } else {
            throw new MaximumCapacityReachedException("Maximum capacity reached!");
        }
    }

    @Override
    @Transactional(rollbackFor = {ClazzNotFoundException.class, StudentNotFoundException.class})
    public void removeStudents(ClazzDTOAssignStudents clazzDTOAssignStudents) throws ClazzNotFoundException, StudentNotFoundException {
        Clazz clazz = classRepository.findById(clazzDTOAssignStudents.getClassId())
                .orElseThrow(() -> new ClazzNotFoundException("Class not found with ID: " + clazzDTOAssignStudents.getClassId()));
        // if deleting student isn't in the existing students list then throw exceptions
        Set<Long> studentIds = clazz.getStudents().stream().map(Student::getId).collect(Collectors.toSet());
        for (Long studentId : clazzDTOAssignStudents.getStudentIds()) {
            if (!studentIds.contains(studentId)) {
                throw new StudentNotFoundException("Student not found with ID: " + studentId);
            } else {
                Student student = studentRepository.findById(studentId)
                        .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));
                clazz.getStudents().remove(student);
                student.getClazzes().remove(clazz);
            }
        }
        clazz.setCurrentCapacity(clazz.getStudents().size());
        classRepository.save(clazz);
    }

    /**
     * Assigns teacher to a class
     *
     * @param clazzDTOAssignTeachers
     * @throws ClazzNotFoundException   if class not found
     * @throws TeacherNotFoundException if teacher not found
     */
    @Override
    @Transactional(rollbackFor = {ClazzNotFoundException.class, TeacherNotFoundException.class, ConflictedScheduleException.class})
    public void assignTeacher(ClazzDTOAssignTeachers clazzDTOAssignTeachers) throws ClazzNotFoundException, TeacherNotFoundException, ConflictedScheduleException {
        Clazz clazz = classRepository.findById(clazzDTOAssignTeachers.getClassId())
                .orElseThrow(() -> new ClazzNotFoundException("Class not found with ID: " + clazzDTOAssignTeachers.getClassId()));
        Teacher teacher = teacherRepository.findById(clazzDTOAssignTeachers.getTeacherId()).orElseThrow(() -> new TeacherNotFoundException("Teacher not found with ID: " + clazzDTOAssignTeachers.getTeacherId()));
        Set<Schedule> teacherSchedules = scheduleRepository.findAllByClazz_Teacher_Id(teacher.getId());
        for (Schedule schedule : teacherSchedules) {
            if (clazz.getSchedules().stream().anyMatch(schedule1 -> schedule1.isScheduleConflicted(schedule)))
                throw new ConflictedScheduleException("Schedule conflicted " + ScheduleInfoDTO.toDTO(schedule));
        }
        clazz.setTeacher(teacher);
        teacher.getClazzes().add(clazz);
        classRepository.save(clazz);
    }

    @Override
    @Transactional(rollbackFor = {ClazzNotFoundException.class, TeacherNotFoundException.class})
    public void removeTeacher(ClazzDTOAssignTeachers clazzDTOAssignTeachers) throws ClazzNotFoundException, TeacherNotFoundException {
        Clazz clazz = classRepository.findById(clazzDTOAssignTeachers.getClassId())
                .orElseThrow(() -> new ClazzNotFoundException("Class not found with ID: " + clazzDTOAssignTeachers.getClassId()));
        Teacher teacher = teacherRepository.findById(clazzDTOAssignTeachers.getTeacherId()).orElseThrow(() -> new TeacherNotFoundException("Teacher not found with ID: " + clazzDTOAssignTeachers.getTeacherId()));
        teacher.getClazzes().remove(clazz);
        clazz.setTeacher(null);
        classRepository.save(clazz);
    }


}

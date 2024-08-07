package com.mpteam1.repository;

import com.mpteam1.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

import java.time.LocalDate;
import java.util.*;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Set<Schedule> findAllByClazz_Teacher_Id(Long id);
    @Query("SELECT s "
            +" FROM Schedule s LEFT JOIN s.clazz c "
    + " LEFT JOIN c.students cs WHERE cs.id = ?1")
    Set<Schedule> findAllByStudentId(Long id);
    @Query("SELECT s FROM Schedule s " +
            "JOIN s.clazz c " +
            "JOIN c.teacher t " +
            "WHERE t.id = :teacherId " +
            "AND c.startDate <= :endDate " +
            "AND :startDate <= c.endDate ")
    List<Schedule> findTeacherSchedules(
            @Param("teacherId") Long teacherId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT s FROM Schedule s " +
            "JOIN s.clazz c " +
            "JOIN c.students st " +
            "WHERE st.id = :studentId " +
            "AND c.startDate <= :endDate " +
            "AND :startDate <= c.endDate ")
    List<Schedule> findStudentSchedules(
            @Param("studentId") Long studentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT s FROM Schedule s " +
            "JOIN s.clazz c " +
            "WHERE c.startDate <= :endDate " +
            "AND :startDate <= c.endDate ")
    List<Schedule> findSchedules(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}

package com.mpteam1.repository;

import com.mpteam1.entities.Clazz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface ClassRepository extends JpaRepository<Clazz, Long> {

    public Optional<Clazz> findByName(String name);

    public Optional<Clazz> findByNameIgnoreCase(String name);

    public Optional<Clazz> findByClazzCodeIgnoreCase(String clazzCode);

    @Query("SELECT t FROM Clazz t WHERE " +
            "t.name LIKE %:keyword% "
    )
    Page<Clazz> findAllByKeyword(String keyword, Pageable pageable);

    @Query("SELECT t FROM Clazz t " +
            "JOIN t.teacher s WHERE " +
            "s.email LIKE :email AND " +
            "t.name LIKE %:keyword% "
    )
    Page<Clazz> findAllByTeacher_Email(String email, String keyword, Pageable pageable);

    @Query("SELECT t FROM Clazz t " +
            "JOIN t.students s WHERE " +
            "s.email LIKE :email AND " +
            "t.name LIKE %:keyword% "
    )
    Page<Clazz> findAllByStudent_Email(String email, String keyword, Pageable pageable);

    List<Clazz> findAllByTeacher_Id(Long teacherId);

    Set<Long> findStudentIdsById(Long classId);
}
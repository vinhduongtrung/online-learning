package com.mpteam1.repository;

import com.mpteam1.entities.TakenQuiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface TakenQuizRepository extends JpaRepository<TakenQuiz, Long> {
    Optional<TakenQuiz> findByStudentIdAndQuizId(Long studentId, long quizId);

    @Query("SELECT tq FROM TakenQuiz tq " +
            "LEFT JOIN tq.quiz q " +
            "LEFT JOIN tq.student s " +
            "WHERE q.id = :quizId " +
            "AND (s.fullName LIKE %:keyword%)")
    Page<TakenQuiz> getAllByQuizId(Long quizId, Pageable pageable, String keyword);

    List<TakenQuiz> findByQuiz_Id(Long quizId);

    int countByQuiz_Id(long id);
}

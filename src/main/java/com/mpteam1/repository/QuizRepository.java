package com.mpteam1.repository;

import com.mpteam1.entities.Quiz;
import com.mpteam1.entities.enums.EQuizStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @Query("SELECT q FROM Quiz q " +
            "JOIN q.clazz c WHERE " +
            "c.id = :id AND " +
            "(q.title LIKE %:keyword% OR q.description LIKE %:keyword%)"
    )
    Page<Quiz> findByClazz_Id(Long id, String keyword, Pageable pageable);

    Long countByClazz_Id(Long id);

    Long countByStatusAndClazz_Id(EQuizStatus status, Long clazzId);
    Optional<Quiz> findByClazzIdAndId(Long clazzId, Long quizId);
}
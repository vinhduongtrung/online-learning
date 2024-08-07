package com.mpteam1.repository;

import com.mpteam1.entities.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findAllByQuiz_Id(Long quizId, Pageable pageable);

    @Query("SELECT q FROM Question q " +
            "JOIN q.quiz z " +
            "WHERE z.id = :quizId AND " +
            "(q.content LIKE %:keyword%)"
    )
    Page<Question> findByQuizIdAndKeyword(Long quizId, String keyword, Pageable pageable);

    Long countByQuiz_Id(Long quizId);

    boolean existsByContent(String content);
}
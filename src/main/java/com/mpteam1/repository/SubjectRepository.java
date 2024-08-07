package com.mpteam1.repository;

import com.mpteam1.entities.Subjects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subjects, Long> {
    Optional<Subjects> findByName(String name);
}

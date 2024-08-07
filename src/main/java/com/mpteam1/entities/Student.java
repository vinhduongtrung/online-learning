package com.mpteam1.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Student extends User {

    private String studentCode;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "students_classes",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "classes_id"))
    private Set<Clazz> clazzes = new HashSet<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private Set<TakenQuiz> takenQuizzes = new HashSet<>();

    public void remove() {
        this.clazzes.clear();
        this.takenQuizzes.forEach(submission -> {
            submission.setStudent(null);
        });
    }
}

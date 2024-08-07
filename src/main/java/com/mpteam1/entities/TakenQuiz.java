package com.mpteam1.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TakenQuiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    private Instant startTime = Instant.now();

    private Instant endTime;

    private Double score;

    @Column(name = "num_of_tab_leaves", columnDefinition = "INT DEFAULT 0")
    private Integer numOfTabLeaves;

    @OneToMany(mappedBy = "takenQuiz")
    private List<TakenAnswer> takenAnswers;
}

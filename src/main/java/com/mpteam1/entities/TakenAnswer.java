package com.mpteam1.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TakenAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "taken_quiz_id")
    private TakenQuiz takenQuiz;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "TakenAnswer_answers",
            joinColumns = @JoinColumn(name = "takenAnswer_id"),
            inverseJoinColumns = @JoinColumn(name = "answers_id"))
    private List<Answer> answers = new ArrayList<>();

}

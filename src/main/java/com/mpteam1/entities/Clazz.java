package com.mpteam1.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "class")
public class Clazz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String clazzCode;

    private LocalDate startDate;

    private LocalDate endDate;

    @CreationTimestamp
    private Instant createdDate;

    private String description;
    private Integer maximumCapacity;
    private Integer currentCapacity;

    @ManyToMany(mappedBy = "clazzes", cascade = {CascadeType.PERSIST})
    private Set<Student> students = new HashSet<>();

    @OneToMany(mappedBy = "clazz", cascade = CascadeType.ALL)
    private Set<Quiz> quizzes = new HashSet<>();

    @OneToMany(mappedBy = "clazz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

}

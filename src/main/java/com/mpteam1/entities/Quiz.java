package com.mpteam1.entities;

import com.mpteam1.entities.enums.EQuizStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Range;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quiz")
@Builder
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "clazz_id")
    private Clazz clazz;

    private Integer duration;

    private Instant openTime;

    private Instant closedTime;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EQuizStatus status = EQuizStatus.PRIVATE;

    @CreationTimestamp
    private Instant createdDate;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User createdBy;
}

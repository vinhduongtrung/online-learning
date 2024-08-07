package com.mpteam1.entities;

import com.mpteam1.entities.enums.EEmploymentStatus;
import com.mpteam1.entities.enums.ERole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends User {
    private String qualification;
    private String subjectTaught;
    @Enumerated(EnumType.STRING)
    private EEmploymentStatus eEmploymentStatus;
    private LocalDate startDate;


    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "teacher_subject",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<Subjects> subjects = new HashSet<>();

    @OneToMany(mappedBy = "teacher", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Clazz> clazzes = new HashSet<>();

    public Teacher(String password, String qualification, EEmploymentStatus eEmploymentStatus,
                   LocalDate startDate, String email, String fullName, String address, LocalDate dateOfBirth,
                   String phoneNumber, boolean gender, ERole eRole, String accountName) {
        super(email, password, fullName, address, dateOfBirth, phoneNumber, gender, eRole, accountName);
        this.qualification = qualification;
        this.eEmploymentStatus = eEmploymentStatus;
        this.startDate = startDate;
    }

    public void addSubject(Subjects subject) {
        this.subjects.add(subject);
        subject.getTeachers().add(this);
    }
    public void remove(){
        for(Subjects subject : subjects){
            subject.getTeachers().remove(this);
        }
        this.subjects.clear();
        for(Clazz clazz : clazzes){
            clazz.setTeacher(null);
        }
        this.clazzes.clear();
    }
}

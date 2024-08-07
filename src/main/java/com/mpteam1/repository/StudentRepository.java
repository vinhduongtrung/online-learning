package com.mpteam1.repository;

import com.mpteam1.entities.Student;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);

    @Query(value =
            "SELECT * FROM ( " +
                    "SELECT " +
                    "        s1_0.id as id, " +
                    "        s1_1.full_name as fullName, " +
                    "        s1_1.email as email, " +
                    "        s1_1.date_of_birth as birthday, " +
                    "        s1_1.address as address, " +
                    "        s1_1.gender as gender, " +
                    "        s1_1.phone_number as phoneNumber, " +
                    "        s1_1.avatar_name as avatarName, " +
                    "        c1_0.classes_id as clazzId, " +
                    "        c1_1.name as clazzName, " +
                    "        c1_1.created_date as createdDate, " +
                    "        c1_1.start_date as startDate, " +
                    "        c1_1.end_date as endDate, " +
                    "        c1_1.teacher_id as teacherId, " +
                    "        t1_2.full_name as teacherName, " +
                    "        s1_1.account_name as accountName, " +
                    "        DENSE_RANK() OVER (ORDER BY s1_0.id DESC) AS rank_id " +
                    "    FROM " +
                    "        student s1_0 " +
                    "    JOIN " +
                    "        user s1_1 " +
                    "            ON s1_0.id=s1_1.id " +
                    "    LEFT JOIN " +
                    "        students_classes c1_0 " +
                    "            ON s1_0.id=c1_0.student_id " +
                    "    LEFT JOIN " +
                    "        class c1_1 " +
                    "            ON c1_1.id=c1_0.classes_id " +
                    "    LEFT JOIN " +
                    "        (teacher t1_1 " +
                    "    JOIN " +
                    "        user t1_2 " +
                    "            ON t1_1.id=t1_2.id" +
                    "         ) " +
                    "        ON t1_1.id=c1_1.teacher_id " +
                    "WHERE s1_1.full_name like CONCAT('%', ?3, '%') " +
                    "OR s1_1.email like CONCAT('%', ?3, '%') " +
                    "OR c1_1.name like CONCAT('%', ?3, '%') " +
                    ") AS data " +
                    "WHERE rank_id > (?1 * ?2) " +
                    "AND rank_id <= (?1 * ?2) + ?2 " +
                    "ORDER BY data.id DESC", nativeQuery = true)
    List<Tuple> findAllWithFilter(int pageNum, int pageSize, String keyword);

    @Query("SELECT COUNT(DISTINCT s.id) " +
            "FROM Student s " +
            "LEFT JOIN s.clazzes c " +
            "LEFT JOIN c.teacher t " +
            "WHERE s.fullName LIKE CONCAT('%', :keyword, '%') " +
            "OR s.email LIKE CONCAT('%', :keyword, '%') " +
            "OR c.name LIKE CONCAT('%', :keyword, '%')")
    long countFindAllWithFilter(String keyword);

    @Query(value =
            "    SELECT " +
                    "        s1_0.id as id, " +
                    "        s1_1.full_name as fullName, " +
                    "        s1_1.email as email, " +
                    "        s1_1.date_of_birth as birthday, " +
                    "        s1_1.address as address, " +
                    "        s1_1.gender as gender, " +
                    "        s1_1.phone_number as phoneNumber, " +
                    "        s1_1.avatar_name as avatarName, " +
                    "        s1_1.account_name as accountName, " +
                    "        c1_0.classes_id as clazzId, " +
                    "        c1_1.name as clazzName, " +
                    "        c1_1.created_date as createdDate, " +
                    "        c1_1.start_date as startDate, " +
                    "        c1_1.end_date as endDate, " +
                    "        c1_1.teacher_id as teacherId, " +
                    "        t1_2.full_name as teacherName " +
                    "    FROM " +
                    "        student s1_0 " +
                    "    JOIN " +
                    "        user s1_1 " +
                    "            ON s1_0.id=s1_1.id " +
                    "    LEFT JOIN " +
                    "        students_classes c1_0 " +
                    "            ON s1_0.id=c1_0.student_id " +
                    "    LEFT JOIN " +
                    "        class c1_1 " +
                    "            ON c1_1.id=c1_0.classes_id " +
                    "    LEFT JOIN " +
                    "        (teacher t1_1 " +
                    "    JOIN " +
                    "        user t1_2 " +
                    "            ON t1_1.id=t1_2.id) " +
                    "        ON t1_1.id=c1_1.teacher_id " +
                    "WHERE s1_0.id = ?1", nativeQuery = true)
    List<Tuple> findStudentById(Long studentId);

    @Query(value =
            "SELECT * FROM ( " +
                    "SELECT " +
                    "        s1_0.id as id, " +
                    "        s1_1.full_name as fullName, " +
                    "        s1_1.email as email, " +
                    "        s1_1.date_of_birth as birthday, " +
                    "        s1_1.address as address, " +
                    "        s1_1.gender as gender, " +
                    "        s1_1.phone_number as phoneNumber, " +
                    "        s1_1.avatar_name as avatarName, " +
                    "        s1_1.account_name as accountName, " +
                    "        c1_0.classes_id as clazzId, " +
                    "        c1_1.name as clazzName, " +
                    "        c1_1.created_date as createdDate, " +
                    "        c1_1.start_date as startDate, " +
                    "        c1_1.end_date as endDate, " +
                    "        c1_1.teacher_id as teacherId, " +
                    "        t1_2.full_name as teacherName, " +
                    "        DENSE_RANK() OVER (ORDER BY s1_0.id) AS rank_id " +
                    "    FROM " +
                    "        student s1_0 " +
                    "    JOIN " +
                    "        user s1_1 " +
                    "            ON s1_0.id=s1_1.id " +
                    "    LEFT JOIN " +
                    "        students_classes c1_0 " +
                    "            ON s1_0.id=c1_0.student_id " +
                    "    LEFT JOIN " +
                    "        class c1_1 " +
                    "            ON c1_1.id=c1_0.classes_id " +
                    "    LEFT JOIN " +
                    "        (teacher t1_1 " +
                    "    JOIN " +
                    "        user t1_2 " +
                    "            ON t1_1.id=t1_2.id) " +
                    "        ON t1_1.id=c1_1.teacher_id " + 
                    "WHERE (c1_0.classes_id is null OR c1_0.classes_id <> ?3 " +
                    "AND s1_0.id NOT IN (SELECT sc.student_id FROM students_classes sc WHERE sc.classes_id = ?3)) " +
                    "AND (s1_1.full_name like CONCAT('%', ?4, '%') " +
                    "OR s1_1.email like CONCAT('%', ?4, '%') " +
                    "OR c1_1.name like CONCAT('%', ?4, '%')) " +
                    ") AS data " +
                    "WHERE rank_id > (?1 * ?2) " +
                    "AND rank_id <= (?1 * ?2) + ?2", nativeQuery = true)
    List<Tuple> findAllStudentOutSideClazz(int pageNum, int pageSize, Long clazzId, String keyword);


    @Query(value = "SELECT COUNT(DISTINCT s1_0.id)" +
            "    FROM " +
            "        student s1_0 " +
            "    JOIN " +
            "        user s1_1 " +
            "            ON s1_0.id=s1_1.id " +
            "    LEFT JOIN " +
            "        students_classes c1_0 " +
            "            ON s1_0.id=c1_0.student_id " +
            "    LEFT JOIN " +
            "        class c1_1 " +
            "            ON c1_1.id=c1_0.classes_id " +
            "    LEFT JOIN " +
            "        (teacher t1_1 " +
            "    JOIN " +
            "        user t1_2 " +
            "            ON t1_1.id=t1_2.id) " +
            "        ON t1_1.id=c1_1.teacher_id " +
            "WHERE (c1_0.classes_id is null OR c1_0.classes_id <> ?1 " +
            "AND s1_0.id NOT IN (SELECT sc.student_id FROM students_classes sc WHERE sc.classes_id = ?1)) " +
            "AND (s1_1.full_name like CONCAT('%', ?2, '%') " +
            "OR s1_1.email like CONCAT('%', ?2, '%') " +
            "OR c1_1.name like CONCAT('%', ?2, '%'))", nativeQuery = true)
    long countAllStudentOutSideClazz(Long clazzId, String keyword);


    @Query(value =
            "SELECT * FROM ( " +
                    " SELECT " +
                    "        s1_0.id as id, " +
                    "        s1_1.full_name as fullName, " +
                    "        s1_1.email as email, " +
                    "        s1_1.date_of_birth as birthday, " +
                    "        s1_1.address as address, " +
                    "        s1_1.gender as gender, " +
                    "        s1_1.phone_number as phoneNumber, " +
                    "        s1_1.avatar_name as avatarName, " +
                    "        s1_1.account_name as accountName, " +
                    "        c1_0.classes_id as clazzId, " +
                    "        c1_1.name as clazzName, " +
                    "        c1_1.created_date as createdDate, " +
                    "        c1_1.start_date as startDate, " +
                    "        c1_1.end_date as endDate, " +
                    "        c1_1.teacher_id as teacherId, " +
                    "        t1_2.full_name as teacherName, " +
                    "        DENSE_RANK() OVER (ORDER BY s1_0.id) AS rank_id " +
                    "    FROM " +
                    "        student s1_0 " +
                    "    JOIN " +
                    "        user s1_1 " +
                    "            ON s1_0.id=s1_1.id " +
                    "    LEFT JOIN " +
                    "        students_classes c1_0 " +
                    "            ON s1_0.id=c1_0.student_id " +
                    "    LEFT JOIN " +
                    "        class c1_1 " +
                    "            ON c1_1.id=c1_0.classes_id " +
                    "    LEFT JOIN " +
                    "        (teacher t1_1 " +
                    "    JOIN " +
                    "        user t1_2 " +
                    "            ON t1_1.id=t1_2.id) " +
                    "        ON t1_1.id=c1_1.teacher_id " +
                    "        WHERE c1_0.classes_id = ?3 " +
                    "AND (s1_1.full_name like CONCAT('%', ?4, '%') " +
                    "OR s1_1.email like CONCAT('%', ?4, '%') " +
                    "OR c1_1.name like CONCAT('%', ?4, '%')) " +
                    ") AS data " +
                    "WHERE rank_id > (?1 * ?2) " +
                    "AND rank_id <= (?1 * ?2) + ?2", nativeQuery = true)
    List<Tuple> findAllStudentInSideClazz(int pageNum, int pageSize, Long clazzId, String keyword);

    @Query("SELECT COUNT(DISTINCT s.id) " +
            "FROM Student s " +
            "LEFT JOIN s.clazzes c " +
            "LEFT JOIN c.teacher t " +
            "WHERE c.id = ?1 " +
            "AND (s.fullName like CONCAT('%', ?2, '%') " +
            "OR s.email like CONCAT('%', ?2, '%') " +
            "OR c.name like CONCAT('%', ?2, '%'))")
    long countFindAllStudentInSideClazz(Long clazzId, String keyword);

    @Query(value =
            "SELECT * FROM ( " +
                    "SELECT " +
                    "        s1_0.id as id, " +
                    "        s1_1.full_name as fullName, " +
                    "        s1_1.email as email, " +
                    "        s1_1.date_of_birth as birthday, " +
                    "        s1_1.address as address, " +
                    "        s1_1.gender as gender, " +
                    "        s1_1.phone_number as phoneNumber, " +
                    "        s1_1.avatar_name as avatarName, " +
                    "        s1_1.account_name as accountName, " +
                    "        c1_0.classes_id as clazzId, " +
                    "        c1_1.name as clazzName, " +
                    "        c1_1.created_date as createdDate, " +
                    "        c1_1.start_date as startDate, " +
                    "        c1_1.end_date as endDate, " +
                    "        c1_1.teacher_id as teacherId, " +
                    "        t1_2.full_name as teacherName, " +
                    "        DENSE_RANK() OVER (ORDER BY s1_0.id) AS rank_id " +
                    "    FROM " +
                    "        student s1_0 " +
                    "    JOIN " +
                    "        user s1_1 " +
                    "            ON s1_0.id=s1_1.id " +
                    "    LEFT JOIN " +
                    "        students_classes c1_0 " +
                    "            ON s1_0.id=c1_0.student_id " +
                    "    LEFT JOIN " +
                    "        class c1_1 " +
                    "            ON c1_1.id=c1_0.classes_id " +
                    "    LEFT JOIN " +
                    "        (teacher t1_1 " +
                    "    JOIN " +
                    "        user t1_2 " +
                    "            ON t1_1.id=t1_2.id) " +
                    "        ON t1_1.id=c1_1.teacher_id " +
                    "        WHERE c1_1.teacher_id = ?3 " +
                    "AND (s1_1.full_name like CONCAT('%', ?4, '%') " +
                    "OR s1_1.email like CONCAT('%', ?4, '%') " +
                    "OR c1_1.name like CONCAT('%', ?4, '%'))"+
                    ") AS data " +
                    "WHERE rank_id > (?1 * ?2) " +
                    "AND rank_id <= (?1 * ?2) + ?2", nativeQuery = true)
    List<Tuple> findAllStudentByTeacher(int pageNum, int pageSize, Long teacherId, String keyword);

    @Query("SELECT COUNT(DISTINCT s.id) " +
            "FROM Student s " +
            "LEFT JOIN s.clazzes c " +
            "LEFT JOIN c.teacher t " +
            "WHERE t.id = ?1 " +
            "AND (s.fullName like CONCAT('%', ?2, '%') " +
            "OR s.email like CONCAT('%', ?2, '%') " +
            "OR c.name like CONCAT('%', ?2, '%'))")
    long countFindAllStudentByTeacher(Long teacherId, String keyword);



    @Query("SELECT COUNT(DISTINCT s.id) FROM Student s " +
            "LEFT JOIN s.clazzes c " +
            "WHERE c.id is null")
    long countInactiveStudent();

}

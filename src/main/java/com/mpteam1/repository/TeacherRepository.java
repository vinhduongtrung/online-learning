package com.mpteam1.repository;

import com.mpteam1.entities.Teacher;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    @Query("SELECT t FROM Teacher t WHERE " +
            "t.email LIKE %:keyword% OR " +
            "t.fullName LIKE %:keyword% OR " +
            "t.subjectTaught LIKE %:keyword%"
    )
    Page<Teacher> findAllByKeyword(String keyword, Pageable pageable);
    Teacher findByEmail(String email);
    long countByClazzesNotEmpty();

    @Query(value = "SELECT * FROM (" +
            "SELECT " +
            "        t1_0.id as teacherId," +
            "        t1_0.qualification as qualification," +
            "        t1_0.e_employment_status as employmentStatus, " +
            "        t1_0.start_date as startDate, " +
            "        t1_1.email as email, " +
            "        t1_1.full_name as fullName, " +
            "        t1_1.address as address, " +
            "        t1_1.date_of_birth as dateOfBirth, " +
            "        t1_1.phone_number as phoneNumber, " +
            "        t1_1.gender as gender, " +
            "        t1_1.e_role as role, " +
            "        t1_1.avatar_name as avatarUrl, " +
            "        DENSE_RANK() OVER (ORDER BY t1_0.id) AS rank_id " +
            "    from " +
            "        teacher t1_0 " +
            "    join " +
            "        user t1_1 " +
            "            on t1_0.id=t1_1.id " +
            "    left join " +
            "        class c1_0 " +
            "            on t1_0.id=c1_0.teacher_id " +
            "WHERE c1_0.id = ?3" +
            ") AS data " +
            "WHERE rank_id > (?1 * ?2) " +
            "AND rank_id <= (?1 * ?2) + ?2", nativeQuery = true)
    List<Tuple> fetchTeacherInsideClazz(int pageNum, int pageSize, Long clazzId);


    @Query(value =
            "SELECT COUNT(DISTINCT t.id) " +
            "FROM Teacher t " +
            "LEFT JOIN t.clazzes c " +
            "WHERE c.id = ?1")
    long countTeacherInsideClazz(Long clazzId);



    @Query(value = "SELECT * FROM (" +
            "SELECT " +
            "        t1_0.id as teacherId," +
            "        t1_0.qualification as qualification," +
            "        t1_0.e_employment_status as employmentStatus, " +
            "        t1_0.start_date as startDate, " +
            "        t1_1.email as email, " +
            "        t1_1.full_name as fullName, " +
            "        t1_1.address as address, " +
            "        t1_1.date_of_birth as dateOfBirth, " +
            "        t1_1.phone_number as phoneNumber, " +
            "        t1_1.gender as gender, " +
            "        t1_1.e_role as role, " +
            "        t1_1.avatar_name as avatarUrl, " +
            "        DENSE_RANK() OVER (ORDER BY t1_0.id) AS rank_id " +
            "    from " +
            "        teacher t1_0 " +
            "    join " +
            "        user t1_1 " +
            "            on t1_0.id=t1_1.id " +
            "    left join " +
            "        class c1_0 " +
            "            on t1_0.id=c1_0.teacher_id " +
            "WHERE c1_0.id <> ?3 " +
            ") AS data " +
            "WHERE rank_id > (?1 * ?2) " +
            "AND rank_id <= (?1 * ?2) + ?2", nativeQuery = true)
    List<Tuple> fetchAllAvailableTeacher(int pageNum, int pageSize, Long clazzId);

    @Query(value =
            "SELECT COUNT(DISTINCT t.id) " +
                    "FROM Teacher t " +
                    "LEFT JOIN t.clazzes c " +
                    "WHERE c.id <> 1")
    long countTeacherOutSideClazz(Long clazzId);

}

package com.mpteam1.dto.response.student;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentStatus {

    long inActiveCount;

    long activeCount;

    long totalCount;
}

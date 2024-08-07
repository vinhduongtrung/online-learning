package com.mpteam1.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class APIErrorResponse<T> {

    int statusCode;

    int count;

    T messages;
}
package com.mpteam1.dto.response.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

/**
 * @author : HCM23_FRF_FJB_04_TriNM
 * @since : 4/9/2024, Tue
 **/


@Getter
@Setter
public class PageForView<T>{
    private List<T> content;
    private long page;
    private int size;
    private long totalElement;

    public PageForView() {
        this.content = Collections.emptyList();
    }
}

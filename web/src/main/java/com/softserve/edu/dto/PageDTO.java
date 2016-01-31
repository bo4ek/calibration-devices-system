package com.softserve.edu.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PageDTO<T> {

    private Long totalItems;
    private List<T> content;

    public PageDTO() {}

    public PageDTO(Long totalItems, List<T> content) {
        this.totalItems = totalItems;
        this.content = content;
    }

    public PageDTO(List<T> content) {
        this.content = content;
    }
}
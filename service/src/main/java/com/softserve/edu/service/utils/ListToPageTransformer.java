package com.softserve.edu.service.utils;

import java.util.List;

public class ListToPageTransformer<T> {

    private Long totalItems;
    private List<T> content;

    public Long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Long count) {
        this.totalItems = count;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
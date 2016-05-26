package com.softserve.edu.service.utils.filter;

import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.util.SavedFilter;

import java.util.List;
import java.util.Map;

public interface SavedFilterService {
    void addFilter(User user,String locationUrl,String filter,String name);
    void saveFilter(SavedFilter filter);
    void updateFilter(User user,String locationUrl,String filter,String name);
    List<Map<String,String >> getSavedFilters(User user, String locationUrl);
    void deleteFilter(User user,String locationUrl,String name);
    void deleteAllFilters(User user,String locationUrl);
}

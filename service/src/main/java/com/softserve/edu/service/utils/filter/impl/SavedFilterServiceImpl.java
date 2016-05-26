package com.softserve.edu.service.utils.filter.impl;

import com.softserve.edu.entity.user.User;
import com.softserve.edu.entity.util.SavedFilter;
import com.softserve.edu.repository.SavedFilterRepository;
import com.softserve.edu.service.utils.filter.Filter;
import com.softserve.edu.service.utils.filter.SavedFilterService;
import com.softserve.edu.service.utils.filter.internal.Comparison;
import com.softserve.edu.service.utils.filter.internal.Condition;
import com.softserve.edu.service.utils.filter.internal.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SavedFilterServiceImpl implements SavedFilterService {
    @Autowired
    SavedFilterRepository filterRepository;

    @Override
    public void addFilter(User user, String locationUrl, String filter, String name) {
        SavedFilter savedFilter = new SavedFilter(user, locationUrl, filter, name);
        filterRepository.save(savedFilter);
    }

    @Override
    public void saveFilter(SavedFilter filter) {
        filterRepository.save(filter);
    }

    @Override
    public void updateFilter(User user, String locationUrl, String filter, String name) {
        SavedFilter oldFilter = filterRepository.findByUserAndLocationUrlAndName(user, locationUrl, name);
        oldFilter.setFilter(filter);
        filterRepository.save(oldFilter);
    }

    @Override
    public void deleteFilter(User user, String locationUrl, String name) {
        SavedFilter savedFilter = filterRepository.findByUserAndLocationUrlAndName(user, locationUrl, name);
        //SavedFilter savedFilter = new SavedFilter(user, locationUrl,name);
        filterRepository.delete(savedFilter.getSavedFilterId());
    }

    @Override
    public void deleteAllFilters(User user, String locationUrl) {
        List<Condition> conditions = new ArrayList<>();
        conditions.add(new Condition.Builder()
                .setType(Type.string)
                .setComparison(Comparison.eq)
                .setField("locationUrl")
                .setValue(locationUrl)
                .build());
        conditions.add(new Condition.Builder()
                .setComparison(Comparison.eq)
                .setType(Type.user)
                .setField("User")
                .setValue(user)
                .build());
        Filter filter = new Filter();
        filter.addConditionList(conditions);
        filterRepository.delete(filterRepository.findAll(filter));
    }

    @Override
    public List<Map<String, String>> getSavedFilters(User user, String locationUrl) {
        List<Map<String, String>> result = new ArrayList<>();
        List<SavedFilter> savedFilters = filterRepository.findByUserAndLocationUrl(user, locationUrl);
        for (SavedFilter savedFilter : savedFilters) {
            Map<String, String> savedFilterMap = new HashMap<>();
            savedFilterMap.put("name", savedFilter.getName());
            savedFilterMap.put("filter", savedFilter.getFilter());
            result.add(savedFilterMap);
        }
        return result;
    }
}

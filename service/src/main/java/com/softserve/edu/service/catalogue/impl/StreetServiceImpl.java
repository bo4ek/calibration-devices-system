package com.softserve.edu.service.catalogue.impl;

import com.softserve.edu.entity.catalogue.Street;
import com.softserve.edu.repository.catalogue.StreetRepository;
import com.softserve.edu.service.catalogue.StreetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StreetServiceImpl implements StreetService {

    @Autowired
    private StreetRepository streetRepository;

    @Override
    public List<Street> getStreetsCorrespondingLocality(Long localityId) {
        return streetRepository.findByLocalityId(localityId);
    }

    @Override
    public Street findStreetById(Long id) {
        return streetRepository.findOne(id);
    }
}

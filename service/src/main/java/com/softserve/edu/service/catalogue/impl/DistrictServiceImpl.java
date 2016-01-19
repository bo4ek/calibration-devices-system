package com.softserve.edu.service.catalogue.impl;

import com.softserve.edu.entity.catalogue.District;
import com.softserve.edu.repository.catalogue.DistrictRepository;
import com.softserve.edu.service.catalogue.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DistrictServiceImpl implements DistrictService {

    @Autowired
    private DistrictRepository districtRepository;

    @Override
    public List<District> getDistrictsCorrespondingRegion(Long regionId) {
        return districtRepository.findByRegionId(regionId);
    }

    @Override
    public District findDistrictByDesignationAndRegion(String designation, Long region){
        return districtRepository.findByDesignationAndRegionId(designation, region);
    }

    @Override
    public District findDistrictById(Long id) {
        return districtRepository.findOne(id);
    }
}

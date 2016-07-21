package com.softserve.edu.service.catalogue.impl;

import com.softserve.edu.entity.catalogue.Locality;
import com.softserve.edu.repository.catalogue.LocalityRepository;
import com.softserve.edu.service.catalogue.LocalityService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocalityServiceImpl implements LocalityService {
    Logger logger = Logger.getLogger(LocalityServiceImpl.class);

    @Autowired
    private LocalityRepository localityRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Locality> getLocalitiesCorrespondingDistrict(Long districtId) {
        return localityRepository.findDistinctByDistrictId(districtId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getMailIndexForLocality(String designation, Long districtId) {
        return localityRepository.findMailIndexByDesignationAndDistrictId(designation, districtId);
    }

    @Override
    @Transactional(readOnly = true)
    public Locality findById(Long id) {
        return localityRepository.findOne(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Locality> findByDistrictIdAndOrganizationId(Long districtId, Long organizationId) {
        return localityRepository.findByDistrictIdAndOrganizationId(districtId, organizationId);
    }

    /**
     * Finds all localities by organization id
     *
     * @param organizationId id of organizaton
     * @return list of localities
     */
    @Override
    @Transactional(readOnly = true)
    public List<Locality> findLocalitiesByOrganizationId(Long organizationId) {
        return localityRepository.findLocalitiesByOrganizationId(organizationId);
    }

    /**
     * Finds all Localities by iDs, that are serviced by the organization
     *
     * @param localityIds List of Localities' ids
     * @return
     */

    @Override
    @Transactional(readOnly = true)
    public List<Locality> findByLocalityIdIn(List<Long> localityIds) {
        return localityRepository.findByIdIn(localityIds);
    }

    @Override
    public List<Locality> findByDesignationAndDistrictId(String designation, Long id) {
        return localityRepository.findByDesignationAndDistrictId(designation, id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existByIdAndDistrictId(Long id, Long districtId) {
        return localityRepository.existByIdAndDistrictId(id, districtId);
    }
}

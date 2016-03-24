package com.softserve.edu.service.catalogue;

import com.softserve.edu.entity.catalogue.Locality;

import java.util.List;

public interface LocalityService {

    List<Locality> getLocalitiesCorrespondingDistrict(Long districtId);

    List<String> getMailIndexForLocality(String designation, Long districtId);

    Locality findById(Long id);

    List<Locality> findByDistrictIdAndOrganizationId(Long districtId, Long organizationId);

    /**
     * Finds all localities by organization id
     *
     * @param organizationId id of organizaton
     * @return list of localities
     */
    List<Locality> findLocalitiesByOrganizationId(Long organizationId);

    List<Locality> findByLocalityIdIn(List<Long> organizationIds);

    boolean existByIdAndDistrictId(Long id, Long districtId);
}
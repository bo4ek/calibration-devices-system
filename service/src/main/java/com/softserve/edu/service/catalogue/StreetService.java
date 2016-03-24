package com.softserve.edu.service.catalogue;

import com.softserve.edu.entity.catalogue.Street;
import com.softserve.edu.service.utils.ListToPageTransformer;

import java.util.List;

public interface StreetService {

    List<Street> getStreetsCorrespondingLocality(Long localityId);

    Street findByDesignation(String designation);

    Street findByLocalityIdAndDesignation(Long localityId, String designation);

    Street findStreetById(Long id);

    Iterable<Street> findAll();

    Street findByIdAndLocalityId(Long id, Long localityId);

    ListToPageTransformer<Street> findPageOfAllStreets(int pageNumber, int itemsPerPage, String city,
                                                         String district, String region, String streetName,
                                                         String sortCriteria, String sortOrder);

}

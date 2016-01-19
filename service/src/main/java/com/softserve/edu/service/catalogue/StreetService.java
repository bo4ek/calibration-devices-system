package com.softserve.edu.service.catalogue;

import com.softserve.edu.entity.catalogue.Street;

import java.util.List;

public interface StreetService {

    List<Street> getStreetsCorrespondingLocality(Long localityId);

    Street findStreetById(Long id);

}

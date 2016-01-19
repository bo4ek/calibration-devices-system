package com.softserve.edu.service.catalogue;

import com.softserve.edu.entity.catalogue.District;

import java.util.List;

public interface DistrictService {

    List<District> getDistrictsCorrespondingRegion(Long regionId);

    District findDistrictByDesignationAndRegion(String designation, Long region);

    District findDistrictById(Long id);

}

package com.softserve.edu.controller.admin;

import com.softserve.edu.dto.PageDTO;
import com.softserve.edu.dto.admin.StreetDTO;
import com.softserve.edu.entity.catalogue.Street;
import com.softserve.edu.service.catalogue.RegionService;
import com.softserve.edu.service.catalogue.StreetService;
import com.softserve.edu.service.utils.ListToPageTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "admin/streets/")
public class StreetDirectoryController {

    @Autowired
    RegionService regionService;

    @Autowired
    StreetService streetService;

    @RequestMapping(value = "{pageNumber}/{itemsPerPage}/{sortCriteria}/{sortOrder}", method = RequestMethod.GET)
    public PageDTO<StreetDTO> getStreetsPage(@PathVariable Integer pageNumber,
                                 @PathVariable Integer itemsPerPage,
                                 @PathVariable String sortCriteria,
                                 @PathVariable String sortOrder,
                                 StreetDTO search) {

        ListToPageTransformer<Street> queryResult = streetService.findPageOfAllStreets(
                pageNumber, itemsPerPage, search.getCity(), search.getDistrict(), search.getRegion(),
                search.getStreetName(), sortCriteria, sortOrder);

        List<StreetDTO> result = queryResult.getContent().stream()
                .map(street -> new StreetDTO(street.getId(), street.getLocality().getDistrict().getRegion().getDesignation(),
                        street.getLocality().getDistrict().getDesignation(), street.getLocality().getDesignation(),
                        street.getDesignation())).collect(Collectors.toList());
        return new PageDTO<>(queryResult.getTotalItems(), result);
    }
}

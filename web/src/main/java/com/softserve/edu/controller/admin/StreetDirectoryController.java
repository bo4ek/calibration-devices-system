package com.softserve.edu.controller.admin;

import com.softserve.edu.dto.PageDTO;
import com.softserve.edu.dto.admin.NewStreetDTO;
import com.softserve.edu.dto.admin.StreetDTO;
import com.softserve.edu.entity.catalogue.Locality;
import com.softserve.edu.entity.catalogue.Street;
import com.softserve.edu.repository.catalogue.StreetRepository;
import com.softserve.edu.service.catalogue.LocalityService;
import com.softserve.edu.service.catalogue.StreetService;
import com.softserve.edu.service.utils.ListToPageTransformer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "admin/streets/")
public class StreetDirectoryController {

    private Logger logger = Logger.getLogger(StreetDirectoryController.class);

    @Autowired
    StreetService streetService;

    @Autowired
    LocalityService localityService;

    @Autowired
    StreetRepository streetRepository;

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

    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseEntity addStreet(@RequestBody NewStreetDTO newStreetDTO) {
        Locality locality = localityService.findById(newStreetDTO.getLocalityId());
        streetRepository.save(new Street(newStreetDTO.getStreetId(), newStreetDTO.getStreetName(), locality));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Check is street with id {@code streetId} already exists
     *
     * @param streetId id of street
     * @return {@literal true} if street with id {@code streetId} already exists,
     * {@literal false} in other case
     */
    @RequestMapping(value = "isDuplicateId/{streetId}", method = RequestMethod.GET)
    public Boolean isDuplicateStreetId(@PathVariable Long streetId) {
        return streetRepository.exists(streetId);
    }

    /**
     * Check is street with name {@code streetName} already exists
     *
     * @param streetName name of street
     * @return {@literal true} if street with name {@code streetName} already exists,
     * {@literal false} in other case
     */
    @RequestMapping(value = "isDuplicateName/{streetName}", method = RequestMethod.GET)
    public Boolean isDuplicateStreetName(@PathVariable String streetName) {
        return streetService.findByDesignation(streetName) != null;
    }
}

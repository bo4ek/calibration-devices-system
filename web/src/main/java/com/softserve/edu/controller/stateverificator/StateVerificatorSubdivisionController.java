package com.softserve.edu.controller.stateverificator;

import com.softserve.edu.dto.PageDTO;
import com.softserve.edu.dto.verificator.VerificatorSubdivisionDTO;
import com.softserve.edu.dto.verificator.VerificatorSubdivisionPageItem;
import com.softserve.edu.entity.catalogue.Team.VerificatorSubdivision;
import com.softserve.edu.entity.organization.Organization;
import com.softserve.edu.entity.user.User;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.state.verificator.StateVerificatorSubdivisionService;
import com.softserve.edu.service.user.SecurityUserDetailsService;
import com.softserve.edu.service.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Project system-calibration-devices
 * Created by bo4ek on 27.01.2016.
 */

@RestController
@RequestMapping(value = "/verificator/subdivision/", produces = "application/json")
public class StateVerificatorSubdivisionController {

    private final Logger logger = Logger.getLogger(StateVerificatorSubdivisionController.class);

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private StateVerificatorSubdivisionService subdivisionService;

    @Autowired
    private UserService userService;

    /**
     * Adds new verificator's subdivision to the database
     *
     * @param subdivisionDTO
     * @param user
     * @return http status response 201 or 409
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseEntity addSubdivision(@RequestBody VerificatorSubdivisionDTO subdivisionDTO,
                                         @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        HttpStatus httpStatus = HttpStatus.CREATED;
        Organization organization = organizationService.getOrganizationById(user.getOrganizationId());
        try {
            VerificatorSubdivision subdivision = new VerificatorSubdivision(subdivisionDTO.getSubdivisionId(), subdivisionDTO.getSubdivisionName(),
                    subdivisionDTO.getSubdivisionLeader(), subdivisionDTO.getSubdivisionLeaderEmail(), subdivisionDTO.getSubdivisionLeaderPhone());
            subdivision.setOrganization(organization);
            subdivisionService.addSubdivision(subdivision);
            logger.info("Subdivision was successfully added");
        } catch (Exception e) {
            logger.error("Error occurred while creating subdivision", e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }

    /**
     * Edits verificator's subdivisions with @param id
     *
     * @param subdivisionDTO
     * @param id
     * @return
     */
    @RequestMapping(value = "edit/{id}", method = RequestMethod.PUT)
    public ResponseEntity editSubdivision(@RequestBody VerificatorSubdivisionDTO subdivisionDTO,
                                          @PathVariable String id) {
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            subdivisionService.editSubdivision(id, subdivisionDTO.getSubdivisionName(), subdivisionDTO.getSubdivisionLeader(),
                    subdivisionDTO.getSubdivisionLeaderEmail(), subdivisionDTO.getSubdivisionLeaderPhone());
            logger.info("Subdivision was successfully edited");
        } catch (Exception e) {
            logger.error("Error occurred while editing subdivision", e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }

    /**
     * Deletes verificator's subdivision from database
     *
     * @param id
     * @return OK or CONFLICT status
     */
    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteSubdivision(@PathVariable String id) {
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            subdivisionService.deleteSubdivision(id);
            logger.info("Subdivision was successfully deleted");
        } catch (Exception e) {
            logger.error("Error occurred while deleting subdivision", e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }

    /**
     * Check's for availability the number for verificator's subdivision
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "available/{id}", method = RequestMethod.GET)
    public Boolean isIdAvailable(@PathVariable String id) {

        return id != null && !subdivisionService.isIdAvailable(id);

    }

    /**
     * Gets verificator's subdivision from database
     *
     * @param id id of subdivision
     * @return data about subdivision
     */
    @RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    public VerificatorSubdivisionDTO getSubdivision(@PathVariable String id) {
        VerificatorSubdivision subdivision = subdivisionService.findById(id);
        return new VerificatorSubdivisionDTO(subdivision.getId(), subdivision.getName(),
                subdivision.getLeader(), subdivision.getLeaderEmail(), subdivision.getLeaderPhone());
    }

    /**
     * Gets all verificator's subdivision from database
     *
     * @param user is used to find subdivisions of specific organization
     * @return list of subdivisions
     */
    @RequestMapping(value = "all", method = RequestMethod.GET)
    public List<String> getAll(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        Organization organization = organizationService.getOrganizationById(user.getOrganizationId());
        return subdivisionService.findByOrganizationId(organization.getId()).stream()
                .map(VerificatorSubdivision::getId).collect(Collectors.toList());
    }

    /**
     * Form page with subdivisions according to the search data, number of items per page and page number
     *
     * @param pageNumber
     * @param itemsPerPage
     * @param search
     * @param user
     * @return
     */
    @RequestMapping(value = "{pageNumber}/{itemsPerPage}/{search}", method = RequestMethod.GET)
    public PageDTO<VerificatorSubdivisionPageItem> subdivisionPageWithSearch(
            @PathVariable Integer pageNumber, @PathVariable Integer itemsPerPage, @PathVariable String search,
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {

        Organization organization = organizationService.getOrganizationById(user.getOrganizationId());

        Page<VerificatorSubdivisionPageItem> page = subdivisionService
                .findByOrganizationAndSearchAndPagination(pageNumber, itemsPerPage, organization, search)
                .map(verificatorSubdivision -> new VerificatorSubdivisionPageItem(verificatorSubdivision.getId(),
                        verificatorSubdivision.getName(), verificatorSubdivision.getLeader(), verificatorSubdivision.getLeaderEmail(),
                        verificatorSubdivision.getLeaderPhone()));
        return new PageDTO<>(page.getTotalElements(), page.getContent());
    }

    /**
     * Form page with subdivisions according to the number of items per page and page number
     *
     * @param pageNumber
     * @param itemsPerPage
     * @param user
     * @return
     */
    @RequestMapping(value = "{pageNumber}/{itemsPerPage}", method = RequestMethod.GET)
    public PageDTO<VerificatorSubdivisionPageItem> subdivisionPage(
            @PathVariable Integer pageNumber, @PathVariable Integer itemsPerPage,
            @AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        return subdivisionPageWithSearch(pageNumber, itemsPerPage, null, user);
    }
}
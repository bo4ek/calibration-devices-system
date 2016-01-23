package com.softserve.edu.controller.admin;

import com.softserve.edu.dto.PageDTO;
import com.softserve.edu.dto.admin.CalibrationModuleDTO;
import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.entity.verification.calibration.CalibrationTask;
import com.softserve.edu.service.admin.CalibrationModuleService;
import com.softserve.edu.service.admin.OrganizationService;
import com.softserve.edu.service.user.SecurityUserDetailsService;
import com.softserve.edu.service.utils.TypeConverter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by roman on 08.11.15.
 */

@RestController
@RequestMapping(value = "/admin/calibration-module/")
public class CalibrationModuleController {

    private static Logger logger = Logger.getLogger(CalibrationModuleController.class);

    @Autowired
    private CalibrationModuleService calibrationModuleService;

    @Autowired
    private OrganizationService organizationService;

    /**
     * Get agreement by id
     *
     * @param id id of agreement to find
     * @return agreementDTO
     */
    @RequestMapping(value = "get/{id}")
    public CalibrationModuleDTO getCalibrationModule(@PathVariable("id") Long id) {
        CalibrationModule calibrationModule = calibrationModuleService.findModuleById(id);
        return new CalibrationModuleDTO(calibrationModule.getModuleId(),
                calibrationModule.getDeviceType().stream().map(Device.DeviceType::toString).collect(Collectors.toList()),
                calibrationModule.getOrganizationCode(), calibrationModule.getCondDesignation(),
                calibrationModule.getSerialNumber(), calibrationModule.getEmployeeFullName(),
                calibrationModule.getTelephone(), calibrationModule.getModuleNumber(),
                calibrationModule.getIsActive(), calibrationModule.getModuleType(), calibrationModule.getEmail(),
                calibrationModule.getCalibrationType(), calibrationModule.getWorkDate(), calibrationModule.getTasks());
    }

    /**
     * Add new calibration module
     *
     * @param calibrationModuleDTO calibration module to add
     * @return http status
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseEntity addModule(@RequestBody CalibrationModuleDTO calibrationModuleDTO) {
        HttpStatus httpStatus = HttpStatus.CREATED;
        CalibrationModule calibrationModule = new CalibrationModule(
                calibrationModuleDTO.getDeviceType().stream().map(Device.DeviceType::valueOf).collect(Collectors.toSet()),
                calibrationModuleDTO.getOrganizationCode(), calibrationModuleDTO.getCondDesignation(),
                calibrationModuleDTO.getSerialNumber(), calibrationModuleDTO.getEmployeeFullName(),
                calibrationModuleDTO.getTelephone(), calibrationModuleDTO.getModuleType(),
                calibrationModuleDTO.getEmail(), calibrationModuleDTO.getCalibrationType(),
                calibrationModuleDTO.getWorkDate());
        try {
            calibrationModuleService.addCalibrationModule(calibrationModule);
        } catch (Exception e) {
            logger.error("Error when adding calibration module", e);
            httpStatus = HttpStatus.CONFLICT;
        }

        return new ResponseEntity(httpStatus);
    }

    /**
     * Get all organization codes
     *
     * @return List of organizationCodes
     */
    @RequestMapping(value = "organizationCode", method = RequestMethod.GET)
    public List<String> getAllOrganizationCodes() {
        return organizationService.findAllOrganizationCodes();
}

    /**
     * Edit selected calibration module
     *
     * @param calibrationModuleDTO calibration module to edit
     * @param calibrationModuleId  id of calibration module to edit
     * @return http status
     */
    @RequestMapping(value = "edit/{calibrationModuleId}", method = RequestMethod.POST)
    public ResponseEntity editModule(@RequestBody CalibrationModuleDTO calibrationModuleDTO,
                                     @PathVariable Long calibrationModuleId) {
        HttpStatus httpStatus = HttpStatus.OK;
        CalibrationModule calibrationModule = new CalibrationModule(
                calibrationModuleDTO.getDeviceType().stream().map(Device.DeviceType::valueOf).collect(Collectors.toSet()),
                calibrationModuleDTO.getOrganizationCode(), calibrationModuleDTO.getCondDesignation(),
                calibrationModuleDTO.getSerialNumber(), calibrationModuleDTO.getEmployeeFullName(),
                calibrationModuleDTO.getTelephone(), calibrationModuleDTO.getModuleType(),
                calibrationModuleDTO.getEmail(), calibrationModuleDTO.getCalibrationType(),
                calibrationModuleDTO.getWorkDate());
        try {
            calibrationModuleService.updateCalibrationModule(calibrationModuleId, calibrationModule);
        } catch (Exception e) {
            logger.error("GOT EXCEPTION WHEN UPDATE CALIBRATION MODULE", e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }

    /**
     * Disable calibration module
     *
     * @param calibrationModuleId id of calibration module to disable
     * @return http status
     */
    @RequestMapping(value = "disable/{calibrationModuleId}", method = RequestMethod.GET)
    public ResponseEntity disableModule(@PathVariable Long calibrationModuleId) {
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            CalibrationModule calibrationModule = calibrationModuleService.findModuleById(calibrationModuleId);
            Set<CalibrationTask> tasks = calibrationModule.getTasks();
            if (tasks == null || tasks.isEmpty()) {
                calibrationModuleService.deleteCalibrationModule(calibrationModuleId);
            } else {
                calibrationModuleService.disableCalibrationModule(calibrationModuleId);
            }
        } catch (Exception e) {
            logger.error("GOT EXCEPTION ", e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }

    /**
     * Enable calibration module
     *
     * @param calibrationModuleId id of calibration module to enable
     * @return http status
     */
    @RequestMapping(value = "enable/{calibrationModuleId}", method = RequestMethod.GET)
    public ResponseEntity enableModule(@PathVariable Long calibrationModuleId) {
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            calibrationModuleService.enableCalibrationModule(calibrationModuleId);
        } catch (Exception e) {
            logger.error("GOT EXCEPTION ", e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }

    /**
     * Return page of calibration module according to filters and sort order
     *
     * @param pageNumber   number of page to return
     * @param itemsPerPage count of items on page
     * @param sortCriteria sorting criteria
     * @param sortOrder    order of sorting
     * @param searchData   filtering data
     * @return sorted and filtered page of calibration modules
     */
    @RequestMapping(value = "{pageNumber}/{itemsPerPage}/{sortCriteria}/{sortOrder}", method = RequestMethod.GET)
    public PageDTO<CalibrationModuleDTO> getSortedAndFilteredPageOfCalibrationModules(@PathVariable Integer pageNumber,
                                                                                      @PathVariable Integer itemsPerPage, @PathVariable String sortCriteria,
                                                                                      @PathVariable String sortOrder, CalibrationModuleDTO searchData) {
        // converting object to map and filtering the map to have only not-null fields
        Map<String, Object> searchDataMap = constructSearchDataMap(searchData);
        // creating Sort object for using as a parameter for Pageable creation
        Sort sort;
        if ((sortCriteria.equals("undefined") && sortOrder.equals("undefined")) ||
                sortCriteria == null && sortOrder == null) {
            sort = new Sort(Sort.Direction.DESC, "moduleId");
        } else {
            sort = new Sort(Sort.Direction.valueOf(sortOrder.toUpperCase()), sortCriteria);
        }
        Pageable pageable = new PageRequest(pageNumber - 1, itemsPerPage, sort);
        // fetching data from database, receiving a sorted and filtered page of calibration modules
        Page<CalibrationModule> queryResult = searchDataMap.isEmpty() ?
                calibrationModuleService.findAllModules(pageable) :
                calibrationModuleService.getFilteredPageOfCalibrationModule(searchDataMap, pageable);
        List<CalibrationModuleDTO> content = new ArrayList<CalibrationModuleDTO>();
        // converting Page of CalibrationModules to List of CalibrationModuleDTOs
        for (CalibrationModule calibrationModule : queryResult) {
            content.add(new CalibrationModuleDTO(calibrationModule.getModuleId(),
                    calibrationModule.getDeviceType().stream().map(Device.DeviceType::toString).collect(Collectors.toList()),
                    calibrationModule.getOrganizationCode(), calibrationModule.getCondDesignation(),
                    calibrationModule.getSerialNumber(), calibrationModule.getEmployeeFullName(),
                    calibrationModule.getTelephone(), calibrationModule.getModuleNumber(), calibrationModule.getIsActive(),
                    calibrationModule.getModuleType(), calibrationModule.getEmail(), calibrationModule.getCalibrationType(),
                    calibrationModule.getWorkDate(), calibrationModule.getTasks()));
        }
        return new PageDTO<>(queryResult.getTotalElements(), content);
    }

    /**
     * Return page of calibration modules
     *
     * @param pageNumber   number of page to return
     * @param itemsPerPage count of items on page
     * @return page of calibration module
     */
    @RequestMapping(value = "{pageNumber}/{itemsPerPage}", method = RequestMethod.GET)
    public PageDTO<CalibrationModuleDTO> getPageOfCalibrationModules(@PathVariable Integer pageNumber,
                                                                     @PathVariable Integer itemsPerPage) {
        return getSortedAndFilteredPageOfCalibrationModules(pageNumber, itemsPerPage, null, null, null);
    }

    @RequestMapping(value = "earliest_date", method = RequestMethod.GET)
    public String getEarliestDate(@AuthenticationPrincipal SecurityUserDetailsService.CustomUserDetails user) {
        if (user != null) {
            Date gottenDate = calibrationModuleService.getEarliestDate();
            Date date = null;
            if (gottenDate != null) {
                date = new Date(gottenDate.getTime());
            } else {
                return null;
            }
            DateTimeFormatter dbDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDateTime localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            return localDate.format(dbDateTimeFormatter);
        } else {
            return null;
        }
    }

    /*
     * method for constructing a map with filtering keys from the DTO, received from the view
     * (each key in a map must be the name of the field, and the value is the desired value)
     */
    private Map<String, Object> constructSearchDataMap(CalibrationModuleDTO searchData) {
        Map<String, Object> searchDataMap;
        List<Date> dateRange;
        searchDataMap = TypeConverter.ObjectToMapWithObjectValues(searchData);
        /**
         * if DTO with filtering parameters contains parameters for filtering by date range, fetch startDate and
         * endDate from DTO and convert them into list with two elements (startDate and endDate correspondingly).
         * Then put the latter into the map with search keys under the key "workDate" (filter class requires that
         * the name of the key in searchDataMap corresponds to the name of the entity fields in the database
         */
        if (searchDataMap.containsKey("startDateToSearch") || searchDataMap.containsKey("endDateToSearch")) {
            dateRange = new ArrayList<Date>();
            Collections.addAll(dateRange, new Date((Long) searchDataMap.get("startDateToSearch")),
                    new Date((Long) searchDataMap.get("endDateToSearch")));
            searchDataMap.put("workDate", dateRange);
            searchDataMap.remove("startDateToSearch");
            searchDataMap.remove("endDateToSearch");
        }
        return searchDataMap;
    }
}
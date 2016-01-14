package com.softserve.edu.controller.admin;

import com.softserve.edu.dto.PageDTO;
import com.softserve.edu.dto.admin.CounterTypeDTO;
import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.service.admin.CounterTypeService;
import com.softserve.edu.service.utils.ListToPageTransformer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/admin/counter-type/")
public class CounterTypeController {

    private final Logger logger = Logger.getLogger(CounterTypeController.class);

    @Autowired
    private CounterTypeService counterTypeService;

    /**
     * Add counter type
     *
     * @param counterTypeDTO object with counter type data
     * @return a response body with http status {@literal OK} if counter type
     * successfully edited or else http status {@literal CONFLICT}
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseEntity addCounterType(@RequestBody CounterTypeDTO counterTypeDTO) {
        HttpStatus httpStatus = HttpStatus.CREATED;
        try {
            counterTypeService.addCounterType(
                    counterTypeDTO.getName(),
                    counterTypeDTO.getSymbol(),
                    counterTypeDTO.getStandardSize(),
                    counterTypeDTO.getManufacturer(),
                    counterTypeDTO.getCalibrationInterval(),
                    counterTypeDTO.getYearIntroduction(),
                    counterTypeDTO.getGost(),
                    counterTypeDTO.getDeviceId()
            );
            logger.info("New counter type was created with params:" + counterTypeDTO);
        } catch (Exception e) {
            logger.error("Got exception while add counter type ", e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }

    /**
     * Edit counter type
     *
     * @param counterTypeDTO object with counter type data
     * @return a response body with http status {@literal OK} if counter type
     * successfully edited or else http status {@literal CONFLICT}
     */
    @RequestMapping(value = "edit/{counterTypeId}", method = RequestMethod.POST)
    public ResponseEntity editCounterType(@RequestBody CounterTypeDTO counterTypeDTO,
                                          @PathVariable Long counterTypeId) {
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            counterTypeService.editCounterType(
                    counterTypeId,
                    counterTypeDTO.getName(),
                    counterTypeDTO.getSymbol(),
                    counterTypeDTO.getStandardSize(),
                    counterTypeDTO.getManufacturer(),
                    counterTypeDTO.getCalibrationInterval(),
                    counterTypeDTO.getYearIntroduction(),
                    counterTypeDTO.getGost(),
                    counterTypeDTO.getDeviceId()
            );
            logger.info("Counter type with id= " + counterTypeId + " was edit successfully");
        } catch (Exception e) {
            logger.error("Got exception while editing counter type ", e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }

    /**
     * Delete counter type
     *
     * @param counterTypeId Long id of counter type
     * @return a response body with http status {@literal OK} if counter type
     * successfully edited or else http status {@literal CONFLICT}
     */
    @RequestMapping(value = "delete/{counterTypeId}", method = RequestMethod.DELETE)
    public ResponseEntity removeCounterType(@PathVariable Long counterTypeId) {
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            counterTypeService.removeCounterType(counterTypeId);
            logger.info("Counter type with id= " + counterTypeId + " was deleted successfully");
        } catch (Exception e) {
            logger.error("Got exception while remove counter type ", e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }

    /**
     * Get counter type with id
     *
     * @param id Long id of counter type
     * @return counterTypeDTO
     */
    @RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    public CounterTypeDTO getCounterType(@PathVariable("id") Long id) {
        CounterType counterType = counterTypeService.findById(id);
        CounterTypeDTO counterTypeDTO = new CounterTypeDTO(
                counterType.getId(),
                counterType.getDevice().getDeviceName(),
                counterType.getDevice().getDeviceType().toString(),
                counterType.getSymbol(),
                counterType.getStandardSize(),
                counterType.getManufacturer(),
                counterType.getCalibrationInterval(),
                counterType.getYearIntroduction(),
                counterType.getGost(),
                counterType.getDevice().getId()

        );
        return counterTypeDTO;
    }

    /**
     * Build page by SortCriteria, SortOrder and Searching data
     *
     * @param pageNumber
     * @param itemsPerPage
     * @param sortCriteria
     * @param sortOrder
     * @param searchData
     * @return
     */
    @RequestMapping(value = "{pageNumber}/{itemsPerPage}/{sortCriteria}/{sortOrder}", method = RequestMethod.GET)
    public PageDTO<CounterTypeDTO> pageCounterTypeWithSearch(@PathVariable Integer pageNumber, @PathVariable Integer itemsPerPage,
                                                             @PathVariable String sortCriteria, @PathVariable String sortOrder,
                                                             CounterTypeDTO searchData) {
        ListToPageTransformer<CounterType> queryResult = counterTypeService.getCounterTypeBySearchAndPagination(
                pageNumber,
                itemsPerPage,
                searchData.getName(),
                searchData.getSymbol(),
                searchData.getStandardSize(),
                searchData.getManufacturer(),
                searchData.getCalibrationInterval(),
                searchData.getYearIntroduction(),
                searchData.getGost(),
                sortCriteria,
                sortOrder
        );
        List<CounterTypeDTO> content = toCounterTypeDtoFromList(queryResult.getContent());
        return new PageDTO(queryResult.getTotalItems(), content);
    }

    /**
     * Build page without sorting, ordering and searching data
     *
     * @param pageNumber
     * @param itemsPerPage
     * @return
     */
    @RequestMapping(value = "{pageNumber}/{itemsPerPage}", method = RequestMethod.GET)
    public PageDTO<CounterTypeDTO> getCounterTypePage(@PathVariable Integer pageNumber, @PathVariable Integer itemsPerPage) {
        return pageCounterTypeWithSearch(pageNumber, itemsPerPage, null, null, null);
    }

    /**
     * Convert list of counter types to list CounterTypeDTO
     *
     * @param list
     * @return
     */
    public static List<CounterTypeDTO> toCounterTypeDtoFromList(List<CounterType> list) {
        List<CounterTypeDTO> resultList = new ArrayList<>();
        for (CounterType counterType : list) {
            resultList.add(new CounterTypeDTO(
                    counterType.getId(),
                    counterType.getDevice().getDeviceName(),
                    counterType.getDevice().getDeviceType().toString(),
                    counterType.getSymbol(),
                    counterType.getStandardSize(),
                    counterType.getManufacturer(),
                    counterType.getCalibrationInterval(),
                    counterType.getYearIntroduction(),
                    counterType.getGost(),
                    counterType.getDevice().getId()
            ));
        }
        return resultList;
    }
}

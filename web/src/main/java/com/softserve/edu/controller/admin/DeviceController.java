package com.softserve.edu.controller.admin;

import com.softserve.edu.dto.PageDTO;
import com.softserve.edu.dto.admin.DeviceDTO;
import com.softserve.edu.entity.device.Device;
import com.softserve.edu.service.tool.DeviceService;
import com.softserve.edu.service.utils.ListToPageTransformer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/admin/device-category/")
public class DeviceController {

    private final Logger logger = Logger.getLogger(DeviceController.class);

    @Autowired
    private DeviceService deviceService;

    /**
     * Looks for available Device with id
     * @param id
     * @return boolean
     */
    @RequestMapping(value = "available/{id}", method = RequestMethod.GET)
    public boolean isValidId(@PathVariable Long id) {
        boolean isAvailable = false;
        if (id != null) {
            isAvailable = deviceService.existsWithDeviceId(id);
        }
        return isAvailable;
    }

    /**
     * Saves device category  in database
     * @param deviceDTO
     * @return a response body with http status {@literal CREATED} if everything
     * device category successfully created or else http status {@literal CONFLICT}
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseEntity addDeviceCategory(@RequestBody DeviceDTO deviceDTO) {
        HttpStatus httpStatus = HttpStatus.CREATED;
        try {
            deviceService.addDeviceCategory(
                    deviceDTO.getDeviceType(),
                    deviceDTO.getDeviceName()
            );
        } catch (Exception e) {
            logger.error("Got exeption while add counter type ", e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }

    /**
     * Edit device category in database
     * @param deviceCategoryDTO object with device category data
     * @return a response body with http status {@literal OK} if device category
     * successfully edited or else http status {@literal CONFLICT}
     */
    @RequestMapping(value = "edit/{deviceCategoryId}", method = RequestMethod.POST)
     public ResponseEntity editDeviceCategory(@RequestBody DeviceDTO deviceCategoryDTO,
                                            @PathVariable Long deviceCategoryId) {
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            deviceService.editDeviceCategory(
                    deviceCategoryId,
                    deviceCategoryDTO.getDeviceType(),
                    deviceCategoryDTO.getDeviceName()
            );
        } catch (Exception e) {
            logger.error("Got exeption while editing counter type ",e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }

    /**
     * Remove device category from database
     * @param deviceCategoryId
     * @return response body with http status {@literal OK} if device category
     * successfully removed or else http status {@literal CONFLICT}
     */
    @RequestMapping(value = "delete/{deviceCategoryId}", method = RequestMethod.DELETE)
    public ResponseEntity removeDeviceCategory(@PathVariable Long deviceCategoryId) {
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            deviceService.removeDeviceCategory(deviceCategoryId);
        } catch (Exception e) {
            logger.error("Got exeption while remove counter type ",e);
            httpStatus = HttpStatus.CONFLICT;
        }
        return new ResponseEntity(httpStatus);
    }

    /**
     * Get Device category by id
     * @param id Long of device category
     * @return deviceDTO
     */
    @RequestMapping(value = "get/{id}")
    public DeviceDTO getDeviceCategory(@PathVariable("id") Long id) {
        Device deviceCategory = deviceService.getById(id);
        DeviceDTO deviceDTO = new DeviceDTO(deviceCategory.getId(), deviceCategory.getDeviceType().name(),
                deviceCategory.getDeviceSign(), deviceCategory.getNumber(), deviceCategory.getDeviceName());
        return deviceDTO;
    }

    /**
     * Building page, Sorting and Filtering
     * @param pageNumber
     * @param itemsPerPage
     * @param sortCriteria
     * @param sortOrder
     * @param searchData
     * @return
     */
    @RequestMapping(value = "{pageNumber}/{itemsPerPage}/{sortCriteria}/{sortOrder}", method = RequestMethod.GET)
    public PageDTO<DeviceDTO> pageDeviceCategoryWithSearch(@PathVariable Integer pageNumber, @PathVariable Integer itemsPerPage,
                                                           @PathVariable String sortCriteria, @PathVariable String sortOrder,
                                                           DeviceDTO searchData) {
        ListToPageTransformer<Device> queryResult = deviceService.getCategoryDevicesBySearchAndPagination(
                pageNumber,
                itemsPerPage,
                searchData.getId(),
                searchData.getDeviceType(),
                searchData.getDeviceName(),
                sortCriteria,
                sortOrder
        );
        List<DeviceDTO> content = toDeviceDtoFromList(queryResult.getContent());
        return new PageDTO(queryResult.getTotalItems(), content);
    }

    /**
     * Building page without sortCriteria and searchData
     * @param pageNumber
     * @param itemsPerPage
     * @return
     */
    @RequestMapping(value = "{pageNumber}/{itemsPerPage}", method = RequestMethod.GET)
    public PageDTO<DeviceDTO> getDeviceCategoryPage(@PathVariable Integer pageNumber, @PathVariable Integer itemsPerPage) {
        return pageDeviceCategoryWithSearch(pageNumber, itemsPerPage, null, null, null);
    }

    public static List<DeviceDTO> toDeviceDtoFromList(List<Device> list){
        List<DeviceDTO> resultList = new ArrayList<>();
        for (Device deviceCategory : list) {
            resultList.add(new DeviceDTO(
                    deviceCategory.getId(),
                    deviceCategory.getDeviceType().toString(),
                    deviceCategory.getDeviceSign(),
                    deviceCategory.getNumber(),
                    deviceCategory.getDeviceName()
            ));
        }
        return resultList;
    }
}

package com.softserve.edu.service.tool;

import com.softserve.edu.entity.device.Device;
import com.softserve.edu.service.utils.ListToPageTransformer;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DeviceService {

    boolean existsWithDeviceId(Long id);

    Device getById(Long id);

    List<Device> getAll();

    Page<Device> getDevicesBySearchAndPagination(int pageNumber, int itemsPerPage, String search);

    ListToPageTransformer<Device> getCategoryDevicesBySearchAndPagination(int pageNumber, int itemsPerPage, Long id,
                                                                        String deviceType, String deviceName,
                                                                        String sortCriteria, String sortOrder);

    List<Device> getAllByDeviceName(String device);

    Device getByDeviceTypeAndDefaultDevice(String deviceType, Boolean defaultDevice);

    void addDeviceCategory(String deviceType, String deviceName);

    void editDeviceCategory(Long id, String deviceType, String deviceName);

    void removeDeviceCategory(Long id);

    Device.DeviceType getDeviceTypeById(Long id);
}

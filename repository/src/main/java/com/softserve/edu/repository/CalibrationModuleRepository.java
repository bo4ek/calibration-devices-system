package com.softserve.edu.repository;

import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.device.Device;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by roman on 08.10.2015.
 */

@Repository
public interface CalibrationModuleRepository
        extends PagingAndSortingRepository<CalibrationModule, Long>, JpaSpecificationExecutor,
        CalibrationModuleRepositoryCustom {

    CalibrationModule findBySerialNumber(String serialNumber);

    CalibrationModule findByModuleNumber(String moduleNumber);

    @Query("SELECT MIN(C.workDate) FROM CalibrationModule C")
    Date findEarliestDateAvailableCalibrationModule();

    List<CalibrationModule> findAll();

    @Query("select m from CalibrationModule m where m.workDate >= CURRENT_DATE")
    List<CalibrationModule> findAllActing();

    @Query("SELECT CM FROM CalibrationModule CM WHERE :deviceType in elements(CM.deviceType)")
    List<CalibrationModule> findCalibrationModulesByDeviceType(@Param("deviceType") Device.DeviceType deviceType);
}

package com.softserve.edu.repository;

import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.verification.calibration.CalibrationTask;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface CalibrationPlanningTaskRepository extends
        PagingAndSortingRepository<CalibrationTask, Long>, JpaSpecificationExecutor {

    CalibrationTask findByDateOfTaskAndModule_SerialNumber(Date dateOfTask, String moduleSerialNumber);

    CalibrationTask findByDateOfTaskAndTeam_Id(Date dateOfTask, String teamId);

    @Query("SELECT c FROM CalibrationTask c WHERE c.dateOfTask = :dateOfTask " +
            "AND c.module = :module")
    CalibrationTask findByDateOfTaskAndModuleSerialNumber(@Param("dateOfTask") Date dateOfTask, @Param("module") CalibrationModule module);

    CalibrationTask findById(String id);
}

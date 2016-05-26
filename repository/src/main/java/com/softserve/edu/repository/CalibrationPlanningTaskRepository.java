package com.softserve.edu.repository;

import com.softserve.edu.entity.verification.calibration.CalibrationTask;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface CalibrationPlanningTaskRepository extends
        PagingAndSortingRepository<CalibrationTask, Long>, JpaSpecificationExecutor {

        CalibrationTask findByDateOfTaskAndModule_SerialNumber(Date dateOfTask, String moduleSerialNumber);

}

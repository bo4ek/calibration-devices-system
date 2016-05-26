package com.softserve.edu.repository;

import com.softserve.edu.entity.verification.calibration.CalibrationTestManual;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalibrationTestManualRepository extends CrudRepository<CalibrationTestManual, Long> {

    CalibrationTestManual findById(Long id);


}

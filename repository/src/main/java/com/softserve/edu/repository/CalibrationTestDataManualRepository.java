package com.softserve.edu.repository;

import com.softserve.edu.entity.verification.calibration.CalibrationTestDataManual;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CalibrationTestDataManualRepository extends CrudRepository<CalibrationTestDataManual, Long> {

    CalibrationTestDataManual findById(Long id);

    @Query("select c from CalibrationTestDataManual c where c.verification.id=:verificationId ")
    CalibrationTestDataManual findByVerificationId(@Param("verificationId") String verificationId);


}

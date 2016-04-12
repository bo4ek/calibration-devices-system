package com.softserve.edu.repository;

import com.softserve.edu.entity.verification.VerificationGroup;
import com.softserve.edu.entity.verification.calibration.CalibrationTask;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface VerificationGroupRepository extends
        PagingAndSortingRepository<VerificationGroup, Long> {

}

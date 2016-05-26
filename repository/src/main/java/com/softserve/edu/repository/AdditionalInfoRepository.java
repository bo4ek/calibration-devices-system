package com.softserve.edu.repository;

import com.softserve.edu.entity.verification.calibration.AdditionalInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalInfoRepository extends CrudRepository<AdditionalInfo, Long>{

    AdditionalInfo findAdditionalInfoByVerificationId(String verificationId);
}

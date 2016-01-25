package com.softserve.edu.service.calibrator.data.test;

import com.softserve.edu.entity.device.UnsuitabilityReason;
import com.softserve.edu.entity.verification.calibration.CalibrationTestDataManual;
import com.softserve.edu.entity.verification.calibration.CalibrationTestManual;



/**
 * Created by Misha on 12/13/2015.
 */
public interface CalibrationTestDataManualService {

    CalibrationTestDataManual findTestDataManual(Long id);

    CalibrationTestDataManual deleteTestDataManual(Long id);

    CalibrationTestDataManual findByVerificationId(String verifId);

    void createNewTestDataManual(String statusTestFirst, String statusTestSecond, String statusTestThird, String statusCommon, Long counterId, CalibrationTestManual calibrationTestManual, String verificationId, UnsuitabilityReason unsuitabilityReason);

    void editTestDataManual(String statusTestFirst, String statusTestSecond, String statusTestThird, String statusCommon, CalibrationTestDataManual cTestDataManual, String verificationId, Boolean verificationEdit, UnsuitabilityReason unsuitabilityReason);


}

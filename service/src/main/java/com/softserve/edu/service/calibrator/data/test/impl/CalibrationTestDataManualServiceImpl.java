package com.softserve.edu.service.calibrator.data.test.impl;

import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.UnsuitabilityReason;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.verification.Verification.CalibrationTestResult;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTestDataManual;
import com.softserve.edu.entity.verification.calibration.CalibrationTestManual;
import com.softserve.edu.repository.CalibrationTestDataManualRepository;
import com.softserve.edu.repository.VerificationRepository;
import com.softserve.edu.repository.CounterRepository;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestDataManualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by Misha on 12/13/2015.
 */

@Service
public class CalibrationTestDataManualServiceImpl implements CalibrationTestDataManualService {

    @Autowired
    private CalibrationTestDataManualRepository calibrationTestDataManualRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private CounterRepository counterRepository;

    @Override
    public CalibrationTestDataManual findTestDataManual(Long id) {
        return null;
    }

    @Override
    public CalibrationTestDataManual deleteTestDataManual(Long id) {
        return null;
    }

    @Override
    @Transactional
    public CalibrationTestDataManual findByVerificationId(String verifId) {
        return calibrationTestDataManualRepository.findByVerificationId(verifId);
    }

    @Override
    @Transactional
    public void createNewTestDataManual(String statusTestFirst, String statusTestSecond, String statusTestThird, String statusCommon, Long counterId, CalibrationTestManual calibrationTestManual, String verificationId, UnsuitabilityReason unsuitabilityReason) {
        Verification verification = verificationRepository.findOne(verificationId);
        Counter counter = counterRepository.findOne(counterId);
        CalibrationTestDataManual calibrationTestDataManual = new CalibrationTestDataManual(CalibrationTestResult.valueOf(statusTestFirst)
                , CalibrationTestResult.valueOf(statusTestSecond), CalibrationTestResult.valueOf(statusTestThird)
                , CalibrationTestResult.valueOf(statusCommon)
                , counter, calibrationTestManual, verification, unsuitabilityReason);
        calibrationTestDataManualRepository.save(calibrationTestDataManual);
        verification.setIsManual(true);
        verification.setStatus(Status.TEST_COMPLETED);
        verificationRepository.save(verification);
    }


    @Override
    @Transactional
    public void editTestDataManual(String statusTestFirst, String statusTestSecond, String statusTestThird, String statusCommon, CalibrationTestDataManual cTestDataManual, String verificationId, Boolean verificationEdit, UnsuitabilityReason unsuitabilityReason) {
        cTestDataManual.setStatusTestFirst(CalibrationTestResult.valueOf(statusTestFirst));
        cTestDataManual.setStatusTestSecond(CalibrationTestResult.valueOf(statusTestSecond));
        cTestDataManual.setStatusTestThird(CalibrationTestResult.valueOf(statusTestThird));
        CalibrationTestResult commonTestResult = CalibrationTestResult.valueOf(statusCommon);
        cTestDataManual.setStatusCommon(commonTestResult);
        cTestDataManual.setUnsuitabilityReason(unsuitabilityReason);
        if (verificationEdit) {
            Verification verification = verificationRepository.findOne(verificationId);
            if (commonTestResult.equals(CalibrationTestResult.SUCCESS)) {
                verification.setStatus(Status.TEST_OK);
                verificationRepository.save(verification);
            } else if (commonTestResult.equals(CalibrationTestResult.FAILED)) {
                verification.setStatus(Status.TEST_NOK);
                verificationRepository.save(verification);
            }
        }
        calibrationTestDataManualRepository.save(cTestDataManual);
    }
}

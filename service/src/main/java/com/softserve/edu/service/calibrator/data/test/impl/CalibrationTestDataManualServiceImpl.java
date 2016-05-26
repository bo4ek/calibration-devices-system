package com.softserve.edu.service.calibrator.data.test.impl;

import com.softserve.edu.entity.device.CalibrationModule;
import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.device.CounterType;
import com.softserve.edu.entity.device.UnsuitabilityReason;
import com.softserve.edu.entity.enumeration.verification.Status;
import com.softserve.edu.entity.verification.Verification.CalibrationTestResult;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTestDataManual;
import com.softserve.edu.entity.verification.calibration.CalibrationTestManual;
import com.softserve.edu.repository.*;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestDataManualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class CalibrationTestDataManualServiceImpl implements CalibrationTestDataManualService {

    @Autowired
    private CalibrationTestDataManualRepository calibrationTestDataManualRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private CounterRepository counterRepository;

    @Autowired
    private CounterTypeRepository counterTypeRepository;

    @Autowired
    CalibrationModuleRepository calibrationModuleRepository;

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
    public void createNewTestDataManual(String statusTestFirst, String statusTestSecond, String statusTestThird,
                                        String statusCommon, CalibrationTestManual calibrationTestManual, String verificationId,
                                        UnsuitabilityReason unsuitabilityReason, int realiseYear, String numberCounter, Long counterTypeId, Long moduleId) {
        Verification verification = verificationRepository.findOne(verificationId);
        CalibrationModule calibrationModule = calibrationModuleRepository.findOne(moduleId);
        CounterType counterType = counterTypeRepository.findOne(counterTypeId);

        Counter counter = verification.getCounter();
        counter.setReleaseYear(Integer.valueOf(realiseYear).toString());
        counter.setNumberCounter(numberCounter);
        counter.setCounterType(counterType);
        counterRepository.save(counter);
        CalibrationTestDataManual calibrationTestDataManual = new CalibrationTestDataManual(CalibrationTestResult.valueOf(statusTestFirst)
                , CalibrationTestResult.valueOf(statusTestSecond), CalibrationTestResult.valueOf(statusTestThird)
                , CalibrationTestResult.valueOf(statusCommon)
                , counter, calibrationTestManual, verification, unsuitabilityReason);
        calibrationTestDataManualRepository.save(calibrationTestDataManual);
        verification.setManual(true);
        verification.setCalibrationTestDataManualId(calibrationTestDataManual);
        verification.setStatus(Status.TEST_COMPLETED);
        verification.setInitialDate(new Date());
        verification.setCalibrationModule(calibrationModule);
        verification.setNumberOfProtocol(calibrationTestManual.getGenerateNumberTest());
        verificationRepository.save(verification);
    }


    @Override
    @Transactional
    public void editTestDataManual(String statusTestFirst, String statusTestSecond, String statusTestThird, String statusCommon,
                                   CalibrationTestDataManual cTestDataManual, String verificationId, Boolean verificationEdit,
                                   UnsuitabilityReason unsuitabilityReason, int realiseYear, String numberCounter, Long counterTypeId, Long moduleId) {
        CalibrationModule calibrationModule = calibrationModuleRepository.findOne(moduleId);
        Verification verification = verificationRepository.findOne(verificationId);
        CounterType counterType = counterTypeRepository.findOne(counterTypeId);
        Counter counter = verificationRepository.findOne(verificationId).getCounter();
        counter.setReleaseYear(Integer.valueOf(realiseYear).toString());
        counter.setNumberCounter(numberCounter);
        counter.setCounterType(counterType);
        counterRepository.save(counter);

        cTestDataManual.setStatusTestFirst(CalibrationTestResult.valueOf(statusTestFirst));
        cTestDataManual.setStatusTestSecond(CalibrationTestResult.valueOf(statusTestSecond));
        cTestDataManual.setStatusTestThird(CalibrationTestResult.valueOf(statusTestThird));
        CalibrationTestResult commonTestResult = CalibrationTestResult.valueOf(statusCommon);
        cTestDataManual.setStatusCommon(commonTestResult);
        cTestDataManual.setUnsuitabilityReason(unsuitabilityReason);

        verification.setInitialDate(new Date());
        verification.setCalibrationModule(calibrationModule);
        verification.setNumberOfProtocol(cTestDataManual.getCalibrationTestManual().getGenerateNumberTest());
        if (verificationEdit) {
            if (commonTestResult.equals(CalibrationTestResult.SUCCESS)) {
                verification.setStatus(Status.TEST_OK);
            } else if (commonTestResult.equals(CalibrationTestResult.FAILED)) {
                verification.setStatus(Status.TEST_NOK);
            }
        }
        verificationRepository.save(verification);
        calibrationTestDataManualRepository.save(cTestDataManual);
    }
}

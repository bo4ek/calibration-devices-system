package com.softserve.edu.controller.calibrator.util;

import com.softserve.edu.dto.CalibrationTestDTO;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;

import java.util.ArrayList;
import java.util.List;


public class CalibratorTestPageDTOTransformer {
    public  static List<CalibrationTestDTO> toDtoFromList (List<CalibrationTest> calibrationTests){
        List<CalibrationTestDTO> resultList = new ArrayList<>();

        for (CalibrationTest calibrationTest : calibrationTests){
            resultList.add(new CalibrationTestDTO(
                    calibrationTest.getVerification().getId(),
                    calibrationTest.getName(),
                    calibrationTest.getDateTest(),
                    calibrationTest.getCapacity(),
                    calibrationTest.getLatitude(),
                    calibrationTest.getLongitude(),
                    calibrationTest.getConsumptionStatus(),
                    calibrationTest.getTestResult(),
                    calibrationTest.getVerification().getClientData().getFullName(),
                    calibrationTest.getVerification().getClientData().getClientAddress().getStreet(),
                    calibrationTest.getVerification().getClientData().getClientAddress().getRegion(),
                    calibrationTest.getVerification().getClientData().getClientAddress().getDistrict(),
                    calibrationTest.getVerification().getClientData().getClientAddress().getLocality(),
                    calibrationTest.getId(),
                    calibrationTest.getVerification().getDevice().getId(),
                    calibrationTest.getVerification().getDevice().getDeviceType().toString(),
                    calibrationTest.getMeteorologicalDocument().getName()
            ));
        }
        return resultList;
    }
}

package com.softserve.edu.dto;

import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.calibration.CalibrationTestData;
import com.softserve.edu.entity.verification.calibration.CalibrationTestIMG;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestService;
import com.softserve.edu.entity.verification.Verification.ConsumptionStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Getter
@Setter
public class CalibrationTestFileDataDTO {

    private String fileName;

    private String verificationId;

    private String counterNumber;

    private Date testDate;

    private String capacity;

    private String accumulatedVolume;

    private int counterProductionYear;

    private long installmentNumber;

    private double latitude;

    private double longitude;

    private ConsumptionStatus consumptionStatus;

    private String testPhoto;

    private Verification.CalibrationTestResult testResult;

    private List<CalibrationTestDataDTO> listTestData;

    private String status;

    private Integer testPosition;

    private String typeWater;

    private Long counterId;

    private String standardSize;

    private String symbol;

    private Long counterTypeId;

    private boolean signed;

    private Long reasonUnsuitabilityId;

    private String reasonUnsuitabilityName;

    private Integer rotateIndex;

    private String condDesignation;

    private String serialNumber;

    private String serviceType;

    private Integer temperature;

    private String moduleNumber;

    private Integer index;

    public CalibrationTestFileDataDTO() {
    }

    public CalibrationTestFileDataDTO(CalibrationTest calibrationTest, CalibrationTestService testService, Verification verification) {
        this.fileName = calibrationTest != null ? calibrationTest.getName() : null;
        this.verificationId = verification.getId();
        Counter counter = verification.getCounter();
        this.counterNumber = counter.getNumberCounter();
        this.testDate = calibrationTest.getDateTest();
        this.accumulatedVolume = calibrationTest.getCapacity();
        this.counterProductionYear = counter.getReleaseYear() != null ? Integer.valueOf(counter.getReleaseYear()) : null;
        if (verification.getCalibrationModule() != null) {
            this.installmentNumber = verification.getCalibrationModule().getModuleId();
            this.serialNumber = verification.getCalibrationModule().getSerialNumber();
            this.moduleNumber = verification.getCalibrationModule().getModuleNumber();
            this.condDesignation = verification.getCalibrationModule().getCondDesignation();
        }
        this.latitude = calibrationTest.getLatitude();
        this.longitude = calibrationTest.getLongitude();
        this.testPhoto = testService.getPhotoAsString(calibrationTest.getPhotoPath(), calibrationTest);
        this.consumptionStatus = calibrationTest.getConsumptionStatus();
        this.testResult = calibrationTest.getTestResult();
        this.listTestData = new ArrayList();
        if (counter.getCounterType() != null) {
            this.typeWater = counter.getCounterType().getDevice() != null && counter.getCounterType().getDevice().getDeviceType() != null ? counter.getCounterType().getDevice().getDeviceType().toString() : null;
            this.standardSize = counter.getCounterType().getStandardSize();
            this.symbol = counter.getCounterType().getSymbol();
            this.counterTypeId = counter.getCounterType().getId();
        }
        this.serviceType = verification.getDevice() != null && verification.getDevice().getDeviceType() != null ? verification.getDevice().getDeviceType().name() : null;
        this.temperature = calibrationTest.getTemperature();
        this.signed = verification.isSigned();
        this.reasonUnsuitabilityId = calibrationTest.getUnsuitabilityReason() == null ? null : calibrationTest.getUnsuitabilityReason().getId();
        this.reasonUnsuitabilityName = calibrationTest.getUnsuitabilityReason() == null ? null : calibrationTest.getUnsuitabilityReason().getName();
        this.rotateIndex = calibrationTest.getRotateIndex();
        int testNumber = 1;
        List<CalibrationTestIMG> calibrationTestIMGList;
        CalibrationTestIMG calibrationTestIMG;
        this.status = calibrationTest.getVerification().getStatus().toString();
        this.counterId = counter.getId();

        for (CalibrationTestData calibrationTestData : testService.getLatestTests(calibrationTest.getCalibrationTestDataList())) {
            CalibrationTestDataDTO testDataDTO = new CalibrationTestDataDTO();
            testDataDTO.setDataAvailable(true);
            testDataDTO.setTestNumber("Test" + testNumber);
            testDataDTO.setGivenConsumption(calibrationTestData.getGivenConsumption());
            testDataDTO.setAcceptableError(calibrationTestData.getAcceptableError());
            testDataDTO.setInitialValue(calibrationTestData.getInitialValue());
            testDataDTO.setEndValue(calibrationTestData.getEndValue());
            testDataDTO.setVolumeInDevice(calibrationTestData.getVolumeInDevice());
            testDataDTO.setVolumeOfStandard(calibrationTestData.getVolumeOfStandard());
            testDataDTO.setActualConsumption(calibrationTestData.getActualConsumption());
            testDataDTO.setCalculationError(calibrationTestData.getCalculationError());
            calibrationTestIMGList = calibrationTestData.getTestIMGs();
            for (int orderPhoto = 0; orderPhoto < calibrationTestIMGList.size(); orderPhoto++) {
                calibrationTestIMG = calibrationTestIMGList.get(orderPhoto);
                if (orderPhoto == 0) {
                    testDataDTO.setBeginPhoto(testService.getPhotoAsString(calibrationTestIMG.getImgName(), calibrationTest));
                } else {
                    testDataDTO.setEndPhoto(testService.getPhotoAsString(calibrationTestIMG.getImgName(), calibrationTest));
                }
            }
            testDataDTO.setTestPosition(calibrationTestData.getTestPosition());
            testDataDTO.setTestTime(round(calibrationTestData.getDuration(), 1));
            testDataDTO.setTestResult(calibrationTestData.getTestResult());
            testDataDTO.setConsumptionStatus(calibrationTestData.getConsumptionStatus());
            listTestData.add(testDataDTO);
            testNumber++;
        }
    }

    private double round(double val, int scale) {
        return BigDecimal.valueOf(val).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

}

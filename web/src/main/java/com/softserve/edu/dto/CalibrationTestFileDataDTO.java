package com.softserve.edu.dto;

import com.softserve.edu.entity.device.Counter;
import com.softserve.edu.entity.verification.Verification;
import com.softserve.edu.entity.verification.calibration.CalibrationTest;
import com.softserve.edu.entity.verification.calibration.CalibrationTestData;
import com.softserve.edu.entity.verification.calibration.CalibrationTestIMG;
import com.softserve.edu.service.calibrator.data.test.CalibrationTestService;
import com.softserve.edu.entity.verification.Verification.ConsumptionStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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

    public CalibrationTestFileDataDTO() {
    }

    public CalibrationTestFileDataDTO(CalibrationTest calibrationTest, CalibrationTestService testService, Verification verification) {
        this.fileName = calibrationTest.getName();
        this.verificationId = verification.getId();
        Counter counter = verification.getCounter();
        this.counterNumber = counter.getNumberCounter();
        this.testDate = calibrationTest.getDateTest();
        this.accumulatedVolume = calibrationTest.getCapacity();
        this.counterProductionYear = Integer.valueOf(counter.getReleaseYear());
        this.installmentNumber = verification.getCalibrationModule().getModuleId();
        this.serialNumber = verification.getCalibrationModule().getSerialNumber();
        this.condDesignation = verification.getCalibrationModule().getCondDesignation();
        this.latitude = calibrationTest.getLatitude();
        this.longitude = calibrationTest.getLongitude();
        this.testPhoto = testService.getPhotoAsString(calibrationTest.getPhotoPath(), calibrationTest);
        this.consumptionStatus = calibrationTest.getConsumptionStatus();
        this.testResult = calibrationTest.getTestResult();
        this.listTestData = new ArrayList();
        this.typeWater = counter.getCounterType().getDevice().getDeviceType().toString();
        this.serviceType = verification.getDevice().getDeviceType().name();
        this.signed = verification.isSigned();
        this.reasonUnsuitabilityId = calibrationTest.getUnsuitabilityReason() == null ? null : calibrationTest.getUnsuitabilityReason().getId();
        this.reasonUnsuitabilityName = calibrationTest.getUnsuitabilityReason() == null ? null : calibrationTest.getUnsuitabilityReason().getName();
        this.rotateIndex = calibrationTest.getRotateIndex();
        int testNumber = 1;
        List<CalibrationTestIMG> calibrationTestIMGList;
        CalibrationTestIMG calibrationTestIMG;
        this.status = calibrationTest.getVerification().getStatus().toString();
        this.counterId = counter.getId();
        this.standardSize = counter.getCounterType().getStandardSize();
        this.symbol = counter.getCounterType().getSymbol();
        this.counterTypeId = counter.getCounterType().getId();
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

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public Long getCounterTypeId() {
        return counterTypeId;
    }

    public void setCounterTypeId(Long counterTypeId) {
        this.counterTypeId = counterTypeId;
    }

    public Long getCounterId() {
        return counterId;
    }

    public void setCounterId(Long counterId) {
        this.counterId = counterId;
    }

    public String getStandardSize() {
        return standardSize;
    }

    public void setStandardSize(String standardSize) {
        this.standardSize = standardSize;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCounterNumber() {
        return counterNumber;
    }

    public void setCounterNumber(String counterNumber) {
        this.counterNumber = counterNumber;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setTemperature(int temperature) {
        this.capacity = capacity;
    }

    public int getCounterProductionYear() {
        return counterProductionYear;
    }

    public void setCounterProductionYear(int counterProductionYear) {
        this.counterProductionYear = counterProductionYear;
    }

    public String getAccumulatedVolume() {
        return accumulatedVolume;
    }

    public void setAccumulatedVolume(String accumulatedVolume) {
        this.accumulatedVolume = accumulatedVolume;
    }

    public long getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(long installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ConsumptionStatus getConsumptionStatus() {
        return consumptionStatus;
    }

    public void setConsumptionStatus(ConsumptionStatus consumptionStatus) {
        this.consumptionStatus = consumptionStatus;
    }

    public Verification.CalibrationTestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(Verification.CalibrationTestResult testResult) {
        this.testResult = testResult;
    }

    public String getTestPhoto() {
        return testPhoto;
    }

    public void setTestPhoto(String testPhoto) {
        this.testPhoto = testPhoto;
    }

    public List<CalibrationTestDataDTO> getListTestData() {
        return listTestData;
    }

    public void setListTestData(List<CalibrationTestDataDTO> listTestData) {
        this.listTestData = listTestData;
    }

    private double round(double val, int scale) {
        return BigDecimal.valueOf(val).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    private double convertImpulsesPerSecToCubicMetersPerHour(double impulses, long impLitPrice) {
        return round(3.6 * impulses / impLitPrice, 3);
    }

    private double countCalculationError(double counterVolume, double standardVolume) {
        if (standardVolume < 0.0001) {
            return 0.0;
        }
        double result = (counterVolume - standardVolume) / standardVolume * 100;
        return round(result, 2);
    }

    public Integer getTestPosition() {
        return testPosition;
    }

    public void setTestPosition(Integer testPosition) {
        this.testPosition = testPosition;
    }

    public String getTypeWater() {
        return typeWater;
    }

    public void setTypeWater(String typeWater) {
        this.typeWater = typeWater;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public Long getReasonUnsuitabilityId() {
        return reasonUnsuitabilityId;
    }

    public void setReasonUnsuitabilityId(Long reasonUnsuitabilityId) {
        this.reasonUnsuitabilityId = reasonUnsuitabilityId;
    }

    public String getReasonUnsuitabilityName() {
        return reasonUnsuitabilityName;
    }

    public void setReasonUnsuitabilityName(String reasonUnsuitabilityName) {
        this.reasonUnsuitabilityName = reasonUnsuitabilityName;
    }

    public Integer getRotateIndex() {
        return rotateIndex;
    }

    public void setRotateIndex(Integer rotateIndex) {
        this.rotateIndex = rotateIndex;
    }

    public String getCondDesignation() {
        return condDesignation;
    }

    public void setCondDesignation(String condDesignation) {
        this.condDesignation = condDesignation;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
}

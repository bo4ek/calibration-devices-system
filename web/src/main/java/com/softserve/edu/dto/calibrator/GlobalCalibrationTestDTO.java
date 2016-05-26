package com.softserve.edu.dto.calibrator;

import com.softserve.edu.entity.verification.Verification;

import java.util.Date;

public class GlobalCalibrationTestDTO {
    private String name;
    private Date dateTest;
    private Integer temperature;
    private Integer settingNumber;
    private Double latitude;
    private Double longitude;
    private String consumptionStatus;
    private Verification.CalibrationTestResult testResult;

    public GlobalCalibrationTestDTO(){}

    public GlobalCalibrationTestDTO(String name, Date dateTest, Integer temperature, Integer settingNumber, Double latitude, Double longitude, String consumptionStatus,
                                    Verification.CalibrationTestResult testResult) {
        this.name = name;
        this.dateTest = dateTest;
        this.temperature = temperature;
        this.settingNumber = settingNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.consumptionStatus = consumptionStatus;
        this.testResult = testResult;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateTest() {
        return dateTest;
    }

    public void setDateTest(Date dateTest) {
        this.dateTest = dateTest;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getSettingNumber() {
        return settingNumber;
    }

    public void setSettingNumber(Integer settingNumber) {
        this.settingNumber = settingNumber;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getConsumptionStatus() {
        return consumptionStatus;
    }

    public void setConsumptionStatus(String consumptionStatus) {
        this.consumptionStatus = consumptionStatus;
    }

    public Verification.CalibrationTestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(Verification.CalibrationTestResult testResult) {
        this.testResult = testResult;
    }

}

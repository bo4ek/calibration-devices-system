package com.softserve.edu.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Date;

@Getter
@Setter
public class CalibrationTestManualDTO {

    private String  serialNumber;
    private List<CalibrationTestDataManualDTO>listOfCalibrationTestDataManual;
    private String numberOfTest;
    private Date dateOfTest;
    private String generateNumber;
    private String pathToScanDoc;
    private Long id;
    private Long moduleId;
    private Long counterTypeId;

    public CalibrationTestManualDTO() {}

    public CalibrationTestManualDTO(String serialNumber, String numberOfTest, Date dateOfTest, String generateNumber, String pathToScanDoc, Long id, Long counterTypeId) {
        this.serialNumber = serialNumber;
        this.numberOfTest = numberOfTest;
        this.dateOfTest = dateOfTest;
        this.generateNumber = generateNumber;
        this.pathToScanDoc = pathToScanDoc;
        this.id = id;
        this.counterTypeId = counterTypeId;
    }


}
